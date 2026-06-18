package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.SuperHappyKillBallEntity;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;

public class KillBallSpell extends Spell {
    public KillBallSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0; //TODO
    }


    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        SuperHappyKillBallEntity killBall = new SuperHappyKillBallEntity(ctx.pos(), ctx.direction().multiply(args.getDouble("speed", 0.25d)), ctx.world(), args.getInt("bounces", 0), args.getFloat("size", 1.0f));
        killBall.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
        ctx.world().spawnEntity(killBall);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 100), BTC.identifierOf("kill_ball"));
    }
}
