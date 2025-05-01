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

import gripe._90.megacells.definition.MEGAComponents;
import gripe._90.megacells.definition.MEGAItems;
import gripe._90.megacells.item.cell.BulkCellInventory;
import gripe._90.megacells.misc.CompressionChain;
import gripe._90.megacells.misc.CompressionService;

@ExtendWith(EphemeralTestServerProvider.class)
public class BulkCellInventoryTest {
    private static final IActionSource SRC = IActionSource.empty();
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

        // ensure variants cannot be inserted or extracted without a card regardless of the chain
        assertThat(((BulkCellInventory) cell).hasCompressionChain()).isTrue();
        assertThat(cell.insert(ingot, 1, Actionable.SIMULATE, SRC)).isOne();
        assertThat(cell.insert(nugget, 1, Actionable.SIMULATE, SRC)).isZero();

        // regression test: ensure that the full amount of an item that happens to be compressible to a higher variant
        // is reported when compression is disabled
        cell.insert(ingot, 64, Actionable.MODULATE, SRC);
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(64);
        cell.extract(ingot, 64, Actionable.MODULATE, SRC);

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
        cell.insert(nugget, 9, Actionable.MODULATE, SRC);
        assertThat(cell.getAvailableStacks().get(nugget)).isZero();
        assertThat(cell.getAvailableStacks().get(ingot)).isOne();

        cell.insert(nugget, 11, Actionable.MODULATE, SRC);
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(2);
        assertThat(cell.getAvailableStacks().get(nugget)).isEqualTo(2);

        // regression testing: ensure units properly flow over when inserting smaller variants in amounts less than
        // the next to's compression factor
        cell.insert(nugget, 8, Actionable.MODULATE, SRC);
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(3);
        assertThat(cell.getAvailableStacks().get(nugget)).isEqualTo(1);

        cell.insert(nugget, 8, Actionable.MODULATE, SRC);
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(4);
        assertThat(cell.getAvailableStacks().get(nugget)).isZero();
        cell.insert(nugget, 1, Actionable.MODULATE, SRC);

        // regression testing: ensure backflow is properly handled when extracting more of a smaller variant than the
        // amount reported for it
        cell.extract(nugget, 8, Actionable.MODULATE, SRC);
        assertThat(cell.getAvailableStacks().get(nugget)).isEqualTo(2);
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(3);

        // ensure only filtered to works again once card is removed
        item.getUpgrades(stack).clear();
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.insert(nugget, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.extract(nugget, 1, Actionable.SIMULATE, SRC)).isZero();
        assertThat(cell.insert(ingot, 1, Actionable.SIMULATE, SRC)).isOne();
        assertThat(cell.extract(ingot, 1, Actionable.SIMULATE, SRC)).isOne();

        // ensure other variants are not reported at all without a card
        assertThat(cell.getAvailableStacks().get(ingot)).isNotZero();
        assertThat(cell.getAvailableStacks().get(nugget)).isZero();
        assertThat(cell.getStatus()).isEqualTo(CellState.NOT_EMPTY);

        // regression testing: ensure bulk cells with compression enabled but storing an item that can't be compressed
        // can't accidentally convert their stored item into any other item when extracted
        //
        // see: https://github.com/62832/MEGACells/issues/183
        item.getUpgrades(stack).addItems(MEGAItems.COMPRESSION_CARD.stack());
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        cell.extract(nugget, MAX, Actionable.MODULATE, SRC); // empty cell before re-partitioning

