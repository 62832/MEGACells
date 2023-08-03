package gripe._90.megacells.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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

        // Retrieve all available "compression" and "decompression" recipes on the current server (if running)
        var allRecipes = recipeManager.getAllRecipesFor(RecipeType.CRAFTING);
        var candidates = Stream.concat(
                        allRecipes.stream().filter(recipe -> isCompressionRecipe(recipe, access)),
                        allRecipes.stream().filter(recipe -> isDecompressionRecipe(recipe, access)))
                .toList();

        // Filter gathered candidate recipes and retain only those that are reversible (i.e. those which can be carried
        // out back and forth to compress/decompress a resource without affecting the underlying quantity of it)
        var validRecipes = candidates.stream()
                .filter(recipe -> {
                    var compressible = false;
                    var decompressible = false;

                    var input = recipe.getIngredients().get(0);
                    var output = recipe.getResultItem(access);

                    var checkAgainst = candidates.stream()
                            .filter(r -> isCompressionRecipe(recipe, access)
                                    ? isDecompressionRecipe(r, access)
                                    : isCompressionRecipe(r, access))
                            .toList();

                    for (var candidate : checkAgainst) {
                        for (var item : candidate.getIngredients().get(0).getItems()) {
                            if (item.getItem().equals(output.getItem())) {
                                compressible = true;
                            }
                        }

                        for (var item : input.getItems()) {
                            if (item.getItem()
                                    .equals(candidate.getResultItem(access).getItem())) {
                                decompressible = true;
                            }
                        }

                        if (compressible && decompressible) {
                            break;
                        }
                    }

                    return compressible && decompressible;
                })
                .toList();

        var compressed = validRecipes.stream()
                .filter(recipe -> isCompressionRecipe(recipe, access))
                .toList();
        var decompressed = validRecipes.stream()
                .filter(recipe -> isDecompressionRecipe(recipe, access))
                .toList();

        // Pull all available compression chains from the recipe shortlist and add these to the handler cache
        for (var recipe : compressed) {
            var baseVariant = recipe.getResultItem(access).getItem();

            if (compressionChains.stream().noneMatch(chain -> chain.containsKey(AEItemKey.of(baseVariant)))) {
                var decompressionChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

                for (var lower = getSubsequentVariant(baseVariant, decompressed, access); lower != null; ) {
                    decompressionChain.put(AEItemKey.of(lower.first()), (int) lower.second());
                    lower = getSubsequentVariant(lower.first(), decompressed, access);
                }

                var compressionChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

                for (var higher = getSubsequentVariant(baseVariant, compressed, access); higher != null; ) {
                    compressionChain.put(AEItemKey.of(higher.first()), (int) higher.second());
                    higher = getSubsequentVariant(higher.first(), compressed, access);
                }

                if (compressionChain.isEmpty() && decompressionChain.isEmpty()) {
                    continue;
                }

                // Collate decompression and compression chains together with base variant
                var fullChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

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

                compressionChains.add(fullChain);
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

    private Pair<Item, Integer> getSubsequentVariant(Item item, List<CraftingRecipe> recipes, RegistryAccess access) {
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
