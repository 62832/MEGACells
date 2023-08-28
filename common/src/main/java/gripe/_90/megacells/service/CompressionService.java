package gripe._90.megacells.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
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
    private static final Set<Object2LongMap<AEItemKey>> compressionChains = new ObjectLinkedOpenHashSet<>();

    // It may be desirable for some items to be included as variants in a chain in spite of any recipes involving those
    // items not being reversible. Hence, we override any reversibility checks and generate a variant for such an item
    // based on its usually irreversible recipe.
    private static final Set<Override> overrides = new ObjectLinkedOpenHashSet<>();

    public static Optional<Object2LongMap<AEItemKey>> getChain(AEItemKey key) {
        return compressionChains.stream()
                .filter(chain -> chain.containsKey(key))
                .findFirst();
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

    private static Object2LongMap<AEItemKey> generateChain(
            Item baseVariant,
            List<CraftingRecipe> compressed,
            List<CraftingRecipe> decompressed,
            RegistryAccess access) {
        var chain = new Object2LongLinkedOpenHashMap<AEItemKey>();
        var compressionFactor = 1L;

        for (var lower = getNextVariant(baseVariant, decompressed, false, access); lower != null; ) {
            baseVariant = lower.first();
            lower = getNextVariant(baseVariant, decompressed, false, access);
        }

        chain.put(AEItemKey.of(baseVariant), compressionFactor);

        for (var higher = getNextVariant(baseVariant, compressed, true, access); higher != null; ) {
            compressionFactor *= higher.second();
            chain.put(AEItemKey.of(higher.first()), compressionFactor);
            higher = getNextVariant(higher.first(), compressed, true, access);
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
