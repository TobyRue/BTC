package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.CreeperPillarEntity;
import io.github.tobyrue.btc.enums.CreeperPillarType;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public class CreeperWallBlockSpell extends Spell {
    public CreeperWallBlockSpell() {
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
        int count = args.getInt("count", 5);
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        double range = args.getDouble("range", 24);
        double offsetTowardsPlayer = args.getDouble("offsetTowardsPlayer", 2.0D);
        boolean includeFluids = args.getBoolean("includeFluids", true);

        @org.jetbrains.annotations.Nullable Entity pillarPosEntity = getEntityLookedAt(user, range, aimingForgiveness);
        @org.jetbrains.annotations.Nullable Vec3d pillarPosBlock = getBlockLookedAt(user, range, 1.0F, includeFluids);
        if (pillarPosEntity instanceof LivingEntity) {
            spawnCreeperPillarWall(world, pillarPosEntity.getPos(), user, count, offsetTowardsPlayer);
        } else if (pillarPosBlock != null) {
            spawnCreeperPillarWall(world, pillarPosBlock, user, count, 0.0);
        }
    }

    public static void spawnCreeperPillarWall(World world, Vec3d centerPos, LivingEntity player, int count, double offsetTowardsPlayer) {
        // Direction from player to centerPos
        Vec3d direction = centerPos.subtract(player.getPos()).normalize();

        // Apply offset toward player (if offset != 0)
        Vec3d adjustedCenter = centerPos.add(direction.multiply(offsetTowardsPlayer * -1));

        // Perpendicular vector to that direction (in XZ plane)
        Vec3d perp = getPerpendicular2D(direction);

        int halfCount = count / 2;

        for (int i = -halfCount; i <= halfCount; i++) {
            Vec3d spawnPos = adjustedCenter.add(perp.multiply(i));
            BlockPos groundPos = findSpawnableGroundPillar(world, BlockPos.ofFloored(spawnPos), 10);
            if (groundPos != null) {
                CreeperPillarEntity pillar = new CreeperPillarEntity(
                        world, spawnPos.x + 0.5, groundPos.getY(), spawnPos.z + 0.5, player.getYaw(), player, CreeperPillarType.NORMAL
                );
                world.emitGameEvent(GameEvent.ENTITY_PLACE, pillar.getPos(), GameEvent.Emitter.of(player));
                world.spawnEntity(pillar);
            }
        }
    }

    public static Vec3d getPerpendicular2D(Vec3d vec) {
        return new Vec3d(-vec.z, 0, vec.x).normalize();
    }

    @org.jetbrains.annotations.Nullable
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
    public static @org.jetbrains.annotations.Nullable Vec3d getBlockLookedAt(LivingEntity player, double range, float tickDelta, boolean includeFluids) {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hitLong = client.cameraEntity.raycast(range, tickDelta, includeFluids);

        switch (hitLong.getType()) {
            case HitResult.Type.MISS:
                return null;
            case HitResult.Type.BLOCK:
                BlockHitResult blockHit = (BlockHitResult) hitLong;
                BlockPos blockPos = blockHit.getBlockPos();
                assert client.world != null;
                BlockState blockState = client.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                return new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        return null;
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
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 200), BTC.identifierOf("creeper_wall_block"));
    }
}
