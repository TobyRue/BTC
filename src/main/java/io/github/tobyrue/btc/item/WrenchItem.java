package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.client.BTCClient;
import io.github.tobyrue.btc.component.BlockPosComponent;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.packets.OpenWrenchMenuPayload;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.SpellDataStore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
//        if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
//            openMenu(stack);
//        }
        return TypedActionResult.success(stack);
    }

    @Environment(EnvType.CLIENT)
    private void openMenu(ItemStack stack) {
        BTCClient.openWrenchMenu(stack);
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