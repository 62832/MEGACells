package gripe._90.megacells.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import appeng.block.crafting.CraftingBlockItem;
import appeng.core.definitions.AEBlocks;

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
    @Redirect(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;placeItemBackInInventory(Lnet/minecraft/world/item/ItemStack;)V",
                    ordinal = 1))
    // spotless:on
    private void placeBackMegaUnit(Inventory inv, ItemStack stack, @Local int itemCount) {
        var unit = BuiltInRegistries.ITEM
                        .getKey((CraftingBlockItem) (Object) this)
                        .getNamespace()
                        .equals(MEGACells.MODID)
                ? MEGABlocks.MEGA_CRAFTING_UNIT
                : AEBlocks.CRAFTING_UNIT;
        inv.placeItemBackInInventory(unit.stack(itemCount));
    }
}
