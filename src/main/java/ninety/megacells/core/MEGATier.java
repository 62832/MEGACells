package ninety.megacells.core;

import net.minecraft.world.item.Item;

import ninety.megacells.item.MEGAItems;

public enum MEGATier {
    _1M(1, "1m"),
    _4M(2, "4m"),
    _16M(3, "16m"),
    _64M(4, "64m"),
    _256M(5, "256m");

    public final int index;
    public final String affix;

    MEGATier(int index, String affix) {
        this.index = index;
        this.affix = affix;
    }

    public int kbFactor() {
        return 256 * (int) Math.pow(4, this.index);
    }

    public Item getComponent() {
        var definition = switch (this) {
            case _1M -> MEGAItems.CELL_COMPONENT_1M;
            case _4M -> MEGAItems.CELL_COMPONENT_4M;
            case _16M -> MEGAItems.CELL_COMPONENT_16M;
            case _64M -> MEGAItems.CELL_COMPONENT_64M;
            case _256M -> MEGAItems.CELL_COMPONENT_256M;
        };
        return definition.asItem();
    }
}
