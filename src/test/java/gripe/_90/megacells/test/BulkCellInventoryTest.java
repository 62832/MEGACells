package gripe._90.megacells.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Items;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.CellState;
import appeng.me.helpers.BaseActionSource;

import gripe._90.megacells.definition.MEGAComponents;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.misc.CompressionChain;
import gripe._90.megacells.misc.CompressionService;

@ExtendWith(EphemeralTestServerProvider.class)
public class BulkCellInventoryTest {
    private static final IActionSource SRC = new BaseActionSource();
    private static final long MAX = Long.MAX_VALUE;

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
        assertThat(cell.insert(content, MAX, Actionable.MODULATE, SRC)).isEqualTo(MAX);
        assertThat(cell.insert(content, MAX, Actionable.MODULATE, SRC)).isEqualTo(MAX);

        var amount = stack.getComponents().get(MEGAComponents.BULK_CELL_UNIT_COUNT);
        assertThat(amount).isEqualTo(BigInteger.valueOf(MAX).multiply(BigInteger.TWO));

        var reported = cell.getAvailableStacks().get(content);
        assertThat(reported).isEqualTo(CompressionChain.STACK_LIMIT);
    }

    @Test
    void testBulkCompression(MinecraftServer ignored) {
        var ingot = AEItemKey.of(Items.IRON_INGOT);
        var chain = CompressionService.getChain(ingot.getItem());
        assertThat(chain.isEmpty()).isFalse();

        var nugget = AEItemKey.of(Items.IRON_NUGGET);
        assertThat(chain.containsVariant(Items.IRON_NUGGET)).isTrue();
        assertThat(CompressionService.getChain(nugget.getItem())).isEqualTo(chain);

        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();
        item.getConfigInventory(stack).addFilter(ingot);
        var cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        cell.insert(ingot, 1, Actionable.MODULATE, SRC);

        // ensure variants cannot be inserted or extracted without a card regardless of the chain
        assertThat(((BulkCellInventory) cell).getCompressionChain()).isEqualTo(chain);
        assertThat(cell.insert(nugget, 1, Actionable.SIMULATE, SRC)).isZero();

        cell.extract(ingot, 1, Actionable.MODULATE, SRC);
        item.getUpgrades(stack).addItems(MEGAItems.COMPRESSION_CARD.stack());
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));

        // ensure other variants are accepted now that there is a card
        assertThat(cell.insert(nugget, 1, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.extract(nugget, 1, Actionable.MODULATE, SRC)).isOne();

        var block = AEItemKey.of(Items.IRON_BLOCK);
        assertThat(cell.insert(block, 1, Actionable.MODULATE, SRC)).isOne();

        // ensure higher variants get "decompressed" into whatever is being extracted
        assertThat(cell.extract(block, MAX, Actionable.SIMULATE, SRC)).isOne();
        assertThat(cell.extract(ingot, MAX, Actionable.SIMULATE, SRC)).isEqualTo(9);
        assertThat(cell.extract(nugget, MAX, Actionable.MODULATE, SRC)).isEqualTo(81);
        assertThat(cell.getStatus()).isEqualTo(CellState.EMPTY);

        // ensure lower variants get appropriately split into higher variants by remainder when reported
        cell.insert(nugget, 11, Actionable.MODULATE, SRC);
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(1);
        assertThat(cell.getAvailableStacks().get(nugget)).isEqualTo(2);

        // ensure only filtered variant works again once card is removed
        item.getUpgrades(stack).clear();
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.insert(nugget, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.extract(nugget, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.insert(ingot, 1, Actionable.SIMULATE, SRC)).isOne();
        assertThat(cell.extract(ingot, 1, Actionable.MODULATE, SRC)).isOne();

        // ensure other variants are not reported at all without a card
        assertThat(cell.getAvailableStacks().get(ingot)).isZero();
        assertThat(cell.getAvailableStacks().get(nugget)).isZero();
        assertThat(cell.getStatus()).isEqualTo(CellState.NOT_EMPTY);
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

        // ensure no-op on insert for items not matching filter
        assertThat(cell.insert(rejected, 1, Actionable.SIMULATE, SRC)).isZero();

        // ensure no-op on any insertion when the cell's filter is cleared, but it still contains an item
        item.getConfigInventory(stack).clear();
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isEqualTo(CellState.FULL);
        assertThat(cell.insert(allowed, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.insert(rejected, 1, Actionable.SIMULATE, SRC)).isZero();

        // ensure contents are recoverable when filter is accidentally cleared
        assertThat(cell.extract(allowed, 1, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.getStatus()).isEqualTo(CellState.EMPTY);

        item.getConfigInventory(stack).addFilter(allowed);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        cell.insert(allowed, 1, Actionable.MODULATE, SRC);

        // ensure no-op on any insertion when filter item does not match already-stored item
        item.getConfigInventory(stack).clear();
        item.getConfigInventory(stack).addFilter(rejected);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isEqualTo(CellState.FULL);
        assertThat(cell.insert(allowed, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.insert(rejected, 1, Actionable.SIMULATE, SRC)).isZero();

        // ensure contents are recoverable and that the correct item is recovered
        assertThat(cell.extract(rejected, 1, Actionable.MODULATE, SRC)).isZero();
        assertThat(cell.extract(allowed, 1, Actionable.MODULATE, SRC)).isOne();

        // ensure that replaced filter works instead now that the cell has been emptied
        assertThat(cell.getStatus()).isEqualTo(CellState.EMPTY);
        assertThat(cell.insert(rejected, 1, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.getStatus()).isEqualTo(CellState.NOT_EMPTY);
        assertThat(cell.insert(allowed, 1, Actionable.SIMULATE, SRC)).isZero();
    }

    @Test
    void testFilterMismatchWithCompression(MinecraftServer ignored) {
        var nugget = AEItemKey.of(Items.IRON_NUGGET);
        var ingot = AEItemKey.of(Items.IRON_INGOT);
        var block = AEItemKey.of(Items.IRON_BLOCK);

        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();
        var card = MEGAItems.COMPRESSION_CARD.stack();
        item.getUpgrades(stack).addItems(card);
        item.getConfigInventory(stack).addFilter(ingot);
        var cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));

        cell.insert(nugget, 1, Actionable.MODULATE, SRC);
        cell.insert(ingot, 1, Actionable.MODULATE, SRC);
        cell.insert(block, 1, Actionable.MODULATE, SRC);

        item.getConfigInventory(stack).clear();
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isEqualTo(CellState.FULL);

        // ensure no-op on insertions for all compression variants already handled
        assertThat(cell.insert(nugget, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.insert(ingot, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.insert(block, 1, Actionable.SIMULATE, SRC)).isZero();

        // ensure items are recoverable only in the exact form the cell reports them in
        assertThat(cell.extract(nugget, MAX, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.extract(ingot, MAX, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.extract(block, MAX, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.getStatus()).isEqualTo(CellState.EMPTY);

        item.getConfigInventory(stack).addFilter(ingot);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        cell.insert(nugget, 1, Actionable.MODULATE, SRC);
        cell.insert(ingot, 1, Actionable.MODULATE, SRC);
        cell.insert(block, 1, Actionable.MODULATE, SRC);

        // ensure no-op on insertions for either variants or wrong filter itm
        item.getConfigInventory(stack).clear();
        var rejected = AEItemKey.of(Items.GOLD_INGOT);
        item.getConfigInventory(stack).addFilter(rejected);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.insert(nugget, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.insert(ingot, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.insert(rejected, 1, Actionable.SIMULATE, SRC)).isZero();

        // ensure all variants are reported when filter is mismatched, even without a card
        item.getUpgrades(stack).clear();
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isEqualTo(CellState.FULL);
        assertThat(cell.getAvailableStacks().get(nugget)).isOne();
        assertThat(cell.getAvailableStacks().get(ingot)).isOne();
        assertThat(cell.getAvailableStacks().get(block)).isOne();

        // ensure variant contents are recoverable even without a card
        assertThat(cell.extract(nugget, MAX, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.extract(ingot, MAX, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.extract(block, MAX, Actionable.MODULATE, SRC)).isOne();
        assertThat(cell.getStatus()).isEqualTo(CellState.EMPTY);

        // ensure compression works with new filter item after emptying
        item.getUpgrades(stack).addItems(card);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        nugget = AEItemKey.of(Items.GOLD_NUGGET);
        block = AEItemKey.of(Items.GOLD_BLOCK);
        assertThat(cell.insert(nugget, 1, Actionable.SIMULATE, SRC)).isOne();
        assertThat(cell.insert(block, 1, Actionable.SIMULATE, SRC)).isOne();
    }
}
