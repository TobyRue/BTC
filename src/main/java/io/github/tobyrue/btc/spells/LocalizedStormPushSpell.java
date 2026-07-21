package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public class LocalizedStormPushSpell extends Spell {
    public LocalizedStormPushSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF02C1DB;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        double shootStrength = args.getDouble("shootStrength", 7d);
        double verticalMultiplier = args.getDouble("verticalMultiplier", 2.2d);
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        double range = args.getDouble("range", 24d);
        var entity = isTargetInRange(ctx.user(), ctx.target(), range);
        double dx = entity.getX() - ctx.user().getX();
        double dy = entity.getY() - ctx.user().getY();
        double dz = entity.getZ() - ctx.user().getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance != 0) {
            entity.setVelocity(dx / distance * shootStrength, (dy / distance * shootStrength), dz / distance * shootStrength);
            entity.setVelocity(entity.getVelocity().add(0, verticalMultiplier, 0));
        }


        entity.damage(ctx.world().getDamageSources().flyIntoWall(), 5);
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        assert ctx.user() != null;
        Entity target = isTargetInRange(ctx.user(), ctx.target(), args.getDouble("range", 24d));
        return target != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("localized_storm_push"));
    }
}
