package gripe._90.megacells.core;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ItemLike;

public interface Platform {
    Loaders getLoader();

    CreativeModeTab.Builder getCreativeTabBuilder();

    boolean isAddonLoaded(Addons addon);

    void initCompression();

    void addVillagerTrade(ItemLike item, int cost, int quantity, int xp);
}
