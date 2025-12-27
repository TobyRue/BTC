package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.MobDetectorBlock;
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
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MobDetectorBlockEntity extends BlockEntity implements BlockEntityTicker<MobDetectorBlockEntity>, CornerStorage {
    private BlockBox customBox;
    private int[] distanceArray;
    private Direction lastDirection = Direction.NORTH;

    public MobDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOB_DETECTOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, MobDetectorBlockEntity blockEntity) {
        if (world.isClient) return;
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

        if (state.get(MobDetectorBlock.FACING) != lastDirection) {
            int rotation = 0;
            if (lastDirection == Direction.NORTH) {
                switch (state.get(MobDetectorBlock.FACING)) {
                    case DOWN, UP, NORTH -> rotation = 0;
                    case EAST -> rotation = 90;
                    case SOUTH -> rotation = 180;
                    case WEST -> rotation = 270;
                }
                var radians = rotation * Math.PI / 180;
                rotateBlockBox(radians);
            } else if (lastDirection == Direction.EAST) {
                switch (state.get(MobDetectorBlock.FACING)) {
                    case NORTH -> rotation = 270;
                    case DOWN, UP, EAST -> rotation = 0;
                    case SOUTH -> rotation = 90;
                    case WEST -> rotation = 180;
                }
                var radians = rotation * Math.PI / 180;
                rotateBlockBox(radians);
            } else if (lastDirection == Direction.SOUTH) {
                switch (state.get(MobDetectorBlock.FACING)) {
                    case NORTH -> rotation = 180;
                    case EAST -> rotation = 270;
                    case DOWN, UP, SOUTH -> rotation = 0;
                    case WEST -> rotation = 90;
                }
                var radians = rotation * Math.PI / 180;
                rotateBlockBox(radians);
            } else if (lastDirection == Direction.WEST) {
                switch (state.get(MobDetectorBlock.FACING)) {
                    case NORTH -> rotation = 90;
                    case EAST -> rotation = 180;
                    case SOUTH -> rotation = 270;
                    case DOWN, UP, WEST -> rotation = 0;
                }
                var radians = rotation * Math.PI / 180;
                rotateBlockBox(radians);
            } else if (lastDirection == Direction.DOWN || lastDirection == Direction.UP) {
                switch (state.get(MobDetectorBlock.FACING)) {
                    case NORTH, EAST, SOUTH, WEST, DOWN, UP -> rotation = 0;
                }
                var radians = rotation * Math.PI / 180;
                rotateBlockBox(radians);
            }
            lastDirection = state.get(MobDetectorBlock.FACING);
        }

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

    private void rotateBlockBox(double radians) {
        var x1 = distanceArray[0];
        var z1 = distanceArray[2];
        var x2 = distanceArray[3];
        var z2 = distanceArray[5];

        var nx1 =  x1 * Math.cos(radians) + z1 * Math.sin(radians);
        var nz1 = -x1 * Math.sin(radians) + z1 * Math.cos(radians);

        var nx2 =  x2 * Math.cos(radians) + z2 * Math.sin(radians);
        var nz2 = -x2 * Math.sin(radians) + z2 * Math.cos(radians);
        setDistanceArray(Math.round((float) nx1), distanceArray[1], Math.round((float) nz1), Math.round((float) nx2), distanceArray[4], Math.round((float) nz2));
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
        if (lastDirection != null) {
            nbt.putInt("DirectionNumber", switch (lastDirection) {
                case DOWN, UP, NORTH -> 1;
                case EAST -> 2;
                case SOUTH -> 3;
                case WEST -> 4;
            });
        }
    }


    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("CustomBox")) {
            distanceArray = nbt.getIntArray("CustomBox");
            customBox = null;
        }
        if (nbt.contains("DirectionNumber")) {
            lastDirection = switch (nbt.getInt("DirectionNumber")) {
                case 1 -> Direction.NORTH;
                case 2 -> Direction.EAST;
                case 3 -> Direction.SOUTH;
                case 4 -> Direction.WEST;
                default -> throw new IllegalStateException("Unexpected value: " + nbt.getInt("DirectionNumber"));
            };
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
    public void setDistanceArray(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        distanceArray = new int[]{
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ
        };
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

    @Override
    public BlockBox getBox(ItemStack stack, BlockPos blockPos, BlockState state, World world) {
        return customBox;
    }
}
