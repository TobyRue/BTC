package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.screen.RadialMenuNoHover;
import io.github.tobyrue.btc.client.screen.RadialNoHoverValues;
import io.github.tobyrue.btc.enums.IWrenchType;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.regestries.ModComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
        IWrenchType type = stack.getOrDefault(ModComponents.WRENCH_TYPE, WrenchType.ROTATE);

//        if (!player.isSneaking()) {
//            if (type == WrenchType.WIRE_COMPLEX) {
//                if (hand == Hand.OFF_HAND) {
//                    Direction current = stack.getOrDefault(ModComponents.WRENCH_DIRECTION, Direction.UP);
//
//                    Direction next = next(current);
//                    player.sendMessage(Text.translatable("item.btc.wrench.wire.face_label", Text.translatable("block.btc.wire.face." + next.asString())), true);
//                    stack.set(ModComponents.WRENCH_DIRECTION, next);
//                }
//            }
//        }
        if (world.isClient()) {
            openRadialMenu(hand, player);
            return TypedActionResult.success(stack);
        }

        return super.use(world, player, hand);
    }

    @Environment(EnvType.CLIENT)
    private void openRadialMenu(Hand hand, PlayerEntity player) {
        List<RadialNoHoverValues.ValueNoHover> list;
        Text title;
        ItemStack stack = player.getStackInHand(hand);
        IWrenchType type = stack.getOrDefault(ModComponents.WRENCH_TYPE, WrenchType.ROTATE);

        if (player.isSneaking() && type == WrenchType.WIRE_COMPLEX) {
            list = getNoHoversComplex();
            title = Text.translatable("radial.btc.spell.select_complex");
        } else {
            list = getValueNoHovers();
            title = Text.translatable("radial.btc.spell.select_mode");
        }

        MinecraftClient.getInstance().setScreen(new RadialMenuNoHover(
                title,
                list,
                0,
                Math.min(list.size(), 6)
        ));
    }
    private static @NotNull List<RadialNoHoverValues.ValueNoHover> getNoHoversComplex() {
        List<RadialNoHoverValues.ValueNoHover> list = new ArrayList<>();

        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("block.btc.wrench.wire.complex_direction", Text.translatable("block.btc.wire.face.up")), "btcwrench complex_wire up"));
        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("block.btc.wrench.wire.complex_direction", Text.translatable("block.btc.wire.face.down")), "btcwrench complex_wire down"));
        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("block.btc.wrench.wire.complex_direction", Text.translatable("block.btc.wire.face.north")), "btcwrench complex_wire north"));
        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("block.btc.wrench.wire.complex_direction", Text.translatable("block.btc.wire.face.east")), "btcwrench complex_wire east"));
        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("block.btc.wrench.wire.complex_direction", Text.translatable("block.btc.wire.face.south")), "btcwrench complex_wire south"));
        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("block.btc.wrench.wire.complex_direction", Text.translatable("block.btc.wire.face.west")), "btcwrench complex_wire "));
        return list;
    }

    private static @NotNull List<RadialNoHoverValues.ValueNoHover> getValueNoHovers() {
        List<RadialNoHoverValues.ValueNoHover> list = new ArrayList<>();

        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("item.btc.wrench.type.rotate"), "btcwrench type rotate"));
        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("item.btc.wrench.type.wire"), "btcwrench type wire"));
        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("item.btc.wrench.type.wire_complex"), "btcwrench type wire_complex"));
        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("item.btc.wrench.type.wire_delay"), "btcwrench type wire_delay"));
        list.add(new RadialNoHoverValues.ValueNoHover(Text.translatable("item.btc.wrench.type.wire_operator"), "btcwrench type wire_operator"));
        return list;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        ItemStack stack = context.getStack();
        WrenchType type = stack.getOrDefault(ModComponents.WRENCH_TYPE, WrenchType.ROTATE);
        WrenchType nextWrench = nextWrench(type);
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction hitSide = context.getSide();

        if (state.getBlock() instanceof IHaveWrenchActions actions) {
            return actions.onWrenchUse(stack, state, world, pos, player, hand, hitSide);
        } else if (player.isSneaking() && hand != Hand.OFF_HAND) {
            stack.set(ModComponents.WRENCH_TYPE, nextWrench);
            player.sendMessage(Text.translatable("item.btc.wrench.type.switch", Text.translatable("item.btc.wrench.type." + nextWrench.asString())), true);
            return ActionResult.SUCCESS;
        } else if (type == WrenchType.WIRE_COMPLEX) {
            if (hand == Hand.OFF_HAND) {
                Direction current = stack.getOrDefault(ModComponents.WRENCH_DIRECTION, Direction.UP);

                Direction next = next(current);

                stack.set(ModComponents.WRENCH_DIRECTION, next);

                player.sendMessage(Text.translatable("item.btc.wrench.wire.face_label", Text.translatable("block.btc.wire.face." + next.asString())), true);
                return ActionResult.SUCCESS;
            }
        } else if (type == WrenchType.ROTATE) {
            var stateRotate = context.getWorld().getBlockState(context.getBlockPos());
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
        }

        return super.useOnBlock(context);
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var dir = stack.getOrDefault(ModComponents.WRENCH_DIRECTION, Direction.UP);
        var typeWrench = stack.getOrDefault(ModComponents.WRENCH_TYPE, WrenchType.ROTATE);
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
