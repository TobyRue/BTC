package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.math.Vec3d;

public class WindChargeSpell extends Spell {

    public WindChargeSpell() {
        super(0x0, SpellTypes.WIND);
    }

    @Override
    protected void use(SpellContext ctx) {
        WindChargeEntity windCharge = new WindChargeEntity(EntityType.WIND_CHARGE, ctx.world());
        if (ctx.user() != null) {
            windCharge.setOwner(ctx.user());
        }
        windCharge.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
        windCharge.setVelocity(ctx.direction().multiply(1.5));
        ctx.world().spawnEntity(windCharge);
    }

    @Override
    public SpellCooldown getCooldown() {
        return new SpellCooldown(20, BTC.identifierOf("wind_charge"));
    }
}
