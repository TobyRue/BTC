package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.MobDetectorBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrialSpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.HoneycombItem;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class MobDetectorBlockEntity extends BlockEntity implements BlockEntityTicker<MobDetectorBlockEntity> {
    public static final int RANGE_XZ = 6;
    public static final int RANGE_Y = 4;

    public MobDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOB_DETECTOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, MobDetectorBlockEntity blockEntity) {
        if (world.isClient) return;

        Box box = new Box(
                pos.getX() - RANGE_XZ,
                pos.getY() - RANGE_Y,
                pos.getZ() - RANGE_XZ,
                pos.getX() + RANGE_XZ + 1,
                pos.getY() + RANGE_Y + 1,
                pos.getZ() + RANGE_XZ + 1
        );


        List<HostileEntity> hostiles =
                world.getEntitiesByClass(HostileEntity.class, box, e -> true);

        boolean shouldBePowered = hostiles.isEmpty();

        for (HostileEntity hostile : hostiles) {
            hostile.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20));
        }

        if (state.get(MobDetectorBlock.POWERED) != shouldBePowered) {
            world.setBlockState(
                    pos,
                    state.with(MobDetectorBlock.POWERED, shouldBePowered),
                    Block.NOTIFY_ALL
            );
        }
    }
}
