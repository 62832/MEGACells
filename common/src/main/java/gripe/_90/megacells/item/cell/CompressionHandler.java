package gripe._90.megacells.item.cell;

import java.util.List;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.core.AppEng;

import gripe._90.megacells.util.Utils;

public class CompressionHandler {
    public static final CompressionHandler INSTANCE = new CompressionHandler();

    private final List<CraftingRecipe> compressionRecipes = new ObjectArrayList<>();
    private final List<CraftingRecipe> decompressionRecipes = new ObjectArrayList<>();

    private CompressionHandler() {
    }

    public void load() {
        // Clear old recipe cache in case of the server restarting or recipes being reloaded
        compressionRecipes.clear();
        decompressionRecipes.clear();

        // Retrieve all available "compression" and "decompression" recipes on the current server (if running)
        var server = AppEng.instance().getCurrentServer();
        var allRecipes = server != null
                ? server.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)
                : new ObjectArrayList<CraftingRecipe>();

        var candidates = Stream.concat(
                allRecipes.stream().filter(this::isCompressionRecipe),
                allRecipes.stream().filter(this::isDecompressionRecipe)).toList();

        // Filter gathered candidate recipes and retain only those that are reversible (i.e. those which can be carried
        // out back and forth to compress/decompress a resource without affecting the underlying quantity of it)
        var validRecipes = candidates.stream().filter(recipe -> {
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
            }

            return compressible && decompressible;
        }).toList();

        // Add respective recipes to handler cache
        validRecipes.forEach(recipe -> {
            if (isCompressionRecipe(recipe)) {
                compressionRecipes.add(recipe);
            }

            if (isDecompressionRecipe(recipe)) {
                decompressionRecipes.add(recipe);
            }
        });

        Utils.LOGGER.info("Loaded bulk cell compression recipes.");
    }

    private boolean isCompressionRecipe(CraftingRecipe recipe) {
        return (recipe.getIngredients().size() == 4 || recipe.getIngredients().size() == 9)
                && recipe.getIngredients().stream().distinct().limit(2).count() == 1
                && recipe.getResultItem().getCount() == 1;
    }

    private boolean isDecompressionRecipe(CraftingRecipe recipe) {
        return (recipe.getResultItem().getCount() == 4 || recipe.getResultItem().getCount() == 9)
                && recipe.getIngredients().size() == 1;
    }

    public Object2IntMap<AEItemKey> getCompressedVariants(AEKey key) {
        return getVariants(key, compressionRecipes);
    }

    public Object2IntMap<AEItemKey> getDecompressedVariants(AEKey key) {
        return getVariants(key, decompressionRecipes);
    }

    private Object2IntMap<AEItemKey> getVariants(AEKey key, List<CraftingRecipe> recipes) {
        var variants = new Object2IntLinkedOpenHashMap<AEItemKey>();

        if (!(key instanceof AEItemKey item)) {
            return variants;
        }

        for (var newVariant = getSubsequentVariant(item.getItem(), recipes); newVariant != null;) {
            variants.put(AEItemKey.of(newVariant.first()), (int) newVariant.second());
            newVariant = getSubsequentVariant(newVariant.first(), recipes);
        }

        return variants;
    }

    private Pair<Item, Integer> getSubsequentVariant(Item item, List<CraftingRecipe> recipes) {
        for (var recipe : recipes) {
            for (var input : recipe.getIngredients().get(0).getItems()) {
                if (input.getItem().equals(item)) {
                    var multiplier = 1;

                    if (isCompressionRecipe(recipe)) {
                        multiplier = recipe.getIngredients().size();
                    } else if (isDecompressionRecipe(recipe)) {
                        multiplier = recipe.getResultItem().getCount();
                    }

                    return Pair.of(recipe.getResultItem().getItem(), multiplier);
                }
            }
        }

        return null;
    }
}
