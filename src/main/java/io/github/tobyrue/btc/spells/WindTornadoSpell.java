package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.WindTornadoEntity;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;

public class WindTornadoSpell extends Spell {
    public WindTornadoSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(final SpellContext ctx, final GrabBag args) {
        WindTornadoEntity windTornado = new WindTornadoEntity(ModEntities.WIND_TORNADO, ctx.world());
        if (ctx.user() != null) {
            windTornado.setUser(ctx.user());
        }
        windTornado.setPos(ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ());
        ctx.world().spawnEntity(windTornado);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("wind_tornado"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFBFFFFA;
    }
}
