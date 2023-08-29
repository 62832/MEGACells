package gripe._90.megacells.service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import appeng.api.stacks.AEItemKey;

import gripe._90.megacells.definition.MEGATags;

public class CompressionService {
    // Each chain is an ordered map with the items themselves as the keys and the values being how much of the smallest
    // "unit" item in the chain makes up each subsequent variant item.
    // e.g. 1 nugget -> 9 nuggets per ingot -> 81 nuggets per block -> etc.
    private static final Set<Object2IntMap<AEItemKey>> compressionChains = new ObjectLinkedOpenHashSet<>();

    // It may be desirable for some items to be included as variants in a chain in spite of any recipes involving those
    // items not being reversible. Hence, we override any reversibility checks and generate a variant for such an item
    // based on its usually irreversible recipe.
    private static final Set<Override> overrides = new ObjectLinkedOpenHashSet<>();

    public static Optional<Object2IntMap<AEItemKey>> getChain(AEItemKey key) {
        return compressionChains.stream()
                .filter(chain -> chain.containsKey(key))
                .findFirst();
    }

    public static Object2IntMap<AEItemKey> getVariants(AEItemKey key, boolean decompress) {
        return getChain(key)
                .map(chain -> {
                    var keys = new ObjectArrayList<>(chain.keySet());

                    // Reverse ordering when going from provided storage/filter variant to least-compressed "base unit"
                    if (decompress) {
                        Collections.reverse(keys);
                    }

                    // Split variant chain into separate compressed/decompressed chains, omitting the initial variant
                    // provided but retaining the appropriate multipliers in the case of decompression
                    var variants = new Object2IntLinkedOpenHashMap<AEItemKey>();

                    for (var i = keys.indexOf(key) + 1; i < keys.size(); i++) {
                        var multiplierIndex = i - (decompress ? 1 : 0);
                        variants.put(keys.get(i), chain.getInt(keys.get(multiplierIndex)));
                    }

                    return variants;
                })
                .orElseGet(Object2IntLinkedOpenHashMap::new);
    }

    public static void loadRecipes(RecipeManager recipeManager, RegistryAccess access) {
        // Clear old variant cache in case of the server restarting or recipes being reloaded
        compressionChains.clear();
        overrides.clear();

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
        for (var recipe :
                Stream.concat(compressed.stream(), decompressed.stream()).toList()) {
            var baseVariant = recipe.getResultItem(access).getItem();

            if (compressionChains.stream().noneMatch(chain -> chain.containsKey(AEItemKey.of(baseVariant)))) {
                compressionChains.add(generateChain(baseVariant, compressed, decompressed, access));
            }
        }
    }

    private static Object2IntMap<AEItemKey> generateChain(
            Item baseVariant,
            List<CraftingRecipe> compressed,
            List<CraftingRecipe> decompressed,
            RegistryAccess access) {
        var variants = new LinkedList<AEItemKey>();
        var multipliers = new LinkedList<Integer>();

        variants.addFirst(AEItemKey.of(baseVariant));

        for (var lower = getNextVariant(baseVariant, decompressed, false, access); lower != null; ) {
            variants.addFirst(AEItemKey.of(lower.key()));
            multipliers.addFirst(lower.value());
            lower = getNextVariant(lower.key(), decompressed, false, access);
        }

        multipliers.addFirst(1);

        var chain = IntStream.range(0, variants.size())
                .boxed()
                .collect(Collectors.toMap(
                        variants::get, multipliers::get, (i, j) -> i, Object2IntLinkedOpenHashMap<AEItemKey>::new));

        for (var higher = getNextVariant(baseVariant, compressed, true, access); higher != null; ) {
            chain.put(AEItemKey.of(higher.key()), (int) higher.value());
            higher = getNextVariant(higher.key(), compressed, true, access);
        }

        return chain;
    }

    private static Pair<Item, Integer> getNextVariant(
            Item item, List<CraftingRecipe> recipes, boolean compressed, RegistryAccess access) {
        for (var override : overrides) {
            if (override.smaller.equals(item) && compressed) {
                return Pair.of(override.larger, override.factor);
            }

            if (override.larger.equals(item) && !compressed) {
                return Pair.of(override.smaller, override.factor);
            }
        }

        for (var recipe : recipes) {
            for (var input : recipe.getIngredients().get(0).getItems()) {
                if (input.getItem().equals(item)) {
                    return Pair.of(
                            recipe.getResultItem(access).getItem(),
                            compressed
                                    ? recipe.getIngredients().size()
                                    : recipe.getResultItem(access).getCount());
                }
            }
        }

        return null;
    }

    private static boolean isDecompressionRecipe(CraftingRecipe recipe, RegistryAccess access) {
        return recipe.getIngredients().size() == 1
                && Set.of(4, 9).contains(recipe.getResultItem(access).getCount());
    }

    private static boolean isCompressionRecipe(CraftingRecipe recipe, RegistryAccess access) {
        return sameIngredient(recipe)
                && recipe.getResultItem(access).getCount() == 1
                && Set.of(4, 9).contains(recipe.getIngredients().size());
    }

    // All this for some fucking melons.
    private static boolean sameIngredient(CraftingRecipe recipe) {
        var ingredients = new ObjectArrayList<>(recipe.getIngredients());

        if (ingredients.isEmpty()) {
            return false;
        }

        var first = ingredients.remove(0).getItems();
        if (first.length == 0) return false;

        for (var ingredient : ingredients) {
            var stacks = ingredient.getItems();

            if (stacks.length != first.length) {
                return false;
            }

            for (var i = 0; i < stacks.length; i++) {
                if (!ItemStack.isSameItemSameTags(stacks[i], first[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isReversibleRecipe(
            CraftingRecipe recipe, List<CraftingRecipe> candidates, RegistryAccess access) {
        if (overrideRecipe(recipe, access)) {
            return true;
        }

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

    private static boolean overrideRecipe(CraftingRecipe recipe, RegistryAccess access) {
        for (var item : recipe.getIngredients().get(0).getItems()) {
            if (item.is(MEGATags.COMPRESSION_OVERRIDES)) {
                var variant = recipe.getResultItem(access);
                var compressed = isCompressionRecipe(recipe, access);
                var factor = compressed ? recipe.getIngredients().size() : variant.getCount();

                overrides.add(new Override(item.getItem(), variant.getItem(), compressed, factor));
                return true;
            }
        }

        return false;
    }

    private record Override(Item smaller, Item larger, boolean compressed, int factor) {}
}
