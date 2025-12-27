package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.component.BlockPosComponent;
import io.github.tobyrue.btc.misc.CornerStorage;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.List;

public class CornerSelectorItem extends Item {
    public CornerSelectorItem(Settings settings) {
        super(settings.component(BTC.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(0, 0, 0)).component(BTC.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(0, 0, 0)));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        var c1 = stack.get(BTC.CORNER_1_POSITION_COMPONENT);
        var c2 = stack.get(BTC.CORNER_2_POSITION_COMPONENT);

        if (entity instanceof PlayerEntity player && (!(player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.CORNER_SELECTOR)) && !(player.getStackInHand(Hand.OFF_HAND).isOf(ModItems.CORNER_SELECTOR)))) {
            return;
        }

        if (c1 != null) {
            spawnHighlightParticles(world, new BlockPos(c1.x(), c1.y(), c1.z()), DustParticleEffect.DEFAULT);
        }
        if (c2 != null) {
            spawnHighlightParticles(world, new BlockPos(c2.x(), c2.y(), c2.z()), new DustParticleEffect(Vec3d.unpackRgb(0x0000FF).toVector3f(), 1.0f));
        }
    }

    private void spawnHighlightParticles(World world, BlockPos pos, ParticleEffect effect) {
        double dob = 0.03; // distance outside block
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

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (hand == Hand.OFF_HAND) {
            var stack = user.getStackInHand(hand);
            stack.set(BTC.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(0,world.getBottomY() - 50,0));
            stack.set(BTC.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(0,world.getBottomY() - 50,0));
            return TypedActionResult.success(user.getStackInHand(hand), true);
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();


        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        BlockState state = world.getBlockState(pos);
        Hand hand = context.getHand();



        if (player == null || !player.isCreative() || world.isClient) {
            return super.useOnBlock(context);
        }

        if (state.getBlock() instanceof CornerStorage cornerStorage && player.isSneaking()) {
            var cs = cornerStorage.getBox(stack, pos, state, world);
            if (cs != null) {
                stack.set(BTC.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(cs.getMinX(), cs.getMinY(), cs.getMinZ()));
                stack.set(BTC.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(cs.getMaxX(), cs.getMaxY(), cs.getMaxZ()));
                player.sendMessage(
                        Text.translatable("item.btc.corner_selector.corner_1_and_2_set", new BlockPos(cs.getMinX(), cs.getMinY(), cs.getMinZ()).toShortString(), new BlockPos(cs.getMaxX(), cs.getMaxY(), cs.getMaxZ()).toShortString()),
                        true
                );
                return ActionResult.SUCCESS;
            }
        }

        if (hand == Hand.OFF_HAND) {
            stack.set(BTC.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(0,world.getBottomY() - 50,0));
            stack.set(BTC.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(0,world.getBottomY() - 50,0));
            return ActionResult.SUCCESS;
        }

        if (!player.isSneaking()) {
            stack.set(BTC.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(pos.getX(), pos.getY(), pos.getZ()));
            player.sendMessage(
                    Text.translatable("item.btc.corner_selector.corner_1_set", pos.toShortString()),
                    true
            );
        } else {
            stack.set(BTC.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(pos.getX(), pos.getY(), pos.getZ()));
            player.sendMessage(
                    Text.translatable("item.btc.corner_selector.corner_2_set", pos.toShortString()),
                    true
            );
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var c1 = stack.get(BTC.CORNER_1_POSITION_COMPONENT);
        var c2 = stack.get(BTC.CORNER_2_POSITION_COMPONENT);
        tooltip.add(Text.translatable("item.btc.corner_selector.clear"));
        if (c1 != null) {
            assert MinecraftClient.getInstance().player != null;
            if (c1.y() > MinecraftClient.getInstance().player.getWorld().getBottomY() - 1) {
                var b1 = new BlockPos(c1.x(), c1.y(), c1.z());
                tooltip.add(Text.translatable("item.btc.corner_selector.corner_1", b1.toShortString()));
            }
        }
        if (c2 != null) {
            assert MinecraftClient.getInstance().player != null;
            if (c2.y() > MinecraftClient.getInstance().player.getWorld().getBottomY() - 1) {
                var b2 = new BlockPos(c2.x(), c2.y(), c2.z());
                tooltip.add(Text.translatable("item.btc.corner_selector.corner_2", b2.toShortString()));
            }
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
