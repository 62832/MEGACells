package gripe._90.megacells.util.forge;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.core.Platform;
import gripe._90.megacells.definition.MEGAItems;

public final class ForgePlatform implements Platform {
    @Override
    public CreativeModeTab getCreativeTab() {
        return new CreativeModeTab(MEGACells.MODID + ".tab") {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(MEGAItems.ITEM_CELL_256M);
            }
        };
    }

    @Override
    public boolean isAddonLoaded(Addons addon) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream()
                    .map(ModInfo::getModId)
                    .anyMatch(addon.getModId()::equals);
        }
        return ModList.get().isLoaded(addon.getModId());
    }
}
