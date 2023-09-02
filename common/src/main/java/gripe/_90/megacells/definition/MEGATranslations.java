package gripe._90.megacells.definition;

import appeng.core.localization.LocalizationEnum;

import gripe._90.megacells.MEGACells;

public enum MEGATranslations implements LocalizationEnum {
    AcceleratorThreads("Provides 4 co-processing threads per block.", Type.TOOLTIP),
    ALot("A lot.", Type.TOOLTIP),
    Compression("Compression: %s", Type.TOOLTIP),
    CompressionChainLength("Bulk Compression chain length", Type.CONFIG),
    Contains("Contains: %s", Type.TOOLTIP),
    Disabled("Disabled", Type.TOOLTIP),
    Empty("Empty", Type.TOOLTIP),
    Enabled("Enabled", Type.TOOLTIP),
    FilterChemicalUnsupported("Filter chemical unsupported!", Type.TOOLTIP),
    MismatchedFilter("Mismatched filter!", Type.TOOLTIP),
    ModName("MEGA Cells", Type.GUI),
    PartitionedFor("Partitioned for: %s", Type.TOOLTIP),
    ProcessingOnly("Supports processing patterns only.", Type.TOOLTIP),
    Quantity("Quantity: %s", Type.TOOLTIP),
    NotPartitioned("Not Partitioned", Type.TOOLTIP);

    private final String englishText;
    private final Type type;

    MEGATranslations(String englishText, Type type) {
        this.englishText = englishText;
        this.type = type;
    }

    @Override
    public String getEnglishText() {
        return englishText;
    }

    @Override
    public String getTranslationKey() {
        return type == Type.CONFIG
                ? type.root.formatted(MEGACells.MODID) + "." + name()
                : String.format("%s.%s.%s", type.root, MEGACells.MODID, name());
    }

    private enum Type {
        GUI("gui"),
        TOOLTIP("gui.tooltips"),
        CONFIG("text.autoconfig.%s.option");

        private final String root;

        Type(String root) {
            this.root = root;
        }
    }
}
