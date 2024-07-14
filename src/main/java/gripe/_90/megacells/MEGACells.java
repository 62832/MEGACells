package gripe._90.megacells;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import appeng.api.AECapabilities;
import appeng.api.client.StorageCellModels;
import appeng.api.features.HotkeyAction;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.GridServices;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.block.networking.EnergyCellBlockItem;
import appeng.client.gui.implementations.InterfaceScreen;
import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.client.render.crafting.CraftingMonitorRenderer;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.hooks.BuiltInModelHooks;
import appeng.hotkeys.HotkeyActions;
import appeng.init.InitVillager;
import appeng.init.client.InitScreens;
import appeng.items.storage.BasicStorageCell;
import appeng.items.tools.powered.AbstractPortableCell;
import appeng.items.tools.powered.PortableCellItem;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;

import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.client.render.MEGACraftingUnitModelProvider;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAComponents;
import gripe._90.megacells.definition.MEGAConfig;
import gripe._90.megacells.definition.MEGACreativeTab;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.integration.Addons;
import gripe._90.megacells.integration.ae2wt.AE2WTIntegration;
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.appmek.RadioactiveCellItem;
import gripe._90.megacells.item.cell.BulkCellItem;
import gripe._90.megacells.menu.MEGAInterfaceMenu;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;
import gripe._90.megacells.misc.CompressionService;
import gripe._90.megacells.misc.DecompressionService;

@Mod(MEGACells.MODID)
public class MEGACells {
    public static final String MODID = "megacells";

    public MEGACells(ModContainer container, IEventBus modEventBus) {
        MEGABlocks.DR.register(modEventBus);
        MEGAItems.DR.register(modEventBus);
        MEGABlockEntities.DR.register(modEventBus);
        MEGAMenus.DR.register(modEventBus);
        MEGAComponents.DR.register(modEventBus);
        MEGACreativeTab.DR.register(modEventBus);

        modEventBus.addListener(MEGACells::initUpgrades);
        modEventBus.addListener(MEGACells::initStorageCells);
        modEventBus.addListener(MEGACells::initCapabilities);
        modEventBus.addListener(MEGACells::initVillagerTrades);

        initCompression();

        container.registerConfig(ModConfig.Type.COMMON, MEGAConfig.SPEC);

        if (FMLEnvironment.dist.isClient()) {
            Client.init(modEventBus);
        }
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    private static void initUpgrades(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            var storageCellGroup = GuiText.StorageCells.getTranslationKey();
            var portableCellGroup = GuiText.PortableCells.getTranslationKey();
            var interfaceGroup = GuiText.Interface.getTranslationKey();
            var wirelessTerminalGroup = GuiText.WirelessTerminals.getTranslationKey();

            for (var itemCell : MEGAItems.getItemCells()) {
                Upgrades.add(AEItems.FUZZY_CARD, itemCell, 1, storageCellGroup);
                Upgrades.add(AEItems.INVERTER_CARD, itemCell, 1, storageCellGroup);
                Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, itemCell, 1, storageCellGroup);
                Upgrades.add(AEItems.VOID_CARD, itemCell, 1, storageCellGroup);
            }

            for (var fluidCell : MEGAItems.getFluidCells()) {
                Upgrades.add(AEItems.INVERTER_CARD, fluidCell, 1, storageCellGroup);
                Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, fluidCell, 1, storageCellGroup);
                Upgrades.add(AEItems.VOID_CARD, fluidCell, 1, storageCellGroup);
            }

