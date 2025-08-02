package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

import java.util.List;

public class LifeStealSpell extends Spell {
    public LifeStealSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        List<LivingEntity> targets = ctx.world().getEntitiesByClass(LivingEntity.class,
                new Box(ctx.user().getBlockPos()).expand(args.getDouble("radius", 10)),
                entity -> entity != ctx.user() && entity.isAlive());

        // Define the percentage of health to take (e.g., 10% = 0.10 or 5% = 0.05)
        float healthPercentage = 0.10f;

        for (LivingEntity target : targets) {
            float targetHealth = target.getHealth();
            float damage = targetHealth * healthPercentage;

            target.damage(ctx.world().getDamageSources().magic(), damage);

            float healAmount = damage * 0.5f; // Heal for 50% of damage dealt
            ctx.user().heal(healAmount);
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 30), BTC.identifierOf("life_steal"));
    }
}
