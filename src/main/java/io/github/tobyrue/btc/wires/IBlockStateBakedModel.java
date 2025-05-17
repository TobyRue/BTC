package io.github.tobyrue.btc.wires;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface IBlockStateBakedModel extends UnbakedModel, BakedModel, FabricBakedModel {
    @Override
    default List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return List.of();
    }

    @Override
    default boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    default boolean hasDepth() {
        return false;
    }

    @Override
    default boolean isSideLit() {
        return false;
    }

    @Override
    default boolean isBuiltin() {
        return false;
    }

    @Override
    default boolean isVanillaAdapter() {
        return false;
    }

    @Override
    default void setParents(Function<Identifier, UnbakedModel> modelLoader) {}

    @Override
    default Collection<Identifier> getModelDependencies() {
        return List.of();
    }
}
