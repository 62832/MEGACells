package gripe._90.megacells.client.render;

import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import appeng.client.api.AEKeyRendering;
import appeng.items.storage.StorageCellTooltipComponent;

import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.item.cell.PortableCellWorkbenchTooltipComponent;

public record PortableCellWorkbenchClientTooltipComponent(PortableCellWorkbenchTooltipComponent tooltipComponent)
        implements ClientTooltipComponent {
    private static final Component CELL_LABEL = MEGATranslations.WorkbenchCell.text();
    private static final Component CONFIG_LABEL = MEGATranslations.WorkbenchConfig.text();

    @Override
    public int getHeight(Font font) {
        var height = 0;

        if (!tooltipComponent.config().isEmpty()) {
            height += 17;
        }

        var cellOpt = tooltipComponent.cell().getTooltipImage();

        if (cellOpt.isPresent() && cellOpt.get() instanceof StorageCellTooltipComponent) {
            height += 17;
        }
        return height;
    }

    @Override
    public int getWidth(@NotNull Font font) {
        var width = 0;

        if (!tooltipComponent.config().isEmpty()) {
            var configWidth = tooltipComponent.config().size() * 17;

            if (tooltipComponent.hasMoreConfig()) {
                configWidth += 10;
            }

            width = font.width(CONFIG_LABEL) + 2 + configWidth;
        }

        var cellOpt = tooltipComponent.cell().getTooltipImage();

        if (cellOpt.isPresent() && cellOpt.get() instanceof StorageCellTooltipComponent cellComponent) {
            width = Math.max(
                    width,
                    font.width(CELL_LABEL) + 2 + 17 * (cellComponent.upgrades().size() + 1));
        }

        return width;
    }

    @Override
    public void extractText(GuiGraphicsExtractor guiGraphics, @NotNull Font font, int x, int y) {
        var yOffset = (16 - font.lineHeight) / 2;

        if (!tooltipComponent.config().isEmpty()) {
            guiGraphics.text(font, CONFIG_LABEL, x, y + yOffset, 0x7E7E7E, false);

            if (tooltipComponent.hasMoreConfig()) {
                guiGraphics.text(
                        font,
                        "\u2026",
                        x
                                + font.width(CONFIG_LABEL)
                                + 4
                                + tooltipComponent.config().size() * 17,
                        y + 2,
                        -1,
                        false);
            }

            y += 17;
        }

        if (!tooltipComponent.cell().isEmpty()) {
            guiGraphics.text(font, CELL_LABEL, x, y + yOffset, 0x7E7E7E, false);
        }
    }

    @Override
    public void extractImage(
            @NotNull Font font, int x, int y, int width, int height, @NotNull GuiGraphicsExtractor guiGraphics) {
        var config = tooltipComponent.config();

        if (!config.isEmpty()) {
            var xOffset = font.width(CONFIG_LABEL) + 2;

            for (var stack : config) {
                AEKeyRendering.drawInGui(Minecraft.getInstance(), guiGraphics, x + xOffset, y, stack.what());
                xOffset += 17;
            }

            y += 17;
        }

        var cellOpt = tooltipComponent.cell().getTooltipImage();

        if (cellOpt.isPresent() && cellOpt.get() instanceof StorageCellTooltipComponent cellComponent) {
            var xOffset = font.width(CELL_LABEL) + 2;
            guiGraphics.item(tooltipComponent.cell(), x + xOffset, y);

            for (var upgrade : cellComponent.upgrades()) {
                xOffset += 17;
                guiGraphics.item(upgrade, x + xOffset, y);
            }
        }
    }
}
