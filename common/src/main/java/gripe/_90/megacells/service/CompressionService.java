package gripe._90.megacells.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import appeng.api.stacks.AEItemKey;

public class CompressionService {
    public static final CompressionService INSTANCE = new CompressionService();

    // Each chain is an ordered map with the items themselves as the keys and the values being the amount of either:
    // - the item itself, needed to compress to its next variant
    // - the next variant, when decompressing the item
    // This value is typically either 4 or 9 for any given item.
    private final Set<Object2IntMap<AEItemKey>> compressionChains = new ObjectLinkedOpenHashSet<>();

    private CompressionService() {}

    public Optional<Object2IntMap<AEItemKey>> getChain(AEItemKey key) {
        return compressionChains.stream()
                .filter(chain -> chain.containsKey(key))
                .findFirst();
    }

    public Object2IntMap<AEItemKey> getVariants(AEItemKey key, boolean decompress) {
        return getChain(key)
                .map(chain -> {
                    var keys = new ObjectArrayList<>(chain.keySet());

                    // Reverse ordering when going from provided storage/filter variant to least-compressed "base unit"
                    if (decompress) {
                        Collections.reverse(keys);
                    }

                    // Split variant chain into separate compressed/decompressed chains, omitting the initial variant
                    // provided
                    var variants = new Object2IntLinkedOpenHashMap<AEItemKey>();
                    keys.subList(keys.indexOf(key) + 1, keys.size()).forEach(k -> variants.put(k, chain.getInt(k)));
                    return variants;
                })
                .orElseGet(Object2IntLinkedOpenHashMap::new);
    }

    public void loadRecipes(RecipeManager recipeManager, RegistryAccess access) {
        // Clear old variant cache in case of the server restarting or recipes being reloaded
        compressionChains.clear();

        // Retrieve all available "compression" and "decompression" recipes from the current server's recipe manager
        var allRecipes = recipeManager.getAllRecipesFor(RecipeType.CRAFTING);
        var compressedCandidates = allRecipes.stream()
                .filter(recipe -> isCompressionRecipe(recipe, access))
                .toList();
        var decompressedCandidates = allRecipes.stream()
                .filter(recipe -> isDecompressionRecipe(recipe, access))
                .toList();

        // Filter gathered candidate recipes and retain only those that are reversible (i.e. those which can be carried
        // out back and forth to compress/decompress a resource without affecting the underlying quantity of it)
        var compressed = compressedCandidates.stream()
                .filter(recipe -> isReversibleRecipe(recipe, decompressedCandidates, access))
                .toList();
        var decompressed = decompressedCandidates.stream()
                .filter(recipe -> isReversibleRecipe(recipe, compressedCandidates, access))
                .toList();

        // Pull all available compression chains from the recipe shortlist and add these to the cache
        for (var recipe : compressed) {
            var baseVariant = recipe.getResultItem(access).getItem();

            if (compressionChains.stream().noneMatch(chain -> chain.containsKey(AEItemKey.of(baseVariant)))) {
                var newChain = generateChain(baseVariant, compressed, decompressed, access);

                if (!newChain.isEmpty()) {
                    compressionChains.add(newChain);
                }
            }
        }
    }

    private boolean isCompressionRecipe(CraftingRecipe recipe, RegistryAccess access) {
        return (recipe.getIngredients().size() == 4 || recipe.getIngredients().size() == 9)
                && recipe.getIngredients().stream().distinct().count() <= 1
                && recipe.getResultItem(access).getCount() == 1;
    }

    private boolean isDecompressionRecipe(CraftingRecipe recipe, RegistryAccess access) {
        return (recipe.getResultItem(access).getCount() == 4
                        || recipe.getResultItem(access).getCount() == 9)
                && recipe.getIngredients().size() == 1;
    }

    private boolean isReversibleRecipe(CraftingRecipe recipe, List<CraftingRecipe> candidates, RegistryAccess access) {
        var compressible = false;
        var decompressible = false;

        var input = recipe.getIngredients().get(0);
        var output = recipe.getResultItem(access);

        for (var candidate : candidates) {
            for (var item : candidate.getIngredients().get(0).getItems()) {
                if (item.getItem().equals(output.getItem())) {
                    compressible = true;
                }
            }

            for (var item : input.getItems()) {
                if (item.getItem().equals(candidate.getResultItem(access).getItem())) {
                    decompressible = true;
                }
            }

            if (compressible && decompressible) return true;
        }

        return false;
    }

    private Object2IntMap<AEItemKey> generateChain(
            Item baseVariant,
            List<CraftingRecipe> compressed,
            List<CraftingRecipe> decompressed,
            RegistryAccess access) {
        var decompressionChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

        for (var lower = getNextVariant(baseVariant, decompressed, access); lower != null; ) {
            decompressionChain.put(AEItemKey.of(lower.first()), (int) lower.second());
            lower = getNextVariant(lower.first(), decompressed, access);
        }

        var compressionChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

        for (var higher = getNextVariant(baseVariant, compressed, access); higher != null; ) {
            compressionChain.put(AEItemKey.of(higher.first()), (int) higher.second());
            higher = getNextVariant(higher.first(), compressed, access);
        }

        // Collate decompression and compression chains together with base variant
        var fullChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

        // In theory this shouldn't even be happening by this point
        if (compressionChain.isEmpty() && decompressionChain.isEmpty()) return fullChain;

        // By default, full chains go from the smallest "unit" variant to the most compressed, so reverse the
        // decompression chain and add it first
        var decompressionKeys = new ObjectArrayList<>(decompressionChain.keySet());
        Collections.reverse(decompressionKeys);
        decompressionKeys.forEach(k -> fullChain.put(k, decompressionChain.getInt(k)));

        // Retrieve appropriate multiplier for base variant for completion's sake
        fullChain.put(
                AEItemKey.of(baseVariant),
                fullChain.isEmpty()
                        ? compressionChain.getInt(compressionChain.firstKey())
                        : fullChain.getInt(fullChain.lastKey()));

        fullChain.putAll(compressionChain);
        return fullChain;
    }

    private Pair<Item, Integer> getNextVariant(Item item, List<CraftingRecipe> recipes, RegistryAccess access) {
        for (var recipe : recipes) {
            for (var input : recipe.getIngredients().get(0).getItems()) {
                if (input.getItem().equals(item)) {
                    return Pair.of(
                            recipe.getResultItem(access).getItem(),
                            isCompressionRecipe(recipe, access)
                                    ? recipe.getIngredients().size()
                                    : recipe.getResultItem(access).getCount());
                }
            }
        }

        return null;
    }
}
