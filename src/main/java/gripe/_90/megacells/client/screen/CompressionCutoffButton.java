package gripe._90.megacells.client.screen;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;

import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.misc.CompressionChain;

public class CompressionCutoffButton extends IconButton {
    private Item item;

    public CompressionCutoffButton(OnPress onPress) {
        super(onPress);
    }

    public void setItem(CompressionChain.Variant variant) {
        item = variant.item().getItem();
    }

    @Override
    protected Icon getIcon() {
        return null;
    }

    @Nullable
    @Override
    protected Item getItemOverlay() {
        return item;
    }

    @Override
    public List<Component> getTooltipMessage() {
        var message = new ArrayList<Component>();
        message.add(MEGATranslations.CompressionCutoff.text());

        if (item != null) {
            message.add(item.getDescription());
        }

        return message;
    }
}
