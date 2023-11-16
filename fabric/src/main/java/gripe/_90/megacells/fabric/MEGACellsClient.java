package gripe._90.megacells.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import appeng.api.IAEAddonEntrypoint;

import gripe._90.megacells.MEGACells;

@Environment(EnvType.CLIENT)
public class MEGACellsClient implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        MEGACells.Client.initClient();
    }
}
