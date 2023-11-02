package gripe._90.megacells.core;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import appeng.api.orientation.BlockOrientation;

public interface Platform {
    Loaders getLoader();

    boolean isClient();

    CreativeModeTab.Builder getCreativeTabBuilder();

    boolean isAddonLoaded(Addons addon);

    void initCompression();

    void initLavaTransform();

    void addVillagerTrade(ItemLike item, int cost, int quantity, int xp);

    interface Client {
        BakedModel createWrappedCellModel(Item cell, BlockOrientation orientation);
    }
}
