package gripe._90.megacells.client.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;

import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;
import appeng.client.render.crafting.LightBakedModel;
import appeng.client.render.crafting.MonitorBakedModel;
import appeng.client.render.crafting.UnitBakedModel;
import appeng.core.AppEng;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;

public class MEGACraftingUnitModelProvider extends AbstractCraftingUnitModelProvider<MEGACraftingUnitType> {
    private static final List<Material> MATERIALS = new ArrayList<>();

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
    protected static final Material MONITOR_LIGHT_DARK = monitorLight("dark");
    protected static final Material MONITOR_LIGHT_MEDIUM = monitorLight("medium");
    protected static final Material MONITOR_LIGHT_BRIGHT = monitorLight("bright");

    public MEGACraftingUnitModelProvider(MEGACraftingUnitType type) {
        super(type);
    }

    @Override
    public List<Material> getMaterials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    public TextureAtlasSprite getLightMaterial(Function<Material, TextureAtlasSprite> textureGetter) {
        return switch (this.type) {
            case ACCELERATOR -> textureGetter.apply(ACCELERATOR_LIGHT);
            case STORAGE_1M -> textureGetter.apply(STORAGE_1M_LIGHT);
            case STORAGE_4M -> textureGetter.apply(STORAGE_4M_LIGHT);
            case STORAGE_16M -> textureGetter.apply(STORAGE_16M_LIGHT);
            case STORAGE_64M -> textureGetter.apply(STORAGE_64M_LIGHT);
            case STORAGE_256M -> textureGetter.apply(STORAGE_256M_LIGHT);
            default -> throw new IllegalArgumentException(
                    "Crafting unit type " + this.type + " does not use a light texture.");
        };
    }

    @Override
    public BakedModel getBakedModel(Function<Material, TextureAtlasSprite> spriteGetter) {
        TextureAtlasSprite ringCorner = spriteGetter.apply(RING_CORNER);
        TextureAtlasSprite ringSideHor = spriteGetter.apply(RING_SIDE_HOR);
        TextureAtlasSprite ringSideVer = spriteGetter.apply(RING_SIDE_VER);

        return switch (this.type) {
            case UNIT -> new UnitBakedModel(ringCorner, ringSideHor, ringSideVer, spriteGetter.apply(UNIT_BASE));
            case ACCELERATOR, STORAGE_1M, STORAGE_4M, STORAGE_16M, STORAGE_64M, STORAGE_256M -> new LightBakedModel(
                    ringCorner,
                    ringSideHor,
                    ringSideVer,
                    spriteGetter.apply(LIGHT_BASE),
                    this.getLightMaterial(spriteGetter));
            case MONITOR -> new MonitorBakedModel(
                    ringCorner,
                    ringSideHor,
                    ringSideVer,
                    spriteGetter.apply(UNIT_BASE),
                    spriteGetter.apply(MONITOR_BASE),
                    spriteGetter.apply(MONITOR_LIGHT_DARK),
                    spriteGetter.apply(MONITOR_LIGHT_MEDIUM),
                    spriteGetter.apply(MONITOR_LIGHT_BRIGHT));
        };
    }

    private static Material texture(String name) {
        var material = new Material(InventoryMenu.BLOCK_ATLAS, MEGACells.makeId("block/crafting/" + name));
        MATERIALS.add(material);
        return material;
    }

    private static Material monitorLight(String suffix) {
        var material = new Material(InventoryMenu.BLOCK_ATLAS, AppEng.makeId("block/crafting/monitor_light_" + suffix));
        MATERIALS.add(material);
        return material;
    }
}
