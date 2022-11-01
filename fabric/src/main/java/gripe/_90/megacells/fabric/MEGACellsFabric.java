package gripe._90.megacells.fabric;

import java.util.Objects;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.api.IAEAddonEntrypoint;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGABlocks;
import gripe._90.megacells.block.entity.MEGABlockEntities;
import gripe._90.megacells.init.InitStorageCells;
import gripe._90.megacells.init.InitUpgrades;
import gripe._90.megacells.init.fabric.Registration;
import gripe._90.megacells.item.MEGAItems;

public class MEGACellsFabric implements IAEAddonEntrypoint {

    public static final String MODID = "megacells";

    public static ResourceLocation makeId(String path) {
        return new ResourceLocation(MEGACells.MODID, path);
    }

    public static String getItemPath(Item item) {
        return Objects.requireNonNull(Registry.ITEM.getKey(item)).getPath();
    }

    public static final CreativeModeTab CREATIVE_TAB = FabricItemGroupBuilder.build(makeId(MODID),
            () -> new ItemStack(MEGAItems.ITEM_CELL_256M));

    @Override
    public void onAe2Initialized() {
        MEGABlocks.init();
        MEGAItems.init();
        MEGABlockEntities.init();

        Registration.registerBlocks(Registry.BLOCK);
        Registration.registerItems(Registry.ITEM);
        Registration.registerBlockEntities(Registry.BLOCK_ENTITY_TYPE);

        InitStorageCells.init();
        InitUpgrades.init();
    }

}
