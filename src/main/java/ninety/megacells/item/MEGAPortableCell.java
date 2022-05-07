package ninety.megacells.item;

import java.util.Objects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import ninety.megacells.util.MEGACellsUtil;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.PortableCellItem;

public class MEGAPortableCell extends PortableCellItem {

    public final MEGACellTier tier;
    public final MEGACellType type;

    public MEGAPortableCell(Properties props, MEGACellTier tier, MEGACellType type) {
        super(type.key, type.portableMenu, makePortableTier(tier), props);
        this.tier = tier;
        this.type = type;
    }

    private static StorageTier makePortableTier(MEGACellTier tier) {
        return new StorageTier(tier.affix, 512 * tier.kbFactor(), 9 + 9 * tier.index, 2048 * tier.index,
                tier::getComponent);
    }

    @Override
    public ResourceLocation getRecipeId() {
        return MEGACellsUtil.makeId("cells/portable/" + Objects.requireNonNull(getRegistryName()).getPath());
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return super.getChargeRate(stack) * 8;
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        return super.getAEMaxPower(stack) * 8;
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 2, this::onUpgradesChanged);
    }

    private void onUpgradesChanged(ItemStack stack, IUpgradeInventory upgrades) {
        var energyCards = upgrades.getInstalledUpgrades(AEItems.ENERGY_CARD);
        setAEMaxPowerMultiplier(stack, 1 + energyCards);
    }

}
