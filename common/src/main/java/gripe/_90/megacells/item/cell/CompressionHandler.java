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

    private final List<CraftingRecipe> validRecipes = new ObjectArrayList<>();

    private CompressionHandler() {
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

    private List<CraftingRecipe> getCompressionRecipes(List<CraftingRecipe> recipes) {
        var compressionRecipes = new ObjectArrayList<CraftingRecipe>();

        for (var recipe : recipes) {
            if (isCompressionRecipe(recipe)) {
                compressionRecipes.add(recipe);
            }
        }

        return compressionRecipes;
    }

    private List<CraftingRecipe> getDecompressionRecipes(List<CraftingRecipe> recipes) {
        var decompressionRecipes = new ObjectArrayList<CraftingRecipe>();

        for (var recipe : recipes) {
            if (isDecompressionRecipe(recipe)) {
                decompressionRecipes.add(recipe);
            }
        }

        return decompressionRecipes;
    }

    private List<CraftingRecipe> getCandidateRecipes() {
        var currentServer = AppEng.instance().getCurrentServer();
        var allRecipes = currentServer != null
                ? currentServer.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)
                : new ObjectArrayList<CraftingRecipe>();

        return Stream.concat(
                allRecipes.stream().filter(this::isCompressionRecipe),
                allRecipes.stream().filter(this::isDecompressionRecipe)).toList();
    }

    private boolean isReversibleRecipe(CraftingRecipe recipe, List<CraftingRecipe> candidates) {
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
    }

    public void load() {
        var candidates = getCandidateRecipes();
        this.validRecipes.clear();
        this.validRecipes.addAll(candidates.stream().filter(r -> isReversibleRecipe(r, candidates)).toList());
        Utils.LOGGER.info("Loaded bulk cell compression recipes.");
    }

    private Pair<Item, Integer> getSubsequentVariant(Item item, List<CraftingRecipe> recipes) {
        for (var recipe : recipes) {
            for (var input : recipe.getIngredients().get(0).getItems()) {
                if (input.getItem().equals(item)) {
                    return Pair.of(recipe.getResultItem().getItem(), getMultiplier(recipe));
                }
            }
        }

        return null;
    }

    private int getMultiplier(CraftingRecipe recipe) {
        if (isCompressionRecipe(recipe)) {
            return recipe.getIngredients().size();
        }

        if (isDecompressionRecipe(recipe)) {
            return recipe.getResultItem().getCount();
        }

        return 1;
    }

    public Object2IntMap<AEItemKey> getCompressedVariants(AEKey key) {
        return getVariants(key, getCompressionRecipes(this.validRecipes));
    }

    public Object2IntMap<AEItemKey> getDecompressedVariants(AEKey key) {
        return getVariants(key, getDecompressionRecipes(this.validRecipes));
    }

    public Object2IntMap<AEItemKey> getVariants(AEKey key, List<CraftingRecipe> recipes) {
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
}
