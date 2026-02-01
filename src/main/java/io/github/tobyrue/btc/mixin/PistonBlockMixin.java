package io.github.tobyrue.btc.mixin;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.dataholders.PistonBlockEntityPatch;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PistonBlock.class})
public abstract class PistonBlockMixin extends FacingBlock {
    @Shadow
    @Final
    private boolean sticky;

    protected PistonBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(
            method = {"isMovable"},
            at = {@At("TAIL")},
            cancellable = true
    )
    private static void moveTileEntities(BlockState state, World world, BlockPos pos, Direction direction, boolean canBreak, Direction pistonDir, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.isIn(BTC.PISTONS_CAN_MOVE_BLOCK_ENTITY) || cir.getReturnValue());
    }

    @Inject(
            method = {"isMovable"},
            at = {@At(
                    value = "RETURN",
                    ordinal = 2
            )},
            cancellable = true
    )
    private static void overrideForDatapacks(BlockState state, World world, BlockPos pos, Direction direction, boolean canBreak, Direction pistonDir, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.isIn(BTC.PISTONS_CAN_MOVE_BLOCK_ENTITY) || cir.getReturnValue());
    }

    @Inject(
            method = {"move"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void overwriteMove(World world, BlockPos pos, Direction dir, boolean retract, CallbackInfoReturnable<Boolean> cir) {
        BlockPos blockPos = pos.offset(dir);
        if (!retract && world.getBlockState(blockPos).isOf(Blocks.PISTON_HEAD)) {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 20);
        }

        PistonHandler pistonHandler = new PistonHandler(world, pos, dir, retract);
        if (!pistonHandler.calculatePush()) {
            cir.setReturnValue(false);
        } else {
            Map<BlockPos, BlockState> map = Maps.newHashMap();
            List<BlockPos> list = pistonHandler.getMovedBlocks();
            List<BlockState> list2 = Lists.newArrayList();
            Iterator var11 = list.iterator();

            while(var11.hasNext()) {
                BlockPos blockPos2 = (BlockPos)var11.next();
                BlockState blockState = world.getBlockState(blockPos2);
                list2.add(blockState);
                map.put(blockPos2, blockState);
            }

            List<BlockPos> list3 = pistonHandler.getBrokenBlocks();
            BlockState[] blockStates = new BlockState[list.size() + list3.size()];
            Direction direction = retract ? dir : dir.getOpposite();
            int i = 0;

            BlockState blockState4;
            BlockEntity blockEntity;
            for(int j = list3.size() - 1; j >= 0; --j) {
                BlockPos blockPos3 = (BlockPos)list3.get(j);
                blockState4 = world.getBlockState(blockPos3);
                blockEntity = blockState4.hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
                dropStacks(blockState4, world, blockPos3, blockEntity);
                world.setBlockState(blockPos3, Blocks.AIR.getDefaultState(), 18);
                    world.emitGameEvent(GameEvent.BLOCK_DESTROY, blockPos3, GameEvent.Emitter.of(blockState4));
                if (!blockState4.isIn(BlockTags.FIRE)) {
                    world.addBlockBreakParticles(blockPos3, blockState4);
                }

                blockStates[i++] = blockState4;
            }

            Map<Integer, BlockEntity> heldBlockEntities = Maps.newHashMap();

            int j;
            BlockPos blockPos3;
            for(j = 0; j < list.size(); ++j) {
                blockPos3 = (BlockPos)list.get(j);
                blockEntity = ((BlockState)list2.get(j)).hasBlockEntity() ? world.getBlockEntity(blockPos3) : null;
                if (blockEntity != null) {
                    heldBlockEntities.put(j, blockEntity);
                    world.removeBlockEntity(blockPos3);
                    blockEntity.markDirty();
                }
            }

            BlockState blockState2;
            for(j = list.size() - 1; j >= 0; --j) {
                blockPos3 = (BlockPos)list.get(j);
                blockState2 = world.getBlockState(blockPos3);
                blockPos3 = blockPos3.offset(direction);
                map.remove(blockPos3);
                BlockState blockState3 = (BlockState)Blocks.MOVING_PISTON.getDefaultState().with(FACING, dir);
                world.setBlockState(blockPos3, blockState3, 68);
                PistonBlockEntity pushedBlocks = new PistonBlockEntity(blockPos3, blockState3, (BlockState)list2.get(j), dir, retract, false);
                if (!heldBlockEntities.isEmpty() && heldBlockEntities.containsKey(j) && pushedBlocks instanceof PistonBlockEntityPatch) {
                    PistonBlockEntityPatch p = (PistonBlockEntityPatch)pushedBlocks;
                    p.setBlockEntity((BlockEntity)heldBlockEntities.get(j));
                }

                world.addBlockEntity(pushedBlocks);
                blockStates[i++] = blockState2;
            }

            if (retract) {
                PistonType pistonType = this.sticky ? PistonType.STICKY : PistonType.DEFAULT;
                blockState4 = (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, dir)).with(PistonHeadBlock.TYPE, pistonType);
                blockState2 = (BlockState)((BlockState)Blocks.MOVING_PISTON.getDefaultState().with(PistonExtensionBlock.FACING, dir)).with(PistonExtensionBlock.TYPE, this.sticky ? PistonType.STICKY : PistonType.DEFAULT);
                map.remove(blockPos);
                world.setBlockState(blockPos, blockState2, 68);
                world.addBlockEntity(PistonExtensionBlock.createBlockEntityPiston(blockPos, blockState2, blockState4, dir, true, true));
            }

            BlockState blockState5 = Blocks.AIR.getDefaultState();
            Iterator var32 = map.keySet().iterator();

            while(var32.hasNext()) {
                BlockPos blockPos4 = (BlockPos)var32.next();
                world.setBlockState(blockPos4, blockState5, 82);
            }

            var32 = map.entrySet().iterator();

            BlockPos blockPos5;
            while(var32.hasNext()) {
                Entry<BlockPos, BlockState> entry = (Entry)var32.next();
                blockPos5 = (BlockPos)entry.getKey();
                BlockState blockState6 = (BlockState)entry.getValue();
                blockState6.prepare(world, blockPos5, 2);
                blockState5.updateNeighbors(world, blockPos5, 2);
                blockState5.prepare(world, blockPos5, 2);
            }

            i = 0;

            int k;
            for(k = list3.size() - 1; k >= 0; --k) {
                blockState2 = blockStates[i++];
                blockPos5 = (BlockPos)list3.get(k);
                blockState2.prepare(world, blockPos5, 2);
                world.updateNeighborsAlways(blockPos5, blockState2.getBlock());
            }

            for(k = list.size() - 1; k >= 0; --k) {
                world.updateNeighborsAlways((BlockPos)list.get(k), blockStates[i++].getBlock());
            }

            if (retract) {
                world.updateNeighborsAlways(blockPos, Blocks.PISTON_HEAD);
            }

            cir.setReturnValue(true);
        }
    }
}