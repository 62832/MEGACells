package gripe._90.megacells.part;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.core.localization.Tooltips;
import appeng.helpers.iface.PatternProviderLogic;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.crafting.PatternProviderPart;

import gripe._90.megacells.definition.MEGAParts;
import gripe._90.megacells.util.Utils;

public class MEGAPatternProviderPart extends PatternProviderPart {

    public static final ResourceLocation MODEL_BASE = Utils.makeId("part/mega_pattern_provider");

    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_off"));

    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_on"));

    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE,
            AppEng.makeId("part/interface_has_channel"));

    public MEGAPatternProviderPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public PatternProviderLogic createLogic() {
        return new PatternProviderLogic(this.getMainNode(), this, 18);
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return MEGAParts.MEGA_PATTERN_PROVIDER.stack();
    }

    public static class Item extends PartItem<MEGAPatternProviderPart> {
        public Item(Properties properties) {
            super(properties, MEGAPatternProviderPart.class, MEGAPatternProviderPart::new);
        }

        @Override
        public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltip, flag);
            tooltip.add(Tooltips.of("Supports processing patterns only."));
        }
    }
}
