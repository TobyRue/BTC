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
import net.minecraft.util.BlockMirror;
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
            if (lastDirection == Direction.NORTH) {
                rotateBlockBox(
                        switch (state.get(MobDetectorBlock.FACING)) {
                    case DOWN, UP, NORTH -> 0;
                    case EAST -> 90;
                    case SOUTH -> 180;
                    case WEST -> 270;
                });
            } else if (lastDirection == Direction.EAST) {

                rotateBlockBox(
                        switch (state.get(MobDetectorBlock.FACING)) {
                    case NORTH -> 270;
                    case DOWN, UP, EAST -> 0;
                    case SOUTH -> 90;
                    case WEST -> 180;
                });
            } else if (lastDirection == Direction.SOUTH) {
                rotateBlockBox(
                        switch (state.get(MobDetectorBlock.FACING)) {
                    case NORTH -> 180;
                    case EAST -> 270;
                    case DOWN, UP, SOUTH -> 0;
                    case WEST -> 90;
                });
            } else if (lastDirection == Direction.WEST) {

                rotateBlockBox(
                        switch (state.get(MobDetectorBlock.FACING)) {
                    case NORTH -> 90;
                    case EAST -> 180;
                    case SOUTH -> 270;
                    case DOWN, UP, WEST -> 0;
                });
            } else if (lastDirection == Direction.DOWN || lastDirection == Direction.UP) {
                rotateBlockBox(
                        switch (state.get(MobDetectorBlock.FACING)) {
                    case NORTH, EAST, SOUTH, WEST, DOWN, UP -> 0;
                });
            }
            lastDirection = state.get(MobDetectorBlock.FACING);
        }
        if (state.get(MobDetectorBlock.MIRRORED) != BlockMirror.NONE) {
            mirrorBlockBox(state.get(MobDetectorBlock.MIRRORED));
            state.with(MobDetectorBlock.MIRRORED, BlockMirror.NONE);
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

    private void mirrorBlockBox(BlockMirror mirror) {
        var x1 = distanceArray[0];
        var z1 = distanceArray[2];
        var x2 = distanceArray[3];
        var z2 = distanceArray[5];

        switch (mirror) {
            case NONE -> {
                setDistanceArray(
                        x1,
                        distanceArray[1],
                        z1,
                        x2,
                        distanceArray[4],
                        z2
                );
            }
            case LEFT_RIGHT -> {
                setDistanceArray(
                        -x1,
                        distanceArray[1],
                        z1,
                        -x2,
                        distanceArray[4],
                        z2
                );
            }
            case FRONT_BACK -> {
                setDistanceArray(
                        x1,
                        distanceArray[1],
                        -z1,
                        x2,
                        distanceArray[4],
                        -z2
                );
            }
        }
    }

    private void rotateBlockBox(int degree) {
        var x1 = distanceArray[0];
        var z1 = distanceArray[2];
        var x2 = distanceArray[3];
        var z2 = distanceArray[5];

        int nx1;
        int nz1;
        int nx2;
        int nz2;
        switch (degree) {
            case 0 -> {
                nx1 = x1;
                nz1 = z1;
                nx2 = x2;
                nz2 = z2;
                int minX = Math.min(nx1, nx2);
                int maxX = Math.max(nx1, nx2);
                int minZ = Math.min(nz1, nz2);
                int maxZ = Math.max(nz1, nz2);
                setDistanceArray(
                        minX,
                        distanceArray[1],
                        minZ,
                        maxX,
                        distanceArray[4],
                        maxZ
                );
            }
            case 90 -> {
                nx1 = z1;
                nz1 = -x1;
                nx2 = z2;
                nz2 = -x2;
                int minX = Math.min(nx1, nx2);
                int maxX = Math.max(nx1, nx2);
                int minZ = Math.min(nz1, nz2);
                int maxZ = Math.max(nz1, nz2);

                setDistanceArray(
                        minX,
                        distanceArray[1],
                        minZ,
                        maxX,
                        distanceArray[4],
                        maxZ
                );
            }
            case 180 -> {
                nx1 = -x1;
                nz1 = -z1;
                nx2 = -x2;
                nz2 = -z2;
                int minX = Math.min(nx1, nx2);
                int maxX = Math.max(nx1, nx2);
                int minZ = Math.min(nz1, nz2);
                int maxZ = Math.max(nz1, nz2);

                setDistanceArray(
                        minX,
                        distanceArray[1],
                        minZ,
                        maxX,
                        distanceArray[4],
                        maxZ
                );
            }
            case 270 -> {
                nx1 = -z1;
                nz1 = x1;
                nx2 = -z2;
                nz2 = x2;
                int minX = Math.min(nx1, nx2);
                int maxX = Math.max(nx1, nx2);
                int minZ = Math.min(nz1, nz2);
                int maxZ = Math.max(nz1, nz2);

                setDistanceArray(
                        minX,
                        distanceArray[1],
                        minZ,
                        maxX,
                        distanceArray[4],
                        maxZ
                );
            }
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
