package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;

public class EnderPearlSpell extends Spell {
    public EnderPearlSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        EnderPearlEntity enderPearl = new EnderPearlEntity(ctx.world(), ctx.user());
        enderPearl.setVelocity(ctx.user(), ctx.user().getPitch(), ctx.user().getYaw(), 0.0F, 1.5F, 1.0F);
        ctx.world().spawnEntity(enderPearl);
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 30), BTC.identifierOf("ender_pearl"));
    }
}
