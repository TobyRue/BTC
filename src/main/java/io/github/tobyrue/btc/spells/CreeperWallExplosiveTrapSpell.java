package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.CreeperPillarEntity;
import io.github.tobyrue.btc.enums.CreeperPillarType;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CreeperWallExplosiveTrapSpell extends Spell {
    public CreeperWallExplosiveTrapSpell() {
        super(SpellTypes.EARTH);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        double radius = args.getDouble("radius", 2d);
        double range = args.getDouble("range", 16d);
        int yRange = args.getInt("yRange", 10);
        int count = args.getInt("count", Math.max(8, (3 * 3) + 8));


        Entity entityLookedAt = getEntityLookedAt(user, range, aimingForgiveness);
        if (entityLookedAt != null) {
            for (int i = 0; i < count; i++) {
                double angle = 2 * Math.PI * i / count;
                double x = entityLookedAt.getX() + radius * Math.cos(angle);
                double z = entityLookedAt.getZ() + radius * Math.sin(angle);
                BlockPos groundPos = findSpawnableGroundPillar(world, new BlockPos((int) x, (int) entityLookedAt.getY(), (int) z), yRange);
                if (entityLookedAt instanceof LivingEntity) {
                    if (groundPos != null) {
                        CreeperPillarEntity pillar = new CreeperPillarEntity(world, x, groundPos.getY(), z, entityLookedAt.getYaw(), user, CreeperPillarType.RANDOM);
                        world.emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(user));
                        world.spawnEntity(pillar);
                    }
                }
            }
        }
    }

    @Nullable
    public static BlockPos findSpawnableGroundPillar(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());


        // Start from top and go downwards
        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            // Improved block check to ensure solid block and air above or open space above
            if (!(world.getBlockState(pos).getBlock() instanceof AirBlock) && world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) {
                return pos;
            }
        }

        // Fallback if no valid ground is found
        return null;
    }


    public static @Nullable Entity getEntityLookedAt(LivingEntity player, double range, double aimmingForgivness) {
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
    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @io.github.tobyrue.xml.util.Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 800), BTC.identifierOf("creeper_wall_explosive_trap"));
    }
}
