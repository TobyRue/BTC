package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.FanBlockEntity;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.ModTickBlockEntityProvider;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CopperFanBlock extends WaxedCopperFanBlock implements Oxidizable, ModBlockEntityProvider<FanBlockEntity>, ModTickBlockEntityProvider<FanBlockEntity> {
    public static final DirectionProperty FACING = WaxedCopperFanBlock.FACING;
    public static final BooleanProperty POWERED = WaxedCopperFanBlock.POWERED;
    public static final EnumProperty<FanMode> MODE = WaxedCopperFanBlock.MODE;
    public static final BooleanProperty TOGGLE_MODE = WaxedCopperFanBlock.TOGGLE_MODE;
    private final OxidationLevel oxidationLevel;

    public CopperFanBlock(Settings settings, OxidationLevel oxidationLevel) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false).with(MODE, FanMode.BLOW).with(TOGGLE_MODE, false));
        this.oxidationLevel = oxidationLevel;
    }

    @Override
    public void tickDegradation(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextFloat() < 0.05688889F) {
            this.tryDegrade(state, world, pos, random).ifPresent((degraded) -> {
                BlockEntity oldBe = world.getBlockEntity(pos);

                if (oldBe instanceof FanBlockEntity) {
                    var registryLookup = world.getRegistryManager();
                    NbtCompound nbt = oldBe.createNbt(registryLookup);
                    world.setBlockState(pos, degraded);
                    BlockEntity newBe = world.getBlockEntity(pos);

                    if (newBe instanceof FanBlockEntity) {
                        newBe.readNbt(nbt, registryLookup);
                        newBe.markDirty();
                        world.updateListeners(pos, state, degraded, 3);
                    }
                }
            });
        }
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

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        var hasPower = IDungeonWire.isReceivingDungeonWirePower(state, world, pos, Arrays.stream(Direction.values().clone()).filter(dir -> dir != state.get(FACING))) || world.isReceivingRedstonePower(pos);
        BlockState newState = state;
        if (state.get(TOGGLE_MODE)) {
            newState = state.with(MODE, hasPower ? FanMode.PULL : FanMode.BLOW);
            newState = newState.with(POWERED, true);
        } else {
            newState = newState.with(POWERED, hasPower);
        }
        world.setBlockState(pos, newState);
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getSide();

        return this.getDefaultState()
                .with(FACING, facing)
                .with(POWERED, false)
                .with(MODE, FanMode.BLOW)
                .with(TOGGLE_MODE, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, MODE, TOGGLE_MODE);
    }

    @Override
    public BlockEntityType<FanBlockEntity> getBlockEntityType() {
        return ModBlockEntities.COPPER_FAN_BLOCK_ENTITY;
    }
}
