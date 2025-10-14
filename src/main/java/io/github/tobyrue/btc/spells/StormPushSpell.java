package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class StormPushSpell extends Spell {
    public StormPushSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(final SpellContext ctx, final GrabBag args) {
        double shoot_radius = args.getDouble("shoot_radius", 15d);
        double shoot_strength = args.getDouble("shoot_strength", 5d);
        List<LivingEntity> entities = ctx.world().getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(shoot_radius), entity -> entity != ctx.user());

        // Shoot all mobs away from the player
        for (LivingEntity entity : entities) {
            double dx = entity.getX() - ctx.user().getX();
            double dy = entity.getY() - ctx.user().getY();
            double dz = entity.getZ() - ctx.user().getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Apply velocity away from the player
            if (distance != 0) {
                entity.setVelocity(dx / distance * shoot_strength, dy / distance * shoot_strength, dz / distance * shoot_strength);
            }

            entity.damage(ctx.world().getDamageSources().flyIntoWall(), 5);
        }
    }

    @Override
    protected boolean canUse(final Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 240), BTC.identifierOf("storm_push"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF87E3FF;
    }
}
