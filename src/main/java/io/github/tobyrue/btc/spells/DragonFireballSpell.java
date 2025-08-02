package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;

public class DragonFireballSpell extends Spell {
    public DragonFireballSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        DragonFireballEntity dragonFireball = ctx.user() == null ? new DragonFireballEntity(EntityType.DRAGON_FIREBALL, ctx.world()) : new DragonFireballEntity(ctx.world(), ctx.user(), ctx.direction());
        dragonFireball.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
        dragonFireball.setVelocity(ctx.direction().multiply(1.5));
        ctx.world().spawnEntity(dragonFireball);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return user == null ? null : new Spell.SpellCooldown(args.getInt("cooldown", 800), BTC.identifierOf("dragon_fireball"));
    }
}
