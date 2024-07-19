package gripe._90.megacells.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

import appeng.api.stacks.AEItemKey;

import gripe._90.megacells.definition.MEGATags;

public class CompressionService {
    public static final CompressionService INSTANCE = new CompressionService();

    // Each chain is a list of "variants", where each variant consists of the item itself along with an associated value
    // dictating how much of the previous variant's item is needed to compress into that variant.
    // This value is typically either 4 or 9 for any given item, or 1 for the smallest base variant.
    private final Set<CompressionChain> compressionChains = new HashSet<>();

    // It may be desirable for some items to be included as variants in a chain in spite of any recipes involving those
    // items not being reversible. Hence, we override any reversibility checks and generate a variant for such an item
    // based on its usually irreversible recipe.
    private final Set<Override> overrides = new HashSet<>();

    private CompressionService() {}

    public Optional<CompressionChain> getChain(AEItemKey item) {
        return compressionChains.stream()
                .filter(chain -> chain.containsVariant(item))
                .findFirst();
    }

    public void loadRecipes(RecipeManager recipeManager, RegistryAccess access) {
        // Clear old chain cache in case of the server restarting or recipes being reloaded
        compressionChains.clear();
        overrides.clear();

        // Retrieve all available "compression" and "decompression" recipes from the current server's recipe manager
        var compressed = new ArrayList<CraftingRecipe>();
        var decompressed = new ArrayList<CraftingRecipe>();

        for (var recipe : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
            if (isCompressionRecipe(recipe.value(), access)) {
                compressed.add(recipe.value());
            } else if (isDecompressionRecipe(recipe.value(), access)) {
                decompressed.add(recipe.value());
            }
        }

        // Filter gathered candidate recipes and retain only those that are reversible (i.e. those which can be carried
        // out back and forth to compress/decompress a resource without affecting the underlying quantity of it)
        compressed.removeIf(recipe -> isIrreversible(recipe, decompressed, access));
        decompressed.removeIf(recipe -> isIrreversible(recipe, compressed, access));

        var ingredientSize = Comparator.<CraftingRecipe>comparingInt(
                r -> r.getIngredients().getFirst().getItems().length);
        compressed.sort(ingredientSize);
        decompressed.sort(ingredientSize);

        // Pull all available compression chains from the recipe shortlist and add these to the cache
        Stream.concat(compressed.stream(), decompressed.stream()).forEach(recipe -> {
            var baseVariant = recipe.getResultItem(access).getItem();

            if (getChain(AEItemKey.of(baseVariant)).isEmpty()) {
                compressionChains.add(generateChain(baseVariant, compressed, decompressed, access));
            }
        });
    }

    private CompressionChain generateChain(
            Item baseVariant,
            List<CraftingRecipe> compressed,
            List<CraftingRecipe> decompressed,
            RegistryAccess access) {
        var variants = new LinkedList<Item>();
        var multipliers = new LinkedList<Byte>();

        variants.addFirst(baseVariant);

        for (var lower = getNextVariant(baseVariant, decompressed, false, access); lower != null; ) {
            variants.addFirst(lower.item().getItem());
            multipliers.addFirst(lower.factor());
            lower = getNextVariant(lower.item().getItem(), decompressed, false, access);
        }

        multipliers.addFirst((byte) 1);
        var chain = new CompressionChain();

        for (var i = 0; i < variants.size(); i++) {
            chain.add(AEItemKey.of(variants.get(i)), multipliers.get(i));
        }

        for (var higher = getNextVariant(baseVariant, compressed, true, access); higher != null; ) {
            chain.add(higher);
            higher = getNextVariant(higher.item().getItem(), compressed, true, access);
        }

        return chain;
    }

    private Variant getNextVariant(Item item, List<CraftingRecipe> recipes, boolean compressed, RegistryAccess access) {
        for (var override : overrides) {
            if (compressed && override.smaller.equals(item)) {
                return new Variant(override.larger, override.factor);
            }

            if (!compressed && override.larger.equals(item)) {
                return new Variant(override.smaller, override.factor);
            }
        }

        for (var recipe : recipes) {
            for (var input : recipe.getIngredients().getFirst().getItems()) {
                if (input.getItem().equals(item)) {
                    return new Variant(recipe.getResultItem(access).getItem(), (byte)
                            (compressed
                                    ? recipe.getIngredients().size()
                                    : recipe.getResultItem(access).getCount()));
                }
            }
        }

        return null;
    }

    private boolean isDecompressionRecipe(CraftingRecipe recipe, RegistryAccess access) {
        return recipe.getIngredients().stream().filter(i -> !i.isEmpty()).count() == 1
                && Set.of(4, 9).contains(recipe.getResultItem(access).getCount());
    }

    private boolean isCompressionRecipe(CraftingRecipe recipe, RegistryAccess access) {
        var ingredients = recipe.getIngredients();
        return recipe.getResultItem(access).getCount() == 1
                && ingredients.stream().noneMatch(Ingredient::isEmpty)
                && Set.of(4, 9).contains(ingredients.size())
                && sameIngredient(recipe);
    }

    private boolean sameIngredient(CraftingRecipe recipe) {
        var ingredients = recipe.getIngredients();

        if (recipe instanceof ShapedRecipe) {
            return ingredients.stream().distinct().count() <= 1;
        }

        // Check further for any odd cases (e.g. melon blocks having a shapeless recipe instead of a shaped one)
        var first = ingredients.getFirst().getItems();

        for (var ingredient : ingredients) {
            var stacks = ingredient.getItems();

            if (stacks.length != first.length) {
                return false;
            }

            for (var i = 0; i < stacks.length; i++) {
                if (!ItemStack.isSameItemSameComponents(stacks[i], first[i])) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isIrreversible(CraftingRecipe recipe, List<CraftingRecipe> candidates, RegistryAccess access) {
        if (overrideRecipe(recipe, access)) {
            return false;
        }

        var testInput = recipe.getIngredients().getFirst().getItems();
        var testOutput = recipe.getResultItem(access).getItem();

        for (var candidate : candidates) {
            var input = candidate.getIngredients().getFirst().getItems();
            var output = candidate.getResultItem(access).getItem();

            var compressible = Arrays.stream(input).anyMatch(i -> i.is(testOutput));
            var decompressible = Arrays.stream(testInput).anyMatch(i -> i.is(output));

            if (compressible && decompressible) {
                return false;
            }
        }

        return true;
    }

    private boolean overrideRecipe(CraftingRecipe recipe, RegistryAccess access) {
        for (var input : recipe.getIngredients().getFirst().getItems()) {
            if (input.is(MEGATags.COMPRESSION_OVERRIDES)) {
                // Less expensive to check for decompression rather than compression, and since this method is only
                // being used on recipes that are candidates for either compression or decompression, this is fine.
                var compressed = !isDecompressionRecipe(recipe, access);
                var output = recipe.getResultItem(access);

                var smaller = (compressed ? input : output).getItem();
                var larger = (compressed ? output : input).getItem();
                var factor = (byte) (compressed ? recipe.getIngredients().size() : output.getCount());

                overrides.add(new Override(smaller, larger, factor));
                return true;
            }
        }

        return false;
    }

    public record Variant(AEItemKey item, byte factor) {
        private Variant(Item item, byte factor) {
            this(AEItemKey.of(item), factor);
        }
    }

    private record Override(Item smaller, Item larger, byte factor) {}
}
