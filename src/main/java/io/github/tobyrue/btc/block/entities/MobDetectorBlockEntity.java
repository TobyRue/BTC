package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.misc.CornerStorage;
import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MobDetectorBlockEntity extends BlockEntity implements BlockEntityTicker<MobDetectorBlockEntity>, CornerStorage {
    private BlockBox customBox;
    private int[] distanceArray;

    public MobDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOB_DETECTOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, MobDetectorBlockEntity blockEntity) {
        if (distanceArray == null) {
            if (customBox == null) {
                distanceArray = new int[]{
                        0, 0, 0, 0, 0, 0
                };
            } else {
                distanceArray = new int[]{
                        customBox.getMinX() - pos.getX(), customBox.getMinY() - pos.getY(), customBox.getMinZ() - pos.getZ(),
                        customBox.getMaxX() - pos.getX(), customBox.getMaxY() - pos.getY(), customBox.getMaxZ() - pos.getZ()
                };
            }
        }
        if (customBox == null) {
            customBox = BlockBox.create(
                    new BlockPos(
                            pos.getX() + distanceArray[0],
                            pos.getY() + distanceArray[1],
                            pos.getZ() + distanceArray[2]
                    ),
                    new BlockPos(
                            pos.getX() + distanceArray[3],
                            pos.getY() + distanceArray[4],
                            pos.getZ() + distanceArray[5]
                    )
            );
        }

        if (world.isClient) return;

        Box box = getBox(pos);

        List<HostileEntity> hostiles =
                world.getEntitiesByClass(HostileEntity.class, box, e -> true);

        boolean shouldBePowered = hostiles.isEmpty();

        for (HostileEntity hostile : hostiles) {
            hostile.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20));
        }

        if (state.get(WireBlock.POWERED) != shouldBePowered) {
            world.setBlockState(
                    pos,
                    state.with(WireBlock.POWERED, shouldBePowered),
                    Block.NOTIFY_ALL
            );
        }
    }

    private @NotNull Box getBox(BlockPos pos) {
        Box box;

        if (customBox != null) {
            box = new Box(
                    customBox.getMinX(),
                    customBox.getMinY(),
                    customBox.getMinZ(),
                    customBox.getMaxX() + 1,
                    customBox.getMaxY() + 1,
                    customBox.getMaxZ() + 1
            );
        } else {
            // fallback to default range
            box = new Box(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ()
            );
        }
        return box;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (distanceArray != null) {
            nbt.putIntArray("CustomBox", distanceArray);
        }
    }


    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("CustomBox")) {
            distanceArray = nbt.getIntArray("CustomBox");
            customBox = null;
        }
    }


    public void setDetectionBox(BlockPos c1, BlockPos c2) {
        this.customBox = BlockBox.create(c1, c2);
        distanceArray = new int[]{
                customBox.getMinX() - pos.getX(),
                customBox.getMinY() - pos.getY(),
                customBox.getMinZ() - pos.getZ(),
                customBox.getMaxX() - pos.getX(),
                customBox.getMaxY() - pos.getY(),
                customBox.getMaxZ() - pos.getZ()
        };
    }

    @Override
    public BlockBox getBox(ItemStack stack, BlockPos blockPos, BlockState state, World world) {
        return customBox;
    }
}
