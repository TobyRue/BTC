package io.github.tobyrue.btc.enums;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.item.IHaveWrenchActions;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.wires.WireBlock;
import io.github.tobyrue.btc.wires.WireBlockEntity;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireDelayHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireOperatorHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
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
}
