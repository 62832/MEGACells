package gripe._90.megacells.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import appeng.api.stacks.AEItemKey;

public class CompressionService {
    private static final Set<CompressionChain> compressionChains = new ObjectLinkedOpenHashSet<>();

    public static Optional<CompressionChain> getChain(AEItemKey item) {
        return compressionChains.stream()
                .filter(chain -> chain.containsVariant(item))
                .findFirst();
    }

    public static void loadRecipes(RecipeManager recipeManager, RegistryAccess access) {
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

            if (compressionChains.stream().noneMatch(chain -> chain.containsVariant(AEItemKey.of(baseVariant)))) {
                compressionChains.add(generateChain(baseVariant, compressed, decompressed, access));
            }
        }
    }

    private static CompressionChain generateChain(
            Item baseVariant,
            List<CraftingRecipe> compressed,
            List<CraftingRecipe> decompressed,
            RegistryAccess access) {
        var variants = new LinkedList<Item>();
        var multipliers = new LinkedList<Integer>();

        variants.addFirst(baseVariant);

        for (var lower = getNextVariant(baseVariant, decompressed, false, access); lower != null; ) {
            variants.addFirst(lower.item().getItem());
            multipliers.addFirst(lower.factor());
            lower = getNextVariant(lower.item().getItem(), decompressed, false, access);
        }

        multipliers.addFirst(1);
        var chain = new CompressionChain();

        for (var i = 0; i < variants.size(); i++) {
            chain.add(new CompressionVariant(variants.get(i), multipliers.get(i)));
        }

        for (var higher = getNextVariant(baseVariant, compressed, true, access); higher != null; ) {
            chain.add(higher.item(), higher.factor());
            higher = getNextVariant(higher.item().getItem(), compressed, true, access);
        }

        return chain;
    }

    private static CompressionVariant getNextVariant(
            Item item, List<CraftingRecipe> recipes, boolean compressed, RegistryAccess access) {
        for (var recipe : recipes) {
            for (var input : recipe.getIngredients().get(0).getItems()) {
                if (input.getItem().equals(item)) {
                    return new CompressionVariant(
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
}
