package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import io.github.tobyrue.btc.enums.EarthStaffAttacks;
import io.github.tobyrue.btc.enums.WaterStaffAttacks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IceBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WaterStaffItem extends StaffItem {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public WaterStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        WaterStaffAttacks current = getElement(stack);
        WaterStaffAttacks next = WaterStaffAttacks.next(current);

        if (!player.isSneaking()) {
            switch (current) {
                case WATER_BLAST -> {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.8f + world.getRandom().nextFloat() * 0.4f);
                    Vec3d velocity = player.getRotationVec(1.0f).multiply(1.5f);
                    if (!world.isClient) {
                        WaterBlastEntity waterBlast = new WaterBlastEntity(player, world, player.getX(), player.getY() + 1.25, player.getZ(), velocity);
                        world.spawnEntity(waterBlast);
                        player.getItemCooldownManager().set(this, 15);
                    }
                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    return TypedActionResult.success(stack);
                }
                case ICE_FREEZE -> {
                    freezeTargetArea(player, world);
                    player.getItemCooldownManager().set(this, 160);
                    return TypedActionResult.success(stack);
                }
                case FROST_WALKER -> {

                }
                case DOLPHINS_GRACE -> {

                }
                case CONDUIT_POWER ->  {

                }
            }
        }

        if (!world.isClient && player.isSneaking()) {
            player.sendMessage(Text.translatable("item.btc.spell.water.set", Text.translatable("item.btc.spell.water." + next.asString())), true);
            setElement(stack, next);
            return TypedActionResult.success(stack);
        }

        return super.use(world, player, hand);
    }

    public static @Nullable Entity getEntityLookedAt(PlayerEntity player, double range, double aimmingForgivness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));

        // Create a box from the eye position to the reach vector
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        // Find the closest entity intersecting that line
        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit()) /*Replace isAttackable() and canHit() in the predicate with any condition you like (e.g., specific entity types or tags)*/) {
            Box entityBox = entity.getBoundingBox().expand(aimmingForgivness); // slightly expanded hitbox
            Optional<Vec3d> optionalHit = entityBox.raycast(eyePos, reachVec);

            if (optionalHit.isPresent()) {
                double distanceSq = eyePos.squaredDistanceTo(optionalHit.get());
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    hitEntity = entity;
                }
            }
        }
        return hitEntity;
    }

    public void freezeTargetArea(PlayerEntity player, World world) {
        // Only run server-side
        if (world.isClient) return;


        Entity target = getEntityLookedAt(player, 16, 0.3D);

        // Iterate in a 3x3x3 cube around the target's block position
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        if (target instanceof LivingEntity) {
            ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 4));

            BlockPos targetPos = target.getBlockPos();
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        mutablePos.set(targetPos.getX() + x, targetPos.getY() + y + 1, targetPos.getZ() + z);

                        // Only replace air or water blocks
                        BlockState state = world.getBlockState(mutablePos);
                        if (state.isAir() || state.getFluidState().isStill()) {
                            world.setBlockState(mutablePos, Blocks.ICE.getDefaultState());
                            scheduler.schedule(() -> {
                                // world.getServer().execute(() -> {
                                BlockState currentState = world.getBlockState(mutablePos);
                                if (currentState.getBlock() instanceof IceBlock) {
                                    world.setBlockState(mutablePos, Blocks.AIR.getDefaultState());
                                }
                                //});
                            }, 10, TimeUnit.SECONDS);
                        }
                    }
                }
            }
            world.playSound(null, targetPos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }

    private WaterStaffAttacks getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        String name = nbt.getString("Element");
        for (WaterStaffAttacks attack : WaterStaffAttacks.values()) {
            if (attack.asString().equals(name)) {
                return attack;
            }
        }
        return WaterStaffAttacks.WATER_BLAST;
    }

    private void setElement(ItemStack stack, WaterStaffAttacks attack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        nbt.putString("Element", attack.asString());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        WaterStaffAttacks current = getElement(stack);
        tooltip.add(Text.translatable("item.btc.spell.context.current", Text.translatable("item.btc.spell.water." + current.asString())).formatted(Formatting.BLUE));
        tooltip.add(this.attackTypes().formatted(Formatting.AQUA));
    }
    
    public MutableText currentAttack(ItemStack stack) {return Text.literal("Current Spell: " + getElement(stack));}
    public MutableText attackTypes() {return Text.literal("Attack Types:");}

}
