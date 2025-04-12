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

    @Inject(method = "scheduledTick", at = @At("TAIL"))
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (world.isClient) return;
        trySpawnCopperGolem(world, pos, state);
    }

    private void trySpawnCopperGolem(World world, BlockPos pos, BlockState state) {
        BlockPos pumpkinPos = pos.down();
        BlockPos copperPos = pumpkinPos.down();

        // Check if the structure is correct
        if (state.get(LightningRodBlock.FACING) == Direction.UP) {
            if (world.getBlockState(pumpkinPos).isOf(Blocks.CARVED_PUMPKIN) &&
                world.getBlockState(copperPos).isOf(Blocks.COPPER_BLOCK)) {

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
}