package gripe._90.megacells.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

import appeng.api.IAEAddonEntrypoint;
import appeng.api.behaviors.GenericInternalInventory;
import appeng.core.AppEng;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGACreativeTab;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.menu.CellDockMenu;
import gripe._90.megacells.menu.MEGAInterfaceMenu;
import gripe._90.megacells.menu.MEGAPatternProviderMenu;

public class MEGACellsFabric implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        MEGACells.initCommon();
        registerAll();

        MEGACells.initUpgrades();
        initPatternProviderTransfer();
    }

    private void registerAll() {
        for (var block : MEGABlocks.getBlocks()) {
            Registry.register(BuiltInRegistries.BLOCK, block.id(), block.block());
            Registry.register(BuiltInRegistries.ITEM, block.id(), block.asItem());
        }

        for (var item : MEGAItems.getItems()) {
            Registry.register(BuiltInRegistries.ITEM, item.id(), item.asItem());
        }

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, MEGACreativeTab.ID, MEGACreativeTab.TAB);

        for (var blockEntity : MEGABlockEntities.getBlockEntityTypes().entrySet()) {
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, blockEntity.getKey(), blockEntity.getValue());
        }

        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("mega_interface"), MEGAInterfaceMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("mega_pattern_provider"), MEGAPatternProviderMenu.TYPE);
        Registry.register(BuiltInRegistries.MENU, AppEng.makeId("cell_dock"), CellDockMenu.TYPE);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void initPatternProviderTransfer() {
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (be, context) -> be.getLogic().getReturnInv(), MEGABlockEntities.MEGA_PATTERN_PROVIDER);
    }
}
