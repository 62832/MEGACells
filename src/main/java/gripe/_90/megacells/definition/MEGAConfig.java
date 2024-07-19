package gripe._90.megacells.definition;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class MEGAConfig {
    public static final MEGAConfig CONFIG;
    public static final ModConfigSpec SPEC;

    static {
        var configured = new ModConfigSpec.Builder().configure(MEGAConfig::new);
        CONFIG = configured.getKey();
        SPEC = configured.getValue();
    }

    private final ModConfigSpec.BooleanValue spentNuclearWasteAllowed;

    public MEGAConfig(ModConfigSpec.Builder builder) {
        spentNuclearWasteAllowed = builder.comment(
                        "Whether to allow the Radioactive Chemical Cell to store Spent Nuclear Waste.",
                        "AppMek integration feature.")
                .define("spentNuclearWasteAllowed", false);
    }

    public boolean isSpentWasteAllowed() {
        return spentNuclearWasteAllowed.get();
    }
}
