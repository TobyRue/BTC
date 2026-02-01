package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.dataholders.PistonBlockEntityPatch;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PistonBlockEntity.class})
public abstract class PistonBlockEntityMixin extends BlockEntity implements PistonBlockEntityPatch {
    @Shadow
    private float lastProgress;
    @Shadow
    private float progress;
    @Shadow
    private boolean source;
    @Shadow
    private BlockState pushedBlock;
    @Unique
    private BlockEntity heldBlockEntity;

    @Shadow
    public abstract Direction getFacing();

    public PistonBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void setBlockEntity(BlockEntity blockEntity) {
        this.heldBlockEntity = blockEntity;
    }

    public BlockEntity getBlockEntity() {
        return this.heldBlockEntity;
    }

    @Inject(
            method = {"tick"},
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
                    shift = Shift.AFTER
            )}
    )
    private static void moveTileEntitiesHere(World world, BlockPos pos, BlockState state, PistonBlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof PistonBlockEntityPatch) {
            PistonBlockEntityPatch p = (PistonBlockEntityPatch)blockEntity;
            BlockEntity var7 = world.getBlockEntity(pos);
            if (var7 instanceof BlockEntity) {
                BTC.HandleBlockEntityShenanigans(p.getBlockEntity(), var7, world);
            }
        }

    }

    @Inject(
            method = {"finish"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void overwriteFinish(CallbackInfo ci) {
        if (this.world != null && (this.lastProgress < 1.0F || this.world.isClient())) {
            this.progress = 1.0F;
            this.lastProgress = this.progress;
            BlockEntity blockEntityHeld = this.getBlockEntity();
            this.world.removeBlockEntity(this.pos);
            this.markRemoved();
            if (this.world.getBlockState(this.pos).isOf(Blocks.MOVING_PISTON)) {
                BlockState blockState;
                if (this.source) {
                    blockState = Blocks.AIR.getDefaultState();
                } else {
                    blockState = Block.postProcessState(this.pushedBlock, this.world, this.pos);
                }

                this.world.setBlockState(this.pos, blockState, 3);
                BlockEntity var5 = this.world.getBlockEntity(this.pos);
                if (var5 instanceof BlockEntity) {
                    BTC.HandleBlockEntityShenanigans(blockEntityHeld, var5, this.world);
                }
                this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
            }
        }

        ci.cancel();
    }
}