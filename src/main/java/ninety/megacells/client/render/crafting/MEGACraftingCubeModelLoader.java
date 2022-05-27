package ninety.megacells.client.render.crafting;

import java.util.Locale;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

import ninety.megacells.block.MEGACraftingUnitType;

public class MEGACraftingCubeModelLoader implements IModelLoader<MEGACraftingCubeModel> {

    public static final MEGACraftingCubeModelLoader INSTANCE = new MEGACraftingCubeModelLoader();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
    }

    @Override
    public MEGACraftingCubeModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        MEGACraftingUnitType unitType = null;

        JsonElement typeEl = modelContents.get("type");
        if (typeEl != null) {
            String typeName = deserializationContext.deserialize(typeEl, String.class);
            if (typeName != null) {
                unitType = MEGACraftingUnitType.valueOf(typeName.toUpperCase(Locale.ROOT));
            }
        }
        if (unitType == null) {
            throw new JsonParseException("type property is missing");
        }

        return new MEGACraftingCubeModel(new MEGACraftingUnitModelProvider(unitType));
    }
}
