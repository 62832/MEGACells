package gripe._90.megacells;

import net.minecraft.core.Registry;

import appeng.api.IAEAddonEntrypoint;

import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.init.Registration;

public class MEGACellsFabric implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        MEGABlocks.init();
        MEGAItems.init();
        MEGABlockEntities.init();

        Registration.registerBlocks(Registry.BLOCK);
        Registration.registerItems(Registry.ITEM);
        Registration.registerBlockEntities(Registry.BLOCK_ENTITY_TYPE);

        InitStorageCells.init();
        InitUpgrades.init();
    }

}
