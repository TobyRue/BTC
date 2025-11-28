package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.math.Vec3d;

public class WindChargeSpell extends Spell {

    public WindChargeSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(final SpellContext ctx, final GrabBag args) {
        WindChargeEntity windCharge = new WindChargeEntity(EntityType.WIND_CHARGE, ctx.world());
        if (ctx.user() != null) {
            windCharge.setOwner(ctx.user());
        }
        windCharge.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
        windCharge.setVelocity(ctx.direction().multiply(1.5));
        ctx.world().spawnEntity(windCharge);
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 20), BTC.identifierOf("wind_charge"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFC1DAFF;
    }
}
