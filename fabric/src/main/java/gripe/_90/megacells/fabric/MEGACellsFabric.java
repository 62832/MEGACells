package gripe._90.megacells.fabric;

import appeng.api.IAEAddonEntrypoint;
import appeng.api.behaviors.GenericInternalInventory;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlockEntities;

public class MEGACellsFabric implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        MEGACells.initCommon();
        initPatternProviderTransfer();
    }

    @SuppressWarnings("UnstableApiUsage")
    private void initPatternProviderTransfer() {
        GenericInternalInventory.SIDED.registerForBlockEntity(
                (be, context) -> be.getLogic().getReturnInv(), MEGABlockEntities.MEGA_PATTERN_PROVIDER);
    }
}
