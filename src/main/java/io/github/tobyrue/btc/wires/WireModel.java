package io.github.tobyrue.btc.wires;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
//TODO: https://wiki.fabricmc.net/tutorial:custom_model and add @Environment(EnvType.CLIENT) to all things in clients i think just models?
@Environment(EnvType.CLIENT)
public class WireModel implements IBlockStateBakedModel {

    private static final Supplier<Map<Integer, Pair<SpriteIdentifier, Sprite>>> SPRITES = Suppliers.memoize(() ->
        ImmutableMap.<Integer, Pair<SpriteIdentifier, Sprite>>builder()
            .put(0, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_base")), null))
            .put(1, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_overlay")), null))
            .put(2, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection")), null))
            .put(3, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection_overlay")), null))
            .put(4, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection_input")), null))
            .put(5, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection_output")), null))
            .put(6, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_input")), null))
            .put(7, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_output")), null))
            .put(8, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_operator_overlay")), null))
            .build()
    );

    @Override
    public Sprite getParticleSprite() {
        return getSprite(0);
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }


    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        for (var entry : SPRITES.get().entrySet()) {
            entry.getValue().setRight(textureGetter.apply(entry.getValue().getLeft()));
        }
        return this;
    }

    protected static Sprite getSprite(int sprite) {
        return SPRITES.get().get(sprite).getRight();
    }

    protected static final Supplier<Map<BlendMode, RenderMaterial>> RENDER_MATERIALS = Suppliers.memoize(HashMap::new);

    protected static int getBakeFlags(int rotation, boolean lockUv) {
        return switch (rotation) {
            case 90 -> MutableQuadView.BAKE_ROTATE_90;
            case 180 -> MutableQuadView.BAKE_ROTATE_180;
            case 270 -> MutableQuadView.BAKE_ROTATE_270;
            default -> MutableQuadView.BAKE_ROTATE_NONE;
        } | (lockUv ? MutableQuadView.BAKE_LOCK_UV : 0);
    }

    protected static void addFaceLayer(QuadEmitter emitter, Direction direction, Sprite sprite, @Nullable Integer maybeTint, BlendMode mode, int bakeFlags) {
        var tint = (maybeTint == null ? 0xFFFFFF : maybeTint) | 0xFF000000;
        emitter.square(direction, 0, 0, 1, 1, 0)
                .material(RENDER_MATERIALS.get().computeIfAbsent(mode, t -> RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(t).find()))
                .color(tint, tint, tint, tint)
                .spriteBake(sprite, bakeFlags)
                .emit();
    }

    protected static int getRotation(Direction normal, Direction to) {
        if (to.getAxis() == normal.getAxis()) return 0;

        List<Direction> directions = switch (normal) {
            case UP, DOWN -> List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
            case NORTH, SOUTH -> List.of(Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST);
            case EAST, WEST -> List.of(Direction.UP, Direction.SOUTH, Direction.DOWN, Direction.NORTH);
        };

        int fromIndex = 0;
        int toIndex = directions.indexOf(to);
        if (toIndex == -1) return 0;

        int rotationSteps = (toIndex - fromIndex + 4) % 4;
        return rotationSteps * 90;
    }

    @Override
    public void emitBlockQuads(@Nullable BlockRenderView blockRenderView, BlockState state, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {
        QuadEmitter emitter = renderContext.getEmitter();
        var powerTint = state.get(WireBlock.POWERED) ? 0xFD0000 : 0x480000;
//        var materialSolid = getRenderMaterial(BlendMode.SOLID);
//        var materialCutout = getRenderMaterial(BlendMode.CUTOUT);

        for(Direction direction : Direction.values()) {
            addFaceLayer(emitter, direction, getSprite(0), null, BlendMode.SOLID, getBakeFlags(0, true));
            addFaceLayer(emitter, direction, getSprite(1), powerTint, BlendMode.CUTOUT, getBakeFlags(0, true));
            switch (state.get(WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(direction))) {
                case NONE:
                    break;
                case INPUT:
                    addFaceLayer(emitter, direction, getSprite(6), null, BlendMode.CUTOUT, getBakeFlags(0, true));
                    break;
                case OUTPUT:
                    addFaceLayer(emitter, direction, getSprite(7), null, BlendMode.CUTOUT, getBakeFlags(0, true));
                    break;
            }
            for (Direction connection : Direction.stream().filter(d -> d.getAxis() != direction.getAxis()).toList()) {
                if (state.get(WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(connection)) != WireBlock.ConnectionType.NONE) {
                    addFaceLayer(emitter, direction, getSprite(3), powerTint, BlendMode.CUTOUT, getBakeFlags(getRotation(direction, connection), true));
                }
            }

            addFaceLayer(emitter, direction, getSprite(8), state.get(WireBlock.OPERATOR).getColor(), BlendMode.CUTOUT, getBakeFlags(0, true));

//            var opColor = state.get(WireBlock.OPERATOR).getColor() | 0xFF000000;
            // Emit base quad
//            emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
//            emitter.spriteBake(SPRITES.get().get(0).getRight(), MutableQuadView.BAKE_LOCK_UV);
//            emitter.color(-1, -1, -1, -1);
//            emitter.emit();

//            emitter.square(direction, 0, 0, 1, 1, 0)
//                .material(materialSolid)
//                .color(-1, -1, -1, -1)
//                .spriteBake(getSprite(0), MutableQuadView.BAKE_LOCK_UV)
//                .emit();
//
//            emitter.square(direction, 0, 0, 1, 1, 0)
//                .material(materialCutout)
//                .color(opColor, opColor, opColor, opColor)
//                .spriteBake(getSprite(5), MutableQuadView.BAKE_LOCK_UV)
//                .emit();
//
//

        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> supplier, RenderContext renderContext) {
        this.emitBlockQuads(null, ((BlockItem) stack.getItem()).getBlock().getDefaultState(), new BlockPos(0, 0, 0), supplier, renderContext);
//        QuadEmitter emitter = renderContext.getEmitter();
//        for(Direction direction : Direction.values()) {
//            emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
//            emitter.spriteBake(SPRITES.get().get(0).getRight(), MutableQuadView.BAKE_LOCK_UV);
//            emitter.color(-1, -1, -1, -1);
//            emitter.emit();
//        }
    }
}
