package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.client.BTCClient;
import io.github.tobyrue.btc.client.radial_menus.RadialMenu;
import io.github.tobyrue.btc.commands.WrenchCommand;
import io.github.tobyrue.btc.component.BlockPosComponent;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WrenchItem extends Item {
    public WrenchItem(Settings settings) {
        super(settings
                .component(ModComponents.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(0, 0, 0))
                .component(ModComponents.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(0, 0, 0))
        );
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        var c1 = stack.get(ModComponents.CORNER_1_POSITION_COMPONENT);
        var c2 = stack.get(ModComponents.CORNER_2_POSITION_COMPONENT);

        if (entity instanceof PlayerEntity player && (!(player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.COPPER_WRENCH)) && !(player.getStackInHand(Hand.OFF_HAND).isOf(ModItems.COPPER_WRENCH)))) {
            return;
        }

        if (stack.contains(ModComponents.WRENCH_TYPE)) {
            if (stack.get(ModComponents.WRENCH_TYPE) == WrenchType.SELECTOR) {
                if (c1 != null) {
                    spawnHighlightParticles(world, new BlockPos(c1.x(), c1.y(), c1.z()), DustParticleEffect.DEFAULT);
                }
                if (c2 != null) {
                    spawnHighlightParticles(world, new BlockPos(c2.x(), c2.y(), c2.z()), new DustParticleEffect(Vec3d.unpackRgb(0x0000FF).toVector3f(), 1.0f));
                }
            }
        }
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


    public void openWrenchMenu(ItemStack stack) {

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

        List<RadialMenu.RadialValue> selectorOptions = List.of(
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.auto_mode"), (m, t) ->  m.sendCommand("btcwrench selector selector_auto")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.corner_1_set_mode"), (m, t) -> m.sendCommand("btcwrench selector selector_pos1")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.corner_2_set_mode"), (m, t) -> m.sendCommand("btcwrench selector selector_pos2")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.clear"), (m, t) -> m.sendCommand("btcwrench selector selector_clear")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        List<RadialMenu.RadialValue> fanDepthTypes = new ArrayList<>();
        fanDepthTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench fan depth")));
        for (int i = 1; i <= 16; i++) {
            final int val = i;
            fanDepthTypes.add(new RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench fan depth " + val)));
        }
        fanDepthTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));

        List<RadialMenu.RadialValue> fanBaseRadiusTypes = new ArrayList<>();
        fanBaseRadiusTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench fan base_radius")));
        for (int i = 0; i <= 8; i++) {
            final int val = i;
            fanBaseRadiusTypes.add(new RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench fan base_radius " + val)));
        }
        fanBaseRadiusTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));

        List<RadialMenu.RadialValue> fanFarRadiusTypes = new ArrayList<>();
        fanFarRadiusTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench fan far_radius")));
        for (int i = 1; i <= 12; i++) {
            final int val = i;
            fanFarRadiusTypes.add(new RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench fan far_radius " + val)));
        }
        fanFarRadiusTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));

        List<RadialMenu.RadialValue> fanOptions = List.of(
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.fan.depth"), fanDepthTypes).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.fan.base_radius"), fanBaseRadiusTypes).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.fan.far_radius"), fanFarRadiusTypes).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.fan.show_cone"), (m, t) -> m.sendCommand("btcwrench fan show_cone")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        List<RadialMenu.RadialValue> mainCategories = List.of(
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.rotate"), (m,t) -> m.sendCommand("btcwrench rotate")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.mirror"), (m,t) -> m.sendCommand("btcwrench mirror")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.copy"), (m,t) -> m.sendCommand("btcwrench copy")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.paste"), (m,t) -> m.sendCommand("btcwrench paste")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.wire"), wireOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.selector"), selectorOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.fan"), fanOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.close"), (menu, type) -> menu.close()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        net.minecraft.client.MinecraftClient.getInstance().setScreen(new RadialMenu(
                Text.translatable("item.btc.wrench.title.modes"),
                mainCategories,
                stack,
                BTCClient.radialMenuKeyBinding,
                0xFFD67B5B,
                false,
                false,
                0
        ));
    }


    private void spawnHighlightParticles(World world, BlockPos pos, ParticleEffect effect) {
        double dob = 0.03;
        int countPerFace = 8;

        if (pos.getY() > world.getBottomY() - 1) {
            for (int i = 0; i < countPerFace; i++) {
                double rx = world.random.nextDouble();
                double ry = world.random.nextDouble();
                double rz = world.random.nextDouble();

                world.addParticle(effect,
                        pos.getX() + 1 + dob,
                        pos.getY() + ry,
                        pos.getZ() + rz,
                        0, 0, 0);

                world.addParticle(effect,
                        pos.getX() - dob,
                        pos.getY() + ry,
                        pos.getZ() + rz,
                        0, 0, 0);

                world.addParticle(effect,
                        pos.getX() + rx,
                        pos.getY() + 1 + dob,
                        pos.getZ() + rz,
                        0, 0, 0);

                world.addParticle(effect,
                        pos.getX() + rx,
                        pos.getY() - dob,
                        pos.getZ() + rz,
                        0, 0, 0);

                world.addParticle(effect,
                        pos.getX() + rx,
                        pos.getY() + ry,
                        pos.getZ() + 1 + dob,
                        0, 0, 0);

                world.addParticle(effect,
                        pos.getX() + rx,
                        pos.getY() + ry,
                        pos.getZ() - dob,
                        0, 0, 0);
            }
        }
    }
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
}