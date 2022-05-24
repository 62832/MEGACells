package ninety.megacells.client.render.crafting;

import java.util.function.Function;
import java.util.stream.Stream;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;

import appeng.client.render.BasicUnbakedModel;
import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;

import ninety.megacells.MEGACells;

public class MEGACraftingCubeModel implements BasicUnbakedModel<MEGACraftingCubeModel> {

    protected static final Material RING_CORNER = texture("ring_corner");
    protected static final Material RING_SIDE_HOR = texture("ring_side_hor");
    protected static final Material RING_SIDE_VER = texture("ring_side_ver");
    protected static final Material UNIT_BASE = texture("unit_base");
    protected static final Material LIGHT_BASE = texture("light_base");
    protected static final Material ACCELERATOR_LIGHT = texture("accelerator_light");
    protected static final Material STORAGE_1M_LIGHT = texture("1m_storage_light");
    protected static final Material STORAGE_4M_LIGHT = texture("4m_storage_light");
    protected static final Material STORAGE_16M_LIGHT = texture("16m_storage_light");
    protected static final Material STORAGE_64M_LIGHT = texture("64m_storage_light");
    protected static final Material STORAGE_256M_LIGHT = texture("256m_storage_light");
    protected static final Material MONITOR_BASE = texture("monitor_base");
    protected static final Material MONITOR_LIGHT_DARK = texture("monitor_light_dark");
    protected static final Material MONITOR_LIGHT_MEDIUM = texture("monitor_light_medium");
    protected static final Material MONITOR_LIGHT_BRIGHT = texture("monitor_light_bright");
    private final AbstractCraftingUnitModelProvider<?> renderer;

    public MEGACraftingCubeModel(AbstractCraftingUnitModelProvider<?> renderer) {
        this.renderer = renderer;
    }

    @Override
    public Stream<Material> getAdditionalTextures() {
        return Stream.of(RING_CORNER, RING_SIDE_HOR, RING_SIDE_VER, UNIT_BASE, LIGHT_BASE, ACCELERATOR_LIGHT,
                STORAGE_1M_LIGHT, STORAGE_4M_LIGHT, STORAGE_16M_LIGHT, STORAGE_64M_LIGHT, STORAGE_256M_LIGHT,
                MONITOR_BASE, MONITOR_LIGHT_DARK, MONITOR_LIGHT_MEDIUM, MONITOR_LIGHT_BRIGHT);
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery,
            Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides,
            ResourceLocation modelLocation) {
        TextureAtlasSprite ringCorner = spriteGetter.apply(RING_CORNER);
        TextureAtlasSprite ringSideHor = spriteGetter.apply(RING_SIDE_HOR);
        TextureAtlasSprite ringSideVer = spriteGetter.apply(RING_SIDE_VER);

        return this.renderer.getBakedModel(spriteGetter, ringCorner, ringSideHor, ringSideVer);
    }

    private static Material texture(String name) {
        return new Material(TextureAtlas.LOCATION_BLOCKS, MEGACells.makeId("block/crafting/" + name));
    }
}
