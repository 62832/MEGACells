package gripe._90.megacells.init.forge.client;

import static appeng.init.client.InitScreens.register;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import appeng.client.gui.implementations.PatternProviderScreen;

import gripe._90.megacells.menu.MEGAPatternProviderMenu;

public class InitScreens {
    public static void init() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(InitScreens::initScreens);
    }

    private static void initScreens(FMLClientSetupEvent event) {
        register(MEGAPatternProviderMenu.TYPE, PatternProviderScreen<MEGAPatternProviderMenu>::new,
                "/screens/megacells/mega_pattern_provider.json");
    }
}
