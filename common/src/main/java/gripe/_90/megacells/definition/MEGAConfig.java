package gripe._90.megacells.definition;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import gripe._90.megacells.MEGACells;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal"})
@Config(name = MEGACells.MODID)
public class MEGAConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static MEGAConfig INSTANCE;

    @ConfigEntry.BoundedDiscrete(min = 2, max = 12)
    private int CompressionChainLength = 3;

    public int getCompressionChainLength() {
        return CompressionChainLength;
    }

    public static void load() {
        if (INSTANCE != null) {
            throw new IllegalStateException("Config has already been loaded");
        }

        AutoConfig.register(MEGAConfig.class, GsonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(MEGAConfig.class).getConfig();
    }
}
