package ninety.megacells.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.items.storage.BasicStorageCell;

import ninety.megacells.integration.appmek.AppMekIntegration;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.util.MEGATier;

public class MEGAStorageCell extends BasicStorageCell {

    private final MEGATier tier;
    private final IMEGACellType type;

    public MEGAStorageCell(Properties properties, MEGATier tier, IMEGACellType type) {
        super(properties, tier.getComponent(), type.housing(), 2.5f + 0.5f * tier.index, tier.kbFactor(),
                tier.kbFactor() * 8, type == MEGACellType.ITEM ? 63 : 9, type.keyType());
        this.tier = tier;
        this.type = type;
    }

    public MEGATier getTier() {
        return this.tier;
    }

    public IMEGACellType getType() {
        return this.type;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, level, lines, advancedTooltips);
        if (!AppMekIntegration.isAppMekLoaded() && this.type == ChemicalCellType.TYPE) {
            lines.add(new TextComponent("AppMek not installed."));
        }
    }
}
