package gripe._90.megacells.item.cell;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import appeng.api.stacks.AEItemKey;
import appeng.core.AppEng;

import gripe._90.megacells.util.Utils;

public class CompressionHandler {
    public static final CompressionHandler INSTANCE = new CompressionHandler();

    private final Set<Object2IntMap<AEItemKey>> compressionChains = new ObjectLinkedOpenHashSet<>();

    private CompressionHandler() {
    }

    public Object2IntMap<AEItemKey> getVariants(AEItemKey key, boolean decompress) {
        var variantChain = compressionChains.stream().filter(chain -> chain.containsKey(key)).findFirst();
        return variantChain.map(chain -> {
            var keys = new ObjectArrayList<>(chain.keySet());

            if (decompress) {
                Collections.reverse(keys);
            }

            var decompressed = new Object2IntLinkedOpenHashMap<AEItemKey>();
            keys.subList(keys.indexOf(key) + 1, keys.size()).forEach(k -> decompressed.put(k, chain.getInt(k)));
            return decompressed;
        }).orElseGet(Object2IntLinkedOpenHashMap::new);
    }

    public void load() {
        // Clear old variant cache in case of the server restarting or recipes being reloaded
        compressionChains.clear();

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
            var constantAmount = false;

            var input = recipe.getIngredients().get(0);
            var output = recipe.getResultItem();

            for (var candidate : candidates) {
                var compressionMultiplier = 0;
                var decompressionMultiplier = 0;

                for (var item : candidate.getIngredients().get(0).getItems()) {
                    if (item.getItem().equals(output.getItem())) {
                        compressible = true;
                        compressionMultiplier = candidate.getIngredients().size();
                    }
                }

                for (var item : input.getItems()) {
                    if (item.getItem().equals(candidate.getResultItem().getItem())) {
                        decompressible = true;
                        decompressionMultiplier = candidate.getResultItem().getCount();
                    }
                }

                constantAmount = compressionMultiplier == decompressionMultiplier;
            }

            return compressible && decompressible && constantAmount;
        }).toList();

        // Add final available variant chains to handler cache
        var compressed = validRecipes.stream().filter(this::isCompressionRecipe).toList();
        var decompressed = validRecipes.stream().filter(this::isDecompressionRecipe).toList();

        compressed.forEach(recipe -> {
            var recipeOutput = recipe.getResultItem().getItem();

            if (compressionChains.stream().noneMatch(chain -> chain.containsKey(AEItemKey.of(recipeOutput)))) {
                var decompressionChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

                for (var lowerVariant = getSubsequentVariant(recipeOutput, decompressed); lowerVariant != null;) {
                    decompressionChain.put(AEItemKey.of(lowerVariant.first()), (int) lowerVariant.second());
                    lowerVariant = getSubsequentVariant(lowerVariant.first(), decompressed);
                }

                var compressionChain = new Object2IntLinkedOpenHashMap<AEItemKey>();
                var decompressionKeys = new ObjectArrayList<>(decompressionChain.keySet());

                Collections.reverse(decompressionKeys);
                decompressionKeys.forEach(k -> compressionChain.put(k, decompressionChain.getInt(k)));
                compressionChain.put(AEItemKey.of(recipeOutput), compressionChain.getInt(compressionChain.lastKey()));

                for (var higherVariant = getSubsequentVariant(recipeOutput, compressed); higherVariant != null; ) {
                    compressionChain.put(AEItemKey.of(higherVariant.first()), (int) higherVariant.second());
                    higherVariant = getSubsequentVariant(higherVariant.first(), compressed);
                }

                compressionChains.add(compressionChain);
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
