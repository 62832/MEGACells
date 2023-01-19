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

public class CompressionService {
    public static final CompressionService INSTANCE = new CompressionService();

    private final Set<Object2IntMap<AEItemKey>> compressionChains = new ObjectLinkedOpenHashSet<>();

    private CompressionService() {
    }

    public Object2IntMap<AEItemKey> getVariants(AEItemKey key, boolean decompress) {
        // Retrieve the (optional) variant chain containing the given item key
        var variantChain = compressionChains.stream().filter(chain -> chain.containsKey(key)).findFirst();

        return variantChain.map(chain -> {
            var keys = new ObjectArrayList<>(chain.keySet());

            // Reverse ordering when going from provided storage/filter variant to least-compressed "base unit"
            if (decompress) {
                Collections.reverse(keys);
            }

            // Split variant chain into separate compressed/decompressed chains, omitting the initial variant provided
            var variants = new Object2IntLinkedOpenHashMap<AEItemKey>();
            keys.subList(keys.indexOf(key) + 1, keys.size()).forEach(k -> variants.put(k, chain.getInt(k)));
            return variants;
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

            var input = recipe.getIngredients().get(0);
            var output = recipe.getResultItem();

            var checkAgainst = candidates.stream().filter(isCompressionRecipe(recipe)
                    ? this::isDecompressionRecipe
                    : this::isCompressionRecipe).toList();

            for (var candidate : checkAgainst) {
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

                if (compressible && decompressible) {
                    break;
                }
            }

            return compressible && decompressible;
        }).toList();

        var compressed = validRecipes.stream().filter(this::isCompressionRecipe).toList();
        var decompressed = validRecipes.stream().filter(this::isDecompressionRecipe).toList();

        // Pull all available compression chains from the recipe shortlist and add these to the handler cache
        compressed.forEach(recipe -> {
            var baseVariant = recipe.getResultItem().getItem();

            if (compressionChains.stream().noneMatch(chain -> chain.containsKey(AEItemKey.of(baseVariant)))) {
                var decompressionChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

                for (var lowerVariant = getSubsequentVariant(baseVariant, decompressed); lowerVariant != null;) {
                    decompressionChain.put(AEItemKey.of(lowerVariant.first()), (int) lowerVariant.second());
                    lowerVariant = getSubsequentVariant(lowerVariant.first(), decompressed);
                }

                var compressionChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

                for (var higherVariant = getSubsequentVariant(baseVariant, compressed); higherVariant != null;) {
                    compressionChain.put(AEItemKey.of(higherVariant.first()), (int) higherVariant.second());
                    higherVariant = getSubsequentVariant(higherVariant.first(), compressed);
                }

                // Collate decompression and compression chains together with base variant
                var fullChain = new Object2IntLinkedOpenHashMap<AEItemKey>();

                var decompressionKeys = new ObjectArrayList<>(decompressionChain.keySet());
                Collections.reverse(decompressionKeys);
                decompressionKeys.forEach(k -> fullChain.put(k, decompressionChain.getInt(k)));

                // Retrieve appropriate multiplier for base variant for completion's sake
                fullChain.put(AEItemKey.of(baseVariant), fullChain.isEmpty()
                        ? compressionChain.getInt(compressionChain.firstKey())
                        : fullChain.getInt(fullChain.lastKey()));
                fullChain.putAll(compressionChain);

                compressionChains.add(fullChain);
            }
        });
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
                    return Pair.of(recipe.getResultItem().getItem(), isCompressionRecipe(recipe)
                            ? recipe.getIngredients().size()
                            : recipe.getResultItem().getCount());
                }
            }
        }

        return null;
    }
}
