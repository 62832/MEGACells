package gripe._90.megacells;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import appeng.api.AECapabilities;
import appeng.api.features.HotkeyAction;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import appeng.core.localization.GuiText;
import appeng.hotkeys.HotkeyActions;
import appeng.init.InitVillager;
import appeng.items.tools.powered.AbstractPortableCell;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;

import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAComponents;
import gripe._90.megacells.definition.MEGAConfig;
import gripe._90.megacells.definition.MEGACreativeTab;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;
import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.integration.Addons;
import gripe._90.megacells.integration.appmek.RadioactiveCellItem;
import gripe._90.megacells.item.cell.BulkCellItem;
import gripe._90.megacells.misc.CompressionService;

@Mod(MEGACells.MODID)
public class MEGACells {
    public static final String MODID = "megacells";
    public static final Logger LOGGER = LoggerFactory.getLogger(MEGATranslations.ModName.getEnglishText());

    public MEGACells(ModContainer container, IEventBus eventBus) {
        MEGABlocks.DR.register(eventBus);
        MEGAItems.DR.register(eventBus);
        MEGABlockEntities.DR.register(eventBus);
        MEGAMenus.DR.register(eventBus);
        MEGAComponents.DR.register(eventBus);
        MEGACreativeTab.DR.register(eventBus);

        eventBus.addListener(MEGACells::initUpgrades);
        eventBus.addListener(MEGACells::initStorageCells);
        eventBus.addListener(MEGACells::initCapabilities);
        eventBus.addListener(MEGACells::initVillagerTrades);

        CompressionService.init();

        container.registerConfig(ModConfig.Type.COMMON, MEGAConfig.SPEC);
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

            for (var cell : MEGAItems.getTieredCells()) {
                if (!(cell.keyType().equals("item") || cell.keyType().equals("fluid"))) {
                    continue;
                }

                Upgrades.add(AEItems.INVERTER_CARD, cell.item(), 1, storageCellGroup);
                Upgrades.add(AEItems.EQUAL_DISTRIBUTION_CARD, cell.item(), 1, storageCellGroup);
                Upgrades.add(AEItems.VOID_CARD, cell.item(), 1, storageCellGroup);

                if (cell.keyType().equals("item")) {
                    Upgrades.add(AEItems.FUZZY_CARD, cell.item(), 1, storageCellGroup);
                }

                if (cell.portable()) {
                    Upgrades.add(MEGAItems.GREATER_ENERGY_CARD, cell.item(), 2, portableCellGroup);
                }
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

            for (var addon : Addons.values()) {
                if (addon.isLoaded()) {
                    addon.getHelper().initUpgrades();
                }
            }
        });
    }

    private static void initStorageCells(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            StorageCells.addCellHandler(BulkCellItem.HANDLER);

            for (var cell : MEGAItems.getTieredCells()) {
                if (cell.item().asItem() instanceof AbstractPortableCell portable) {
                    HotkeyActions.register(
                            portable,
                            portable::openFromInventory,
                            cell.keyType().equals("item")
                                    ? HotkeyAction.PORTABLE_ITEM_CELL
                                    : HotkeyAction.PORTABLE_FLUID_CELL);
                }
            }

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

        for (var cell : MEGAItems.getTieredCells()) {
            if (cell.portable()) {
                registerPoweredItemCapability(event, cell.item().asItem());
            }
        }
    }

    private static <T extends Item> void registerPoweredItemCapability(RegisterCapabilitiesEvent event, T item) {
        if (item instanceof IAEItemPowerStorage powered) {
            event.registerItem(
                    Capabilities.EnergyStorage.ITEM, (stack, ctx) -> new PoweredItemCapabilities(stack, powered), item);
        }
    }
}
