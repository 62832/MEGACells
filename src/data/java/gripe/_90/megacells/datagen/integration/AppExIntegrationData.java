package gripe._90.megacells.datagen.integration;

import java.util.List;

import net.minecraft.world.level.ItemLike;

import appeng.core.definitions.ItemDefinition;

import es.degrassi.appexp.definition.AExpItems;
import es.degrassi.appexp.item.ExperienceStorageCell;

public class AppExIntegrationData {
    public static final ItemLike EXPERIENCE_CELL_HOUSING = AExpItems.EXPERIENCE_CELL_HOUSING;

    public static List<ItemDefinition<ExperienceStorageCell>> getCells() {
        return AExpItems.getCells();
    }
}
