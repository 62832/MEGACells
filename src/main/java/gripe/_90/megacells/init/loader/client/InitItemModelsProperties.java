package gripe._90.megacells.init.loader.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import appeng.core.AppEng;

import gripe._90.megacells.block.MEGAEnergyCellBlockItem;

@Environment(EnvType.CLIENT)
public class InitItemModelsProperties {
    public static final ResourceLocation ENERGY_FILL_LEVEL_ID = AppEng.makeId("fill_level");

    public static void init() {
        Registry.ITEM.forEach(item -> {
            if (!(item instanceof MEGAEnergyCellBlockItem chargeable)) {
                return;
            }

            ItemProperties.register(chargeable,
                    ENERGY_FILL_LEVEL_ID,
                    (is, level, entity, seed) -> {
                        double curPower = chargeable.getAECurrentPower(is);
                        double maxPower = chargeable.getAEMaxPower(is);

                        return (float) (curPower / maxPower);
                    });
        });
    }
}
