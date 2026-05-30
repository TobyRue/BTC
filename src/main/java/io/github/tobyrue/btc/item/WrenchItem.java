package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.client.radial_menus.RadialMenu;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WrenchItem extends Item {
    public WrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient()) {
            openWrenchMenu(stack);
        }
        return TypedActionResult.success(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        ItemStack stack = context.getStack();
        WrenchType type = stack.getOrDefault(ModComponents.WRENCH_TYPE, WrenchType.ROTATE);
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        Direction hitSide = context.getSide();


        if (stack.contains(ModComponents.WRENCH_TYPE)) {
            return Objects.requireNonNull(stack.get(ModComponents.WRENCH_TYPE)).useOnBlock(context);
        }
        return super.useOnBlock(context);
    }


    private void openWrenchMenu(ItemStack stack) {

        //CONNECTIONS
        List<RadialMenu.RadialValue> connTypes = new ArrayList<>();
        connTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench wire connection")));
        for (var type : WireBlock.ConnectionType.values()) {
            connTypes.add(new RadialMenu.RadialValue(Text.translatable("block.btc.wire.connection." + type.asString()),
                    (menu,triggerType) -> menu.sendCommand("btcwrench wire connection " + type.asString())));
        }
        connTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"),  (menu, type) -> menu.goBack()));


        //OPERATOR
        List<RadialMenu.RadialValue> opTypes = new ArrayList<>();
        opTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu,type) -> menu.sendCommand("btcwrench wire operator")));
        for (var op : WireBlock.Operator.values()) {
            opTypes.add(new RadialMenu.RadialValue(Text.translatable("block.btc.wire.operator." + op.asString()),
                    (menu,type) -> menu.sendCommand("btcwrench wire operator " + op.asString())));
        }
        opTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));


        //DELAY
        List<RadialMenu.RadialValue> delayTypes = new ArrayList<>();
        delayTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu,type) -> menu.sendCommand("btcwrench wire delay")));
        for (int i = 0; i <= 7; i++) {
            final int val = i;
            delayTypes.add(new RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu,type) -> menu.sendCommand("btcwrench wire delay " + val)));
        }
        delayTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));


        //CONNECTION, OPERATOR, AND DELAY, NESTED TOGETHER
        List<RadialMenu.RadialValue> wireOptions = List.of(
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.connections"), connTypes),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.operator"), opTypes),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.delay"), delayTypes),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        List<RadialMenu.RadialValue> mainCategories = List.of(
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.rotate"), (m,t) -> m.sendCommand("btcwrench rotate")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.mirror"), (m,t) -> m.sendCommand("btcwrench mirror")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.copy"), (m,t) -> m.sendCommand("btcwrench copy")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.paste"), (m,t) -> m.sendCommand("btcwrench paste")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.wire"), wireOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.close"), (menu, type) -> menu.close()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        net.minecraft.client.MinecraftClient.getInstance().setScreen(new RadialMenu(
                Text.translatable("item.btc.wrench.title.modes"),
                mainCategories,
                stack,
                null,
                0xFFD67B5B,
                true,
                false,
                0
        ));
    }
}