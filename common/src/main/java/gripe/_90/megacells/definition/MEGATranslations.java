package gripe._90.megacells.definition;

import appeng.core.localization.LocalizationEnum;

import gripe._90.megacells.MEGACells;

public enum MEGATranslations implements LocalizationEnum {
    AcceleratorThreads("Provides 4 co-processing threads per block.", Type.TOOLTIP),
    ALot("A lot.", Type.TOOLTIP),
    Compression("Compression: %s", Type.TOOLTIP),
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
    NotPartitioned("Not Partitioned", Type.TOOLTIP),

    CompressionChainLimit("Bulk Compression chain limit", Type.CONFIG_OPTION),
    CompressionChainLimitTooltip(
            "The maximum number of variants that a compression-enabled Bulk Cell may report as being stored.",
            Type.CONFIG_TOOLTIP,
            CompressionChainLimit),
    AllowSpentWaste("(AppMek) Allow Spent Nuclear Waste", Type.CONFIG_OPTION),
    AllowSpentWasteTooltip(
            "Whether the MEGA Radioactive Cell should be able to store Spent Nuclear Waste.",
            Type.CONFIG_TOOLTIP,
            AllowSpentWaste);

    private final String englishText;
    private final Type type;
    private final MEGATranslations associated;

    MEGATranslations(String englishText, Type type) {
        this(englishText, type, null);
    }

    MEGATranslations(String englishText, Type type, MEGATranslations associated) {
        this.englishText = englishText;
        this.type = type;
        this.associated = associated;
    }

    @Override
    public String getEnglishText() {
        return englishText;
    }

    @Override
    public String getTranslationKey() {
        return switch (type) {
            case CONFIG_OPTION -> type.root.formatted(MEGACells.MODID) + "." + name();
            case CONFIG_TOOLTIP -> type.root.formatted(MEGACells.MODID) + "." + associated.name() + ".@Tooltip";
            default -> String.format("%s.%s.%s", type.root, MEGACells.MODID, name());
        };
    }

    private enum Type {
        GUI("gui"),
        TOOLTIP("gui.tooltips"),
        CONFIG_OPTION("text.autoconfig.%s.option"),
        CONFIG_TOOLTIP("text.autoconfig.%s.option");

        private final String root;

        Type(String root) {
            this.root = root;
        }
    }
}
