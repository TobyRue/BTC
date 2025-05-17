package io.github.tobyrue.btc.wires;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import io.github.tobyrue.btc.BTC;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;

import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WireBlockModel implements IBlockStateBakedModel {

    private static final Supplier<Map<String, SpriteIdentifier>> SPRITE_IDS = Suppliers.memoize(() ->
            ImmutableMap.<String, SpriteIdentifier>builder()
                    .put("base", new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of(BTC.MOD_ID, "block/wire_base")))
                    .put("powered", new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of(BTC.MOD_ID, "block/wire_powered")))
                    .put("input", new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of(BTC.MOD_ID, "block/wire_input")))
                    .put("output", new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of(BTC.MOD_ID, "block/wire_output")))
                    .put("operator", new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Identifier.of(BTC.MOD_ID, "block/wire_operator")))
                    .build()
    );

    private final Map<String, Sprite> sprites;
    private final Mesh mesh;

    public WireBlockModel(Map<String, Sprite> sprites, Mesh mesh) {
        this.sprites = sprites;
        this.mesh = mesh;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos,
                               Supplier<Random> randomSupplier, RenderContext renderContext) {
        renderContext.meshConsumer().accept(mesh);
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> randomSupplier, RenderContext renderContext) {
        renderContext.meshConsumer().accept(mesh);
    }

    @Override
    public Sprite getParticleSprite() {
        return sprites.get("base");
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        Map<String, Sprite> loadedSprites = SPRITE_IDS.get().entrySet().stream()
                .collect(ImmutableMap.toImmutableMap(
                        Map.Entry::getKey,
                        entry -> textureGetter.apply(entry.getValue())
                ));

        MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        QuadEmitter emitter = meshBuilder.getEmitter();

        // Base layer
        emitter.square(Direction.UP, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        emitter.spriteBake(loadedSprites.get("base"), QuadEmitter.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        // Example overlay: powered
        emitter.square(Direction.UP, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
        emitter.spriteBake(loadedSprites.get("powered"), QuadEmitter.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        // Additional overlays (input, output, operator) can be added similarly

        return new WireBlockModel(loadedSprites, meshBuilder.build());
    }
}

