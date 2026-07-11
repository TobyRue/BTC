package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class EncasedRedstoneBlock extends Block {
    private final int redstoneLevel;

    public EncasedRedstoneBlock(Settings settings, int redstoneLevel) {
        super(settings);
        this.redstoneLevel = redstoneLevel;
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return redstoneLevel;
    }
}
