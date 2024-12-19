package gripe._90.megacells.mixin;

import java.util.Objects;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import appeng.block.AEBaseBlock;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.ICraftingUnitType;
import appeng.core.definitions.AEBlocks;
import appeng.recipes.AERecipeTypes;

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
    public abstract InteractionResult removeUpgrade(Level level, Player player, BlockPos pos, BlockState newState);

    @Shadow
    protected abstract boolean transform(Level level, BlockPos pos, BlockState state);

    // spotless:off
    @Redirect(
            method = "useWithoutItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/block/crafting/AbstractCraftingUnitBlock;removeUpgrade(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/InteractionResult;"))
    // spotless:on
    private InteractionResult removeMegaUpgrade(
            AbstractCraftingUnitBlock<?> instance, Level level, Player player, BlockPos pos, BlockState newState) {
        var unitBlock = Objects.requireNonNull(instance.getRegistryName())
                        .getNamespace()
                        .equals(MEGACells.MODID)
                ? MEGABlocks.MEGA_CRAFTING_UNIT
                : AEBlocks.CRAFTING_UNIT;
        return removeUpgrade(level, player, pos, unitBlock.block().defaultBlockState());
    }

    @ModifyExpressionValue(
            method = "removeUpgrade",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z"))
    private boolean isMegaUnit(boolean original) {
        return original || type == MEGACraftingUnitType.UNIT;
    }

    // spotless:off
    @Redirect(
            method = "upgrade",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/recipes/game/CraftingUnitTransformRecipe;getUpgradedBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/level/block/Block;"))
    // spotless:on
    private Block isMegaUpgrade(Level level, ItemStack heldItem) {
        for (var holder : level.getRecipeManager().getAllRecipesFor(AERecipeTypes.CRAFTING_UNIT_TRANSFORM)) {
            if (heldItem.is(holder.value().getUpgradeItem())) {
                var upgraded = holder.value().getUpgradedBlock();

                if (BuiltInRegistries.BLOCK
                        .getKey((AbstractCraftingUnitBlock<?>) (Object) this)
                        .getNamespace()
                        .equals(BuiltInRegistries.BLOCK.getKey(upgraded).getNamespace())) {
                    return upgraded;
                }
            }
        }

        return null;
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
            @Local(name = "newState") BlockState newState) {
        return state.getBlock() == MEGABlocks.MEGA_CRAFTING_UNIT.block()
                ? transform(level, pos, newState) ? InteractionResult.SUCCESS : InteractionResult.FAIL
                : original;
    }
}
