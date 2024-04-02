package gripe._90.megacells.fabric;

import appeng.api.IAEAddonEntrypoint;
import appeng.api.behaviors.GenericInternalInventory;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlockEntities;

public class MEGACellsFabric implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        MEGACells.initCommon();
        initTransfers();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void initTransfers() {
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (be, context) -> be.getInterfaceLogic().getStorage(), MEGABlockEntities.MEGA_INTERFACE);
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (be, context) -> be.getLogic().getReturnInv(), MEGABlockEntities.MEGA_PATTERN_PROVIDER);
    }
}
