package io.github.tobyrue.btc.block.fluids;

import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.world.WorldView;

public abstract class ToxicSludgeFluid extends AbstractFluid {
    @Override
    public Fluid getSource() {
        return ModFluids.TOXIC_SLUDGE_SOURCE;
    }

    @Override
    public Fluid getFlowing() {
        return ModFluids.FLOWING_TOXIC_SLUDGE;
    }

    @Override
    public Item getBucketItem() {
        return ModItems.TOXIC_SLUDGE_BUCKET;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return ModBlocks.TOXIC_SLUDGE.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getSource() || fluid == getFlowing();
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 5;
    }

    public static class Flowing extends ToxicSludgeFluid {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(LEVEL);
        }

        @Override
        public Fluid getStill() {
            return ModFluids.TOXIC_SLUDGE_SOURCE;
        }

        @Override
        public Fluid getFlowing() {
            return ModFluids.FLOWING_TOXIC_SLUDGE;
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }
    }

    public static class Still extends ToxicSludgeFluid {
        @Override
        public Fluid getStill() {
            return ModFluids.TOXIC_SLUDGE_SOURCE;
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }
    }
}