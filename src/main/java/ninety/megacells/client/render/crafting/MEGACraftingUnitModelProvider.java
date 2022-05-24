package ninety.megacells.client.render.crafting;

import java.util.function.Function;

import appeng.client.render.crafting.LightBakedModel;
import appeng.client.render.crafting.MonitorBakedModel;
import appeng.client.render.crafting.UnitBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;

import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;

import ninety.megacells.block.MEGACraftingUnitType;

public class MEGACraftingUnitModelProvider extends AbstractCraftingUnitModelProvider<MEGACraftingUnitType> {
    public MEGACraftingUnitModelProvider(MEGACraftingUnitType type) {
        super(type);
    }

    @Override
    public TextureAtlasSprite getLightMaterial(Function<Material, TextureAtlasSprite> textureGetter) {
        return switch (this.type) {
            case ACCELERATOR -> textureGetter.apply(MEGACraftingCubeModel.ACCELERATOR_LIGHT);
            case STORAGE_1M -> textureGetter.apply(MEGACraftingCubeModel.STORAGE_1M_LIGHT);
            case STORAGE_4M -> textureGetter.apply(MEGACraftingCubeModel.STORAGE_4M_LIGHT);
            case STORAGE_16M -> textureGetter.apply(MEGACraftingCubeModel.STORAGE_16M_LIGHT);
            case STORAGE_64M -> textureGetter.apply(MEGACraftingCubeModel.STORAGE_64M_LIGHT);
            case STORAGE_256M -> textureGetter.apply(MEGACraftingCubeModel.STORAGE_256M_LIGHT);
            default -> throw new IllegalArgumentException(
                    "Crafting unit type " + this.type + " does not use a light texture.");
        };
    }

    @Override
    public BakedModel getBakedModel(Function<Material, TextureAtlasSprite> spriteGetter, TextureAtlasSprite ringCorner,
            TextureAtlasSprite ringSideHor, TextureAtlasSprite ringSideVer) {
        return switch (this.type) {
            case UNIT -> new UnitBakedModel(ringCorner, ringSideHor, ringSideVer,
                    spriteGetter.apply(MEGACraftingCubeModel.UNIT_BASE));
            case ACCELERATOR, STORAGE_1M, STORAGE_4M, STORAGE_16M, STORAGE_64M, STORAGE_256M -> new LightBakedModel(
                    ringCorner, ringSideHor, ringSideVer, spriteGetter.apply(MEGACraftingCubeModel.LIGHT_BASE),
                    this.getLightMaterial(spriteGetter));
            case MONITOR -> new MonitorBakedModel(ringCorner, ringSideHor, ringSideVer,
                    spriteGetter.apply(MEGACraftingCubeModel.UNIT_BASE),
                    spriteGetter.apply(MEGACraftingCubeModel.MONITOR_BASE),
                    spriteGetter.apply(MEGACraftingCubeModel.MONITOR_LIGHT_DARK),
                    spriteGetter.apply(MEGACraftingCubeModel.MONITOR_LIGHT_MEDIUM),
                    spriteGetter.apply(MEGACraftingCubeModel.MONITOR_LIGHT_BRIGHT));
        };
    }
}
