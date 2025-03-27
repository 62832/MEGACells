package gripe._90.megacells.mixin;

import java.util.Objects;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import appeng.block.AEBaseBlock;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.ICraftingUnitType;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.BlockDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;
import gripe._90.megacells.definition.MEGABlocks;

/**
 * Temporary solution to the current crafting unit transform recipe system being hard-coded against AE2's own crafting
 * unit block.
 * <p>
 * See also: {@link CraftingBlockItemMixin}
 */
@Mixin(AbstractCraftingUnitBlock.class)
public abstract class AbstractCraftingUnitBlockMixin extends AEBaseBlock {
    @Shadow
    @Final
    public ICraftingUnitType type;

    protected AbstractCraftingUnitBlockMixin(Properties props) {
        super(props);
    }

    @Shadow
    protected abstract boolean transform(Level level, BlockPos pos, BlockState state);

    // spotless:off
    @ModifyReceiver(
            method = "useWithoutItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/core/definitions/BlockDefinition;block()Lnet/minecraft/world/level/block/Block;"))
    // spotless:on
    private BlockDefinition<?> removeMegaUpgrade(BlockDefinition<?> instance) {
        return Objects.requireNonNull(getRegistryName()).getNamespace().equals(MEGACells.MODID)
                ? MEGABlocks.MEGA_CRAFTING_UNIT
                : AEBlocks.CRAFTING_UNIT;
    }

    @ModifyExpressionValue(
            method = "removeUpgrade",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean isMegaUnit(boolean original) {
        return original || type == MEGACraftingUnitType.UNIT;
    }

    // spotless:off
    @WrapOperation(
            method = "upgrade",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/recipes/game/CraftingUnitTransformRecipe;getUpgradedBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/level/block/Block;"))
    // spotless:on
    private Block isMegaUpgrade(Level level, ItemStack heldItem, Operation<Block> original) {
        var upgraded = original.call(level, heldItem);
        return Objects.requireNonNull(getRegistryName())
                        .getNamespace()
                        .equals(BuiltInRegistries.BLOCK.getKey(upgraded).getNamespace())
                ? upgraded
                : null;
    }

    // spotless:off
    @ModifyExpressionValue(
            method = "upgrade",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/block/crafting/AbstractCraftingUnitBlock;removeUpgrade(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/InteractionResult;"))
    // spotless:on
    private InteractionResult upgradeMegaUnit(
            InteractionResult original,
            @Local(argsOnly = true) BlockState state,
            @Local(argsOnly = true) Level level,
            @Local(argsOnly = true) BlockPos pos,
            @Local(ordinal = 1) BlockState newState) {
        return state.getBlock() == MEGABlocks.MEGA_CRAFTING_UNIT.block()
                ? transform(level, pos, newState) ? InteractionResult.SUCCESS : InteractionResult.FAIL
                : original;
    }
}
