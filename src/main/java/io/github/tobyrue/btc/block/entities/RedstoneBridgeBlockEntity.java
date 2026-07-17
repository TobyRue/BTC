package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.RedstoneBridgeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneBridgeBlockEntity extends BlockEntity {
    private final int[] cachedOutputs = new int[6];

    public RedstoneBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REDSTONE_BRIDGE_BLOCK_ENTITY, pos, state);
    }

    public int getWeakPower(Direction direction) {
        return cachedOutputs[direction.getOpposite().getId()];
    }

    public void updateCachedPower(World world, BlockState state) {
        boolean changed = false;

        for (Direction dir : Direction.values()) {
            int oldPower = cachedOutputs[dir.getId()];
            int newPower = calculateOutputPowerForSide(world, state, dir);

            if (oldPower != newPower) {
                cachedOutputs[dir.getId()] = newPower;
                changed = true;
            }
        }

        if (changed) {
            markDirty();

            boolean hasXPower = cachedOutputs[Direction.EAST.getId()] > 0 || cachedOutputs[Direction.WEST.getId()] > 0;
            boolean hasYPower = cachedOutputs[Direction.UP.getId()] > 0 || cachedOutputs[Direction.DOWN.getId()] > 0;
            boolean hasZPower = cachedOutputs[Direction.NORTH.getId()] > 0 || cachedOutputs[Direction.SOUTH.getId()] > 0;

            BlockState newState = state
                    .with(RedstoneBridgeBlock.X_POWERED, hasXPower)
                    .with(RedstoneBridgeBlock.Y_POWERED, hasYPower)
                    .with(RedstoneBridgeBlock.Z_POWERED, hasZPower);

            world.setBlockState(pos, newState, 3);
            world.updateNeighbors(pos, newState.getBlock());
        }
    }

    private int calculateOutputPowerForSide(World world, BlockState state, Direction direction) {
        return switch (direction) {
            case EAST -> switch (state.get(RedstoneBridgeBlock.X_AXIS)) {
                case FORWARD -> getPowerFromSide(world, Direction.WEST);
                case BACKWARD, NONE -> 0;
            };
            case WEST -> switch (state.get(RedstoneBridgeBlock.X_AXIS)) {
                case BACKWARD -> getPowerFromSide(world, Direction.EAST);
                case FORWARD, NONE -> 0;
            };

            case UP -> switch (state.get(RedstoneBridgeBlock.Y_AXIS)) {
                case FORWARD -> getPowerFromSide(world, Direction.DOWN);
                case BACKWARD, NONE -> 0;
            };
            case DOWN -> switch (state.get(RedstoneBridgeBlock.Y_AXIS)) {
                case BACKWARD -> getPowerFromSide(world, Direction.UP);
                case FORWARD, NONE -> 0;
            };

            case SOUTH -> switch (state.get(RedstoneBridgeBlock.Z_AXIS)) {
                case FORWARD -> getPowerFromSide(world, Direction.NORTH);
                case BACKWARD, NONE -> 0;
            };
            case NORTH -> switch (state.get(RedstoneBridgeBlock.Z_AXIS)) {
                case BACKWARD -> getPowerFromSide(world, Direction.SOUTH);
                case FORWARD, NONE -> 0;
            };
        };
    }

    private int getPowerFromSide(World world, Direction direction) {
        BlockPos neighborPos = this.pos.offset(direction);

        return world.getEmittedRedstonePower(neighborPos, direction);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putIntArray("CachedOutputs", cachedOutputs);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("CachedOutputs")) {
            int[] savedData = nbt.getIntArray("CachedOutputs");
            System.arraycopy(savedData, 0, this.cachedOutputs, 0, Math.min(savedData.length, this.cachedOutputs.length));
        }
    }
}