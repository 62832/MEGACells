package gripe._90.megacells.datagen.integration;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;

public class DummyObjects {
    private static final DummyDeferredRegister<Item> MEKANISM = DummyDeferredRegister.items("mekanism");
    private static final DummyDeferredRegister<Item> MEKGENERATORS = DummyDeferredRegister.items("mekanismgenerators");

    public static final Holder<Item> HDPE_SHEET = MEKANISM.register("hdpe_sheet");
    public static final Holder<Item> POLONIUM_PELLET = MEKANISM.register("pellet_polonium");
    public static final Holder<Item> RADIOACTIVE_WASTE_BARREL = MEKANISM.register("radioactive_waste_barrel");
    public static final Holder<Item> REACTOR_GLASS = MEKGENERATORS.register("reactor_glass");

    private static final DummyDeferredRegister<Item> APPLIEDE = DummyDeferredRegister.items("appliede");
    public static final Holder<Item> EMC_INTERFACE = APPLIEDE.register("emc_interface");

    public static void init(IEventBus modBus) {
        MEKANISM.register(modBus);
        MEKGENERATORS.register(modBus);
        APPLIEDE.register(modBus);
    }

    private DummyObjects() {}
}
