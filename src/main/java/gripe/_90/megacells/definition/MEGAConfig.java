package gripe._90.megacells.definition;

import net.neoforged.neoforge.common.ModConfigSpec;

public class MEGAConfig {
    public static final MEGAConfig CONFIG;
    public static final ModConfigSpec SPEC;

    private final ModConfigSpec.BooleanValue spentNuclearWasteAllowed;

    public MEGAConfig(ModConfigSpec.Builder builder) {
        spentNuclearWasteAllowed = builder.define("spentNuclearWasteAllowed", false);
    }

    public boolean isSpentWasteAllowed() {
        return spentNuclearWasteAllowed.get();
    }

    static {
        var configured = new ModConfigSpec.Builder().configure(MEGAConfig::new);
        CONFIG = configured.getKey();
        SPEC = configured.getValue();
    }
}
