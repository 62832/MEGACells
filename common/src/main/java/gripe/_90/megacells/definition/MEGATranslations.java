package gripe._90.megacells.definition;

import appeng.core.localization.LocalizationEnum;

import gripe._90.megacells.util.Utils;

public enum MEGATranslations implements LocalizationEnum {
    AcceleratorThreads("Provides 4 co-processing threads per block.", Type.TOOLTIP),
    Compression("Compression: %s", Type.TOOLTIP),
    Contains("Contains: %s", Type.TOOLTIP),
    Disabled("Disabled", Type.TOOLTIP),
    Empty("Empty", Type.TOOLTIP),
    Enabled("Enabled", Type.TOOLTIP),
    FilterChemicalUnsupported("Filter chemical unsupported!", Type.TOOLTIP),
    MismatchedFilter("Mismatched filter!", Type.TOOLTIP),
    PartitionedFor("Partitioned for: %s", Type.TOOLTIP),
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
        return String.format("%s.%s.%s", type.getRoot(), Utils.MODID, this.name());
    }

    private enum Type {
        GUI("gui"),
        TOOLTIP("gui.tooltips");

        private final String root;

        Type(String root) {
            this.root = root;
        }

        private String getRoot() {
            return root;
        }
    }
}
