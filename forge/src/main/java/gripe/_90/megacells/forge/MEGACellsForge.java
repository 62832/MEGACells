package gripe._90.megacells.forge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.integration.appmek.AppMekIntegration;

@Mod(MEGACells.MODID)
public class MEGACellsForge {
    public MEGACellsForge() {
        MEGACells.initCommon();

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            AppMekIntegration.init();
        }

        if (FMLEnvironment.dist.isClient()) {
            MEGACells.Client.initClient();
        }
    }
}
