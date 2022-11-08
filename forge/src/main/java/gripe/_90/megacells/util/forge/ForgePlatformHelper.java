package gripe._90.megacells.util.forge;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.item.MEGAItems;
import gripe._90.megacells.util.service.IPlatformHelper;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return new CreativeModeTab(MEGACells.MODID) {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(MEGAItems.ITEM_CELL_256M);
            }
        };
    }

    @Override
    public boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        }
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
}
