package gripe._90.megacells.definition;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.blockentity.crafting.CraftingMonitorBlockEntity;
import appeng.blockentity.networking.EnergyCellBlockEntity;
import appeng.core.definitions.BlockDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.entity.MEGAInterfaceBlockEntity;
import gripe._90.megacells.block.entity.MEGAPatternProviderBlockEntity;

@SuppressWarnings({"unused", "unchecked"})
public final class MEGABlockEntities {
    public static void init() {
        // controls static load order
        MEGACells.LOGGER.info("Initialised block entities.");
    }

    private static final Map<ResourceLocation, BlockEntityType<?>> BLOCK_ENTITY_TYPES = new HashMap<>();

    public static Map<ResourceLocation, BlockEntityType<?>> getBlockEntityTypes() {
        return ImmutableMap.copyOf(BLOCK_ENTITY_TYPES);
    }

    public static final BlockEntityType<EnergyCellBlockEntity> MEGA_ENERGY_CELL = create(
            "mega_energy_cell", EnergyCellBlockEntity.class, EnergyCellBlockEntity::new, MEGABlocks.MEGA_ENERGY_CELL);

    public static final BlockEntityType<CraftingBlockEntity> MEGA_CRAFTING_UNIT = create(
            "mega_crafting_unit",
            CraftingBlockEntity.class,
            CraftingBlockEntity::new,
            MEGABlocks.MEGA_CRAFTING_UNIT,
            MEGABlocks.CRAFTING_ACCELERATOR);
    public static final BlockEntityType<CraftingBlockEntity> MEGA_CRAFTING_STORAGE = create(
            "mega_crafting_storage",
            CraftingBlockEntity.class,
            CraftingBlockEntity::new,
            MEGABlocks.CRAFTING_STORAGE_1M,
            MEGABlocks.CRAFTING_STORAGE_4M,
            MEGABlocks.CRAFTING_STORAGE_16M,
            MEGABlocks.CRAFTING_STORAGE_64M,
            MEGABlocks.CRAFTING_STORAGE_256M);
    public static final BlockEntityType<CraftingMonitorBlockEntity> MEGA_CRAFTING_MONITOR = create(
            "mega_crafting_monitor",
            CraftingMonitorBlockEntity.class,
            CraftingMonitorBlockEntity::new,
            MEGABlocks.CRAFTING_MONITOR);

    public static final BlockEntityType<MEGAInterfaceBlockEntity> MEGA_INTERFACE = create(
            "mega_interface", MEGAInterfaceBlockEntity.class, MEGAInterfaceBlockEntity::new, MEGABlocks.MEGA_INTERFACE);
    public static final BlockEntityType<MEGAPatternProviderBlockEntity> MEGA_PATTERN_PROVIDER = create(
            "mega_pattern_provider",
            MEGAPatternProviderBlockEntity.class,
            MEGAPatternProviderBlockEntity::new,
            MEGABlocks.MEGA_PATTERN_PROVIDER);

    @SafeVarargs
    private static <T extends AEBaseBlockEntity> BlockEntityType<T> create(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefinitions) {
        Preconditions.checkArgument(blockDefinitions.length > 0);

        var blocks = Arrays.stream(blockDefinitions).map(BlockDefinition::block).toArray(AEBaseEntityBlock[]::new);

        var typeHolder = new AtomicReference<BlockEntityType<T>>();
        var type = BlockEntityType.Builder.of(
                        (blockPos, blockState) -> factory.create(typeHolder.get(), blockPos, blockState), blocks)
                .build(null);
        typeHolder.set(type); // Makes it available to the supplier used above
        BLOCK_ENTITY_TYPES.put(MEGACells.makeId(id), type);

        AEBaseBlockEntity.registerBlockEntityItem(type, blockDefinitions[0].asItem());

        for (var block : blocks) {
            var baseBlock = (AEBaseEntityBlock<T>) block;
            baseBlock.setBlockEntity(entityClass, type, null, null);
        }

        return type;
    }

    private interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }
}
