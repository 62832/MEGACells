package gripe._90.megacells.integration.appliede;

import java.util.function.Supplier;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

import appeng.api.upgrades.Upgrades;

import gripe._90.appliede.AppliedE;
import gripe._90.appliede.menu.EMCInterfaceMenu;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.integration.IntegrationHelper;

public class AppliedEIntegration implements IntegrationHelper {
    public static Supplier<BlockEntityType<MEGAEMCInterfaceBlockEntity>> MEGA_EMC_INTERFACE_BE = null;
    public static Supplier<MenuType<EMCInterfaceMenu>> MEGA_EMC_INTERFACE_MENU = null;

    @Override
    public void initUpgrades() {
        var emcInterfaceGroup = AppliedE.EMC_INTERFACE.get().getDescriptionId();
        Upgrades.add(AppliedE.LEARNING_CARD, MEGABlocks.MEGA_EMC_INTERFACE, 1, emcInterfaceGroup);
        Upgrades.add(AppliedE.LEARNING_CARD, MEGAItems.MEGA_EMC_INTERFACE, 1, emcInterfaceGroup);
    }
}
