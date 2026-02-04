package io.github.tobyrue.btc.block.entities;

import com.mojang.logging.LogUtils;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.DungeonWireBlock;
import io.github.tobyrue.btc.block.PotionPillar;
import io.github.tobyrue.btc.enums.AntierType;
import io.github.tobyrue.btc.item.SelectorItem;
import io.github.tobyrue.btc.misc.CornerStorage;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static io.github.tobyrue.btc.block.DungeonWireBlock.POWERED;


public class PotionPillarBlockEntity extends BlockEntity implements BlockEntityTicker<PotionPillarBlockEntity>, CornerStorage {
    private BlockBox customBox;
    private int[] distanceArray;
    private Direction lastDirection = Direction.NORTH;

    private RegistryEntry<StatusEffect> storedEffect = ModStatusEffects.BUILDER_BLUNDER;
    private int amplifier = 0;
    private int duration = 160;

    public PotionPillarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POTION_PILLAR_BLOCK_ENTITY, pos, state);
    }
    private int tickCounter = 0; // Counter to track ticks

    public RegistryEntry<StatusEffect> getStoredEffect() {
        return storedEffect;
    }
    public int getStoredAmplifier() {
        return amplifier;
    }
    public int getStoredDuration() {
        return duration;
    }

    public int getColor() {
        return storedEffect.value().getColor();
    }

    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getComponents().contains(DataComponentTypes.POTION_CONTENTS) && Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.POTION_CONTENTS)).potion().isPresent()) {
            setPotionContents(Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.POTION_CONTENTS)).potion().get().value().getEffects().getFirst().getEffectType());
            setDuration(Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.POTION_CONTENTS)).potion().get().value().getEffects().getFirst().getDuration());
            setAmplifier(Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.POTION_CONTENTS)).potion().get().value().getEffects().getFirst().getAmplifier());
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }


    public void checkPlayersInRange(ServerWorld world, BlockPos blockPos, BlockState state, double range) {
        if (storedEffect == null) {
            return;
        }
        Box box = new Box(new Vec3d(pos.getX() - range, pos.getY() - range, pos.getZ() - range), new Vec3d(pos.getX() + range, pos.getY() + range, pos.getZ() + range));
        for (var l : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (!state.get(PotionPillar.DISABLE)) {
                l.addStatusEffect(new StatusEffectInstance(storedEffect, duration, amplifier));
            } else {
                for (Direction direction : Direction.values()) {
                    BlockPos neighborPos = blockPos.offset(direction);
                    BlockState neighborState = world.getBlockState(neighborPos);
                    if (neighborState.getBlock() instanceof WireBlock) {
                        var property = WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(direction.getOpposite());
                        if (state.get(property) == WireBlock.ConnectionType.OUTPUT) {
                            if (!neighborState.get(WireBlock.POWERED)) {
                                l.addStatusEffect(new StatusEffectInstance(storedEffect, duration, amplifier));
                            }
                        }
                    }
                }
            }
        }
    }

    public void checkPlayersInRangeViaSelector(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (storedEffect == null) {
            return;
        }
        Box box = getBox(pos);
        for (var l : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (!state.get(PotionPillar.DISABLE)) {
                l.addStatusEffect(new StatusEffectInstance(storedEffect, duration, amplifier));
            } else {
                for (Direction direction : Direction.values()) {
                    BlockPos neighborPos = blockPos.offset(direction);
                    BlockState neighborState = world.getBlockState(neighborPos);
                    if (neighborState.getBlock() instanceof WireBlock) {
                        var property = WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(direction.getOpposite());
                        if (state.get(property) == WireBlock.ConnectionType.OUTPUT) {
                            if (!neighborState.get(WireBlock.POWERED)) {
                                l.addStatusEffect(new StatusEffectInstance(storedEffect, duration, amplifier));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick(World world, BlockPos blockPos, BlockState state, PotionPillarBlockEntity blockEntity) {
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

        if (state.get(PotionPillar.FACING) != lastDirection) {
            if (lastDirection == Direction.NORTH) {
                rotateBlockBox(
                        switch (state.get(PotionPillar.FACING)) {
                            case DOWN, UP, NORTH -> 0;
                            case EAST -> 90;
                            case SOUTH -> 180;
                            case WEST -> 270;
                        });
            } else if (lastDirection == Direction.EAST) {

                rotateBlockBox(
                        switch (state.get(PotionPillar.FACING)) {
                            case NORTH -> 270;
                            case DOWN, UP, EAST -> 0;
                            case SOUTH -> 90;
                            case WEST -> 180;
                        });
            } else if (lastDirection == Direction.SOUTH) {
                rotateBlockBox(
                        switch (state.get(PotionPillar.FACING)) {
                            case NORTH -> 180;
                            case EAST -> 270;
                            case DOWN, UP, SOUTH -> 0;
                            case WEST -> 90;
                        });
            } else if (lastDirection == Direction.WEST) {

                rotateBlockBox(
                        switch (state.get(PotionPillar.FACING)) {
                            case NORTH -> 90;
                            case EAST -> 180;
                            case SOUTH -> 270;
                            case DOWN, UP, WEST -> 0;
                        });
            } else if (lastDirection == Direction.DOWN || lastDirection == Direction.UP) {
                rotateBlockBox(
                        switch (state.get(PotionPillar.FACING)) {
                            case NORTH, EAST, SOUTH, WEST, DOWN, UP -> 0;
                        });
            }
            lastDirection = state.get(PotionPillar.FACING);
        }
        if (state.get(PotionPillar.MIRRORED) != BlockMirror.NONE) {
            mirrorBlockBox(state.get(PotionPillar.MIRRORED));
            world.setBlockState(
                    pos,
                    state.with(PotionPillar.MIRRORED, BlockMirror.NONE),
                    Block.NOTIFY_LISTENERS | Block.NO_REDRAW
            );
        }

        if (world instanceof ServerWorld serverWorld) {
            if (!serverWorld.isChunkLoaded(blockPos)) {
                return; // Prevent ticking if the chunk is unloaded
            }

            tickCounter++;

            if (tickCounter % 20 == 0) {
                if (state.get(PotionPillar.USES_SELECTOR)) {
                    checkPlayersInRangeViaSelector(serverWorld, blockPos, state);
                } else {
                    checkPlayersInRange(serverWorld, blockPos, state, 15.0);
                }
            }
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
                int nz1 = -z1;
                int nz2 = -z2;
                setDistanceArray(
                        x1,
                        distanceArray[1],
                        Math.min(nz1, nz2),
                        x2,
                        distanceArray[4],
                        Math.max(nz1, nz2)
                );
            }
            case FRONT_BACK -> {
                int nx1 = -x1;
                int nx2 = -x2;
                setDistanceArray(
                        Math.min(nx1, nx2),
                        distanceArray[1],
                        z1,
                        Math.max(nx1, nx2),
                        distanceArray[4],
                        z2
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

    public @NotNull Box getBox(BlockPos pos) {
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

        if (storedEffect != null) {
            nbt.put("Effect", StatusEffect.ENTRY_CODEC.encodeStart(NbtOps.INSTANCE, storedEffect).getOrThrow());
        }
        nbt.putInt("Duration", duration);
        nbt.putInt("Amplifier", amplifier);
    }

    public void setPotionContents(RegistryEntry<StatusEffect> storedEffect) {
        this.storedEffect = storedEffect;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
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
        if (nbt.contains("Effect")) {
            if (nbt.get("Effect") instanceof NbtCompound compound) {
                setPotionContents(StatusEffect.ENTRY_CODEC.parse(NbtOps.INSTANCE, compound).resultOrPartial(LogUtils.getLogger()::error).orElse(null));
            }
        }
        if (nbt.contains("Duration")) {
            setDuration(nbt.getInt("Duration"));
        }
        if (nbt.contains("Amplifier")) {
            setAmplifier(nbt.getInt("Amplifier"));
        }
    }


//    @Override
//    public void onDungeonWireChange(BlockState state, World world, BlockPos pos, boolean powered) {
//        if (state.get(AntierBlock.DISABLE)) {
//
//        }
//    }
//
//    @Override
//    public void onDungeonWireDestroy(BlockState state, World world, BlockPos pos, boolean powered) {
//        if (state.get(AntierBlock.DISABLE)) {
//
//        }
//    }
}