        var dud = AEItemKey.of(Items.BEDROCK); // uncraftable, therefore cannot be compressed in any way
        item.getConfigInventory(stack).clear();
        item.getConfigInventory(stack).addFilter(dud);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));

        cell.insert(dud, 256, Actionable.MODULATE, SRC);
        assertThat(cell.extract(ingot, 64, Actionable.MODULATE, SRC)).isZero();
        assertThat(cell.getAvailableStacks().get(dud)).isEqualTo(256);
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

        // ensure all variants from the base "stored" item onto the smallest are reported when filter is mismatched,
        // even without a card
        item.getUpgrades(stack).clear();
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isEqualTo(CellState.FULL);
        assertThat(cell.getAvailableStacks().get(nugget)).isOne();
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(10);
        assertThat(cell.getAvailableStacks().get(block)).isZero();

        // ensure all contents are recoverable even without a card
        assertThat(cell.extract(nugget, MAX, Actionable.MODULATE, SRC)).isOne();

        item.getUpgrades(stack).addItems(card);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        cell.extract(ingot, MAX, Actionable.MODULATE, SRC);
        cell.extract(block, MAX, Actionable.MODULATE, SRC);
        assertThat(cell.getStatus()).isEqualTo(CellState.EMPTY);

        // ensure compression works with new filter item after emptying (without needing to recreate the cell inv
        nugget = AEItemKey.of(Items.GOLD_NUGGET);
        block = AEItemKey.of(Items.GOLD_BLOCK);
        assertThat(cell.insert(nugget, 1, Actionable.SIMULATE, SRC)).isOne();
        assertThat(cell.insert(block, 1, Actionable.SIMULATE, SRC)).isOne();
    }

    @Test
    void testCompressionCutoff(MinecraftServer ignored) {
        var nugget = AEItemKey.of(Items.IRON_NUGGET);
        var ingot = AEItemKey.of(Items.IRON_INGOT);
        var block = AEItemKey.of(Items.IRON_BLOCK);

        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();
        item.getUpgrades(stack).addItems(MEGAItems.COMPRESSION_CARD.stack());
        item.getConfigInventory(stack).addFilter(ingot);

        var cell = (BulkCellInventory) Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        cell.insert(nugget, 1, Actionable.MODULATE, SRC);
        cell.insert(ingot, 1, Actionable.MODULATE, SRC);
        cell.insert(block, 1, Actionable.MODULATE, SRC);
        assertThat(cell.getCutoffItem()).isEqualTo(block.getItem());
        assertThat(cell.getAvailableStacks().get(ingot)).isOne();
        assertThat(cell.getAvailableStacks().get(block)).isOne();

        cell.switchCompressionCutoff(false);
        cell = (BulkCellInventory) Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getCutoffItem()).isEqualTo(ingot.getItem());
        assertThat(cell.getAvailableStacks().get(block)).isZero();
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(10);

        cell.switchCompressionCutoff(true);
        cell = (BulkCellInventory) Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getCutoffItem()).isEqualTo(block.getItem());
        assertThat(cell.getAvailableStacks().get(block)).isOne();
        assertThat(cell.getAvailableStacks().get(ingot)).isOne();
    }

    @Test
    void testCompressionPatterns(MinecraftServer ignored) {
        var nugget = AEItemKey.of(Items.IRON_NUGGET);
        var ingot = AEItemKey.of(Items.IRON_INGOT);
        var block = AEItemKey.of(Items.IRON_BLOCK);

        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();
        item.getUpgrades(stack).addItems(MEGAItems.COMPRESSION_CARD.stack());
        item.getConfigInventory(stack).addFilter(ingot);

        var cell = (BulkCellInventory) Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        var patterns = cell.getDecompressionPatterns();

        // first pass: default cutoff (block), both decompression patterns (block → ingot → nugget)
        var firstOutput = patterns.getFirst().getPrimaryOutput();
        assertThat(firstOutput.what()).isEqualTo(nugget);
        assertThat(firstOutput.amount()).isEqualTo(9);
        var firstInput = patterns.getFirst().getInputs()[0].getPossibleInputs()[0];
        assertThat(firstInput.what()).isEqualTo(ingot);
        assertThat(firstInput.amount()).isOne();

        var secondOutput = patterns.getLast().getPrimaryOutput();
        assertThat(secondOutput.what()).isEqualTo(ingot);
        assertThat(secondOutput.amount()).isEqualTo(9);
        var secondInput = patterns.getLast().getInputs()[0].getPossibleInputs()[0];
        assertThat(secondInput.what()).isEqualTo(block);
        assertThat(secondInput.amount()).isOne();

        // second pass: lowered cutoff (ingot)
        // one decompression pattern (ingot → nugget) and one compression (ingot → block)
        cell.switchCompressionCutoff(false);
        patterns = cell.getDecompressionPatterns();

        firstOutput = patterns.getFirst().getPrimaryOutput();
        assertThat(firstOutput.what()).isEqualTo(nugget);
        assertThat(firstOutput.amount()).isEqualTo(9);
        firstInput = patterns.getFirst().getInputs()[0].getPossibleInputs()[0];
        assertThat(firstInput.what()).isEqualTo(ingot);
        assertThat(firstInput.amount()).isOne();

        secondOutput = patterns.getLast().getPrimaryOutput();
        assertThat(secondOutput.what()).isEqualTo(block);
        assertThat(secondOutput.amount()).isOne();
        secondInput = patterns.getLast().getInputs()[0].getPossibleInputs()[0];
        assertThat(secondInput.what()).isEqualTo(ingot);
        assertThat(secondInput.amount()).isEqualTo(9);

        // third pass: lowest cutoff, both compression patterns (nugget → ingot → block)
        cell.switchCompressionCutoff(false);
        patterns = cell.getDecompressionPatterns();

        firstOutput = patterns.getFirst().getPrimaryOutput();
        assertThat(firstOutput.what()).isEqualTo(ingot);
        assertThat(firstOutput.amount()).isOne();
        firstInput = patterns.getFirst().getInputs()[0].getPossibleInputs()[0];
        assertThat(firstInput.what()).isEqualTo(nugget);
        assertThat(firstInput.amount()).isEqualTo(9);

        secondOutput = patterns.getLast().getPrimaryOutput();
        assertThat(secondOutput.what()).isEqualTo(block);
        assertThat(secondOutput.amount()).isOne();
        secondInput = patterns.getLast().getInputs()[0].getPossibleInputs()[0];
        assertThat(secondInput.what()).isEqualTo(ingot);
        assertThat(secondInput.amount()).isEqualTo(9);
    }

    @Test
    void testFilterChangeWithinCompressionChain(MinecraftServer ignored) {
        var nugget = AEItemKey.of(Items.IRON_NUGGET);
        var ingot = AEItemKey.of(Items.IRON_INGOT);

        var item = MEGAItems.BULK_ITEM_CELL.asItem();
        var stack = item.getDefaultInstance();
        item.getUpgrades(stack).addItems(MEGAItems.COMPRESSION_CARD.stack());
        item.getConfigInventory(stack).addFilter(ingot);

        var cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        var ingots = cell.insert(ingot, 2, Actionable.MODULATE, SRC);
        var nuggets = cell.insert(nugget, 3, Actionable.MODULATE, SRC);
        var units = cell.extract(nugget, MAX, Actionable.SIMULATE, SRC);

        // first pass: switch filter to a smaller to
        item.getConfigInventory(stack).clear();
        item.getConfigInventory(stack).addFilter(nugget);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isNotEqualTo(CellState.FULL);
        assertThat(cell.insert(ingot, ingots, Actionable.MODULATE, SRC)).isEqualTo(ingots);
        assertThat(cell.insert(nugget, nuggets, Actionable.MODULATE, SRC)).isEqualTo(nuggets);
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(ingots * 2);
        assertThat(cell.getAvailableStacks().get(nugget)).isEqualTo(nuggets * 2);
        assertThat(cell.extract(nugget, MAX, Actionable.SIMULATE, SRC)).isEqualTo(units * 2);
        assertThat(cell.extract(ingot, ingots, Actionable.MODULATE, SRC)).isEqualTo(ingots);
        assertThat(cell.extract(nugget, nuggets, Actionable.MODULATE, SRC)).isEqualTo(nuggets);

        // second pass: switch filter back to initial to
        item.getConfigInventory(stack).clear();
        item.getConfigInventory(stack).addFilter(ingot);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isNotEqualTo(CellState.FULL);
        assertThat(cell.insert(ingot, ingots, Actionable.MODULATE, SRC)).isEqualTo(ingots);
        assertThat(cell.insert(nugget, nuggets, Actionable.MODULATE, SRC)).isEqualTo(nuggets);
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(ingots * 2);
        assertThat(cell.getAvailableStacks().get(nugget)).isEqualTo(nuggets * 2);
        assertThat(cell.extract(nugget, MAX, Actionable.SIMULATE, SRC)).isEqualTo(units * 2);
        assertThat(cell.extract(ingot, ingots, Actionable.MODULATE, SRC)).isEqualTo(ingots);
        assertThat(cell.extract(nugget, nuggets, Actionable.MODULATE, SRC)).isEqualTo(nuggets);

        // second pass: switch filter to even larger to
        item.getConfigInventory(stack).clear();
        item.getConfigInventory(stack).addFilter(Items.IRON_BLOCK);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isNotEqualTo(CellState.FULL);
        assertThat(cell.insert(ingot, ingots, Actionable.MODULATE, SRC)).isEqualTo(ingots);
        assertThat(cell.insert(nugget, nuggets, Actionable.MODULATE, SRC)).isEqualTo(nuggets);
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(ingots * 2);
        assertThat(cell.getAvailableStacks().get(nugget)).isEqualTo(nuggets * 2);
        assertThat(cell.extract(nugget, MAX, Actionable.SIMULATE, SRC)).isEqualTo(units * 2);
        assertThat(cell.extract(ingot, ingots, Actionable.MODULATE, SRC)).isEqualTo(ingots);
        assertThat(cell.extract(nugget, nuggets, Actionable.MODULATE, SRC)).isEqualTo(nuggets);

        // fourth pass: switch filter to a non-to altogether
        item.getConfigInventory(stack).clear();
        item.getConfigInventory(stack).addFilter(Items.GOLD_INGOT);
        cell = Objects.requireNonNull(StorageCells.getCellInventory(stack, null));
        assertThat(cell.getStatus()).isEqualTo(CellState.FULL);
        assertThat(cell.insert(ingot, ingots, Actionable.MODULATE, SRC)).isZero();
        assertThat(cell.insert(nugget, nuggets, Actionable.MODULATE, SRC)).isZero();
        assertThat(cell.getAvailableStacks().get(ingot)).isEqualTo(ingots);
        assertThat(cell.getAvailableStacks().get(nugget)).isEqualTo(nuggets);
        assertThat(cell.extract(nugget, MAX, Actionable.SIMULATE, SRC)).isEqualTo(nuggets);
    }
}
