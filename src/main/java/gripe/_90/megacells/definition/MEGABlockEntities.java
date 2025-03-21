package gripe._90.megacells.definition;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.crafting.CraftingBlockEntity;
import appeng.blockentity.crafting.CraftingMonitorBlockEntity;
import appeng.blockentity.networking.EnergyCellBlockEntity;
import appeng.core.definitions.BlockDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.entity.MEGAInterfaceBlockEntity;
import gripe._90.megacells.block.entity.MEGAPatternProviderBlockEntity;
import gripe._90.megacells.integration.Addons;
import gripe._90.megacells.integration.appliede.AppliedEIntegration;
import gripe._90.megacells.integration.appliede.MEGAEMCInterfaceBlock;
import gripe._90.megacells.integration.appliede.MEGAEMCInterfaceBlockEntity;

@SuppressWarnings({"unused", "unchecked"})
public final class MEGABlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> DR =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MEGACells.MODID);

    public static final Supplier<BlockEntityType<EnergyCellBlockEntity>> MEGA_ENERGY_CELL = create(
            "mega_energy_cell", EnergyCellBlockEntity.class, EnergyCellBlockEntity::new, MEGABlocks.MEGA_ENERGY_CELL);

    public static final Supplier<BlockEntityType<CraftingBlockEntity>> MEGA_CRAFTING_UNIT = create(
            "mega_crafting_unit",
            CraftingBlockEntity.class,
            CraftingBlockEntity::new,
            MEGABlocks.MEGA_CRAFTING_UNIT,
            MEGABlocks.CRAFTING_ACCELERATOR);
    public static final Supplier<BlockEntityType<CraftingBlockEntity>> MEGA_CRAFTING_STORAGE = create(
            "mega_crafting_storage",
            CraftingBlockEntity.class,
            CraftingBlockEntity::new,
            MEGABlocks.CRAFTING_STORAGE_1M,
            MEGABlocks.CRAFTING_STORAGE_4M,
            MEGABlocks.CRAFTING_STORAGE_16M,
            MEGABlocks.CRAFTING_STORAGE_64M,
            MEGABlocks.CRAFTING_STORAGE_256M);
    public static final Supplier<BlockEntityType<CraftingMonitorBlockEntity>> MEGA_CRAFTING_MONITOR = create(
            "mega_crafting_monitor",
            CraftingMonitorBlockEntity.class,
            CraftingMonitorBlockEntity::new,
            MEGABlocks.CRAFTING_MONITOR);

    public static final Supplier<BlockEntityType<MEGAInterfaceBlockEntity>> MEGA_INTERFACE = create(
            "mega_interface", MEGAInterfaceBlockEntity.class, MEGAInterfaceBlockEntity::new, MEGABlocks.MEGA_INTERFACE);
    public static final Supplier<BlockEntityType<MEGAPatternProviderBlockEntity>> MEGA_PATTERN_PROVIDER = create(
            "mega_pattern_provider",
            MEGAPatternProviderBlockEntity.class,
            MEGAPatternProviderBlockEntity::new,
            MEGABlocks.MEGA_PATTERN_PROVIDER);

    static {
        if (Addons.APPLIEDE.isLoaded()) {
            if (AppliedEIntegration.MEGA_EMC_INTERFACE_BE == null) {
                AppliedEIntegration.MEGA_EMC_INTERFACE_BE = create(
                        "mega_emc_interface",
                        MEGAEMCInterfaceBlockEntity.class,
                        MEGAEMCInterfaceBlockEntity::new,
                        (BlockDefinition<MEGAEMCInterfaceBlock>) MEGABlocks.MEGA_EMC_INTERFACE);
            }
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @SafeVarargs
    private static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefs) {
        if (blockDefs.length == 0) {
            throw new IllegalArgumentException();
        }

        return DR.register(id, () -> {
            var blocks = Arrays.stream(blockDefs).map(BlockDefinition::block).toArray(AEBaseEntityBlock[]::new);

            var typeHolder = new AtomicReference<BlockEntityType<T>>();
            var type = BlockEntityType.Builder.of((pos, state) -> factory.create(typeHolder.get(), pos, state), blocks)
                    .build(null);
            typeHolder.set(type);

            AEBaseBlockEntity.registerBlockEntityItem(type, blockDefs[0].asItem());

            for (var block : blocks) {
                block.setBlockEntity(entityClass, type, null, null);
            }

            return type;
        });
    }

    private interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }
}
