package gripe._90.megacells.util;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.util.service.IPlatformHelper;

public final class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public CreativeModeTab getCreativeTab() {
        return FabricItemGroupBuilder.build(Utils.makeId("tab"),
                () -> new ItemStack(MEGAItems.ITEM_CELL_256M));
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoaderImpl.INSTANCE.isModLoaded(modId);
    }
}
