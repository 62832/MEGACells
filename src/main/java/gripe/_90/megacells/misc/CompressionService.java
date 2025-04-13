package gripe._90.megacells.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
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

import gripe._90.megacells.definition.MEGADataMaps;

public class CompressionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompressionService.class);
    private static final CompressionChain EMPTY = new CompressionChain(List.of());

    // Each chain is a list of "variants", where each variant consists of the item itself along with an associated value
    // dictating how much of the previous variant's item is needed to compress into that variant.
    // This value will be between 1 and 9 (inclusive) for any given item, or just 1 for the smallest base variant.
    private static final List<CompressionChain> chains = new ArrayList<>();
    private static final Map<Item, Integer> chainIndexCache = new HashMap<>();

    // Should always be true on the client side, only the server needs to toggle this when loading
    private static boolean loaded = true;

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

    public static CompressionChain getChain(Item item) {
        if (item == null) {
            return EMPTY;
        }

        var cachedIndex = chainIndexCache.get(item);

        if (cachedIndex != null) {
            return cachedIndex >= 0 ? chains.get(cachedIndex) : EMPTY;
        }

        for (var i = 0; i < chains.size(); i++) {
            var chain = chains.get(i);

            if (chain.containsVariant(item)) {
                chainIndexCache.put(item, i);
                return chain;
            }
        }

        if (loaded) {
            chainIndexCache.put(item, -1);
        }

        return EMPTY;
    }

    public static void syncToClient(SyncCompressionChainsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            chains.clear();
            chainIndexCache.clear();
            chains.addAll(packet.chains());
        });
    }

    private static void loadRecipes(RecipeManager recipeManager, RegistryAccess access) {
        // Clear old chain cache in case of the server restarting or recipes being reloaded
        loaded = false;
        chains.clear();

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

        var ingredientSize = Comparator.<CraftingRecipe>comparingInt(
                r -> r.getIngredients().getFirst().getItems().length);
        compressed.sort(ingredientSize);
        decompressed.sort(ingredientSize);

        // Pull all available compression chains from the recipe shortlist and add these to the cache
        Stream.concat(compressed.stream(), decompressed.stream()).forEach(recipe -> {
            var baseVariant = recipe.getResultItem(access).getItem();

            if (getChain(baseVariant).isEmpty()) {
                chains.add(generateChain(baseVariant, compressed, decompressed, overrides, access));
            }
        });

        LOGGER.info("Initialised bulk compression. {} compression chains gathered.", chains.size());
        loaded = true;
        chainIndexCache.clear();
    }

    private static CompressionChain generateChain(
            Item baseVariant,
            List<CraftingRecipe> compressed,
            List<CraftingRecipe> decompressed,
            List<Override> overrides,
            RegistryAccess access) {
        var variants = new LinkedList<Item>();
        var multipliers = new LinkedList<Integer>();

        variants.addFirst(baseVariant);

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

            variants.addFirst(item);
            multipliers.addFirst(lower.factor());
            lower = getNextVariant(item, decompressed, overrides, false, access);
        }

        multipliers.addFirst(1);
        var chain = new ObjectArrayList<CompressionChain.Variant>();

        for (var i = 0; i < variants.size(); i++) {
            chain.add(new CompressionChain.Variant(variants.get(i), multipliers.get(i)));
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
            higher = getNextVariant(higher.item(), compressed, overrides, true, access);
        }

        LOGGER.debug("Gathered bulk compression chain: {}", chain);
        return new CompressionChain(chain);
    }

    private static CompressionChain.Variant getNextVariant(
            Item item,
            List<CraftingRecipe> recipes,
            List<Override> overrides,
            boolean compressed,
            RegistryAccess access) {
        for (var override : overrides) {
            if (compressed && override.smaller.equals(item)) {
                return new CompressionChain.Variant(override.larger, override.factor);
            }

            if (!compressed && override.larger.equals(item)) {
                return new CompressionChain.Variant(override.smaller, override.factor);
            }
        }

        for (var recipe : recipes) {
            for (var input : recipe.getIngredients().getFirst().getItems()) {
                if (input.getItem().equals(item)) {
                    return new CompressionChain.Variant(
                            recipe.getResultItem(access).getItem(),
                            compressed
                                    ? recipe.getIngredients().size()
                                    : recipe.getResultItem(access).getCount());
                }
            }
        }

        return null;
    }

    private static boolean isDecompressionRecipe(CraftingRecipe recipe) {
        return recipe.getIngredients().stream().filter(i -> !i.isEmpty()).count() == 1;
    }

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

    // It may be desirable for some items to be included as variants in a chain in spite of any recipes involving those
    // items not being reversible. Hence, we override any reversibility checks and generate a variant for such an item
    // based on its usually irreversible recipe.
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
            var smaller = (decompressed ? output : input).getItem();
            var larger = (decompressed ? input : output).getItem();
            var factor = !decompressed ? recipe.getIngredients().size() : output.getCount();

            var override = new Override(smaller, larger, factor);
            LOGGER.debug("Found bulk compression override: {}", override);
            overrides.add(override);

            return true;
        }

        return false;
    }

    private static boolean isBlacklisted(ItemStack stack) {
        return stack.getItemHolder().getData(MEGADataMaps.COMPRESSION_OVERRIDE) == Items.AIR;
    }

    private record Override(Item smaller, Item larger, int factor) {
        @java.lang.Override
        public String toString() {
            return larger + " → " + factor + "x " + smaller;
        }
    }
}
