package gripe._90.megacells.datagen.forge;

import org.jetbrains.annotations.NotNull;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.core.AppEng;

import gripe._90.megacells.util.Utils;

public class PartModelProvider extends ModelProvider<ItemModelBuilder> {
    static final ResourceLocation PATTERN_PROVIDER = AppEng.makeId("part/pattern_provider_base");

    public PartModelProvider(DataGenerator gen, ExistingFileHelper efh) {
        super(gen, Utils.MODID, "part", ItemModelBuilder::new, efh);
        efh.trackGenerated(PATTERN_PROVIDER, MODEL);
    }

    @Override
    public @NotNull String getName() {
        return "Part Models: " + modid;
    }

    @Override
    protected void registerModels() {
        withExistingParent("part/mega_pattern_provider", PATTERN_PROVIDER)
                .texture("sides", Utils.makeId("part/mega_monitor_sides"))
                .texture("sidesStatus", Utils.makeId("part/mega_monitor_sides_status"))
                .texture("back", Utils.makeId("part/mega_monitor_back"))
                .texture("front", Utils.makeId("part/mega_pattern_provider"))
                .texture("particle", Utils.makeId("part/mega_monitor_back"));
    }
}
