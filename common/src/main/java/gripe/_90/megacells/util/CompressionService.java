package gripe._90.megacells.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import appeng.api.stacks.AEItemKey;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGATags;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

public class CompressionService {
    public static final CompressionService INSTANCE = new CompressionService();

    // Each chain is a list of "variants", where each variant consists of the item itself along with an associated value
    // dictating how much of the previous variant's item is needed to compress into that variant.
    // This value is typically either 4 or 9 for any given item, or 1 for the smallest base variant.
    private final Set<CompressionChain> compressionChains = new ObjectLinkedOpenHashSet<>();

    // It may be desirable for some items to be included as variants in a chain in spite of any recipes involving those
    // items not being reversible. Hence, we override any reversibility checks and generate a variant for such an item
    // based on its usually irreversible recipe.
    private final Set<Override> overrides = new ObjectLinkedOpenHashSet<>();

    private CompressionService() {}

    public Optional<CompressionChain> getChain(AEItemKey item) {
        return compressionChains.stream()
                .filter(chain -> chain.containsVariant(item))
                .findFirst();
    }

    public void loadRecipes(RecipeManager recipeManager) {
        // Clear old chain cache in case of the server restarting or recipes being reloaded
        compressionChains.clear();
        overrides.clear();

        // Retrieve all available "compression" and "decompression" recipes from the current server's recipe manager
        var allRecipes = recipeManager.getAllRecipesFor(RecipeType.CRAFTING);
        var compressedCandidates =
                allRecipes.stream().filter(this::isCompressionRecipe).toList();
        var decompressedCandidates =
                allRecipes.stream().filter(this::isDecompressionRecipe).toList();

        // Filter gathered candidate recipes and retain only those that are reversible (i.e. those which can be carried
        // out back and forth to compress/decompress a resource without affecting the underlying quantity of it)
        var compressed = compressedCandidates.stream()
                .filter(recipe -> isReversibleRecipe(recipe, decompressedCandidates))
                .toList();
        var decompressed = decompressedCandidates.stream()
                .filter(recipe -> isReversibleRecipe(recipe, compressedCandidates))
                .toList();

        // Pull all available compression chains from the recipe shortlist and add these to the cache
        Stream.concat(compressed.stream(), decompressed.stream()).forEach(recipe -> {
            var baseVariant = recipe.getResultItem().getItem();

            if (compressionChains.stream().noneMatch(chain -> chain.containsVariant(AEItemKey.of(baseVariant)))) {
                compressionChains.add(generateChain(baseVariant, compressed, decompressed));
            }
        });

        if (!compressionChains.isEmpty()) {
            MEGACells.LOGGER.info("(Re-)initialised bulk cell compression.");
        }
    }

    private CompressionChain generateChain(
            Item baseVariant, List<CraftingRecipe> compressed, List<CraftingRecipe> decompressed) {
        var variants = new LinkedList<Item>();
        var multipliers = new LinkedList<Integer>();

        variants.addFirst(baseVariant);

        for (var lower = getNextVariant(baseVariant, decompressed, false); lower != null; ) {
            variants.addFirst(lower.item().getItem());
            multipliers.addFirst(lower.factor());
            lower = getNextVariant(lower.item().getItem(), decompressed, false);
        }

        multipliers.addFirst(1);
        var chain = new CompressionChain();

        for (var i = 0; i < variants.size(); i++) {
            chain.add(AEItemKey.of(variants.get(i)), multipliers.get(i));
        }

        for (var higher = getNextVariant(baseVariant, compressed, true); higher != null; ) {
            chain.add(higher);
            higher = getNextVariant(higher.item().getItem(), compressed, true);
        }

        return chain;
    }

    private CompressionVariant getNextVariant(Item item, List<CraftingRecipe> recipes, boolean compressed) {
        for (var override : overrides) {
            if (compressed && override.smaller.equals(item)) {
                return new CompressionVariant(override.larger, override.factor);
            }

            if (!compressed && override.larger.equals(item)) {
                return new CompressionVariant(override.smaller, override.factor);
            }
        }

        for (var recipe : recipes) {
            for (var input : recipe.getIngredients().get(0).getItems()) {
                if (input.getItem().equals(item)) {
                    return new CompressionVariant(
                            recipe.getResultItem().getItem(),
                            compressed
                                    ? recipe.getIngredients().size()
                                    : recipe.getResultItem().getCount());
                }
            }
        }

        return null;
    }

    private boolean isDecompressionRecipe(CraftingRecipe recipe) {
        return recipe.getIngredients().stream().filter(i -> !i.isEmpty()).count() == 1
                && Set.of(4, 9).contains(recipe.getResultItem().getCount());
    }

    private boolean isCompressionRecipe(CraftingRecipe recipe) {
        var ingredients = recipe.getIngredients();
        return recipe.getResultItem().getCount() == 1
                && ingredients.stream().noneMatch(Ingredient::isEmpty)
                && Set.of(4, 9).contains(ingredients.size())
                && sameIngredient(ingredients);
    }

    private boolean sameIngredient(List<Ingredient> ingredients) {
        if (ingredients.stream().distinct().count() <= 1) {
            return true;
        }

        // Check further for any odd cases (e.g. melon blocks having a shapeless recipe instead of a shaped one)
        var first = ingredients.get(0).getItems();

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

    private boolean isReversibleRecipe(CraftingRecipe recipe, List<CraftingRecipe> candidates) {
        if (overrideRecipe(recipe)) {
            return true;
        }

        var compressible = false;
        var decompressible = false;

        var input = recipe.getIngredients().get(0);
        var output = recipe.getResultItem();

        for (var candidate : candidates) {
            for (var item : candidate.getIngredients().get(0).getItems()) {
                if (item.getItem().equals(output.getItem())) {
                    compressible = true;
                }
            }

            for (var item : input.getItems()) {
                if (item.getItem().equals(candidate.getResultItem().getItem())) {
                    decompressible = true;
                }
            }

            if (compressible && decompressible) return true;
        }

        return false;
    }

    private boolean overrideRecipe(CraftingRecipe recipe) {
        for (var input : recipe.getIngredients().get(0).getItems()) {
            if (input.is(MEGATags.COMPRESSION_OVERRIDES)) {
                // Less expensive to check for decompression rather than compression, and since this method is only
                // being used on recipes that are candidates for either compression or decompression, this is fine.
                var compressed = !isDecompressionRecipe(recipe);
                var output = recipe.getResultItem();

                var smaller = compressed ? input.getItem() : output.getItem();
                var larger = compressed ? output.getItem() : input.getItem();
                var factor = compressed ? recipe.getIngredients().size() : output.getCount();

                overrides.add(new Override(smaller, larger, compressed, factor));
                return true;
            }
        }

        return false;
    }

    private record Override(Item smaller, Item larger, boolean compressed, int factor) {}
}
