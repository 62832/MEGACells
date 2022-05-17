package ninety.megacells.item;

import java.util.List;
import java.util.Objects;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;
import appeng.items.tools.powered.PortableCellItem;

import ninety.megacells.MEGACells;
import ninety.megacells.integration.appmek.AppMekIntegration;
import ninety.megacells.integration.appmek.ChemicalCellType;
import ninety.megacells.item.util.IMEGACellType;
import ninety.megacells.item.util.MEGACellTier;

public class MEGAPortableCell extends PortableCellItem {

    public final MEGACellTier tier;
    public final IMEGACellType type;

    public MEGAPortableCell(Properties props, MEGACellTier tier, IMEGACellType type) {
        super(type.keyType(), type.portableCellMenu(), makePortableTier(tier), props);
        this.tier = tier;
        this.type = type;
    }

    private static StorageTier makePortableTier(MEGACellTier tier) {
        return new StorageTier(tier.affix, 512 * tier.kbFactor(), 9 + 9 * tier.index, 2048 * tier.index,
                tier::getComponent);
    }

    @Override
    public ResourceLocation getRecipeId() {
        return MEGACells.makeId("cells/portable/" + Objects.requireNonNull(getRegistryName()).getPath());
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

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, level, lines, advancedTooltips);
        if (!AppMekIntegration.isAppMekLoaded() && this.type == ChemicalCellType.TYPE) {
            lines.add(new TextComponent("AppMek not installed."));
        }
    }

}
