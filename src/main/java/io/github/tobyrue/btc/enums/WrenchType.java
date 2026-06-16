package io.github.tobyrue.btc.enums;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.WaxedCopperFanBlock;
import io.github.tobyrue.btc.block.entities.FanBlockEntity;
import io.github.tobyrue.btc.component.BlockPosComponent;
import io.github.tobyrue.btc.misc.CornerStorage;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.wires.WireBlock;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireDelayHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireOperatorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Optional;

public enum WrenchType implements IWrenchType {
    ROTATE("rotate") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();

            var stateRotate = world.getBlockState(pos);
            if (!stateRotate.streamTags().anyMatch(t -> t == BTC.WRENCH_ROTATION_BLACKLIST) && !(stateRotate.getBlock() instanceof PistonBlock && stateRotate.get(PistonBlock.EXTENDED))) {
                Property<?> facingProperty = null;

                if (stateRotate.contains(Properties.FACING)) {
                    facingProperty = Properties.FACING;
                } else if (stateRotate.contains(Properties.HORIZONTAL_FACING)) {
                    facingProperty = Properties.HORIZONTAL_FACING;
                } else if (stateRotate.contains(Properties.HORIZONTAL_AXIS)) {
                    facingProperty = Properties.HORIZONTAL_AXIS;
                } else if (stateRotate.contains(Properties.ORIENTATION)) {
                    facingProperty = Properties.ORIENTATION;
                } else if (stateRotate.contains(Properties.AXIS)) {
                    facingProperty = Properties.AXIS;
                }

                if (facingProperty != null) {
                    world.setBlockState(pos, stateRotate.cycle(facingProperty));
                    return ActionResult.SUCCESS;
                } else {
                    world.setBlockState(pos, stateRotate.rotate(BlockRotation.CLOCKWISE_90));
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.FAIL;
        }
    },
    MIRROR("mirror") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            BlockState state = world.getBlockState(pos);

