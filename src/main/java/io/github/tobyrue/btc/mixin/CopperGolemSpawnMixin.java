package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.block.FireDispenserBlock;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.enums.FireDispenserType;
import net.minecraft.block.*;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningRodBlock.class)
public abstract class CopperGolemSpawnMixin  {

//    @Inject(method = "onBlockAdded", at = @At("HEAD"))
//    private void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
//        if (world.isClient) return;
//        System.out.println("⚡ Lightning Rod placed at " + pos);
//        trySpawnCopperGolem(world, pos);
//    }
    @Inject(method = "scheduledTick", at = @At("TAIL"))
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.isClient) return;
        System.out.println("⚡ Lightning Rod placed at " + pos);
        trySpawnCopperGolem(world, pos, state);
    }

    private void trySpawnCopperGolem(World world, BlockPos pos, BlockState state) {
        BlockPos pumpkinPos = pos.down();
        BlockPos copperPos = pumpkinPos.down();

        // Check if the structure is correct
        if (state.get(LightningRodBlock.FACING) == Direction.UP) {
            if (world.getBlockState(pumpkinPos).isOf(Blocks.CARVED_PUMPKIN) &&
                    world.getBlockState(copperPos).isOf(Blocks.COPPER_BLOCK)) {
                System.out.println("Rod - " + world.getBlockState(pos) + " Pumpkin - " + world.getBlockState(pumpkinPos) + " Copper - " + world.getBlockState(copperPos));

                // Mimic breakPatternBlocks() to properly remove structure
                destroyBlockWithEffect(world, pos);
                destroyBlockWithEffect(world, pumpkinPos);
                destroyBlockWithEffect(world, copperPos);

                // Spawn the Copper Golem
                CopperGolemEntity golem = new CopperGolemEntity(ModEntities.COPPER_GOLEM, world);
                golem.refreshPositionAndAngles(copperPos.getX() + 0.5, copperPos.getY(), copperPos.getZ() + 0.5, 0, 0);

                // Prevent the golem from catching fire due to lightning
                world.spawnEntity(golem);
            }
        }
        if (state.get(LightningRodBlock.FACING) == Direction.UP) {
            if (world.getBlockState(pumpkinPos).isOf(Blocks.CARVED_PUMPKIN) &&
                    world.getBlockState(copperPos).isOf(Blocks.WAXED_COPPER_BLOCK)) {
                System.out.println("Rod - " + world.getBlockState(pos) + " Pumpkin - " + world.getBlockState(pumpkinPos) + " Copper - " + world.getBlockState(copperPos));

                // Mimic breakPatternBlocks() to properly remove structure
                destroyBlockWithEffect(world, pos);
                destroyBlockWithEffect(world, pumpkinPos);
                destroyBlockWithEffect(world, copperPos);

                // Spawn the Copper Golem
                CopperGolemEntity golem = new CopperGolemEntity(ModEntities.COPPER_GOLEM, world);
                golem.refreshPositionAndAngles(copperPos.getX() + 0.5, copperPos.getY(), copperPos.getZ() + 0.5, 0, 0);

                // Prevent the golem from catching fire due to lightning
                world.spawnEntity(golem);
                golem.setWaxed(true);
            }
        }
    }
    private void destroyBlockWithEffect(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2); // Properly remove block
        world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state)); // Show break particles & sound
    }




//    @Inject(method = "setPowered", at = @At("HEAD"))
//    private void onPowered(BlockState state, World world, BlockPos pos, CallbackInfo ci) {
//        if (world.isClient) return;
//        System.out.println("⚡ Lightning Rod powered at " + pos);
//        trySpawnCopperGolem((ServerWorld) world, pos, true);
//    }
//
//    @Inject(method = "scheduledTick", at = @At("HEAD"))
//    private void onScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
//        if (!state.get(Properties.POWERED)) {
//            System.out.println("🔄 Lightning Rod lost power at " + pos);
//            trySpawnCopperGolem(world, pos, false);
//        }
//    }


