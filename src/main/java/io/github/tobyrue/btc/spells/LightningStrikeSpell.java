package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.client.MinecraftClient;

import java.util.Optional;

public class LightningStrikeSpell extends Spell {

    public LightningStrikeSpell () {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        if (user == null || world.isClient) return;

        double range = args.getDouble("range", 24.0d);
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3d);

        Entity targetEntity = isTargetInRange(ctx.user(), ctx.target(), range);
        Vec3d strikePos;

        if (targetEntity != null) {
            strikePos = targetEntity.getPos();
        } else {
            Vec3d blockPos = getBlockLookedAt(user, range, 1.0F, true);
            if (blockPos == null) return;
            strikePos = blockPos.add(0.5, 0, 0.5);
        }

        if (world instanceof ServerWorld serverWorld) {
            LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(serverWorld);
            if (lightning != null) {
                lightning.refreshPositionAfterTeleport(strikePos);
                lightning.setCosmetic(false);
                serverWorld.spawnEntity(lightning);
            }
        }
    }

    public static @org.jetbrains.annotations.Nullable Vec3d getBlockLookedAt(LivingEntity player, double range, float tickDelta, boolean includeFluids) {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.cameraEntity.raycast(range, tickDelta, includeFluids);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            return Vec3d.ofBottomCenter(blockHit.getBlockPos());
        }
        return null;
    }


    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 200), BTC.identifierOf("lightning_strike"));
    }
    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }
    @Override
    public int getColor(final GrabBag args) {
        return 0xFF80E1FF;
    }
}
