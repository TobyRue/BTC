package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class EldritchIllusionSpell extends Spell {

    public EldritchIllusionSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        if (ctx.user() instanceof EldritchLuminaryEntity luminaryEntity) {
            luminaryEntity.setIllusionTime(1);
            luminaryEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 3, false, false, false));
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() instanceof EldritchLuminaryEntity && super.canUse(ctx, args);
    }
    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 100), BTC.identifierOf("eldritch_illusion"));
    }
}
