package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.DungeonWireBlock;
import io.github.tobyrue.btc.enums.AntierType;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.AntierBlock;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static io.github.tobyrue.btc.block.DungeonWireBlock.POWERED;


public class AntierBlockEntity extends BlockEntity implements BlockEntityTicker<AntierBlockEntity> {

    public AntierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANTIER_BLOCK_ENTITY, pos, state);
    }
    private int tickCounter = 0; // Counter to track ticks
    public void checkPlayersInRange(ServerWorld world, BlockPos blockPos, BlockState state, double range) {
        List<ServerPlayerEntity> players = world.getPlayers();

        for (ServerPlayerEntity player : players) {
            Vec3d playerPos = player.getPos();
            double distance = playerPos.squaredDistanceTo(Vec3d.ofCenter(blockPos));
            if (distance <= range * range) {
                if (!state.get(AntierBlock.DISABLE)) {
                    if (state.get(AntierBlock.ANTIER_TYPE) == AntierType.NO_MINE || state.get(AntierBlock.ANTIER_TYPE) == AntierType.BOTH ) {
                        player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.MINER_MISHAP, 300, 100));
                    }
                    if (state.get(AntierBlock.ANTIER_TYPE) == AntierType.NO_BUILD || state.get(AntierBlock.ANTIER_TYPE) == AntierType.BOTH ) {
                        player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.BUILDER_BLUNDER, 300, 100));
                    }
                } else {
                    for(Direction direction : Direction.values()) {
                        BlockPos neighborPos = blockPos.offset(direction);
                        BlockState neighborState = world.getBlockState(neighborPos);

                        if (neighborState.getBlock() instanceof DungeonWireBlock) {
                            if (!neighborState.get(POWERED)) {
                                if (state.get(AntierBlock.ANTIER_TYPE) == AntierType.NO_MINE || state.get(AntierBlock.ANTIER_TYPE) == AntierType.BOTH) {
                                    player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.MINER_MISHAP, 300, 100));
                                }
                                if (state.get(AntierBlock.ANTIER_TYPE) == AntierType.NO_BUILD || state.get(AntierBlock.ANTIER_TYPE) == AntierType.BOTH) {
                                    player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.BUILDER_BLUNDER, 300, 100));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick(World world, BlockPos blockPos, BlockState state, AntierBlockEntity blockEntity) {
        if (world instanceof ServerWorld serverWorld) {
            if (!serverWorld.isChunkLoaded(blockPos)) {
                return; // Prevent ticking if the chunk is unloaded
            }

            // Increase tick counter
            tickCounter++;

            // Example: Check every 20 ticks
            if (tickCounter % 20 == 0) {
                // Call checkPlayersInRange with a range of 15 blocks
                checkPlayersInRange(serverWorld, blockPos, state, 15.0);
            }
        }
    }



//    @Override
//    public void onDungeonWireChange(BlockState state, World world, BlockPos pos, boolean powered) {
//        if (state.get(AntierBlock.DISABLE)) {
//
//        }
//    }
//
//    @Override
//    public void onDungeonWireDestroy(BlockState state, World world, BlockPos pos, boolean powered) {
//        if (state.get(AntierBlock.DISABLE)) {
//
//        }
//    }
}
