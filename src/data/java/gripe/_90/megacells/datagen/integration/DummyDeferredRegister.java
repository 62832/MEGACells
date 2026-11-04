package gripe._90.megacells.datagen.integration;

import java.util.function.Function;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * {@link DeferredRegister} wrapper for registering datagen-only dummy objects in the absence of the compat mod required
 * to create the real object.
 * @author <a href=https://github.com/XFactHD/FramedBlocks/blob/26.1/src/datagen/java/io/github/xfacthd/framedblocks/common/datagen/dummy/DummyDeferredRegister.java>XFactHD</a>
 */
public final class DummyDeferredRegister<T> {
    private final DeferredRegister<T> register;
    private final Function<String, Holder<T>> registrar;

    public static DummyDeferredRegister<Block> blocks(String modID) {
        var blocks = DeferredRegister.createBlocks(modID);
        return new DummyDeferredRegister<>(blocks, blocks::registerSimpleBlock);
    }

    public static DummyDeferredRegister<Item> items(String modID) {
        var items = DeferredRegister.createItems(modID);
        return new DummyDeferredRegister<>(items, items::registerSimpleItem);
    }

    private DummyDeferredRegister(DeferredRegister<T> register, Function<String, Holder<T>> registrar) {
        this.register = register;
        this.registrar = registrar;
    }

    public Holder<T> register(String id) {
        if (ModList.get().isLoaded(register.getNamespace())) {
            return DeferredHolder.create(
                    register.getRegistryName(), Identifier.fromNamespaceAndPath(register.getNamespace(), id));
        } else {
            return registrar.apply(id);
        }
    }

    public void register(IEventBus modBus) {
        register.register(modBus);
    }
}
