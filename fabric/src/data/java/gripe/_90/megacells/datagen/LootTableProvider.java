package gripe._90.megacells.datagen;

import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;

class LootTableProvider extends SimpleFabricLootTableProvider {
    LootTableProvider(FabricDataOutput output) {
        super(output, LootContextParamSets.BLOCK);
    }

    @Override
    public void generate(@NotNull BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
        for (var block : MEGABlocks.getBlocks()) {
            consumer.accept(
                    MEGACells.makeId("blocks/" + block.id().getPath()),
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(block))
                                    .when(ExplosionCondition.survivesExplosion())));
        }
    }
}
