package ninety.megacells.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import appeng.items.materials.MaterialItem;
import appeng.items.materials.StorageComponentItem;

import ninety.megacells.MEGACells;
import ninety.megacells.integration.appmek.AppMekIntegration;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.item.util.IMEGACellType;
import ninety.megacells.item.util.MEGACellTier;
import ninety.megacells.item.util.MEGACellType;

public final class MEGAItems {

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    // spotless:off
    public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(MEGACells.MODID) {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(MEGAItems.ITEM_CELL_256M);
        }
    };

    public static final ItemDefinition<MaterialItem> MEGA_ITEM_CELL_HOUSING = item("mega_item_cell_housing", MaterialItem::new, true);
    public static final ItemDefinition<MaterialItem> MEGA_FLUID_CELL_HOUSING = item("mega_fluid_cell_housing", MaterialItem::new, true);
    public static final ItemDefinition<MaterialItem> MEGA_CHEMICAL_CELL_HOUSING = item("mega_chemical_cell_housing", MaterialItem::new, AppMekIntegration.isAppMekLoaded());

    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_1M = component(MEGACellTier._1M);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_4M = component(MEGACellTier._4M);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_16M = component(MEGACellTier._16M);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_64M = component(MEGACellTier._64M);
    public static final ItemDefinition<StorageComponentItem> CELL_COMPONENT_256M = component(MEGACellTier._256M);

    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_1M = cell(MEGACellTier._1M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_4M = cell(MEGACellTier._4M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_16M = cell(MEGACellTier._16M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_64M = cell(MEGACellTier._64M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAStorageCell> ITEM_CELL_256M = cell(MEGACellTier._256M, MEGACellType.ITEM);

    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_1M = cell(MEGACellTier._1M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_4M = cell(MEGACellTier._4M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_16M = cell(MEGACellTier._16M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_64M = cell(MEGACellTier._64M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAStorageCell> FLUID_CELL_256M = cell(MEGACellTier._256M, MEGACellType.FLUID);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_1M = portable(MEGACellTier._1M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_4M = portable(MEGACellTier._4M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_16M = portable(MEGACellTier._16M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_64M = portable(MEGACellTier._64M, MEGACellType.ITEM);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_ITEM_CELL_256M = portable(MEGACellTier._256M, MEGACellType.ITEM);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_1M = portable(MEGACellTier._1M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_4M = portable(MEGACellTier._4M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_16M = portable(MEGACellTier._16M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_64M = portable(MEGACellTier._64M, MEGACellType.FLUID);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_FLUID_CELL_256M = portable(MEGACellTier._256M, MEGACellType.FLUID);

    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_1M = cell(MEGACellTier._1M, ChemicalCellType.TYPE);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_4M = cell(MEGACellTier._4M, ChemicalCellType.TYPE);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_16M = cell(MEGACellTier._16M, ChemicalCellType.TYPE);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_64M = cell(MEGACellTier._64M, ChemicalCellType.TYPE);
    public static final ItemDefinition<MEGAStorageCell> CHEMICAL_CELL_256M = cell(MEGACellTier._256M, ChemicalCellType.TYPE);

    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_1M = portable(MEGACellTier._1M, ChemicalCellType.TYPE);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_4M = portable(MEGACellTier._4M, ChemicalCellType.TYPE);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_16M = portable(MEGACellTier._16M, ChemicalCellType.TYPE);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_64M = portable(MEGACellTier._64M, ChemicalCellType.TYPE);
    public static final ItemDefinition<MEGAPortableCell> PORTABLE_CHEMICAL_CELL_256M = portable(MEGACellTier._256M, ChemicalCellType.TYPE);
    // spotless:on

    private static ItemDefinition<StorageComponentItem> component(MEGACellTier tier) {
        return item("cell_component_" + tier.affix, p -> new StorageComponentItem(p, tier.kbFactor()), true);
    }

    private static ItemDefinition<MEGAStorageCell> cell(MEGACellTier tier, IMEGACellType type) {
        return item(type.affix() + "_storage_cell_" + tier.affix,
                p -> new MEGAStorageCell(p.stacksTo(1), tier, type),
                type != ChemicalCellType.TYPE || AppMekIntegration.isAppMekLoaded());
    }

    private static ItemDefinition<MEGAPortableCell> portable(MEGACellTier tier, IMEGACellType type) {
        return item("portable_" + type.affix() + "_cell_" + tier.affix,
                p -> new MEGAPortableCell(p.stacksTo(1), tier, type),
                type != ChemicalCellType.TYPE || AppMekIntegration.isAppMekLoaded());
    }

    private static <T extends Item> ItemDefinition<T> item(String id, Function<Item.Properties, T> factory,
            boolean register) {
        if (register) {
            Item.Properties p = new Item.Properties().tab(CREATIVE_TAB);
            T item = factory.apply(p);

            ItemDefinition<T> definition = new ItemDefinition<>(MEGACells.makeId(id), item);
            ITEMS.add(definition);

            return definition;
        }
        return null;
    }

    public static String getItemPath(Item item) {
        return Objects.requireNonNull(item.getRegistryName()).getPath();
    }

    public static class ItemDefinition<T extends Item> implements ItemLike {

        private final ResourceLocation id;
        private final T item;

        ItemDefinition(ResourceLocation id, T item) {
            Objects.requireNonNull(id);
            this.id = id;
            this.item = item;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public T asItem() {
            return item;
        }
    }
}
