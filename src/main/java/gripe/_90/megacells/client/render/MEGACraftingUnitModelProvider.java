package gripe._90.megacells.client.render;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;
import appeng.client.render.crafting.LightBakedModel;
import appeng.client.render.crafting.MonitorBakedModel;
import appeng.client.render.crafting.UnitBakedModel;
import appeng.core.AppEng;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.block.MEGACraftingUnitType;

public class MEGACraftingUnitModelProvider extends AbstractCraftingUnitModelProvider<MEGACraftingUnitType>
        implements ModelDebugName {
    private static final Material RING_CORNER = texture("ring_corner");
    private static final Material RING_SIDE_HOR = texture("ring_side_hor");
    private static final Material RING_SIDE_VER = texture("ring_side_ver");
    private static final Material UNIT_BASE = texture("unit_base");
    private static final Material LIGHT_BASE = texture("light_base");
    private static final Material ACCELERATOR_LIGHT = texture("accelerator_light");
    private static final Material STORAGE_1M_LIGHT = texture("1m_storage_light");
    private static final Material STORAGE_4M_LIGHT = texture("4m_storage_light");
    private static final Material STORAGE_16M_LIGHT = texture("16m_storage_light");
    private static final Material STORAGE_64M_LIGHT = texture("64m_storage_light");
    private static final Material STORAGE_256M_LIGHT = texture("256m_storage_light");
    private static final Material MONITOR_BASE = texture("monitor_base");
    private static final Material MONITOR_LIGHT_DARK = monitorLight("dark");
    private static final Material MONITOR_LIGHT_MEDIUM = monitorLight("medium");
    private static final Material MONITOR_LIGHT_BRIGHT = monitorLight("bright");

    public MEGACraftingUnitModelProvider(MEGACraftingUnitType type) {
        super(type);
    }

    private Material.Baked getLightMaterial(MaterialBaker materialBaker) {
        return switch (type) {
            case ACCELERATOR -> materialBaker.get(ACCELERATOR_LIGHT, this);
            case STORAGE_1M -> materialBaker.get(STORAGE_1M_LIGHT, this);
            case STORAGE_4M -> materialBaker.get(STORAGE_4M_LIGHT, this);
            case STORAGE_16M -> materialBaker.get(STORAGE_16M_LIGHT, this);
            case STORAGE_64M -> materialBaker.get(STORAGE_64M_LIGHT, this);
            case STORAGE_256M -> materialBaker.get(STORAGE_256M_LIGHT, this);
            default ->
                throw new IllegalArgumentException("Crafting unit type " + type + " does not use a light texture.");
        };
    }

    @Override
    public BlockStateModel bake(MaterialBaker materialBaker) {
        var ringCorner = materialBaker.get(RING_CORNER, this);
        var ringSideHor = materialBaker.get(RING_SIDE_HOR, this);
        var ringSideVer = materialBaker.get(RING_SIDE_VER, this);

        return switch (type) {
            case UNIT -> new UnitBakedModel(ringCorner, ringSideHor, ringSideVer, materialBaker.get(UNIT_BASE, this));
            case ACCELERATOR, STORAGE_1M, STORAGE_4M, STORAGE_16M, STORAGE_64M, STORAGE_256M ->
                new LightBakedModel(
                        ringCorner,
                        ringSideHor,
                        ringSideVer,
                        materialBaker.get(LIGHT_BASE, this),
                        getLightMaterial(materialBaker));
            case MONITOR ->
                new MonitorBakedModel(
                        ringCorner,
                        ringSideHor,
                        ringSideVer,
                        materialBaker.get(UNIT_BASE, this),
                        materialBaker.get(MONITOR_BASE, this),
                        materialBaker.get(MONITOR_LIGHT_DARK, this),
                        materialBaker.get(MONITOR_LIGHT_MEDIUM, this),
                        materialBaker.get(MONITOR_LIGHT_BRIGHT, this));
        };
    }

    private static Material texture(String name) {
        return new Material(MEGACells.makeId("block/crafting/" + name));
    }

    private static Material monitorLight(String suffix) {
        return new Material(AppEng.makeId("block/crafting/monitor_light_" + suffix));
    }

    @Override
    public String debugName() {
        return getClass().toString();
    }

    public record Unbaked(MEGACraftingUnitType type) implements CustomUnbakedBlockStateModel {
        public static final Identifier ID = MEGACells.makeId("crafting_cube");
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        MEGACraftingUnitType.CODEC.fieldOf("unit_type").forGetter(Unbaked::type))
                .apply(instance, Unbaked::new));

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            return new MEGACraftingUnitModelProvider(type).bake(baker.materials());
        }

        @Override
        public void resolveDependencies(Resolver resolver) {}

        @Override
        public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
            return MAP_CODEC;
        }
    }
}
