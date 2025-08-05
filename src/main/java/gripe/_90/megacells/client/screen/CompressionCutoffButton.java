package gripe._90.megacells.client.screen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;

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
    public List<Component> getTooltipMessage() {
        var message = new ArrayList<Component>();
        message.add(MEGATranslations.CompressionCutoff.text());

        if (!item.isEmpty()) {
            message.add(item.getHoverName());
        }

        return message;
    }

    // TODO (AE2): Probably better that IconButton::getItemOverlay simply returned an ItemStack rather than just Item?
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            var yOffset = isHovered() ? 1 : 0;
            var bgIcon = isHovered()
                    ? Icon.TOOLBAR_BUTTON_BACKGROUND_HOVER
                    : isFocused() ? Icon.TOOLBAR_BUTTON_BACKGROUND_FOCUS : Icon.TOOLBAR_BUTTON_BACKGROUND;

            bgIcon.getBlitter()
                    .dest(getX() - 1, getY() + yOffset, 18, 20)
                    .zOffset(2)
                    .blit(guiGraphics);
            guiGraphics.renderItem(item, getX(), getY() + 1 + yOffset, 0, 3);
        }
    }
}
