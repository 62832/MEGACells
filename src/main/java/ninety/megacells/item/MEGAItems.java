package ninety.megacells.item;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import ninety.megacells.MEGACells;

import appeng.items.materials.MaterialItem;
import appeng.items.materials.StorageComponentItem;

public class MEGAItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MEGACells.MODID);

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
    }

    // spotless:off
    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MEGACells.MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(MEGAItems.ITEM_CELL_256M.get());
        }
    };

    private static final Item.Properties props = new Item.Properties().tab(CREATIVE_TAB);

    public static final RegistryObject<Item> MEGA_ITEM_CELL_HOUSING = ITEMS.register("mega_item_cell_housing", () -> new MaterialItem(props));
    public static final RegistryObject<Item> MEGA_FLUID_CELL_HOUSING = ITEMS.register("mega_fluid_cell_housing", () -> new MaterialItem(props));

    public static final RegistryObject<Item> CELL_COMPONENT_1M = component(MEGACellTier._1M);
    public static final RegistryObject<Item> CELL_COMPONENT_4M = component(MEGACellTier._4M);
    public static final RegistryObject<Item> CELL_COMPONENT_16M = component(MEGACellTier._16M);
    public static final RegistryObject<Item> CELL_COMPONENT_64M = component(MEGACellTier._64M);
    public static final RegistryObject<Item> CELL_COMPONENT_256M = component(MEGACellTier._256M);

    public static final RegistryObject<Item> ITEM_CELL_1M = cell(CELL_COMPONENT_1M, MEGACellTier._1M, MEGACellType.ITEM);
    public static final RegistryObject<Item> ITEM_CELL_4M = cell(CELL_COMPONENT_4M, MEGACellTier._4M, MEGACellType.ITEM);
    public static final RegistryObject<Item> ITEM_CELL_16M = cell(CELL_COMPONENT_16M, MEGACellTier._16M, MEGACellType.ITEM);
    public static final RegistryObject<Item> ITEM_CELL_64M = cell(CELL_COMPONENT_64M, MEGACellTier._64M, MEGACellType.ITEM);
    public static final RegistryObject<Item> ITEM_CELL_256M = cell(CELL_COMPONENT_256M, MEGACellTier._256M, MEGACellType.ITEM);

    public static final RegistryObject<Item> FLUID_CELL_1M = cell(CELL_COMPONENT_1M, MEGACellTier._1M, MEGACellType.FLUID);
    public static final RegistryObject<Item> FLUID_CELL_4M = cell(CELL_COMPONENT_4M, MEGACellTier._4M, MEGACellType.FLUID);
    public static final RegistryObject<Item> FLUID_CELL_16M = cell(CELL_COMPONENT_16M, MEGACellTier._16M, MEGACellType.FLUID);
    public static final RegistryObject<Item> FLUID_CELL_64M = cell(CELL_COMPONENT_64M, MEGACellTier._64M, MEGACellType.FLUID);
    public static final RegistryObject<Item> FLUID_CELL_256M = cell(CELL_COMPONENT_256M, MEGACellTier._256M, MEGACellType.FLUID);

    public static final RegistryObject<Item> PORTABLE_ITEM_CELL_1M = portable(MEGACellTier._1M, MEGACellType.ITEM);
    public static final RegistryObject<Item> PORTABLE_ITEM_CELL_4M = portable(MEGACellTier._4M, MEGACellType.ITEM);
    public static final RegistryObject<Item> PORTABLE_ITEM_CELL_16M = portable(MEGACellTier._16M, MEGACellType.ITEM);
    public static final RegistryObject<Item> PORTABLE_ITEM_CELL_64M = portable(MEGACellTier._64M, MEGACellType.ITEM);
    public static final RegistryObject<Item> PORTABLE_ITEM_CELL_256M = portable(MEGACellTier._256M, MEGACellType.ITEM);

    public static final RegistryObject<Item> PORTABLE_FLUID_CELL_1M = portable(MEGACellTier._1M, MEGACellType.FLUID);
    public static final RegistryObject<Item> PORTABLE_FLUID_CELL_4M = portable(MEGACellTier._4M, MEGACellType.FLUID);
    public static final RegistryObject<Item> PORTABLE_FLUID_CELL_16M = portable(MEGACellTier._16M, MEGACellType.FLUID);
    public static final RegistryObject<Item> PORTABLE_FLUID_CELL_64M = portable(MEGACellTier._64M, MEGACellType.FLUID);
    public static final RegistryObject<Item> PORTABLE_FLUID_CELL_256M = portable(MEGACellTier._256M, MEGACellType.FLUID);
    // spotless:on

    private static RegistryObject<Item> component(MEGACellTier cellTier) {
        return ITEMS.register("cell_component_" + cellTier.affix,
                () -> new StorageComponentItem(props, cellTier.kbFactor()));
    }

    private static RegistryObject<Item> cell(RegistryObject<Item> component, MEGACellTier tier, MEGACellType type) {
        return ITEMS.register(type.affix + "_storage_cell_" + tier.affix,
                () -> new MEGAStorageCell(props.stacksTo(1), component.get(), tier, type));
    }

    private static RegistryObject<Item> portable(MEGACellTier tier, MEGACellType type) {
        return ITEMS.register("portable_" + type.affix + "_cell_" + tier.affix,
                () -> new MEGAPortableCell(props.stacksTo(1), tier, type));
    }

}