//    private void trySpawnCopperGolem(World world, BlockPos pos) {
//        BlockPos pumpkinPos = pos.down();
//        BlockPos copperPos = pumpkinPos.down();
//
//        // Check if the structure is correct
//        if (world.getBlockState(pumpkinPos).isOf(Blocks.CARVED_PUMPKIN) &&
//                world.getBlockState(copperPos).isOf(Blocks.COPPER_BLOCK)) {
//            System.out.println("Rod - " + world.getBlockState(pos) + " Pumpkin - " + world.getBlockState(pumpkinPos) + " Copper - " + world.getBlockState(copperPos));
//
//            // Mimic breakPatternBlocks() to properly remove structure
//            destroyBlockWithEffect(world, pos);
//            destroyBlockWithEffect(world, pumpkinPos);
//            destroyBlockWithEffect(world, copperPos);
//
//            // Spawn the Copper Golem
//            CopperGolemEntity golem = new CopperGolemEntity(ModEntities.COPPER_GOLEM, world);
//            golem.refreshPositionAndAngles(copperPos.getX() + 0.5, copperPos.getY(), copperPos.getZ() + 0.5, 0, 0);
//
//            // Prevent the golem from catching fire due to lightning
//            world.spawnEntity(golem);
//            golem.extinguish();
//            golem.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 5));
//        }
//    }
//    private void destroyBlockWithEffect(World world, BlockPos pos) {
//        BlockState state = world.getBlockState(pos);
//        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2); // Properly remove block
//        world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state)); // Show break particles & sound
//    }
//private void trySpawnCopperGolem(ServerWorld world, BlockPos pos, boolean wasPowered) {
//    BlockPos lightningRodPos = pos;
//
//    // ✅ Step 1: Only Remove If It Was Previously Powered
//    if (!wasPowered) {
//        System.out.println("🛑 Lightning Rod unpowered, removing at " + lightningRodPos);
//        world.setBlockState(lightningRodPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
//    }
//
//    // ✅ Step 2: Remove POI Before Checking Structure
//    world.getServer().execute(() -> {
//        world.getPointOfInterestStorage().remove(lightningRodPos);
//        System.out.println("📌 Removed POI at " + lightningRodPos);
//    });
//
//    // ✅ Step 3: Check for Copper Golem Structure
//    BlockPattern.Result patternResult = getCopperGolemPattern().searchAround(world, pos);
//    if (patternResult == null) {
//        System.out.println("❌ Pattern not found at " + pos);
//        return;
//    }
//
//    System.out.println("✅ Copper Golem structure detected at " + pos);
//
//    // ✅ Step 4: Remove Structure Blocks
//    world.getServer().execute(() -> {
//        for (int i = 0; i < getCopperGolemPattern().getHeight(); ++i) {
//            CachedBlockPosition cachedBlockPosition = patternResult.translate(0, i, 0);
//            BlockPos blockPos = cachedBlockPosition.getBlockPos();
//
//            System.out.println("🛠 Removing block at " + blockPos + ": " + cachedBlockPosition.getBlockState());
//
//            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
//            world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(cachedBlockPosition.getBlockState()));
//
//            // Ensure the world updates correctly
//            world.updateListeners(blockPos, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), 3);
//            world.updateNeighbors(blockPos, Blocks.AIR);
//        }
//    });
//
//    // ✅ Step 5: Spawn the Copper Golem
//    BlockPos golemPos = patternResult.translate(0, 2, 0).getBlockPos();
//    CopperGolemEntity golem = new CopperGolemEntity(ModEntities.COPPER_GOLEM, world);
//    golem.refreshPositionAndAngles(golemPos.getX() + 0.5, golemPos.getY(), golemPos.getZ() + 0.5, 0, 0);
//    golem.extinguish();
//
//    System.out.println("🤖 Copper Golem spawned at " + golemPos);
//    world.spawnEntity(golem);
//}
//
//    private static BlockPattern copperGolemPattern;
//
//    private static BlockPattern getCopperGolemPattern() {
//        if (copperGolemPattern == null) {
//            copperGolemPattern = BlockPatternBuilder.start()
//                    .aisle("|", "^", "#")
//                    .where('|', CachedBlockPosition.matchesBlockState(state -> state.isOf(Blocks.LIGHTNING_ROD)))
//                    .where('^', CachedBlockPosition.matchesBlockState(state -> state.isOf(Blocks.CARVED_PUMPKIN)))
//                    .where('#', CachedBlockPosition.matchesBlockState(state -> state.isOf(Blocks.COPPER_BLOCK)))
//                    .build();
//        }
//        return copperGolemPattern;
//    }
}