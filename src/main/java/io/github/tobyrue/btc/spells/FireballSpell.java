package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;

public class FireballSpell extends Spell {

    public FireballSpell() {
        super(SpellTypes.FIRE);
    }

    @Override
    public void use(final Spell.SpellContext ctx, final GrabBag args) {
        FireballEntity fireball = ctx.user() == null ? new FireballEntity(EntityType.FIREBALL, ctx.world()) : new FireballEntity(ctx.world(), ctx.user(), ctx.direction(), args.getInt("level", 1));
        fireball.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
        fireball.setVelocity(ctx.direction().multiply(1.5));
        ctx.world().spawnEntity(fireball);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", (40 * args.getInt("level", 1))), BTC.identifierOf("fireball"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFFF5400;
    }
}
