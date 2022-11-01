package gripe._90.megacells.datagen;

import java.io.IOException;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGABlocks;

public class BlockDropProvider extends BlockLoot implements DataProvider {

    private final Path outputFolder;

    public BlockDropProvider(Path outputFolder) {
        this.outputFolder = outputFolder;
    }

    @Override
    public void run(@NotNull CachedOutput cache) throws IOException {
        for (var block : MEGABlocks.getBlocks()) {
            var entry = LootItem.lootTableItem(block.asBlock());
            var pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry)
                    .when(ExplosionCondition.survivesExplosion());

            DataProvider.saveStable(cache,
                    LootTables.serialize(
                            LootTable.lootTable().withPool(pool).setParamSet(LootContextParamSets.BLOCK).build()),
                    outputFolder.resolve(
                            "data/" + MEGACells.MODID + "/loot_tables/blocks/" + block.getId().getPath() + ".json"));
        }
    }

    @Override
    public String getName() {
        return "Block Drops: " + MEGACells.MODID;
    }
}
