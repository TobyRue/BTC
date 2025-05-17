package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.wires.WireModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.util.ModelIdentifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BTCModelLoadingPlugin implements ModelLoadingPlugin {
    public static final Identifier DUNGEON_WIRE_V2 = BTC.identifierOf("dungeon_wire_v2");

    @Override
    public void onInitializeModelLoader(Context pluginContext) {
        // We want to add our model when the models are loaded
        pluginContext.modifyModelOnLoad().register((original, context) -> {
            // This is called for every model that is loaded, so make sure we only target ours
            final Identifier id = context.topLevelId().id();
            if (id != null && id.equals(DUNGEON_WIRE_V2)) {
                return new WireModel();
            } else {
                // If we don't modify the model we just return the original as-is
                return original;
            }
        });
    }
}
