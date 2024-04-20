package gripe._90.megacells.definition;

import java.util.ArrayList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.ItemDefinition;
import appeng.items.AEBaseItem;

import gripe._90.megacells.MEGACells;

public final class MEGACreativeTab {
    public static final ResourceLocation ID = MEGACells.makeId("tab");

    public static final CreativeModeTab TAB = CreativeModeTab.builder()
            .title(MEGATranslations.ModName.text())
            .icon(MEGAItems.ITEM_CELL_256M::stack)
            .displayItems(MEGACreativeTab::populateTab)
            .build();

    private static void populateTab(CreativeModeTab.ItemDisplayParameters ignored, CreativeModeTab.Output output) {
        var itemDefs = new ArrayList<ItemDefinition<?>>();
        itemDefs.addAll(MEGAItems.getItems());
        itemDefs.addAll(MEGABlocks.getBlocks());

        for (var itemDef : itemDefs) {
            var item = itemDef.asItem();

            // For block items, the block controls the creative tab
            if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(output);
            } else {
                output.accept(itemDef);
            }
        }
    }
}
