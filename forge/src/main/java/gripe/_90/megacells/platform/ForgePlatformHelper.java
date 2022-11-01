package gripe._90.megacells.platform;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import gripe._90.megacells.item.MEGAItems;
import gripe._90.megacells.platform.service.IPlatformHelper;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return new CreativeModeTab("megacells.megacells") {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(MEGAItems.ITEM_CELL_256M);
            }
        };
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
}
