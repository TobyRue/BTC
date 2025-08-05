package io.github.tobyrue.btc.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.item.UnlockScrollItem;
import io.github.tobyrue.btc.wires.IBlockStateBakedModel;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class UnlockScrollItemModel implements IBlockStateBakedModel {
    protected static final Supplier<Map<BlendMode, RenderMaterial>> RENDER_MATERIALS = Suppliers.memoize(HashMap::new);
    private Function<SpriteIdentifier, Sprite> textureGetter;
    private UnbakedModel originalModel;

    @Override
    public Sprite getParticleSprite() {
        return null;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    @Nullable
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
        this.textureGetter = textureGetter;
        return this;
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        if (stack.getItem() instanceof UnlockScrollItem && stack.contains(BTC.UNLOCK_SPELL_COMPONENT)) {
            Identifier component = stack.get(BTC.SPELL_TEXTURE_COMPONENT);

            if (component != null && textureGetter != null) {
                SpriteIdentifier spriteId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, component);
                Sprite sprite = textureGetter.apply(spriteId);

                if (sprite == null) return;

                QuadEmitter emitter = context.getEmitter();
                emitter.material(RENDER_MATERIALS.get().computeIfAbsent(BlendMode.SOLID,
                        b -> Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer()).materialFinder().blendMode(0, b).find()));

                emitter.square(Direction.UP, 0, 0, 1, 1, 0);
                emitter.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED);
                emitter.emit();
            }
        }
    }

}
