package io.github.tobyrue.btc.block;

import net.minecraft.block.*;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class CopperButtonBlock extends ButtonBlock implements Oxidizable {
    private final OxidationLevel oxidationLevel;

    public CopperButtonBlock(BlockSetType type, int pressTicks, AbstractBlock.Settings settings, OxidationLevel oxidationLevel) {
        super(type, pressTicks, settings);
        this.oxidationLevel = oxidationLevel;
    }

    @Override
    public OxidationLevel getDegradationLevel() {
        return this.oxidationLevel;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.tickDegradation(state, world, pos, random);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return Oxidizable.getIncreasedOxidationBlock(state.getBlock()).isPresent();
    }
}
