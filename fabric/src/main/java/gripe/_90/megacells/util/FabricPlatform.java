package gripe._90.megacells.util;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.definition.MEGAItems;

public final class FabricPlatform implements Platform {
    @Override
    public CreativeModeTab getCreativeTab() {
        return FabricItemGroupBuilder.build(MEGACells.makeId("tab"), () -> new ItemStack(MEGAItems.ITEM_CELL_256M));
    }

    @Override
    public boolean isAddonLoaded(Addons addon) {
        return FabricLoaderImpl.INSTANCE.isModLoaded(addon.getModId());
    }
}
