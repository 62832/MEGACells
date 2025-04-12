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
import appeng.api.storage.cells.CellState;
import appeng.me.helpers.BaseActionSource;

import gripe._90.megacells.definition.MEGAComponents;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.BulkCellInventory;

public class BulkCellInventoryTest {
    private static final IActionSource SRC = new BaseActionSource();

    @Test
    void testNoOpBeforeFiltering() {
        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();

        var cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        var content = AEItemKey.of(Items.STICK);
        assertThat(cell.insert(content, 1, Actionable.MODULATE, SRC)).isZero();

        item.getConfigInventory(stack).addFilter(content);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.insert(content, 1, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.extract(content, 1, Actionable.MODULATE, SRC)).isOne();
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
    void testFilterMismatchOperations() {
        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();

        var allowed = AEItemKey.of(Items.STONE);
        var rejected = AEItemKey.of(Items.COBBLESTONE);
        item.getConfigInventory(stack).addFilter(allowed);

        var cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isEqualTo(CellState.EMPTY);
        cell.insert(allowed, 1, Actionable.MODULATE, SRC);
        assertThat(cell.getStatus()).isEqualTo(CellState.NOT_EMPTY);

        // no-op on insert for items not matching filter
        assertThat(cell.insert(rejected, 1, Actionable.MODULATE, SRC)).isZero();

        // no-op on any insertion when the cell's filter is cleared, but it still contains an item
        item.getConfigInventory(stack).clear();
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isEqualTo(CellState.FULL);
        assertThat(cell.insert(allowed, 1, Actionable.MODULATE, SRC)).isZero();
        assertThat(cell.insert(rejected, 1, Actionable.MODULATE, SRC)).isZero();

        // allow item recovery when filter is accidentally cleared
        assertThat(cell.extract(allowed, 1, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.getStatus()).isEqualTo(CellState.EMPTY);

        // reset filter and contents
        item.getConfigInventory(stack).addFilter(allowed);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        cell.insert(allowed, 1, Actionable.MODULATE, SRC);

        // no-op on any insertion when filter item does not match already-stored item
        item.getConfigInventory(stack).clear();
        item.getConfigInventory(stack).addFilter(rejected);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isEqualTo(CellState.FULL);
        assertThat(cell.insert(allowed, 1, Actionable.MODULATE, SRC)).isZero();
        assertThat(cell.insert(rejected, 1, Actionable.MODULATE, SRC)).isZero();

        // still allow item recovery, but make sure the right item is being recovered
        assertThat(cell.extract(rejected, 1, Actionable.MODULATE, SRC)).isZero();
        assertThat(cell.extract(allowed, 1, Actionable.MODULATE, SRC)).isOne();

        // check that replaced filter works instead now that the cell has been emptied
        assertThat(cell.getStatus()).isEqualTo(CellState.EMPTY);
        assertThat(cell.insert(rejected, 1, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.getStatus()).isEqualTo(CellState.NOT_EMPTY);
        assertThat(cell.insert(allowed, 1, Actionable.MODULATE, SRC)).isZero();
    }
}
