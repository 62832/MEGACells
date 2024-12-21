package gripe._90.megacells.mixin;

import com.llamalad7.mixinextras.injector.ModifyReceiver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.core.registries.BuiltInRegistries;

import appeng.block.crafting.CraftingBlockItem;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.BlockDefinition;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.definition.MEGABlocks;

/**
 * Temporary solution to the current crafting unit transform recipe system being hard-coded against AE2's own crafting
 * unit block.
 * <p>
 * See also: {@link AbstractCraftingUnitBlockMixin}
 */
@Mixin(CraftingBlockItem.class)
public abstract class CraftingBlockItemMixin {
    // spotless:off
    @ModifyReceiver(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/core/definitions/BlockDefinition;stack(I)Lnet/minecraft/world/item/ItemStack;"))
    // spotless:on
    private BlockDefinition<?> placeBackMegaUnit(BlockDefinition<?> instance, int stackSize) {
        return BuiltInRegistries.ITEM
                        .getKey((CraftingBlockItem) (Object) this)
                        .getNamespace()
                        .equals(MEGACells.MODID)
                ? MEGABlocks.MEGA_CRAFTING_UNIT
                : AEBlocks.CRAFTING_UNIT;
    }
}
