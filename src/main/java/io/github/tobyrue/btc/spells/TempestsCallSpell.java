package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class TempestsCallSpell extends Spell {
    protected final double pull_radius;
    protected final double pull_strength;

    public TempestsCallSpell(double pullRadius, double pullStrength) {
        super(0x0, SpellTypes.WIND);
        pull_radius = pullRadius;
        pull_strength = pullStrength;
    }

    @Override
    protected void use(SpellContext ctx) {
        List<LivingEntity> entities = ctx.world().getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(pull_radius), entity -> entity != ctx.user());
        // Log the number of entities found
        // Pull all mobs towards the player
        for (LivingEntity entity : entities) {
            // Calculate the direction towards the player
            double dx = ctx.user().getX() - entity.getX();
            double dy = ctx.user().getY() - entity.getY();
            double dz = ctx.user().getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (entities != null) {
                if (distance != 0) {
                    entity.setVelocity(dx / distance * pull_strength, dy / distance * pull_strength, dz / distance * pull_strength);
                }
            }
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx) {
        return ctx.user() != null && super.canUse(ctx);
    }

    @Override
    public Spell.SpellCooldown getCooldown() {
        return new Spell.SpellCooldown(160, BTC.identifierOf("tempests_call"));
    }
}