            if (state.contains(Properties.HORIZONTAL_FACING)) {
                world.setBlockState(pos, state.with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING).getOpposite()));
                return ActionResult.SUCCESS;
            } else if (state.contains(Properties.FACING)) {
                world.setBlockState(pos, state.with(Properties.FACING, state.get(Properties.FACING).getOpposite()));
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        }
    },
    COPY("copy") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            BlockState state = world.getBlockState(pos);
            var player = context.getPlayer();

            if (player != null) {
                NbtComponent nbt = null;
                var be = world.getBlockEntity(pos);
                if (be != null) {
                    nbt = NbtComponent.of(be.createNbtWithId(world.getRegistryManager()));
                }

                WrenchClipboardComponent data = new WrenchClipboardComponent(state, Optional.ofNullable(nbt));
                context.getStack().set(ModComponents.WRENCH_CLIPBOARD, data);

                player.sendMessage(Text.translatable("message.btc.wrench.copied", state.getBlock().getName()), true);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    },
    PASTE("paste") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            BlockState state = world.getBlockState(pos);
            PlayerEntity player = context.getPlayer();
            WrenchClipboardComponent clipboard = context.getStack().get(ModComponents.WRENCH_CLIPBOARD);

            if (clipboard != null && player != null) {
                world.setBlockState(pos.offset(context.getSide()), clipboard.state());

                clipboard.component().ifPresent(nbt -> {
                    var be = world.getBlockEntity(pos);
                    if (be != null) {
                        nbt.applyToBlockEntity(be, world.getRegistryManager());
                    }
                });
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        }
    },
    WIRE("wire") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            ItemStack stack = context.getStack();
            WireSubtype subtype = stack.get(ModComponents.WRENCH_SUBTYPE);
            if (subtype != null) {
                return subtype.useOnBlock(context);
            }
            return WireSubtype.CONNECTION.useOnBlock(context);
        }
    },
    SELECTOR("selector") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            ItemStack stack = context.getStack();
            SelectorSubtype subtype = stack.get(ModComponents.WRENCH_SELECTOR_SUBTYPE);
            if (subtype != null) {
                return subtype.useOnBlock(context);
            }
            return SelectorSubtype.AUTO.useOnBlock(context);
       }
    },
    FAN("fan") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            ItemStack stack = context.getStack();
            FanSubtype subtype = stack.get(ModComponents.WRENCH_FAN_SUBTYPE);
            if (subtype != null) {
                return subtype.useOnBlock(context);
            }
            return FanSubtype.BASE_RADIUS.useOnBlock(context);
        }
    },
    NULL("null") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            return ActionResult.FAIL;
        }
    };
    private final String name;
    public static final Codec<WrenchType> CODEC = Codec.STRING.xmap(
            WrenchType::valueOf,
            WrenchType::name
    );

    WrenchType(String name) {
        this.name = name;
    }
    public String asString() {
        return this.name;
    }
    public String toString() {
        return this.name;
    }

    public record WrenchClipboardComponent(BlockState state, Optional<NbtComponent> component) {
        public static final Codec<WrenchClipboardComponent> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(
                    BlockState.CODEC.fieldOf("state").forGetter(WrenchClipboardComponent::state),
                    NbtComponent.CODEC.optionalFieldOf("component").forGetter(WrenchClipboardComponent::component)
            ).apply(builder, WrenchClipboardComponent::new);
        });
    }
    public enum WireSubtype implements IWrenchType {
        CONNECTION("connection") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);
                Direction side = context.getSide();
                Vec3d hitPos = context.getHitPos();
                ItemStack stack = context.getStack();
                PlayerEntity player = context.getPlayer();

                if ((state.getBlock() instanceof IWireConnectionHelper connectionHelper)) {
                    double u, v;
                    double x = hitPos.x - pos.getX();
                    double y = hitPos.y - pos.getY();
                    double z = hitPos.z - pos.getZ();

                    switch (side) {
                        case UP -> { u = x; v = z; }
                        case DOWN -> { u = x; v = 1 - z; }
                        case NORTH -> { u = 1 - x; v = 1 - y; }
                        case SOUTH -> { u = x; v = 1 - y; }
                        case EAST -> { u = 1 - z; v = 1 - y; }
                        case WEST -> { u = z; v = 1 - y; }
                        default -> { u = 0.5; v = 0.5; }
                    }

                    Direction targetDirection;
                    if (u > 0.25 && u < 0.75 && v > 0.25 && v < 0.75) {
                        targetDirection = (player != null && player.isSneaking()) ? side.getOpposite() : side;
                    } else {
                        if (v < 0.25) targetDirection = getRelativeTop(side);
                        else if (v > 0.75) targetDirection = getRelativeBottom(side);
                        else if (u < 0.25) targetDirection = getRelativeLeft(side);
                        else targetDirection = getRelativeRight(side);
                    }

                    String config = stack.get(ModComponents.WRENCH_CONNECTION);
                    if (config != null && Arrays.stream(WireBlock.ConnectionType.values()).anyMatch(connectionType -> connectionType.asString().equals(config))) {
                        try {
                            connectionHelper.setConnection(targetDirection, WireBlock.ConnectionType.valueOf(config.toUpperCase()), world, state, pos);
                        } catch (Exception e) {
                            connectionHelper.cycleConnection(targetDirection, world, state, pos);
                        }
                    } else {
                        connectionHelper.cycleConnection(targetDirection, world, state, pos);
                    }
                    player.sendMessage(Text.translatable("block.btc.wire.change_connection",
                        Text.translatable("block.btc.wire.face." + targetDirection.asString()),
                        Text.translatable("block.btc.wire.connection." + connectionHelper.getConnection(targetDirection, world, state, pos).asString())), true);
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }
        },
        OPERATOR("operator") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);
                ItemStack stack = context.getStack();
                PlayerEntity player = context.getPlayer();
                if ((context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof IWireOperatorHelper operatorHelper)) {
                    String op = stack.get(ModComponents.WRENCH_OPERATOR);
                    if (op != null && !op.equals("null")) {
                        try {
                            operatorHelper.setOperator(WireBlock.Operator.valueOf(op.toUpperCase()), context.getWorld(), context.getWorld().getBlockState(context.getBlockPos()), context.getBlockPos());
                        } catch (Exception e) {
                            operatorHelper.cycleOperator(context.getWorld(), context.getWorld().getBlockState(context.getBlockPos()), context.getBlockPos());
                        }
                    } else {
                        operatorHelper.cycleOperator(context.getWorld(), context.getWorld().getBlockState(context.getBlockPos()), context.getBlockPos());
                    }
                    player.sendMessage(Text.translatable("block.btc.wire.change_operator",
                        Text.translatable("block.btc.wire.operator." + operatorHelper.getOperator(world, state, pos).asString())), true);
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }
        },
        DELAY("delay") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                ItemStack stack = context.getStack();
                PlayerEntity player = context.getPlayer();
                if ((context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof IWireDelayHelper delayHelper)) {

                    int delay = stack.getOrDefault(ModComponents.WRENCH_DELAY, -1);
                    if (!world.isClient()) {
                        if (delay >= 0) {
                            delayHelper.setDelay(delay, context.getWorld(), context.getWorld().getBlockState(context.getBlockPos()), context.getBlockPos());
                        } else {
                            int current = delayHelper.getDelay(context.getWorld(), context.getWorld().getBlockState(context.getBlockPos()), context.getBlockPos());
                            delayHelper.setDelay(((current + 1) % 8), context.getWorld(), context.getWorld().getBlockState(context.getBlockPos()), context.getBlockPos());
                        }
                    }
                    int current = delayHelper.getDelay(context.getWorld(), context.getWorld().getBlockState(context.getBlockPos()), context.getBlockPos());
                    player.sendMessage(Text.translatable("block.btc.wire.delay.change_delay", (current) % 8), true);
                    return ActionResult.SUCCESS;
                } else {
                    return ActionResult.FAIL;
                }
            }
        },
        NULL("null") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                return ActionResult.FAIL;
            }
        };

        private static Direction getRelativeTop(Direction side) {
            return switch (side) {
                case UP -> Direction.NORTH;
                case DOWN -> Direction.SOUTH;
                default -> Direction.UP;
            };
        }
        private static Direction getRelativeBottom(Direction side) {
            return getRelativeTop(side).getOpposite();
        }

        private static Direction getRelativeLeft(Direction side) {
            return switch (side) {
                case UP -> Direction.WEST;
                case DOWN -> Direction.EAST;
                case NORTH -> Direction.EAST;
                case SOUTH -> Direction.WEST;
                case EAST -> Direction.SOUTH;
                case WEST -> Direction.NORTH;
            };
        }
        private static Direction getRelativeRight(Direction side) {
            return getRelativeLeft(side).getOpposite();
        }
        private final String name;
        public static final Codec<WireSubtype> CODEC = Codec.STRING.xmap(
                 WireSubtype::valueOf,
                WireSubtype::name
        );

        WireSubtype(String name) {
            this.name = name;
        }
        public String asString() {
            return this.name;
        }
        public String toString() {
            return this.name;
        }

    }
    public enum SelectorSubtype implements IWrenchType {
        AUTO("selector_auto") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                PlayerEntity player = context.getPlayer();
                if (player == null || !player.isCreative()) return ActionResult.PASS;

                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);
                ItemStack stack = context.getStack();
                int clearY = world.getBottomY() - 50;

                if (state.getBlock() instanceof CornerStorage cornerStorage) {
                    var cs = cornerStorage.getBox(stack, pos, state, world);
                    if (cs != null) {
                        if (!world.isClient()) {
                            stack.set(ModComponents.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(cs.getMinX(), cs.getMinY(), cs.getMinZ()));
                            stack.set(ModComponents.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(cs.getMaxX(), cs.getMaxY(), cs.getMaxZ()));
                            player.sendMessage(Text.translatable("item.btc.selector.corner_1_and_2_set",
                                    new BlockPos(cs.getMinX(), cs.getMinY(), cs.getMinZ()).toShortString(),
                                    new BlockPos(cs.getMaxX(), cs.getMaxY(), cs.getMaxZ()).toShortString()), true);
                            return ActionResult.SUCCESS;
                        }
                    }
                }

                var corner2 = stack.get(ModComponents.CORNER_2_POSITION_COMPONENT);

                if (corner2 != null && corner2.y() > clearY) {
                    stack.set(ModComponents.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(pos.getX(), pos.getY(), pos.getZ()));

                    stack.set(ModComponents.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(0, clearY, 0));

                    player.sendMessage(Text.translatable("item.btc.selector.corner_1_set", pos.toShortString()), true);
                } else {
                    stack.set(ModComponents.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(pos.getX(), pos.getY(), pos.getZ()));
                    player.sendMessage(Text.translatable("item.btc.selector.corner_2_set", pos.toShortString()), true);
                }

                return ActionResult.SUCCESS;
            }
        },
        POS1("selector_pos1") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                PlayerEntity player = context.getPlayer();
                if (player == null || !player.isCreative()) return ActionResult.PASS;

                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);
                ItemStack stack = context.getStack();

                if (state.getBlock() instanceof CornerStorage cornerStorage) {
                    var cs = cornerStorage.getBox(stack, pos, state, world);
                    if (cs != null) {
                        if (!world.isClient()) {
                            stack.set(ModComponents.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(cs.getMinX(), cs.getMinY(), cs.getMinZ()));
                            stack.set(ModComponents.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(cs.getMaxX(), cs.getMaxY(), cs.getMaxZ()));
                            player.sendMessage(Text.translatable("item.btc.selector.corner_1_and_2_set",
                                    new BlockPos(cs.getMinX(), cs.getMinY(), cs.getMinZ()).toShortString(),
                                    new BlockPos(cs.getMaxX(), cs.getMaxY(), cs.getMaxZ()).toShortString()), true);
                            return ActionResult.SUCCESS;
                        }
                    }
                }

                stack.set(ModComponents.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(pos.getX(), pos.getY(), pos.getZ()));
                player.sendMessage(Text.translatable("item.btc.selector.corner_1_set", pos.toShortString()), true);
                return ActionResult.SUCCESS;
            }
        },
        POS2("selector_pos2") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                PlayerEntity player = context.getPlayer();
                if (player == null || !player.isCreative()) return ActionResult.PASS;

                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);
                ItemStack stack = context.getStack();

                if (state.getBlock() instanceof CornerStorage cornerStorage) {
                    var cs = cornerStorage.getBox(stack, pos, state, world);
                    if (cs != null) {
                        if (!world.isClient()) {
                            stack.set(ModComponents.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(cs.getMinX(), cs.getMinY(), cs.getMinZ()));
                            stack.set(ModComponents.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(cs.getMaxX(), cs.getMaxY(), cs.getMaxZ()));
                            player.sendMessage(Text.translatable("item.btc.selector.corner_1_and_2_set",
                                    new BlockPos(cs.getMinX(), cs.getMinY(), cs.getMinZ()).toShortString(),
                                    new BlockPos(cs.getMaxX(), cs.getMaxY(), cs.getMaxZ()).toShortString()), true);
                            return ActionResult.SUCCESS;
                        }
                    }
                }

                stack.set(ModComponents.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(pos.getX(), pos.getY(), pos.getZ()));
                player.sendMessage(Text.translatable("item.btc.selector.corner_2_set", pos.toShortString()), true);
                return ActionResult.SUCCESS;
            }
        },
        CLEAR("selector_clear") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                PlayerEntity player = context.getPlayer();
                if (player == null) return ActionResult.PASS;

                ItemStack stack = context.getStack();
                World world = context.getWorld();
                int clearY = world.getBottomY() - 50;

                stack.set(ModComponents.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(0, clearY, 0));
                stack.set(ModComponents.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(0, clearY, 0));

                player.sendMessage(Text.translatable("item.btc.wrench.selector.cleared"), true);
                return ActionResult.SUCCESS;
            }
        },
        NULL("null") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                return ActionResult.FAIL;
            }
        };

        private final String name;
        public static final Codec<SelectorSubtype> CODEC = Codec.STRING.xmap(SelectorSubtype::valueOf, SelectorSubtype::name);
        SelectorSubtype(String name) { this.name = name; }
        public String asString() { return this.name; }
    }
    public enum FanSubtype implements IWrenchType {
        DEPTH("fan_depth") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);
                ItemStack stack = context.getStack();
                PlayerEntity player = context.getPlayer();

                if (world.getBlockEntity(pos) instanceof FanBlockEntity fanBlock) {
                    double wrenchDepth = stack.getOrDefault(ModComponents.WRENCH_FAN_DEPTH, -1.0);

                    if (!world.isClient()) {
                        if (wrenchDepth >= 0) {
                            fanBlock.setDepth(wrenchDepth);
                        } else {
                            double current = fanBlock.getDepth();
                            fanBlock.setDepth((current % 16) + 1);
                        }
                    }
                    double finalDepth = fanBlock.getDepth();
                    if (player != null) {
                        player.sendMessage(Text.translatable("item.btc.wrench.fan.depth_changed", finalDepth), true);
                    }
                    return ActionResult.SUCCESS;
                }
                return ActionResult.FAIL;
            }
        },
        BASE_RADIUS("fan_base_radius") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);
                ItemStack stack = context.getStack();
                PlayerEntity player = context.getPlayer();

                if (world.getBlockEntity(pos) instanceof FanBlockEntity fanBlock) {
                    double wrenchRadius = stack.getOrDefault(ModComponents.WRENCH_FAN_BASE_RADIUS, -1.0);

                    if (!world.isClient()) {
                        if (wrenchRadius >= 0) {
                            fanBlock.setBaseRadius(wrenchRadius);
                        } else {
                            double current = fanBlock.getBaseRadius();
                            fanBlock.setBaseRadius((current + 1) % 9);
                        }
                    }
                    double finalRadius = fanBlock.getBaseRadius();
                    if (player != null) {
                        player.sendMessage(Text.translatable("item.btc.wrench.fan.base_radius_changed", finalRadius), true);
                    }
                    return ActionResult.SUCCESS;
                }
                return ActionResult.FAIL;
            }
        },
        FAR_RADIUS("fan_far_radius") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);
                ItemStack stack = context.getStack();
                PlayerEntity player = context.getPlayer();

                if (world.getBlockEntity(pos) instanceof FanBlockEntity fanBlock) {
                    double wrenchRadius = stack.getOrDefault(ModComponents.WRENCH_FAN_FAR_RADIUS, -1.0);

                    if (!world.isClient()) {
                        if (wrenchRadius >= 0) {
                            fanBlock.setFarRadius(wrenchRadius);
                        } else {
                            double current = fanBlock.getFarRadius();
                            fanBlock.setFarRadius((current % 12) + 1);
                        }
                    }
                    double finalRadius = fanBlock.getFarRadius();
                    if (player != null) {
                        player.sendMessage(Text.translatable("item.btc.wrench.fan.far_radius_changed", finalRadius), true);
                    }
                    return ActionResult.SUCCESS;
                }
                return ActionResult.FAIL;
            }
        },
        SHOW_CONE("fan_show_cone") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                ItemStack stack = context.getStack();
                PlayerEntity player = context.getPlayer();
                World world = context.getWorld();
                BlockPos pos = context.getBlockPos();
                BlockState state = world.getBlockState(pos);


                if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() instanceof WaxedCopperFanBlock) {
                    Direction side = context.getWorld().getBlockState(context.getBlockPos()).get(FacingBlock.FACING);
                    Vec3d start = context.getBlockPos().toCenterPos().add(Vec3d.of(side.getVector()).multiply(0.5));
                    Vec3d direction = new Vec3d(side.getOffsetX(), side.getOffsetY(), side.getOffsetZ());

                    double depth = stack.getOrDefault(ModComponents.WRENCH_FAN_DEPTH, 5.0);
                    double baseRadius = stack.getOrDefault(ModComponents.WRENCH_FAN_BASE_RADIUS, 1.0);
                    double farRadius = stack.getOrDefault(ModComponents.WRENCH_FAN_FAR_RADIUS, 3.0);

                    if (world.getBlockEntity(pos) instanceof FanBlockEntity fanBlock) {
                        depth = fanBlock.getDepth();
                        baseRadius = fanBlock.getBaseRadius();
                        farRadius = fanBlock.getFarRadius();
                    }

                    stack.set(ModComponents.WRENCH_FAN_DEPTH, depth);
                    stack.set(ModComponents.WRENCH_FAN_BASE_RADIUS, baseRadius);
                    stack.set(ModComponents.WRENCH_FAN_FAR_RADIUS ,farRadius);

                    drawDebugCone(world, start, direction, depth, baseRadius, farRadius);
                }
                return ActionResult.SUCCESS;
            }
        },
        NULL("null") {
            @Override
            public ActionResult useOnBlock(ItemUsageContext context) {
                return ActionResult.FAIL;
            }
        };

        private static void drawDebugCone(World world, Vec3d start, Vec3d direction, double depth, double base_radius, double far_radius) {
            int levels = 12;
            int steps = 12;

            Vec3d right = direction.crossProduct(new Vec3d(0, 1, 0));
            if (right.lengthSquared() < 1e-6) right = direction.crossProduct(new Vec3d(1, 0, 0));
            right = right.normalize();
            Vec3d up = right.crossProduct(direction).normalize();

            for (int j = 0; j <= levels; j++) {
                double t = (double) j / levels;
                double currentDepth = t * depth;
                double currentRadius = base_radius + t * (far_radius - base_radius);
                Vec3d center = start.add(direction.multiply(currentDepth));

                for (int i = 0; i < steps; i++) {
                    double angle = (2 * Math.PI * i) / steps;
                    Vec3d offset = right.multiply(Math.cos(angle) * currentRadius)
                            .add(up.multiply(Math.sin(angle) * currentRadius));
                    Vec3d point = center.add(offset);

                    world.addParticle(ParticleTypes.END_ROD, point.x, point.y, point.z, 0, 0, 0);
                }
            }
        }
        private final String name;
        public static final Codec<FanSubtype> CODEC = Codec.STRING.xmap(FanSubtype::valueOf, FanSubtype::name);
        FanSubtype(String name) { this.name = name; }
        public String asString() { return this.name; }
    }
}
