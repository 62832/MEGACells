package gripe._90.megacells.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import appeng.api.stacks.AEItemKey;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGATags;

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

    public void loadRecipes(RecipeManager recipeManager, RegistryAccess access) {
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
        Stream.concat(compressed.stream(), decompressed.stream()).forEach(recipe -> {
            var baseVariant = recipe.getResultItem(access).getItem();

            if (compressionChains.stream().noneMatch(chain -> chain.containsVariant(AEItemKey.of(baseVariant)))) {
                compressionChains.add(generateChain(baseVariant, compressed, decompressed, access));
            }
        });

        if (!compressionChains.isEmpty()) {
            MEGACells.LOGGER.info("Initialised bulk cell compression.");
        }
    }

    private CompressionChain generateChain(
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
            chain.add(AEItemKey.of(variants.get(i)), multipliers.get(i));
        }

        for (var higher = getNextVariant(baseVariant, compressed, true, access); higher != null; ) {
            chain.add(higher);
            higher = getNextVariant(higher.item().getItem(), compressed, true, access);
        }

        return chain;
    }

    private CompressionVariant getNextVariant(
            Item item, List<CraftingRecipe> recipes, boolean compressed, RegistryAccess access) {
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
                            recipe.getResultItem(access).getItem(),
                            compressed
                                    ? recipe.getIngredients().size()
                                    : recipe.getResultItem(access).getCount());
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
        var ingredients =
                recipe.getIngredients().stream().filter(i -> !i.isEmpty()).toList();
        return recipe.getResultItem(access).getCount() == 1
                && Set.of(4, 9).contains(ingredients.size())
                && sameIngredient(ingredients);
    }

    // All this for some fucking melons.
    private boolean sameIngredient(List<Ingredient> ingredients) {
        if (ingredients.stream().distinct().count() <= 1) {
            return true;
        }

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

    private boolean isReversibleRecipe(CraftingRecipe recipe, List<CraftingRecipe> candidates, RegistryAccess access) {
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

    private boolean overrideRecipe(CraftingRecipe recipe, RegistryAccess access) {
        var compressed = isCompressionRecipe(recipe, access);
        var output = recipe.getResultItem(access);

        for (var input : recipe.getIngredients().get(0).getItems()) {
            if (input.is(MEGATags.COMPRESSION_OVERRIDES)) {
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