            for (var itemPortable : MEGAItems.getItemPortables()) {
                Upgrades.add(AEItems.FUZZY_CARD, itemPortable, 1, portableCellGroup);
                Upgrades.add(AEItems.INVERTER_CARD, itemPortable, 1, portableCellGroup);
                Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, itemPortable, 2, portableCellGroup);
                Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, itemPortable, 1, storageCellGroup);
                Upgrades.add(AEItems.VOID_CARD, itemPortable, 1, storageCellGroup);
            }

            for (var fluidPortable : MEGAItems.getFluidPortables()) {
                Upgrades.add(AEItems.INVERTER_CARD, fluidPortable, 1, portableCellGroup);
                Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, fluidPortable, 2, portableCellGroup);
                Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, fluidPortable, 1, storageCellGroup);
                Upgrades.add(AEItems.VOID_CARD, fluidPortable, 1, storageCellGroup);
            }

            Upgrades.add(AEItems.CRAFTING_CARD, MEGABlocks.MEGA_INTERFACE, 1, interfaceGroup);
            Upgrades.add(AEItems.CRAFTING_CARD, MEGAItems.MEGA_INTERFACE, 1, interfaceGroup);
            Upgrades.add(AEItems.FUZZY_CARD, MEGABlocks.MEGA_INTERFACE, 1, interfaceGroup);
            Upgrades.add(AEItems.FUZZY_CARD, MEGAItems.MEGA_INTERFACE, 1, interfaceGroup);

            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.WIRELESS_TERMINAL, 2, wirelessTerminalGroup);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.WIRELESS_CRAFTING_TERMINAL, 2, wirelessTerminalGroup);

            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.COLOR_APPLICATOR, 2);
            Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, AEItems.MATTER_CANNON, 2);

            Upgrades.add(MEGAItems.COMPRESSION_CARD, MEGAItems.BULK_ITEM_CELL, 1);

            for (var portableCell : List.of(
                    AEItems.PORTABLE_ITEM_CELL1K,
                    AEItems.PORTABLE_ITEM_CELL4K,
                    AEItems.PORTABLE_ITEM_CELL16K,
                    AEItems.PORTABLE_ITEM_CELL64K,
                    AEItems.PORTABLE_ITEM_CELL256K,
                    AEItems.PORTABLE_FLUID_CELL1K,
                    AEItems.PORTABLE_FLUID_CELL4K,
                    AEItems.PORTABLE_FLUID_CELL16K,
                    AEItems.PORTABLE_FLUID_CELL64K,
                    AEItems.PORTABLE_FLUID_CELL256K)) {
                Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, portableCell, 2, portableCellGroup);
            }

            if (Addons.AE2WTLIB.isLoaded()) {
                AE2WTIntegration.initUpgrades();
            }

            if (Addons.APPMEK.isLoaded()) {
                AppMekIntegration.initUpgrades();
            }
        });
    }

    private static void initStorageCells(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            StorageCells.addCellHandler(BulkCellItem.HANDLER);

            MEGAItems.getItemPortables()
                    .forEach(cell -> HotkeyActions.registerPortableCell(cell, HotkeyAction.PORTABLE_ITEM_CELL));
            MEGAItems.getFluidPortables()
                    .forEach(cell -> HotkeyActions.registerPortableCell(cell, HotkeyAction.PORTABLE_FLUID_CELL));

            if (Addons.APPMEK.isLoaded()) {
                StorageCells.addCellHandler(RadioactiveCellItem.HANDLER);
            }
        });
    }

    private static void initVillagerTrades(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            addVillagerTrade(MEGAItems.SKY_STEEL_INGOT, 8, 3, 20);
            addVillagerTrade(MEGAItems.ACCUMULATION_PROCESSOR_PRESS, 40, 1, 50);
        });
    }

    private static void addVillagerTrade(ItemLike item, int cost, int quantity, int xp) {
        var offers = VillagerTrades.TRADES.computeIfAbsent(InitVillager.PROFESSION, k -> new Int2ObjectOpenHashMap<>());
        var masterEntries = offers.computeIfAbsent(5, k -> new VillagerTrades.ItemListing[0]);
        masterEntries = ArrayUtils.add(
                masterEntries,
                (i, j) -> new MerchantOffer(
                        new ItemCost(Items.EMERALD, cost), new ItemStack(item, quantity), 12, xp, 0.05F));
        offers.put(5, masterEntries);
    }

    private static void initCompression() {
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> {
            var server = event.getServer();
            CompressionService.INSTANCE.loadRecipes(server.getRecipeManager(), server.registryAccess());
        });

        NeoForge.EVENT_BUS.addListener((OnDatapackSyncEvent event) -> {
            // Only rebuild cache in the event of a data pack /reload and not when a new player joins
            if (event.getPlayer() == null) {
                var server = event.getPlayerList().getServer();
                CompressionService.INSTANCE.loadRecipes(server.getRecipeManager(), server.registryAccess());
            }
        });

        GridServices.register(DecompressionService.class, DecompressionService.class);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void initCapabilities(RegisterCapabilitiesEvent event) {
        for (var type : MEGABlockEntities.DR.getEntries()) {
            event.registerBlockEntity(
                    AECapabilities.IN_WORLD_GRID_NODE_HOST, type.get(), (be, context) -> (IInWorldGridNodeHost) be);
        }

        event.registerBlockEntity(
                AECapabilities.GENERIC_INTERNAL_INV,
                MEGABlockEntities.MEGA_INTERFACE.get(),
                (be, context) -> be.getInterfaceLogic().getStorage());
        event.registerBlockEntity(
                AECapabilities.ME_STORAGE,
                MEGABlockEntities.MEGA_INTERFACE.get(),
                (be, context) -> be.getInterfaceLogic().getInventory());

        event.registerBlockEntity(
                AECapabilities.GENERIC_INTERNAL_INV,
                MEGABlockEntities.MEGA_PATTERN_PROVIDER.get(),
                (be, context) -> be.getLogic().getReturnInv());

        MEGAItems.getItemPortables().forEach(portable -> registerPoweredItemCapability(event, portable.get()));
        MEGAItems.getFluidPortables().forEach(portable -> registerPoweredItemCapability(event, portable.get()));

        if (Addons.APPMEK.isLoaded()) {
            for (var portable : MEGAItems.getChemicalPortables()) {
                if (portable.get() instanceof AbstractPortableCell cell) {
                    registerPoweredItemCapability(event, cell);
                }
            }
        }
    }

    private static <T extends Item & IAEItemPowerStorage> void registerPoweredItemCapability(
            RegisterCapabilitiesEvent event, T item) {
        event.registerItem(
                Capabilities.EnergyStorage.ITEM, (object, context) -> new PoweredItemCapabilities(object, item), item);
    }

    @OnlyIn(Dist.CLIENT)
    private static class Client {
        private static void init(IEventBus modEventBus) {
            modEventBus.addListener(Client::initScreens);
            modEventBus.addListener(Client::initCraftingUnitModels);
            modEventBus.addListener(Client::initEnergyCellProps);
            modEventBus.addListener(Client::initStorageCellModels);
            modEventBus.addListener(Client::initItemColours);
        }

        private static void initScreens(RegisterMenuScreensEvent event) {
            InitScreens.register(
                    event,
                    MEGAMenus.MEGA_INTERFACE,
                    InterfaceScreen<MEGAInterfaceMenu>::new,
                    "/screens/megacells/mega_interface.json");
            InitScreens.register(
                    event,
                    MEGAMenus.MEGA_PATTERN_PROVIDER,
                    PatternProviderScreen<MEGAPatternProviderMenu>::new,
                    "/screens/megacells/mega_pattern_provider.json");
        }

        @SuppressWarnings("deprecation")
        private static void initCraftingUnitModels(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                for (var type : MEGACraftingUnitType.values()) {
                    BuiltInModelHooks.addBuiltInModel(
                            MEGACells.makeId("block/crafting/" + type.getAffix() + "_formed"),
                            new CraftingCubeModel(new MEGACraftingUnitModelProvider(type)));

                    ItemBlockRenderTypes.setRenderLayer(type.getDefinition().block(), RenderType.cutout());
                }

                BlockEntityRenderers.register(
                        MEGABlockEntities.MEGA_CRAFTING_MONITOR.get(), CraftingMonitorRenderer::new);
            });
        }

        private static void initEnergyCellProps(FMLClientSetupEvent event) {
            event.enqueueWork(() -> ItemProperties.register(
                    MEGABlocks.MEGA_ENERGY_CELL.asItem(), AppEng.makeId("fill_level"), (is, level, entity, i) -> {
                        var energyCell = (EnergyCellBlockItem) MEGABlocks.MEGA_ENERGY_CELL.asItem();

                        double curPower = energyCell.getAECurrentPower(is);
                        double maxPower = energyCell.getAEMaxPower(is);

                        return (float) (curPower / maxPower);
                    }));
        }

        private static void initStorageCellModels(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                var modelPrefix = "block/drive/cells/";

                for (var cell : MEGAItems.getAllCells()) {
                    StorageCellModels.registerModel(
                            cell.item(),
                            makeId(modelPrefix + cell.tier().namePrefix() + "_" + cell.keyType() + "_cell"));
                }

                StorageCellModels.registerModel(
                        MEGAItems.BULK_ITEM_CELL,
                        makeId(modelPrefix + MEGAItems.BULK_ITEM_CELL.id().getPath()));
            });
        }

        private static void initItemColours(RegisterColorHandlersEvent.Item event) {
            var standardCells = new ArrayList<ItemLike>();
            standardCells.addAll(MEGAItems.getItemCells());
            standardCells.addAll(MEGAItems.getFluidCells());
            standardCells.add(MEGAItems.BULK_ITEM_CELL);

            var portableCells = new ArrayList<ItemLike>();
            portableCells.addAll(MEGAItems.getItemPortables());
            portableCells.addAll(MEGAItems.getFluidPortables());

            event.register(
                    (stack, tintIndex) -> FastColor.ARGB32.opaque(BasicStorageCell.getColor(stack, tintIndex)),
                    standardCells.toArray(new ItemLike[0]));
            event.register(
                    (stack, tintIndex) -> FastColor.ARGB32.opaque(PortableCellItem.getColor(stack, tintIndex)),
                    portableCells.toArray(new ItemLike[0]));
        }
    }
}
