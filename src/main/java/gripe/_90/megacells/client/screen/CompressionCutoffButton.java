package gripe._90.megacells.client.screen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import appeng.client.gui.widgets.IconButton;
import appeng.util.Icon;

import gripe._90.megacells.definition.MEGATranslations;

public class CompressionCutoffButton extends IconButton {
    private ItemStack item = ItemStack.EMPTY;

    public CompressionCutoffButton(OnPress onPress) {
        super(onPress);
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    protected Icon getIcon() {
        return null;
    }

    @Override
    protected Item getItemOverlay() {
        return item.isEmpty() ? null : item.getItem();
    }

    @Override
    public List<Component> getTooltipMessage() {
        var message = new ArrayList<Component>();
        message.add(MEGATranslations.CompressionCutoff.text());

        if (!item.isEmpty()) {
            message.add(item.getHoverName());
        }

        return message;
    }
}
