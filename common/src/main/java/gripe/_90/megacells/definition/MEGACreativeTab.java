package gripe._90.megacells.definition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.ItemDefinition;
import appeng.items.AEBaseItem;

import gripe._90.megacells.util.Utils;

public class MEGACreativeTab {
    public static final CreativeModeTab TAB = Utils.PLATFORM.getCreativeTab(MEGACreativeTab::populateTab);
    public static final ResourceLocation ID = Utils.makeId("tab");

    private static void populateTab(CreativeModeTab.ItemDisplayParameters itemDisplayParameters,
            CreativeModeTab.Output output) {
        var itemDefs = new ArrayList<ItemDefinition<?>>();
        var blacklist = List.of(MEGAItems.DECOMPRESSION_PATTERN);

        itemDefs.addAll(MEGAItems.getItems());
        itemDefs.addAll(MEGABlocks.getBlocks());
        itemDefs.removeAll(blacklist);

        for (var itemDef : itemDefs) {
            var item = itemDef.asItem();

            // For block items, the block controls the creative tab
            if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock()instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(output);
            } else {
                output.accept(itemDef);
            }
        }
    }
}
