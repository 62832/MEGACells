package gripe._90.megacells.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import appeng.api.networking.GridServices;
import appeng.api.stacks.AEItemKey;

import gripe._90.megacells.definition.MEGADataMaps;

public class CompressionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompressionService.class);
    private static final CompressionChain EMPTY = new CompressionChain(List.of());

    /**
     * Each chain is a list of "variants", where each variant consists of the item itself along with an associated value
     * dictating how much of the previous variant's item is needed to compress into that variant. This value will be
     * between 1 and 9 (inclusive) for any given item, or just 1 for the smallest base variant.
     */
    private static final List<CompressionChain> chains = new ArrayList<>();

    /**
     * So as not to keep reiterating a potentially large list of chains to search for the chain containing a given item,
     * this map is used to keep track of which items have already been used to request a chain and cache the index
     * within the {@code chains} field at which that chain has been found. If a chain is present, this index is then
     * tracked for every variant item within it, otherwise an index of {@code -1} is used to denote that a chain does
     * not exist for the item.
     */
    private static final Map<AEItemKey, Integer> chainIndexes = new HashMap<>();

    public static void init() {
        GridServices.register(DecompressionService.class, DecompressionService.class);

        NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, event -> {
            var server = event.getServer();
            loadRecipes(server.getRecipeManager(), server.registryAccess());
        });

        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, event -> {
            // Only rebuild cache in the event of a data pack /reload and not when a new player joins
            if (event.getPlayer() == null) {
                var server = event.getPlayerList().getServer();
                loadRecipes(server.getRecipeManager(), server.registryAccess());
                PacketDistributor.sendToAllPlayers(new SyncCompressionChainsPacket(chains));
            } else {
                PacketDistributor.sendToPlayer(event.getPlayer(), new SyncCompressionChainsPacket(chains));
            }
        });
    }

    /**
     * Retrieves a compression chain containing a given item as any of its "variants".
     *
     * @param item The item to retrieve a corresponding chain for.
     * @return The {@link CompressionChain} corresponding to this item, or the {@code EMPTY} chain if no real chain
     * exists for it or the given item was {@code null}.
     */
    @NotNull
    public static CompressionChain getChain(@Nullable AEItemKey item) {
        if (item == null) {
            return EMPTY;
        }

        var cachedIndex = chainIndexes.get(item);

        if (cachedIndex != null) {
            return cachedIndex >= 0 ? chains.get(cachedIndex) : EMPTY;
        }

        for (var i = 0; i < chains.size(); i++) {
            var chain = chains.get(i);

            if (chain.containsVariant(item)) {
                for (var j = 0; j < chain.size(); j++) {
                    chainIndexes.put(chain.getItem(j), i);
                }

                return chain;
            }
        }

        chainIndexes.put(item, -1);
        return EMPTY;
    }

    /**
     * Synchronises chains gathered on the server to connected clients. Required in order for the compression
     * <b>cutoff</b> feature of Bulk Cells to be accessible to clients playing on a multiplayer server.
     */
    public static void syncToClient(SyncCompressionChainsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            chains.clear();
            chainIndexes.clear();
            chains.addAll(packet.chains());
        });
    }

    /**
     * (Re-)Initialises all compression chains upon initial server start-up, and upon any datapack {@code /reload}s in
     * case of any crafting recipes changing which may differ from those used to generate previous chains.
     */
    private static void loadRecipes(RecipeManager recipeManager, RegistryAccess access) {
        // Clear old chain cache in case of the server restarting or recipes being reloaded
        chains.clear();
        chainIndexes.clear();

        var compressed = new ArrayList<CraftingRecipe>();
        var decompressed = new ArrayList<CraftingRecipe>();
        var overrides = new ArrayList<Override>();

        // Retrieve all available "compression" and "decompression" recipes from the current server's recipe manager
        for (var recipe : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
            if (isCompressionRecipe(recipe.value(), access)) {
                compressed.add(recipe.value());
            } else if (isDecompressionRecipe(recipe.value())) {
                decompressed.add(recipe.value());
            }
        }

        // Filter gathered candidate recipes and retain only those that are reversible (i.e. those which can be carried
        // out back and forth to compress/decompress a resource without affecting the underlying quantity of it)
        compressed.removeIf(recipe -> isIrreversible(recipe, decompressed, overrides, access));
        decompressed.removeIf(recipe -> isIrreversible(recipe, compressed, overrides, access));

        // Prioritise recipes whose ingredients have only one potential item, to try and mitigate situations where items
        // have not been unified properly and some (modded) item's subsequent variant is of an identical resource from
        // a different mod than intended
        var ingredientSize = Comparator.<CraftingRecipe>comparingInt(
                r -> r.getIngredients().getFirst().getItems().length);
        compressed.sort(ingredientSize);
        decompressed.sort(ingredientSize);

        // Pull all available compression chains from the recipe shortlist and add these to the cache
        while (!compressed.isEmpty()) {
            var baseVariant =
                    Objects.requireNonNull(AEItemKey.of(compressed.removeFirst().getResultItem(access)));
            decompressed.removeIf(recipe -> baseVariant.matches(recipe.getResultItem(access)));
            chains.add(generateChain(baseVariant, compressed, decompressed, overrides, access));
        }

        LOGGER.info("Initialised bulk compression. {} compression chains gathered.", chains.size());
    }

    /**
     * Generates and returns a new {@link CompressionChain} given an initial "base variant" item, two lists of
     * "compression" and "decompression" recipe candidates and a list of "overrides" gathered previously. Operates
     * destructively on the given recipe lists in order to remove corresponding "opposite" recipes and prevent further
     * unnecessary reiterations with items already put into a previous chain.
     */
    private static CompressionChain generateChain(
            AEItemKey baseVariant,
            List<CraftingRecipe> compressed,
            List<CraftingRecipe> decompressed,
            List<Override> overrides,
            RegistryAccess access) {
        var variants = new ArrayList<AEItemKey>();
        var multipliers = new ArrayList<Integer>();

        variants.add(baseVariant);

        for (var lower = getNextVariant(baseVariant, decompressed, overrides, false, access); lower != null; ) {
            var item = lower.item();

            if (variants.contains(item)) {
                if (lower.factor() != 1) {
                    LOGGER.warn(
                            "Duplicate lower compression variant detected: {}. Check any recipe involving this item for problems.",
                            lower);
                }

                break;
            }

            variants.add(item);
            multipliers.add(lower.factor());
            compressed.removeIf(recipe -> item.matches(recipe.getResultItem(access)));
            lower = getNextVariant(item, decompressed, overrides, false, access);
        }

        multipliers.add(1);
        var chain = new ArrayList<CompressionChain.Variant>();

        for (var i = variants.size(); i > 0; i--) {
            chain.add(new CompressionChain.Variant(variants.get(i - 1), multipliers.get(i - 1)));
        }

        for (var higher = getNextVariant(baseVariant, compressed, overrides, true, access); higher != null; ) {
            if (chain.contains(higher)) {
                if (higher.factor() != 1) {
                    LOGGER.warn(
                            "Duplicate higher compression variant detected: {}. Check any recipe involving this item for problems.",
                            higher);
                }

                break;
            }

            chain.add(higher);
            var item = higher.item();
            decompressed.removeIf(recipe -> item.matches(recipe.getResultItem(access)));
            higher = getNextVariant(higher.item(), compressed, overrides, true, access);
        }

        LOGGER.debug("Gathered bulk compression chain: {}", chain);
        return new CompressionChain(chain);
    }

    /**
     * Retrieves the "next" {@link CompressionChain.Variant} for a given item based on a given list of recipes and
     * overrides and whether the recipes given are "compression" or "decompression" recipes.
     */
    private static CompressionChain.Variant getNextVariant(
            AEItemKey item,
            List<CraftingRecipe> recipes,
            List<Override> overrides,
            boolean compressed,
            RegistryAccess access) {
        for (var override : overrides) {
            if (compressed && override.smaller.equals(item)) {
                overrides.remove(override);
                return new CompressionChain.Variant(override.larger, override.factor);
            }

            if (!compressed && override.larger.equals(item)) {
                overrides.remove(override);
                return new CompressionChain.Variant(override.smaller, override.factor);
            }
        }

        for (var recipe : recipes) {
            for (var input : recipe.getIngredients().getFirst().getItems()) {
                if (item.matches(input)) {
                    recipes.remove(recipe);
                    return new CompressionChain.Variant(
                            AEItemKey.of(recipe.getResultItem(access)),
                            compressed
                                    ? recipe.getIngredients().size()
                                    : recipe.getResultItem(access).getCount());
                }
            }
        }

        return null;
    }

    /**
     * Tests that a given recipe is a "decompression" recipe, i.e. a recipe with only a single ingredient presumably
     * being split into some quantity of a "smaller" result item.
     */
    private static boolean isDecompressionRecipe(CraftingRecipe recipe) {
        return recipe.getIngredients().stream().filter(i -> !i.isEmpty()).count() == 1;
    }

    /**
     * Tests that a given recipe is a "compression" recipe, i.e. a recipe where every non-empty ingredient is the same
     * and corresponds to the same set of item values. It is assumed that at least one such ingredient is being crafted
     * up into a "larger" item such as a storage block, of which no more than one (1) of this resulting item should be
     * crafted.
     */
    private static boolean isCompressionRecipe(CraftingRecipe recipe, RegistryAccess access) {
        if (recipe.getResultItem(access).getCount() != 1) {
            return false;
        }

        var ingredients = recipe.getIngredients().stream()
                .filter(i -> !i.isEmpty())
                .distinct()
                .toList();

        if (ingredients.isEmpty()) {
            return false;
        }

        if (ingredients.size() == 1) {
            return true;
        }

        // Check further for any odd cases such as certain mods' metal ingot/block recipes post-unification
        var first = ingredients.getFirst().getItems();

        for (var i = 1; i < ingredients.size(); i++) {
            var stacks = ingredients.get(i).getItems();

            if (stacks.length != first.length) {
                return false;
            }

            for (var j = 0; j < stacks.length; j++) {
                if (!ItemStack.isSameItemSameComponents(stacks[j], first[j])) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Tests a given recipe against a list of corresponding candidate recipes in the opposite direction to check whether
     * the initial recipe is (ir)reversible. In other words, given a compression recipe and a list of candidate
     * <b>de</b>compression recipes (without accounting for overrides), this method returns {@code true} if there is no
     * corresponding decompression recipe which can be carried out to "reverse" the result of the initial compression
     * recipe and hence restore the original quantity of the smaller variant item previously compressed.
     */
    private static boolean isIrreversible(
            CraftingRecipe recipe, List<CraftingRecipe> candidates, List<Override> overrides, RegistryAccess access) {
        if (overrideRecipe(recipe, overrides, access)) {
            return false;
        }

        var testInput = recipe.getIngredients().getFirst().getItems();
        var testOutput = recipe.getResultItem(access).getItem();

        for (var candidate : candidates) {
            var input = candidate.getIngredients().getFirst().getItems();
            var output = candidate.getResultItem(access).getItem();

            var compressible = false;
            var decompressible = false;

            for (var i : input) {
                if (i.is(testOutput) && !isBlacklisted(i)) {
                    compressible = true;
                    break;
                }
            }

            for (var i : testInput) {
                if (i.is(output) && !isBlacklisted(i)) {
                    decompressible = true;
                    break;
                }
            }

            // spotless:off
            var sameQuantity = candidate.getResultItem(access).getCount() == recipe.getIngredients().size()
                            && recipe.getResultItem(access).getCount() == candidate.getIngredients().size();
            // spotless:on

            if (compressible && decompressible && sameQuantity) {
                return false;
            }
        }

        return true;
    }

    /**
     * It may be desirable for some items to be included as variants in a chain in spite of any recipes involving those
     * items not being reversible. Hence, we override any reversibility checks and generate a variant for such an item
     * based on its usually irreversible recipe.
     */
    private static boolean overrideRecipe(CraftingRecipe recipe, List<Override> overrides, RegistryAccess access) {
        var output = recipe.getResultItem(access);

        if (isBlacklisted(output)) {
            return false;
        }

        for (var input : recipe.getIngredients().getFirst().getItems()) {
            var inputVariant = input.getItemHolder().getData(MEGADataMaps.COMPRESSION_OVERRIDE);

            if (inputVariant == null) {
                continue;
            }

            if (inputVariant != output.getItem()) {
                continue;
            }

            var decompressed = isDecompressionRecipe(recipe);
            var smaller = AEItemKey.of(decompressed ? output : input);
            var larger = AEItemKey.of(decompressed ? input : output);
            var factor = !decompressed ? recipe.getIngredients().size() : output.getCount();

            var override = new Override(smaller, larger, factor);
            LOGGER.debug("Found bulk compression override: {}", override);
            overrides.add(override);

            return true;
        }

        return false;
    }

    /**
     * Conversely, it may be decided that some items should not be handled by the compression system at all and instead
     * excluded from any chains. In this case, a value of {@code "NONE"} for a given item within the overrides data map
     * (or equivalently, the "air" item that this value resolves to) specifies that this item should be excluded.
     */
    private static boolean isBlacklisted(ItemStack stack) {
        return stack.getItemHolder().getData(MEGADataMaps.COMPRESSION_OVERRIDE) == Items.AIR;
    }

    private record Override(AEItemKey smaller, AEItemKey larger, int factor) {
        @NotNull
        @java.lang.Override
        public String toString() {
            return larger + " → " + factor + "x " + smaller;
        }
    }
}
