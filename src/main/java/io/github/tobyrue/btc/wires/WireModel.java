package io.github.tobyrue.btc.wires;

import com.google.common.base.Functions;
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
            .put(9, new Pair<>(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_none_overlay")), null))
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

    protected static int getRotation(Direction face, Direction to) {
        return switch (face) {
            case UP -> switch (to) {
                case NORTH -> 0;
                case EAST -> 90;
                case SOUTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            case DOWN -> switch (to) {
                case SOUTH -> 0;
                case EAST -> 90;
                case NORTH -> 180;
                case WEST -> 270;
                default -> 0;
            };
            case NORTH -> switch (to) {
                case UP -> 0;
                case EAST -> 270;
                case DOWN -> 180;
                case WEST -> 90;
                default -> 0;
            };
            case SOUTH -> switch (to) {
                case UP -> 0;
                case WEST -> 270;
                case DOWN -> 180;
                case EAST -> 90;
                default -> 0;
            };
            case WEST -> switch (to) {
                case UP -> 0;
                case NORTH -> 270;
                case DOWN -> 180;
                case SOUTH -> 90;
                default -> 0;
            };
            case EAST -> switch (to) {
                case UP -> 0;
                case SOUTH -> 270;
                case DOWN -> 180;
                case NORTH -> 90;
                default -> 0;
            };
        };
    }

    protected static final Map<ItemStack, BlockState> ITEM_STATE_CACHE = new HashMap<>();

    protected static final Function<ItemStack, BlockState> ITEM_STATE = t -> ITEM_STATE_CACHE.computeIfAbsent(t, stack -> WireBlock.CONNECTION_TO_DIRECTION.get().keySet().stream().reduce(
        ((BlockItem) stack.getItem()).getBlock().getDefaultState(),
        (acc, con) -> acc.with(con, WireBlock.ConnectionType.NONE),
        (lhs, rhs) -> {
            throw new RuntimeException("Don't fold in parallel");
        }
    ));

    @Override
    public void emitBlockQuads(@Nullable BlockRenderView blockRenderView, BlockState state, BlockPos blockPos, Supplier<Random> supplier, RenderContext renderContext) {
        QuadEmitter emitter = renderContext.getEmitter();
        var powerTint = state.get(WireBlock.POWERED) ? 0xE50000 : 0x990000;
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
                } else if ((state.get(WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(connection)) != WireBlock.ConnectionType.NONE || state.get(WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(direction)) != WireBlock.ConnectionType.NONE)) {
                    addFaceLayer(emitter, direction, getSprite(9), powerTint, BlendMode.CUTOUT, getBakeFlags(getRotation(direction, connection), true));
                }
            }
            if (!(Direction.stream().filter(d -> state.get(WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(d)) == WireBlock.ConnectionType.INPUT).count() == 1 && state.get(WireBlock.OPERATOR) == WireBlock.Operator.OR)) {
                addFaceLayer(emitter, direction, getSprite(8), state.get(WireBlock.OPERATOR).getColor(), BlendMode.CUTOUT, getBakeFlags(0, true));
            }
        }
    }


    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> supplier, RenderContext renderContext) {
        this.emitBlockQuads(null, ITEM_STATE.apply(stack), new BlockPos(0, 0, 0), supplier, renderContext);
    }
}
