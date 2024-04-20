package gripe._90.megacells.forge;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import gripe._90.megacells.MEGACells;
import gripe._90.megacells.core.Addons;
import gripe._90.megacells.integration.appmek.AppMekIntegration;
import gripe._90.megacells.integration.arseng.ArsEngIntegration;

@Mod(MEGACells.MODID)
public class MEGACellsForge {
    public MEGACellsForge() {
        MEGAConfig.load();
        MEGACells.initCommon();

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.APPMEK)) {
            AppMekIntegration.init();
        }

        if (MEGACells.PLATFORM.isAddonLoaded(Addons.ARSENG)) {
            ArsEngIntegration.init();
        }
    }
}
