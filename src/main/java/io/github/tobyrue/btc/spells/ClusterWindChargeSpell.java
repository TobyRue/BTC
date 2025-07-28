package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.math.Vec3d;

public class ClusterWindChargeSpell extends Spell {
    protected final int count;
    protected final double spreadFactor;

    public ClusterWindChargeSpell(int count, double spreadFactor) {
        super(0x0, SpellTypes.WIND);
        this.count = count;
        this.spreadFactor = spreadFactor;
    }

    @Override
    protected void use(SpellContext ctx) {
        for (int i = 0; i < count; i++) {
            // Create a new WindChargeEntity
            WindChargeEntity windCharge = new WindChargeEntity(EntityType.WIND_CHARGE, ctx.world());
            if (ctx.user() != null) {
                windCharge.setOwner(ctx.user());
            }

            // Apply random spread within a controlled cone
            double randomPitch = (Math.random() - 0.5) * spreadFactor;
            double randomYaw = (Math.random() - 0.5) * spreadFactor;

            // Calculate the scattered direction by modifying the player's original facing direction
            Vec3d scatterDirection = ctx.direction().add(randomYaw, randomPitch, randomYaw).normalize();

            // Set the spawn position slightly in front of the player
            Vec3d spawnPosition = new Vec3d(ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ()).add(ctx.direction().multiply(1.5));
            windCharge.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);

            // Set the velocity of the wind charge
            windCharge.setVelocity(scatterDirection.multiply(2.0)); // Set speed of the wind charge
            ctx.world().spawnEntity(windCharge);
        }
    }

    @Override
    public SpellCooldown getCooldown() {
        return new SpellCooldown(80, BTC.identifierOf("cluster_wind_charge"));
    }
}
