package gripe._90.megacells.datagen;

import gripe._90.megacells.definition.MEGABlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class MEGALootProvider extends LootTableProvider {
    public MEGALootProvider(PackOutput output) {
        super(output, Set.of(), List.of(new SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)));
    }

    private static class BlockLoot extends BlockLootSubProvider {
        protected BlockLoot() {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS);
        }

        @Override
        public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> writer) {
            generate();
            map.forEach(writer);
        }

        @Override
        public void generate() {
            for (var block : MEGABlocks.getBlocks()) {
                add(
                        block.block(),
                        LootTable.lootTable()
                                .withPool(LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(block))
                                        .when(ExplosionCondition.survivesExplosion())));
            }
        }
    }
}