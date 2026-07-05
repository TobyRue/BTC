package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.PotionPillar;
import io.github.tobyrue.btc.client.RuneTextLoader;
import io.github.tobyrue.btc.misc.CornerStorage;
import io.github.tobyrue.btc.misc.StatusEffectHolderBlockEntity;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;



public class PotionPillarBlockEntity extends BlockEntity implements BlockEntityTicker<PotionPillarBlockEntity>, CornerStorage, StatusEffectHolderBlockEntity {
    private BlockBox customBox;
    private int[] distanceArray;
    private Direction lastDirection = Direction.NORTH;
    private int runeIndex = 1;

    private RegistryEntry<StatusEffect> storedEffect = ModStatusEffects.BUILDER_BLUNDER;
    private int amplifier = 0;
    private int duration = 160;

    public PotionPillarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POTION_PILLAR_BLOCK_ENTITY, pos, state);
    }
    private int tickCounter = 0;

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
        return isPowered() ? hslToRGB(rgbToHSL(storedEffect.value().getColor()).multiply(1d, 1d, 0.35d)) : storedEffect.value().getColor();
    }




    private boolean isPowered() {
        if (world.getBlockState(pos).getBlock() instanceof PotionPillar) {
            return IDungeonWire.isReceivingDungeonWirePower(world.getBlockState(pos), world, pos, switch (world.getBlockState(pos).get(PotionPillar.AXIS)) {
                case X -> new Direction[]{Direction.EAST, Direction.WEST};
                case Y -> new Direction[]{Direction.UP, Direction.DOWN};
                case Z -> new Direction[]{Direction.NORTH, Direction.SOUTH};
            });
        }
        return false;
    }

    public int getRuneIndex() {
        return runeIndex;
    }


    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getComponents().contains(DataComponentTypes.POTION_CONTENTS) && Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.POTION_CONTENTS)).potion().isPresent() && player.isCreative() && player.hasPermissionLevel(2)) {
            setPotionContents(Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.POTION_CONTENTS)).potion().get().value().getEffects().getFirst().getEffectType());
            setDuration(Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.POTION_CONTENTS)).potion().get().value().getEffects().getFirst().getDuration());
            setAmplifier(Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.POTION_CONTENTS)).potion().get().value().getEffects().getFirst().getAmplifier());
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    public void assignRandomRune(World world) {
        runeIndex = world.getRandom().nextInt(RuneTextLoader.getRunes().size() - 1);
        markDirty();
    }

    public void checkPlayersInRange(ServerWorld world, BlockPos blockPos, BlockState state, double range) {
        if (storedEffect == null) {
            return;
        }
        boolean powered = IDungeonWire.isReceivingDungeonWirePower(state, world, pos, state.get(PotionPillar.AXIS).getType().stream());
        if (!powered) {
            Box box = new Box(new Vec3d(pos.getX() - range, pos.getY() - range, pos.getZ() - range), new Vec3d(pos.getX() + range, pos.getY() + range, pos.getZ() + range));
            for (var l : world.getEntitiesByClass(LivingEntity.class, box, LivingEntity::isAlive)) {
                l.addStatusEffect(new StatusEffectInstance(storedEffect, duration, amplifier));
            }
        }
    }

    public void checkPlayersInRangeViaSelector(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (storedEffect == null) {
            return;
        }
        boolean powered = IDungeonWire.isReceivingDungeonWirePower(state, world, pos, state.get(PotionPillar.AXIS).getType().stream());
        if (!powered) {
            for (var l : world.getEntitiesByClass(LivingEntity.class, getBox(pos), LivingEntity::isAlive)) {
                l.addStatusEffect(new StatusEffectInstance(storedEffect, duration, amplifier));
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
                return;
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
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
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
            nbt.putString("Effect", String.valueOf(storedEffect.getKey().get().getValue()));
        }
        nbt.putInt("Duration", duration);
        nbt.putInt("Amplifier", amplifier);
        nbt.putInt("RuneIndex", Math.min(runeIndex, RuneTextLoader.getRunes().size() - 1));
    }

    @Override
    public void setPotionContents(RegistryEntry<StatusEffect> storedEffect) {
        this.storedEffect = storedEffect;
        this.markDirty();
    }
    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }
    @Override
    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
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

            Identifier id = Identifier.of(nbt.getString("Effect"));

            setPotionContents(
                    Objects.requireNonNull(Registries.STATUS_EFFECT.getEntry(id).orElse(null))
            );
        }
        if (nbt.contains("Duration")) {
            setDuration(nbt.getInt("Duration"));
        }
        if (nbt.contains("Amplifier")) {
            setAmplifier(nbt.getInt("Amplifier"));
        }
        if (nbt.contains("RuneIndex")) {
            runeIndex = Math.min(nbt.getInt("RuneIndex"), RuneTextLoader.getRunes().size() - 1);
        }
    }
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public static Vec3d rgbToHSL(int hex) {
        double r = ((hex & 0xFF0000) >> 16) / 255d;
        double g = ((hex & 0x00FF00) >> 8) / 255d;
        double b = ((hex & 0x0000FF) >> 0) / 255d;

        double cMax = Math.max(r, Math.max(g, b));
        double cMin = Math.min(r, Math.min(g, b));

        double delta = cMax - cMin;

        double l = (cMax + cMin) / 2;
        double h =
                cMax == r ? 60d * (((g-b)/delta) % 6d) :
                cMax == g ? 60d * (((b-r)/delta) + 2d) :
                cMax == b ? 60d * (((r-g)/delta) + 4d) : 0d;
        double s = delta != 0d ? delta/(1d-Math.abs(2d*l-1d)) : 0d;

        return new Vec3d(h, s, l);
    }

    public static int hslToRGB(Vec3d hsl) {
        double h = hsl.x;
        double s = hsl.y;
        double l = hsl.z;

        double c = (1d - Math.abs(2d*l-1d)) * s;
        double x = c*(1d-Math.abs(((h/60d)%2d)-1d));
        double m = l-(c/2d);

        Vec3d t;

        if (0d <= h && h < 60d) {
            t = new Vec3d(c, x, 0d);
        } else if (60d <= h && h < 120d) {
            t = new Vec3d(x, c, 0d);
        } else if (120d <= h && h < 180d) {
            t = new Vec3d(0d, c, x);
        } else if (180d <= h && h < 240d) {
            t = new Vec3d(0d, x, c);
        } else if (240d <= h && h < 300d) {
            t = new Vec3d(x, 0d, c);
        } else if (300d <= h && h < 360d) {
            t = new Vec3d(c, 0d, x);
        } else {
            t = new Vec3d(0d, 0d, 0d);
        }

        return (((int) ((t.x + m) * 255d)) << 16) | (((int) ((t.y + m) * 255d)) << 8) | (((int) ((t.z + m) * 255d)) << 0);
    }
}
