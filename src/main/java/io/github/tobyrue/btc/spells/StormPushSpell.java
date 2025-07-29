package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class StormPushSpell extends Spell {
    protected final double shoot_radius;
    protected final double shoot_strength;

    public StormPushSpell(double shootRadius, double shootStrength) {
        super(0x0, SpellTypes.WIND);
        shoot_radius = shootRadius;
        shoot_strength = shootStrength;
    }

    @Override
    protected void use(SpellContext ctx) {
        List<LivingEntity> entities = ctx.world().getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(shoot_radius), entity -> entity != ctx.user());

        // Shoot all mobs away from the player
        for (LivingEntity entity : entities) {
            double dx = entity.getX() - ctx.user().getX();
            double dy = entity.getY() - ctx.user().getY();
            double dz = entity.getZ() - ctx.user().getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Apply velocity away from the player
            if (entities != null) {
                if (distance != 0) {
                    entity.setVelocity(dx / distance * shoot_strength, dy / distance * shoot_strength, dz / distance * shoot_strength);
                }

                // Optionally, deal damage to the entity
                if (entity instanceof PlayerEntity) {
                    entity.damage(ModDamageTypes.of(ctx.world(), ModDamageTypes.WIND_BURST), 5.0f);
                } else {
                    entity.damage(ctx.world().getDamageSources().flyIntoWall(), 5);
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
        return new Spell.SpellCooldown(240, BTC.identifierOf("storm_push"));
    }
}
