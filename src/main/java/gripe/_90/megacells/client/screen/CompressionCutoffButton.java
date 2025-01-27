package gripe._90.megacells.client.screen;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;

import gripe._90.megacells.definition.MEGATranslations;

public class CompressionCutoffButton extends IconButton {
    private final Handler onPress;
    private Item item;

    public CompressionCutoffButton(Handler onPress) {
        super(btn -> {
            if (btn instanceof CompressionCutoffButton cutoff) {
                if (Minecraft.getInstance().screen instanceof AEBaseScreen<?> screen) {
                    cutoff.onPress.handle(cutoff, screen.isHandlingRightClick());
                }
            }
        });
        this.onPress = onPress;
    }

    public void setItem(Item item) {
        this.item = item;
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

    @FunctionalInterface
    public interface Handler {
        void handle(CompressionCutoffButton button, boolean backwards);
    }
}
