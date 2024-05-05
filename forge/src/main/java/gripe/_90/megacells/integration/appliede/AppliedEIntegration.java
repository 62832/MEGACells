package gripe._90.megacells.integration.appliede;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;

import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;

import gripe._90.appliede.me.misc.EMCInterfaceLogicHost;
import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlockEntities;
import gripe._90.megacells.definition.MEGABlocks;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGAMenus;

public final class AppliedEIntegration {
    public static final BlockDefinition<MEGAEMCInterfaceBlock> EMC_INTERFACE = MEGABlocks.block(
            "MEGA Transmutation Interface", "mega_emc_interface", MEGAEMCInterfaceBlock::new, BlockItem::new);
    public static final ItemDefinition<PartItem<MEGAEMCInterfacePart>> CABLE_EMC_INTERFACE = MEGAItems.part(
            "MEGA Transmutation Interface",
            "cable_mega_emc_interface",
            MEGAEMCInterfacePart.class,
            MEGAEMCInterfacePart::new);
    public static final MenuType<MEGAEMCInterfaceMenu> EMC_INTERFACE_MENU =
            MEGAMenus.create("mega_emc_interface", MEGAEMCInterfaceMenu::new, EMCInterfaceLogicHost.class);

    static {
        MEGABlockEntities.create(
                "mega_emc_interface",
                MEGAEMCInterfaceBlockEntity.class,
                MEGAEMCInterfaceBlockEntity::new,
                EMC_INTERFACE);
    }

    public static void init() {
        MEGACells.LOGGER.info("Initialised AppliedE integration.");
    }
}
