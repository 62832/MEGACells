package gripe._90.megacells.client.render;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

import appeng.api.client.AEKeyRendering;
import appeng.items.storage.StorageCellTooltipComponent;

import gripe._90.megacells.definition.MEGATranslations;
import gripe._90.megacells.item.cell.PortableCellWorkbenchTooltipComponent;

public record PortableCellWorkbenchClientTooltipComponent(PortableCellWorkbenchTooltipComponent tooltipComponent)
        implements ClientTooltipComponent {
    private static final Component CELL_LABEL = MEGATranslations.WorkbenchCell.text();
    private static final Component CONFIG_LABEL = MEGATranslations.WorkbenchConfig.text();

    @Override
    public int getHeight() {
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

            width = font.width(CONFIG_LABEL) + 2 + Math.max(width, configWidth);
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
    public void renderText(
            @NotNull Font font,
            int x,
            int y,
            @NotNull Matrix4f matrix,
            @NotNull MultiBufferSource.BufferSource bufferSource) {
        y += (16 - font.lineHeight) / 2;

        if (!tooltipComponent.config().isEmpty()) {
            font.drawInBatch(
                    CONFIG_LABEL,
                    x,
                    y,
                    0x7E7E7E,
                    false,
                    matrix,
                    bufferSource,
                    Font.DisplayMode.NORMAL,
                    0,
                    LightTexture.FULL_BRIGHT);

            if (tooltipComponent.hasMoreConfig()) {
                font.drawInBatch(
                        "…",
                        x
                                + font.width(CONFIG_LABEL)
                                + 4
                                + tooltipComponent.config().size() * 17,
                        y + 2,
                        -1,
                        false,
                        matrix,
                        bufferSource,
                        Font.DisplayMode.NORMAL,
                        0,
                        LightTexture.FULL_BRIGHT);
            }

            y += 17;
        }

        if (!tooltipComponent.cell().isEmpty()) {
            font.drawInBatch(
                    CELL_LABEL,
                    x,
                    y,
                    0x7E7E7E,
                    false,
                    matrix,
                    bufferSource,
                    Font.DisplayMode.NORMAL,
                    0,
                    LightTexture.FULL_BRIGHT);
        }
    }

    @Override
    public void renderImage(@NotNull Font font, int x, int y, @NotNull GuiGraphics guiGraphics) {
        var config = tooltipComponent.config();

        if (!config.isEmpty()) {
            var xOff = font.width(CONFIG_LABEL) + 2;

            for (var stack : config) {
                AEKeyRendering.drawInGui(Minecraft.getInstance(), guiGraphics, x + xOff, y, stack.what());
                xOff += 17;
            }

            y += 17;
        }

        var cellOpt = tooltipComponent.cell().getTooltipImage();

        if (cellOpt.isPresent() && cellOpt.get() instanceof StorageCellTooltipComponent cellComponent) {
            var xOff = font.width(CELL_LABEL) + 2;
            guiGraphics.renderItem(tooltipComponent.cell(), x + xOff, y);

            var upgrades = cellComponent.upgrades();

            if (!upgrades.isEmpty()) {
                xOff += 17;

                for (var upgrade : upgrades) {
                    guiGraphics.renderItem(upgrade, x + xOff, y);
                    xOff += 17;
                }
            }
        }
    }
}
