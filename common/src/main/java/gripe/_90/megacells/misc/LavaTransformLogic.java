package gripe._90.megacells.misc;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import appeng.recipes.transform.TransformRecipe;

public final class LavaTransformLogic {
    private static final Set<Item> lavaCache = new HashSet<>();

    public static boolean canTransformInLava(ItemEntity entity) {
        return getLavaTransformableItems(entity.level())
                .contains(entity.getItem().getItem());
    }

    @SuppressWarnings("resource")
    public static boolean allIngredientsPresent(ItemEntity entity) {
        var x = entity.getX();
        var y = entity.getY();
        var z = entity.getZ();
        var level = entity.level();

        var items = level.getEntities(null, new AABB(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1)).stream()
                .filter(e -> e instanceof ItemEntity && !e.isRemoved())
                .map(e -> ((ItemEntity) e).getItem().getItem())
                .toList();

        for (var recipe : level.getRecipeManager().getAllRecipesFor(TransformRecipe.TYPE)) {
            if (recipe.circumstance.isFluidTag(FluidTags.LAVA)) {
                return recipe.getIngredients().stream().noneMatch(ingredient -> {
                    for (var stack : ingredient.getItems()) {
                        if (items.contains(stack.getItem())) {
                            return false;
                        }
                    }

                    return true;
                });
            }
        }

        return false;
    }

    @SuppressWarnings("SameReturnValue")
    private static Set<Item> getLavaTransformableItems(Level level) {
        if (lavaCache.isEmpty()) {
            for (var recipe : level.getRecipeManager().getAllRecipesFor(TransformRecipe.TYPE)) {
                if (!recipe.circumstance.isFluidTag(FluidTags.LAVA)) {
                    continue;
                }

                for (var ingredient : recipe.ingredients) {
                    for (var stack : ingredient.getItems()) {
                        lavaCache.add(stack.getItem());
                    }

                    // Don't break here unlike AE2's TransformLogic, otherwise non-processed items will burn up
                }
            }
        }

        return lavaCache;
    }

    public static void clearCache() {
        lavaCache.clear();
    }
}
