package gripe._90.megacells.util.forge;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.util.service.Platform;

public final class ForgePlatform implements Platform {
    @Override
    public CreativeModeTab getCreativeTab() {
        return CreativeModeTab.builder()
                .title(MEGATranslations.CreativeTab.text())
                .icon(() -> MEGAItems.ITEM_CELL_256M.stack(1))
                .build();
    }

    @Override
    public boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        }

        return ModList.get().isLoaded(modId);
    }
}
