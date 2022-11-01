package gripe._90.megacells.init.client.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import appeng.block.networking.EnergyCellBlockItem;
import appeng.core.AppEng;

@Environment(EnvType.CLIENT)
public class InitItemModelsProperties {
    public static final ResourceLocation ENERGY_FILL_LEVEL_ID = AppEng.makeId("fill_level");

    public static void init() {
        Registry.ITEM.forEach(item -> {
            if (!(item instanceof EnergyCellBlockItem energyCell)) {
                return;
            }

            ItemProperties.register(energyCell,
                    ENERGY_FILL_LEVEL_ID,
                    (is, level, entity, seed) -> {
                        double curPower = energyCell.getAECurrentPower(is);
                        double maxPower = energyCell.getAEMaxPower(is);

                        return (float) (curPower / maxPower);
                    });
        });
    }
}
