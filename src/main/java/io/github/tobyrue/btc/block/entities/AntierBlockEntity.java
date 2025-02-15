package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.enums.AntierType;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.AntierBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class AntierBlockEntity extends BlockEntity implements BlockEntityTicker<AntierBlockEntity> {

    public AntierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANTIER_BLOCK_ENTITY, pos, state);
    }
    private int tickCounter = 0; // Counter to track ticks
    public void checkPlayersInRange(ServerWorld world, BlockPos blockPos, BlockState state, double range) {
        // Iterate through all players in the world
        List<ServerPlayerEntity> players = world.getPlayers();
        for (ServerPlayerEntity player : players) {
            // Get the player's position
            Vec3d playerPos = player.getPos();
            // Calculate the distance between the player's position and the block position
            double distance = playerPos.squaredDistanceTo(Vec3d.ofCenter(blockPos));
            if (distance <= range * range) {
                // The player is within range
                // Apply logic here
                if (state.get(AntierBlock.ANTIER_TYPE) == AntierType.NO_MINE || state.get(AntierBlock.ANTIER_TYPE) == AntierType.BOTH ) {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 200, 100));
                }
                if (state.get(AntierBlock.ANTIER_TYPE) == AntierType.NO_BUILD || state.get(AntierBlock.ANTIER_TYPE) == AntierType.BOTH ) {
                    player.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(BTC.ANTI_PLACE), 200, 100));
                }
            }
        }
    }
    @Override
    public void tick(World world, BlockPos blockPos, BlockState state, AntierBlockEntity blockEntity) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;

            // Increase tick counter
            tickCounter++;

            // Example: Check every 20 ticks
            if (tickCounter % 20 == 0) {
                // Call checkPlayersInRange with a range of 15 blocks
                checkPlayersInRange(serverWorld, blockPos, state, 15.0);
            }
        }
    }
}
