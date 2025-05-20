package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.CopperWireBlock;
import io.github.tobyrue.btc.enums.WrenchType;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(Settings settings) {
        super(settings);
    }
    public static Direction next(Direction current) {
        int len = Direction.values().length;
        int idx = current.ordinal();
        return Direction.values()[(idx + 1 + len) % len];
    }
    public static WrenchType nextWrench(WrenchType current) {
        int len = WrenchType.values().length;
        int idx = current.ordinal();
        return WrenchType.values()[(idx + 1 + len) % len];
    }

        @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        WrenchType type = stack.getOrDefault(BTC.WRENCH_TYPE, WrenchType.ROTATE);
        WrenchType nextWrench = nextWrench(type);

        if (player.isSneaking() && hand != Hand.OFF_HAND) {
            stack.set(BTC.WRENCH_TYPE, nextWrench);
            if (world.isClient) {
                player.sendMessage(Text.translatable("item.btc.wrench.type.switch", Text.translatable("item.btc.wrench.type." + nextWrench.asString())), true);
            }
            return TypedActionResult.success(stack);
        } else if (type == WrenchType.WIRE_COMPLEX) {
            if (hand == Hand.OFF_HAND) {
                Direction current = stack.getOrDefault(BTC.WRENCH_DIRECTION, Direction.UP);

                Direction next = next(current);

                stack.set(BTC.WRENCH_DIRECTION, next);

                if (world.isClient) {
                    player.sendMessage(Text.translatable("item.btc.wrench.wire.face_label", Text.translatable("block.btc.wire.face." + next.asString())), true);
                }
                return TypedActionResult.success(stack);
            }
        }

            return super.use(world, player, hand);
        }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        ItemStack stack = context.getStack();
        WrenchType type = stack.getOrDefault(BTC.WRENCH_TYPE, WrenchType.ROTATE);
        WrenchType nextWrench = nextWrench(type);
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction hitSide = context.getSide();

        if (state.getBlock() instanceof IHaveWrenchActions actions) {
            return actions.onWrenchUse(stack, state, world, pos, player, hand, hitSide);
        } else if (player.isSneaking() && hand != Hand.OFF_HAND) {
            stack.set(BTC.WRENCH_TYPE, nextWrench);
            if (world.isClient) {
                player.sendMessage(Text.translatable("item.btc.wrench.type.switch", Text.translatable("item.btc.wrench.type." + nextWrench.asString())), true);
            }
            return ActionResult.SUCCESS;
        } else if (type == WrenchType.WIRE_COMPLEX) {
            if (hand == Hand.OFF_HAND) {
                Direction current = stack.getOrDefault(BTC.WRENCH_DIRECTION, Direction.UP);

                Direction next = next(current);

                stack.set(BTC.WRENCH_DIRECTION, next);

                if (world.isClient) {
                    player.sendMessage(Text.translatable("item.btc.wrench.wire.face_label", Text.translatable("block.btc.wire.face." + next.asString())), true);
                }
                return ActionResult.SUCCESS;
            }
        } else if (type == WrenchType.ROTATE) {
            var stateRotate = context.getWorld().getBlockState(context.getBlockPos());
            if (!stateRotate.streamTags().anyMatch(t -> t == BTC.WRENCH_BLACKLIST) && !(stateRotate.getBlock() instanceof PistonBlock && stateRotate.get(PistonBlock.EXTENDED))) {
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
        }

        return super.useOnBlock(context);
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var dir = stack.getOrDefault(BTC.WRENCH_DIRECTION, Direction.UP);
        var typeWrench = stack.getOrDefault(BTC.WRENCH_TYPE, WrenchType.ROTATE);
        tooltip.add(Text.translatable("item.btc.wrench.type.switch", Text.translatable("item.btc.wrench.type." + typeWrench.asString())).formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.btc.wrench.wire.face_label", Text.translatable("block.btc.wire.face." + dir.asString())).formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.btc.wrench.description").formatted(Formatting.DARK_RED));
        tooltip.add(Text.translatable("item.btc.wrench.action.click").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("item.btc.wrench.action.shift_click").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("item.btc.wrench.types.rotate").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("item.btc.wrench.types.warn").formatted(Formatting.DARK_RED));
        tooltip.add(Text.translatable("item.btc.wrench.types.wire").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("item.btc.wrench.types.wire_offhand").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("item.btc.wrench.types.wire_complex").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("item.btc.wrench.types.wire_complex_offhand").formatted(Formatting.GOLD));
        super.appendTooltip(stack, context, tooltip, type);
    }
}
