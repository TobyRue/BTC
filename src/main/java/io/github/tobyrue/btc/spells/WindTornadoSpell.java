package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.WindTornadoEntity;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.Spell;

public class WindTornadoSpell extends Spell {
    public WindTornadoSpell() {
        super(0x0, SpellTypes.WIND);
    }

    @Override
    protected void use(SpellContext ctx) {
        WindTornadoEntity windTornado = new WindTornadoEntity(ModEntities.WIND_TORNADO, ctx.world());
        if (ctx.user() != null) {
            windTornado.setUser(ctx.user());
        }
        windTornado.setPos(ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ());
        ctx.world().spawnEntity(windTornado);
    }

    @Override
    public Spell.SpellCooldown getCooldown() {
        return new Spell.SpellCooldown(400, BTC.identifierOf("wind_tornado"));
    }
}
