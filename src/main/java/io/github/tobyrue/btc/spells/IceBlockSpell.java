package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class IceBlockSpell extends Spell {
    public IceBlockSpell() {
        super(SpellTypes.WATER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        freezeTargetArea(ctx, args, ctx.user(), ctx.world());
    }
    public void freezeTargetArea(SpellContext ctx, GrabBag args, LivingEntity player, World world) {
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        double range = args.getDouble("range", 24);
        int duration = args.getInt("duration", 200);
        int amplifier = args.getInt("amplifier", 4);

        // Only run server-side
        if (!world.isClient) {
            Entity target = getEntityLookedAt(player, range, aimingForgiveness);

            // Iterate over the target's bounding box dimensions
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, amplifier));

                BlockPos targetPos = target.getBlockPos();
                // Get the size of the entity's bounding box
                double entityWidth = target.getWidth();
                double entityHeight = target.getHeight();
                double entityLength = target.getWidth(); // Assuming a roughly cubic bounding box for simplicity

                // Round up the bounding box sizes to the nearest integer
                int rangeX = (int) Math.ceil(entityWidth / 2.0); // Half the width, rounded up
                int rangeY = (int) Math.ceil(entityHeight / 2.0); // Half the height, rounded up
                int rangeZ = (int) Math.ceil(entityLength / 2.0); // Half the length, rounded up

                BlockPos.Mutable mutablePos = new BlockPos.Mutable();

                // Freeze area based on rounded-up entity size
                for (int x = -rangeX; x <= rangeX; x++) {
                    for (int y = -rangeY; y <= rangeY; y++) {
                        for (int z = -rangeZ; z <= rangeZ; z++) {
                            mutablePos.set(targetPos.getX() + x, targetPos.getY() + y + 1, targetPos.getZ() + z);

                            // Only replace air or water blocks
                            BlockState state = world.getBlockState(mutablePos);
                            if (state.isReplaceable() || state.getFluidState().isStill()) {
                                world.setBlockState(mutablePos, ModBlocks.MELTING_ICE.getDefaultState());
                            }
                        }
                    }
                }

                world.playSound(null, targetPos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    public static @org.jetbrains.annotations.Nullable Entity getEntityLookedAt(LivingEntity player, double range, double aimingForgiveness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));

        // Create a box from the eye position to the reach vector
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        // Find the closest entity intersecting that line
        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit()) /*Replace isAttackable() and canHit() in the predicate with any condition you like (e.g., specific entity types or tags)*/) {
            Box entityBox = entity.getBoundingBox().expand(aimingForgiveness); // slightly expanded hitbox
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
    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        assert ctx.user() != null;
        Entity target = getEntityLookedAt(ctx.user(), args.getDouble("range", 24), args.getDouble("aimingForgiveness", 0.3D));
        return target != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("ice_block"));
    }
}
