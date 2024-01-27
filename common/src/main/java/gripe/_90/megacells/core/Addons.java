package gripe._90.megacells.core;

public enum Addons {
    APPMEK("appmek"),
    APPBOT("appbot"),
    ARSENG("arseng"),
    AE2WTLIB("ae2wtlib");

    private final String modId;

    Addons(String modId) {
        this.modId = modId;
    }

    public String getModId() {
        return modId;
    }
}
