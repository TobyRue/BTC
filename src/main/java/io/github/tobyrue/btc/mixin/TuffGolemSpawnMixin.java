package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import net.minecraft.block.*;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(CarvedPumpkinBlock.class)
public class TuffGolemSpawnMixin {
    @Inject(method = "onBlockAdded", at = @At("TAIL"))
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if (!oldState.isOf(state.getBlock())) {
            this.trySpawnTuffGolemEntity(world, pos, state);
        }
    }

    private void trySpawnTuffGolemEntity(World world, BlockPos pos, BlockState state) {
        BlockPos tuffPos = pos.down();
        if (world.getBlockState(tuffPos).isOf(Blocks.CHISELED_TUFF_BRICKS)) {

            destroyBlockWithEffect(world, pos);
            destroyBlockWithEffect(world, tuffPos);

            TuffGolemEntity golem = new TuffGolemEntity(ModEntities.TUFF_GOLEM, world);
            golem.refreshPositionAndAngles(tuffPos.getX() + 0.5, tuffPos.getY(), tuffPos.getZ() + 0.5, 0, 0);

            // Prevent the golem from catching fire due to lightning
            world.spawnEntity(golem);
            golem.setColor(DyeColor.RED.getEntityColor());
        }
    }
    private void destroyBlockWithEffect(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2); // Properly remove block
        world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state)); // Show break particles & sound
    }
}
