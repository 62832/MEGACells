package gripe._90.megacells.util.forge;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.util.Utils;
import gripe._90.megacells.util.service.Platform;

public final class ForgePlatform implements Platform {
    @Override
    public CreativeModeTab getCreativeTab() {
        return new CreativeModeTab(Utils.MODID + ".tab") {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(MEGAItems.ITEM_CELL_256M);
            }
        };
    }

    @Override
    public boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream()
                    .map(ModInfo::getModId)
                    .anyMatch(modId::equals);
        }
        return ModList.get().isLoaded(modId);
    }
}
