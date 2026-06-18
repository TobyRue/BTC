package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.entity.custom.SuperHappyKillBallEntity;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class BellowBlock extends HorizontalFacingBlock {
    public static final MapCodec<BellowBlock> CODEC = createCodec(BellowBlock::new);

    // --- BASELINE DEFINITIONS (Your exact Floor-North Inputs) ---
    private static final VoxelShape V1_BASE = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.25),
            VoxelShapes.cuboid(0, 0, 0.75, 1, 1, 1),
            VoxelShapes.cuboid(0.125, 0, 0.25, 0.875, 0.875, 0.75),
            VoxelShapes.cuboid(0.375, 0.875, 0.375, 0.625, 1, 0.625)
    );

    private static final VoxelShape V2_BASE = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.25),
            VoxelShapes.cuboid(0, 0, 0.6875, 1, 1, 0.9375),
            VoxelShapes.cuboid(0.125, 0, 0.25, 0.875, 0.875, 0.6875),
            VoxelShapes.cuboid(0.375, 0.875, 0.3125, 0.625, 1, 0.5625)
    );

    private static final VoxelShape V3_BASE = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.25),
            VoxelShapes.cuboid(0, 0, 0.625, 1, 1, 0.875),
            VoxelShapes.cuboid(0.125, 0, 0.25, 0.875, 0.875, 0.625),
            VoxelShapes.cuboid(0.375, 0.875, 0.3125, 0.625, 1, 0.5625)
    );

    // --- LEVEL 0 (V1) STATIC BOUNDS ---
    public static final VoxelShape V1_FLOOR_NORTH = rotateShape(BlockFace.FLOOR, Direction.NORTH, V1_BASE);
    public static final VoxelShape V1_FLOOR_SOUTH = rotateShape(BlockFace.FLOOR, Direction.SOUTH, V1_BASE);
    public static final VoxelShape V1_FLOOR_EAST  = rotateShape(BlockFace.FLOOR, Direction.EAST,  V1_BASE);
    public static final VoxelShape V1_FLOOR_WEST  = rotateShape(BlockFace.FLOOR, Direction.WEST,  V1_BASE);

    public static final VoxelShape V1_CEILING_NORTH = rotateShape(BlockFace.CEILING, Direction.NORTH, V1_BASE);
    public static final VoxelShape V1_CEILING_SOUTH = rotateShape(BlockFace.CEILING, Direction.SOUTH, V1_BASE);
    public static final VoxelShape V1_CEILING_EAST  = rotateShape(BlockFace.CEILING, Direction.EAST,  V1_BASE);
    public static final VoxelShape V1_CEILING_WEST  = rotateShape(BlockFace.CEILING, Direction.WEST,  V1_BASE);

    public static final VoxelShape V1_WALL_NORTH = rotateShape(BlockFace.WALL, Direction.NORTH, V1_BASE);
    public static final VoxelShape V1_WALL_SOUTH = rotateShape(BlockFace.WALL, Direction.SOUTH, V1_BASE);
    public static final VoxelShape V1_WALL_EAST  = rotateShape(BlockFace.WALL, Direction.EAST,  V1_BASE);
    public static final VoxelShape V1_WALL_WEST  = rotateShape(BlockFace.WALL, Direction.WEST,  V1_BASE);

    // --- LEVEL 1 (V2) STATIC BOUNDS ---
    public static final VoxelShape V2_FLOOR_NORTH = rotateShape(BlockFace.FLOOR, Direction.NORTH, V2_BASE);
    public static final VoxelShape V2_FLOOR_SOUTH = rotateShape(BlockFace.FLOOR, Direction.SOUTH, V2_BASE);
    public static final VoxelShape V2_FLOOR_EAST  = rotateShape(BlockFace.FLOOR, Direction.EAST,  V2_BASE);
    public static final VoxelShape V2_FLOOR_WEST  = rotateShape(BlockFace.FLOOR, Direction.WEST,  V2_BASE);

    public static final VoxelShape V2_CEILING_NORTH = rotateShape(BlockFace.CEILING, Direction.NORTH, V2_BASE);
    public static final VoxelShape V2_CEILING_SOUTH = rotateShape(BlockFace.CEILING, Direction.SOUTH, V2_BASE);
    public static final VoxelShape V2_CEILING_EAST  = rotateShape(BlockFace.CEILING, Direction.EAST,  V2_BASE);
    public static final VoxelShape V2_CEILING_WEST  = rotateShape(BlockFace.CEILING, Direction.WEST,  V2_BASE);

    public static final VoxelShape V2_WALL_NORTH = rotateShape(BlockFace.WALL, Direction.NORTH, V2_BASE);
    public static final VoxelShape V2_WALL_SOUTH = rotateShape(BlockFace.WALL, Direction.SOUTH, V2_BASE);
    public static final VoxelShape V2_WALL_EAST  = rotateShape(BlockFace.WALL, Direction.EAST,  V2_BASE);
    public static final VoxelShape V2_WALL_WEST  = rotateShape(BlockFace.WALL, Direction.WEST,  V2_BASE);

    // --- LEVEL 2 (V3) STATIC BOUNDS ---
    public static final VoxelShape V3_FLOOR_NORTH = rotateShape(BlockFace.FLOOR, Direction.NORTH, V3_BASE);
    public static final VoxelShape V3_FLOOR_SOUTH = rotateShape(BlockFace.FLOOR, Direction.SOUTH, V3_BASE);
    public static final VoxelShape V3_FLOOR_EAST  = rotateShape(BlockFace.FLOOR, Direction.EAST,  V3_BASE);
    public static final VoxelShape V3_FLOOR_WEST  = rotateShape(BlockFace.FLOOR, Direction.WEST,  V3_BASE);

    public static final VoxelShape V3_CEILING_NORTH = rotateShape(BlockFace.CEILING, Direction.NORTH, V3_BASE);
    public static final VoxelShape V3_CEILING_SOUTH = rotateShape(BlockFace.CEILING, Direction.SOUTH, V3_BASE);
    public static final VoxelShape V3_CEILING_EAST  = rotateShape(BlockFace.CEILING, Direction.EAST,  V3_BASE);
    public static final VoxelShape V3_CEILING_WEST  = rotateShape(BlockFace.CEILING, Direction.WEST,  V3_BASE);

    public static final VoxelShape V3_WALL_NORTH = rotateShape(BlockFace.WALL, Direction.NORTH, V3_BASE);
    public static final VoxelShape V3_WALL_SOUTH = rotateShape(BlockFace.WALL, Direction.SOUTH, V3_BASE);
    public static final VoxelShape V3_WALL_EAST  = rotateShape(BlockFace.WALL, Direction.EAST,  V3_BASE);
    public static final VoxelShape V3_WALL_WEST  = rotateShape(BlockFace.WALL, Direction.WEST,  V3_BASE);


    private static VoxelShape rotateShape(BlockFace face, Direction facing, VoxelShape baseFloorNorth) {
        VoxelShape[] buffer = new VoxelShape[]{baseFloorNorth};

        if (face == BlockFace.CEILING) {
            VoxelShape current = VoxelShapes.empty();
            for (Box box : buffer[0].getBoundingBoxes()) {
                current = VoxelShapes.union(current, VoxelShapes.cuboid(
                        box.minX, 1.0 - box.maxY, box.minZ,
                        box.maxX, 1.0 - box.minY, box.maxZ
                ));
            }
            buffer[0] = current;
        } else if (face == BlockFace.WALL) {
            VoxelShape current = VoxelShapes.empty();
            for (Box box : buffer[0].getBoundingBoxes()) {
                current = VoxelShapes.union(current, VoxelShapes.cuboid(
                        box.minX, box.minZ, 1.0 - box.maxY,
                        box.maxX, box.maxZ, 1.0 - box.minY
                ));
            }
            buffer[0] = current;
        }

        int rotationSteps = switch (facing) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };

        for (int i = 0; i < rotationSteps; i++) {
            VoxelShape rotated = VoxelShapes.empty();
            for (Box box : buffer[0].getBoundingBoxes()) {
                rotated = VoxelShapes.union(rotated, VoxelShapes.cuboid(
                        1.0 - box.maxZ, box.minY, box.minX,
                        1.0 - box.minZ, box.maxY, box.maxX
                ));
            }
            buffer[0] = rotated;
        }

        return buffer[0];
    }

    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;
    public static final IntProperty LEVEL = IntProperty.of("level", 0, 2);


    protected BellowBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LEVEL, 0).with(FACE, BlockFace.WALL));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, LEVEL);
    }

    private boolean isReceivingAnyPower(BlockState state, World world, BlockPos pos) {
        if (world.isReceivingRedstonePower(pos)) {
            return true;
        }
        return IDungeonWire.isReceivingDungeonWirePower(state, world, pos, getDirections(state).stream());
    }


    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        this.checkAndScheduleTransition(state, world, pos);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient || state.isOf(oldState.getBlock())) return;
        this.checkAndScheduleTransition(state, world, pos);
    }

    private void checkAndScheduleTransition(BlockState state, World world, BlockPos pos) {
        boolean receivingPower = isReceivingAnyPower(state, world, pos);
        int currentLevel = state.get(LEVEL);

        if ((receivingPower && currentLevel < 2) || (!receivingPower && currentLevel > 0)) {
            if (!world.getBlockTickScheduler().isQueued(pos, this)) {
                world.scheduleBlockTick(pos, this, 2);
            }
        }
    }


    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean receivingPower = isReceivingAnyPower(state, world, pos);
        int currentLevel = state.get(LEVEL);
        int nextLevel = currentLevel;

        if (receivingPower) {
            if (currentLevel < 2) {
                nextLevel = currentLevel + 1;
            }
        } else {
            if (currentLevel > 0) {
                nextLevel = currentLevel - 1;
            }
        }

        if (nextLevel != currentLevel) {
            state = state.with(LEVEL, nextLevel);
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
        }

        if ((receivingPower && nextLevel < 2) || (!receivingPower && nextLevel > 0)) {
            world.scheduleBlockTick(pos, this, 2);
        }

        if (receivingPower && nextLevel == 2) {
            Direction nozzleDir = getDirection(state).getOpposite();

            final int COOK_TIME_REDUCTION_TICKS = 60;

            BlockPos targetPos = pos.offset(nozzleDir);
            BlockEntity targetEntity = world.getBlockEntity(targetPos);

            if (targetEntity instanceof AbstractFurnaceBlockEntity furnace) {
                var delegate = ((io.github.tobyrue.btc.mixin.AbstractFurnaceBlockEntityAccessor) furnace).btc$getPropertyDelegate();

                int currentCookTime = delegate.get(2);
                int totalCookTime = delegate.get(3);

                if (totalCookTime > 0) {
                    int newCookTime = Math.min(totalCookTime - 1, currentCookTime + (COOK_TIME_REDUCTION_TICKS));
                    delegate.set(2, newCookTime);
                    furnace.markDirty();
                }
            }



            Vec3d blockCenter = pos.toCenterPos();

            Vec3d faceCenter = blockCenter.add(Vec3d.of(nozzleDir.getVector()).multiply(0.5));
            Vec3d blastEnd = faceCenter.add(Vec3d.of(nozzleDir.getVector()).multiply(3.0));

            Box searchBox = new Box(faceCenter, blastEnd);

            double expandX = nozzleDir.getAxis() == Direction.Axis.X ? 0.0 : 1.0;
            double expandY = nozzleDir.getAxis() == Direction.Axis.Y ? 0.0 : 1.0;
            double expandZ = nozzleDir.getAxis() == Direction.Axis.Z ? 0.0 : 1.0;
            searchBox = searchBox.expand(expandX, expandY, expandZ);

            Direction facing = getDirection(state).getOpposite();
            Vec3d direction = Vec3d.of(facing.getVector()).normalize();

            double forceStrength = 0.8;
            Vec3d forceVec = direction.multiply(forceStrength);

            double baseFallbackSpeed = 0.75;
            Vec3d fanFaceCenter = pos.toCenterPos().add(direction.multiply(0.5));

            for (var entity : world.getEntitiesByClass(Entity.class, searchBox, entity -> true)) {
                Vec3d currentVel = entity.getVelocity();

                if (entity instanceof SuperHappyKillBallEntity shkbEntity) {
                    double currentSpeed = currentVel.length();

                    double finalSpeed = currentSpeed > 0.001 ? currentSpeed : baseFallbackSpeed;

                    entity.setVelocity(direction.multiply(finalSpeed));
                    entity.velocityModified = true;
                } else {
                    entity.setVelocity(currentVel.add(forceVec));
                    entity.velocityModified = true;
                }
                Vec3d entityCenter = entity.getBoundingBox().getCenter();

                scanForEffects(world, entity, fanFaceCenter, entityCenter);
            }
            world.playSound(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    net.minecraft.sound.SoundEvents.ENTITY_GHAST_SHOOT,
                    net.minecraft.sound.SoundCategory.BLOCKS,
                    0.2f,
                    0.7f
            );
            for (int i = 0; i < 15; i++) {
                spawnBellowsBurstParticles(world, pos, nozzleDir);
            }
        }
    }
    private void scanForEffects(World world, Entity entity, Vec3d source, Vec3d target) {
        Vec3d path = target.subtract(source);
        double distance = path.length();
        Vec3d unitDir = path.normalize();

        for (double d = 0; d < distance; d += 0.5) {
            BlockPos checkPos = BlockPos.ofFloored(source.add(unitDir.multiply(d)));
            BlockState checkState = world.getBlockState(checkPos);

            if (applyElementalEffect(entity, checkState)) {
                break;
            }
        }
    }
    private boolean applyElementalEffect(Entity entity, BlockState state) {
        if (state.isOf(Blocks.FIRE) ||
                state.isOf(Blocks.SOUL_FIRE) ||
                state.getFluidState().isIn(FluidTags.LAVA)) {
            entity.setOnFireFor(5);
            return true;
        }
        else if (state.getFluidState().isIn(FluidTags.WATER)) {
            entity.extinguish();
            if (entity instanceof LivingEntity living && living.hurtByWater()) {
                living.damage(entity.getDamageSources().magic(), 1.0f);
            }
            return true;
        }
        return false;
    }

    private void spawnBellowsBurstParticles(ServerWorld world, BlockPos pos, Direction direction) {
        Vec3d start = pos.toCenterPos().add(Vec3d.of(direction.getVector()).multiply(0.55));

        Vec3d ortho = direction.getAxis() == Direction.Axis.Y ? new Vec3d(1, 0, 0) : new Vec3d(0, 1, 0);

        double dirX = direction.getOffsetX();
        double dirY = direction.getOffsetY();
        double dirZ = direction.getOffsetZ();

        double secX = dirY * ortho.z - dirZ * ortho.y;
        double secY = dirZ * ortho.x - dirX * ortho.z;
        double secZ = dirX * ortho.y - dirY * ortho.x;
        Vec3d secondaryOrtho = new Vec3d(secX, secY, secZ).normalize();

        double ortX = dirY * secondaryOrtho.z - dirZ * secondaryOrtho.y;
        double ortY = dirZ * secondaryOrtho.x - dirX * secondaryOrtho.z;
        double ortZ = dirX * secondaryOrtho.y - dirY * secondaryOrtho.x;
        ortho = new Vec3d(ortX, ortY, ortZ).normalize();

        double offsetX = (world.random.nextDouble() * 2.0 - 1.0) * 0.25;
        double offsetY = (world.random.nextDouble() * 2.0 - 1.0) * 0.25;

        Vec3d crossSectionOffset = ortho.multiply(offsetX).add(secondaryOrtho.multiply(offsetY));
        Vec3d actualStart = start.add(crossSectionOffset);

        Vec3d targetPoint = actualStart.add(Vec3d.of(direction.getVector()).multiply(3.0));

        BlockHitResult hit = world.raycast(new RaycastContext(
                actualStart, targetPoint,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                ShapeContext.absent()
        ));

        double fullPathLength = targetPoint.distanceTo(actualStart);
        double maxTravel = hit.getType() == HitResult.Type.MISS ? fullPathLength : hit.getPos().distanceTo(actualStart);

        if (maxTravel < 0.2) maxTravel = 0.5;

        Vec3d pathDir = Vec3d.of(direction.getVector());

        ParticleEffect elementalParticle = null;
        double elementDist = -1;
        for (double d = 0; d < maxTravel; d += 0.5) {
            BlockPos checkPos = BlockPos.ofFloored(actualStart.add(pathDir.multiply(d)));
            BlockState checkState = world.getBlockState(checkPos);
            elementalParticle = getElementalParticle(checkState);
            if (elementalParticle != null) {
                elementDist = d;
                break;
            }
        }

        int maxAge = 15;
        double dragCompensation = 1.2;
        double finalMultiplier = (1.0 / (double) maxAge) * dragCompensation;

        double spreadFactor = 0.12;
        double randomSpreadX = (world.random.nextDouble() * 2.0 - 1.0) * spreadFactor;
        double randomSpreadY = (world.random.nextDouble() * 2.0 - 1.0) * spreadFactor;
        double randomSpreadZ = (world.random.nextDouble() * 2.0 - 1.0) * spreadFactor;

        Vec3d windTravel = pathDir.multiply(maxTravel);
        Vec3d windVel = windTravel.multiply(finalMultiplier).add(randomSpreadX, randomSpreadY, randomSpreadZ);

        world.spawnParticles(ParticleTypes.CLOUD,
                actualStart.x, actualStart.y, actualStart.z,
                0, windVel.x, windVel.y, windVel.z, 1.0);

        if (elementalParticle != null) {
            Vec3d elemSpawn = actualStart.add(pathDir.multiply(elementDist));
            Vec3d elemTravel = pathDir.multiply(maxTravel - elementDist);
            Vec3d elemVel = elemTravel.multiply(finalMultiplier).add(randomSpreadX, randomSpreadY, randomSpreadZ);

            world.spawnParticles(elementalParticle,
                    elemSpawn.x, elemSpawn.y, elemSpawn.z,
                    0, elemVel.x, elemVel.y, elemVel.z, 1.0);
        }
    }

    private ParticleEffect getElementalParticle(BlockState state) {
        if (!state.getFluidState().isEmpty()) {
            if (state.getFluidState().isIn(FluidTags.LAVA)) return ParticleTypes.FLAME;
            if (state.getFluidState().isIn(FluidTags.WATER)) return ParticleTypes.FISHING;
        }
        if (state.isOf(Blocks.FIRE)) return ParticleTypes.FLAME;
        if (state.isOf(Blocks.SOUL_FIRE)) return ParticleTypes.SOUL_FIRE_FLAME;
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int level = state.get(LEVEL);
        BlockFace face = state.get(FACE);
        Direction facing = state.get(FACING);

        return switch (level) {
            case 0 -> switch (face) {
                case FLOOR -> switch (facing) {
                    case SOUTH -> V1_FLOOR_SOUTH;
                    case EAST -> V1_FLOOR_EAST;
                    case WEST -> V1_FLOOR_WEST;
                    default -> V1_FLOOR_NORTH;
                };
                case CEILING -> switch (facing) {
                    case SOUTH -> V1_CEILING_SOUTH;
                    case EAST -> V1_CEILING_EAST;
                    case WEST -> V1_CEILING_WEST;
                    default -> V1_CEILING_NORTH;
                };
                case WALL -> switch (facing) {
                    case SOUTH -> V1_WALL_SOUTH;
                    case EAST -> V1_WALL_EAST;
                    case WEST -> V1_WALL_WEST;
                    default -> V1_WALL_NORTH;
                };
            };
            case 1 -> switch (face) {
                case FLOOR -> switch (facing) {
                    case SOUTH -> V2_FLOOR_SOUTH;
                    case EAST -> V2_FLOOR_EAST;
                    case WEST -> V2_FLOOR_WEST;
                    default -> V2_FLOOR_NORTH;
                };
                case CEILING -> switch (facing) {
                    case SOUTH -> V2_CEILING_SOUTH;
                    case EAST -> V2_CEILING_EAST;
                    case WEST -> V2_CEILING_WEST;
                    default -> V2_CEILING_NORTH;
                };
                case WALL -> switch (facing) {
                    case SOUTH -> V2_WALL_SOUTH;
                    case EAST -> V2_WALL_EAST;
                    case WEST -> V2_WALL_WEST;
                    default -> V2_WALL_NORTH;
                };
            };
            default -> switch (face) { // level == 2
                case FLOOR -> switch (facing) {
                    case SOUTH -> V3_FLOOR_SOUTH;
                    case EAST -> V3_FLOOR_EAST;
                    case WEST -> V3_FLOOR_WEST;
                    default -> V3_FLOOR_NORTH;
                };
                case CEILING -> switch (facing) {
                    case SOUTH -> V3_CEILING_SOUTH;
                    case EAST -> V3_CEILING_EAST;
                    case WEST -> V3_CEILING_WEST;
                    default -> V3_CEILING_NORTH;
                };
                case WALL -> switch (facing) {
                    case SOUTH -> V3_WALL_SOUTH;
                    case EAST -> V3_WALL_EAST;
                    case WEST -> V3_WALL_WEST;
                    default -> V3_WALL_NORTH;
                };
            };
        };
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] var2 = ctx.getPlacementDirections();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Direction direction = var2[var4];
            BlockState blockState;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockState = this.getDefaultState().with(FACE, direction == Direction.UP ? BlockFace.CEILING : BlockFace.FLOOR).with(FACING, ctx.getHorizontalPlayerFacing());
            } else {
                blockState = this.getDefaultState().with(FACE, BlockFace.WALL).with(FACING, direction.getOpposite());
            }

            if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                return blockState;
            }
        }

        return null;
    }

    protected static Direction getDirection(BlockState state) {
        return switch (state.get(FACE)) {
            case CEILING -> Direction.UP;
            case FLOOR -> Direction.DOWN;
            default -> state.get(FACING).getOpposite();
        };
    }
    protected static List<Direction> getDirections(BlockState state) {
        List<Direction> dirs = new ArrayList<>();

        dirs.add(switch (state.get(FACE)) {
            case CEILING -> Direction.UP;
            case FLOOR -> Direction.DOWN;
            default -> state.get(FACING).getOpposite();
        });
        dirs.add(switch (state.get(FACE)) {
            case WALL -> Direction.DOWN;
            case FLOOR -> state.get(FACING);
            case CEILING -> state.get(FACING).getOpposite();
        });
        return dirs;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }
}
