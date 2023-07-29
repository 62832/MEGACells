package gripe._90.megacells.util;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.world.item.CreativeModeTab;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.util.service.Platform;

public final class FabricPlatform implements Platform {
    @Override
    public Loader getLoader() {
        return Loader.FABRIC;
    }

    @Override
    public CreativeModeTab getCreativeTab(CreativeModeTab.DisplayItemsGenerator display) {
        return FabricItemGroup.builder()
                .title(MEGATranslations.CreativeTab.text())
                .icon(() -> MEGAItems.ITEM_CELL_256M.stack(1))
                .displayItems(display)
                .build();
    }

    @Override
    public boolean isAddonLoaded(Addons addon) {
        return FabricLoaderImpl.INSTANCE.isModLoaded(addon.getModId());
    }
}
