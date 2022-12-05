package gripe._90.megacells.init.client;

import static appeng.init.client.InitScreens.register;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import appeng.client.gui.implementations.PatternProviderScreen;

import gripe._90.megacells.menu.MEGAPatternProviderMenu;

@Environment(EnvType.CLIENT)
public class InitScreens {
    public static void init(Minecraft client) {
        register(MEGAPatternProviderMenu.TYPE, PatternProviderScreen<MEGAPatternProviderMenu>::new,
                "/screens/megacells/mega_pattern_provider.json");
    }
}
