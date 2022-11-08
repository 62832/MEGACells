package gripe._90.megacells.util;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.util.service.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return FabricItemGroupBuilder.build(MEGACells.makeId(MEGACells.MODID),
                () -> new ItemStack(MEGAItems.ITEM_CELL_256M));
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoaderImpl.INSTANCE.isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
