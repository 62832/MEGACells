package gripe._90.megacells.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import net.minecraft.world.item.Items;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageCells;
import appeng.me.helpers.BaseActionSource;

import gripe._90.megacells.definition.MEGAComponents;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.BulkCellInventory;

public class BulkCellInventoryTest {
    private static final IActionSource SRC = new BaseActionSource();

    @Test
    void testNoopWhenUnfiltered() {
        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();

        var cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        var content = AEItemKey.of(Items.STICK);
        assertThat(cell.insert(content, 1, Actionable.MODULATE, SRC)).isZero();

        item.getConfigInventory(stack).addFilter(content);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.insert(content, 1, Actionable.MODULATE, SRC)).isEqualTo(1);
        assertThat(cell.extract(content, 1, Actionable.MODULATE, SRC)).isEqualTo(1);
        assertThat(cell.insert(content, 1, Actionable.MODULATE, SRC)).isEqualTo(1);

        item.getConfigInventory(stack).clear();
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.extract(content, 1, Actionable.MODULATE, SRC)).isZero();
    }

    @Test
    void testGreaterCapacity() {
        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();

        var content = AEItemKey.of(Items.STICK);
        item.getConfigInventory(stack).addFilter(content);

        var cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        var max = Long.MAX_VALUE;
        assertThat(cell.insert(content, max, Actionable.MODULATE, SRC)).isEqualTo(max);
        assertThat(cell.insert(content, max, Actionable.MODULATE, SRC)).isEqualTo(max);

        var amount = stack.getComponents().get(MEGAComponents.BULK_CELL_UNIT_COUNT);
        assertThat(amount).isEqualTo(BigInteger.valueOf(max).multiply(BigInteger.TWO));

        var reported = cell.getAvailableStacks().get(content);
        assertThat(reported).isEqualTo(BulkCellInventory.STACK_LIMIT);
    }

    @Test
    void testFilterLimit() {
        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();

        var allowed = AEItemKey.of(Items.STONE);
        var rejected = AEItemKey.of(Items.COBBLESTONE);
        item.getConfigInventory(stack).addFilter(allowed).addFilter(rejected);

        var cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.insert(allowed, 1, Actionable.MODULATE, SRC)).isEqualTo(1);
        assertThat(cell.insert(rejected, 1, Actionable.MODULATE, SRC)).isZero();
    }
}
