package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.TrialShroudBlock;
import io.github.tobyrue.btc.misc.CornerStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.TrialSpawnerBlockEntity;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TrialShroudBlockEntity extends BlockEntity implements BlockEntityTicker<TrialShroudBlockEntity>, CornerStorage {
    private BlockBox customBox;
    private int[] distanceArray;
    private Direction lastDirection = Direction.NORTH;

    public TrialShroudBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIAL_SHROUD_BLOCK_ENTITY, pos, state);
    }
    @Override
    public void tick(World world, BlockPos pos, BlockState state, TrialShroudBlockEntity blockEntity) {
        if (world.isClient) return;

        if (distanceArray == null || distanceArray.length < 6) {
            if (customBox == null) {
                distanceArray = new int[]{ 0, 0, 0, 0, 0, 0 };
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

        Direction currentDirection = state.get(TrialShroudBlock.FACING);
        if (currentDirection != lastDirection) {
            int rotationAngle = getDegreesBetween(lastDirection, currentDirection);
            if (rotationAngle != 0) {
                rotateBlockBox(rotationAngle);
            }
            lastDirection = currentDirection;
        }

        if (state.get(TrialShroudBlock.MIRRORED) != BlockMirror.NONE) {
            mirrorBlockBox(state.get(TrialShroudBlock.MIRRORED));
            world.setBlockState(
                    pos,
                    state.with(TrialShroudBlock.MIRRORED, BlockMirror.NONE),
                    Block.NOTIFY_LISTENERS | Block.NO_REDRAW
            );
        }

        Box box = getBox(pos);

        List<Entity> entities =
                world.getEntitiesByClass(Entity.class, box, e -> e instanceof PlayerEntity);


        if (entities.isEmpty() && world instanceof ServerWorld serverWorld) {
            resetTrialSpawnerUnderneath(serverWorld, pos);
        }
    }

    private int getDegreesBetween(Direction from, Direction to) {
        if (from == to || from == Direction.UP || from == Direction.DOWN || to == Direction.UP || to == Direction.DOWN) {
            return 0;
        }
        int fromAngle = (int) from.asRotation();
        int toAngle = (int) to.asRotation();
        return (toAngle - fromAngle + 360) % 360;
    }


    private void mirrorBlockBox(BlockMirror mirror) {
        if (distanceArray == null || distanceArray.length < 6) return;

        int x1 = distanceArray[0];
        int y1 = distanceArray[1];
        int z1 = distanceArray[2];
        int x2 = distanceArray[3];
        int y2 = distanceArray[4];
        int z2 = distanceArray[5];

        switch (mirror) {
            case NONE -> {}
            case LEFT_RIGHT -> {
                int nz1 = -z2;
                int nz2 = -z1;
                setDistanceArray(x1, y1, nz1, x2, y2, nz2);
            }
            case FRONT_BACK -> {
                int nx1 = -x2;
                int nx2 = -x1;
                setDistanceArray(nx1, y1, z1, nx2, y2, z2);
            }
        }
    }

    private void rotateBlockBox(int degree) {
        if (distanceArray == null || distanceArray.length < 6) return;

        int x1 = distanceArray[0];
        int y1 = distanceArray[1];
        int z1 = distanceArray[2];
        int x2 = distanceArray[3];
        int y2 = distanceArray[4];
        int z2 = distanceArray[5];

        int nx1, nz1, nx2, nz2;

        switch (degree) {
            case 90 -> {
                nx1 = -z2;
                nz1 = x1;
                nx2 = -z1;
                nz2 = x2;
            }
            case 180 -> {
                nx1 = -x2;
                nz1 = -z2;
                nx2 = -x1;
                nz2 = -z1;
            }
            case 270 -> {
                nx1 = z1;
                nz1 = -x2;
                nx2 = z2;
                nz2 = -x1;
            }
            default -> {
                nx1 = x1; nz1 = z1; nx2 = x2; nz2 = z2;
            }
        }

        int minX = Math.min(nx1, nx2);
        int maxX = Math.max(nx1, nx2);
        int minZ = Math.min(nz1, nz2);
        int maxZ = Math.max(nz1, nz2);

        setDistanceArray(minX, y1, minZ, maxX, y2, maxZ);
    }

    public @NotNull Box getBox(BlockPos pos) {
        if (customBox != null) {
            return new Box(
                    customBox.getMinX(),
                    customBox.getMinY(),
                    customBox.getMinZ(),
                    customBox.getMaxX() + 1,
                    customBox.getMaxY() + 1,
                    customBox.getMaxZ() + 1
            );
        } else {
            return new Box(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    pos.getX() + 1,
                    pos.getY() + 1,
                    pos.getZ() + 1
            );
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        if (distanceArray != null && distanceArray.length == 6) {
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
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("CustomBox")) {
            int[] loadedArray = nbt.getIntArray("CustomBox");
            if (loadedArray.length == 6) {
                distanceArray = loadedArray;
                customBox = null;
            }
        }
        if (nbt.contains("DirectionNumber")) {
            lastDirection = switch (nbt.getInt("DirectionNumber")) {
                case 1 -> Direction.NORTH;
                case 2 -> Direction.EAST;
                case 3 -> Direction.SOUTH;
                case 4 -> Direction.WEST;
                default -> Direction.NORTH;
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
        distanceArray = new int[]{ minX, minY, minZ, maxX, maxY, maxZ };
        customBox = BlockBox.create(
                new BlockPos(pos.getX() + distanceArray[0], pos.getY() + distanceArray[1], pos.getZ() + distanceArray[2]),
                new BlockPos(pos.getX() + distanceArray[3], pos.getY() + distanceArray[4], pos.getZ() + distanceArray[5])
        );
    }

    @Override
    public BlockBox getBox(ItemStack stack, BlockPos blockPos, BlockState state, World world) {
        return customBox;
    }

    public static void resetTrialSpawnerUnderneath(ServerWorld world, BlockPos targetPos) {
        BlockPos spawnerPos = targetPos.down();
        BlockEntity blockEntity = world.getBlockEntity(spawnerPos);

        if (blockEntity instanceof TrialSpawnerBlockEntity trialSpawner) {
            TrialSpawnerLogic logic = trialSpawner.getSpawner();
            TrialSpawnerState currentState = logic.getSpawnerState();

            if (currentState != TrialSpawnerState.INACTIVE && currentState != TrialSpawnerState.COOLDOWN) {
                TrialSpawnerData data = logic.getData();

                Set<UUID> spawnedUuids = data.spawnedMobsAlive;
                for (UUID uuid : spawnedUuids) {
                    Entity entity = world.getEntity(uuid);
                    if (entity != null && entity.isAlive()) {
                        entity.kill();
                    }
                }

                data.resetAndClearMobs(logic, world);
                logic.setSpawnerState(world, TrialSpawnerState.WAITING_FOR_PLAYERS);

                trialSpawner.markDirty();
                world.updateListeners(spawnerPos, trialSpawner.getCachedState(), trialSpawner.getCachedState(), 3);
            }
        }
    }
}
