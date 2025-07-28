package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireballEntity;

public class FireballSpell extends Spell {
    protected final int level;

    public FireballSpell(final int level) {
        super(0x0, SpellTypes.FIRE);
        this.level = level;
    }

    @Override
    public void use(final Spell.SpellContext ctx) {
        FireballEntity fireball = ctx.user() == null ? new FireballEntity(EntityType.FIREBALL, ctx.world()) : new FireballEntity(ctx.world(), ctx.user(), ctx.direction(), level);
        fireball.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
        fireball.setVelocity(ctx.direction().multiply(1.5));
        System.out.println("Direction: " + ctx.direction());
        ctx.world().spawnEntity(fireball);
    }

    @Override
    public Spell.SpellCooldown getCooldown() {
        return new Spell.SpellCooldown(40 * level, BTC.identifierOf("fireball"));
    }
}
