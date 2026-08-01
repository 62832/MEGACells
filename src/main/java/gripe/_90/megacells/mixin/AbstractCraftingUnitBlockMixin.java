package gripe._90.megacells.mixin;

import java.util.Objects;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import appeng.block.AEBaseBlock;
import appeng.block.crafting.AbstractCraftingUnitBlock;
import appeng.block.crafting.ICraftingUnitType;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.BlockDefinition;

import gripe._90.megacells.MEGACells;
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

    // spotless:off
    @WrapOperation(
            method = "upgrade",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/recipes/game/CraftingUnitTransformRecipe;getUpgradedBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/level/block/Block;"))
    // spotless:on
    private Block isMegaUpgrade(ServerLevel level, ItemStack heldItem, Operation<Block> original) {
        var upgraded = original.call(level, heldItem);
        return Objects.requireNonNull(getRegistryName())
                        .getNamespace()
                        .equals(BuiltInRegistries.BLOCK.getKey(upgraded).getNamespace())
                ? upgraded
                : null;
    }

    @ModifyReceiver(
            method = "upgrade",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lappeng/core/definitions/BlockDefinition;block()Lnet/minecraft/world/level/block/Block;"))
    private BlockDefinition<?> upgradeMegaUnit(BlockDefinition<?> instance) {
        return Objects.requireNonNull(getRegistryName()).getNamespace().equals(MEGACells.MODID)
                ? MEGABlocks.MEGA_CRAFTING_UNIT
                : instance;
    }
}
