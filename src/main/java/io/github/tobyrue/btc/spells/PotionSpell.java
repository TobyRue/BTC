package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public class PotionSpell extends Spell {
    protected final RegistryEntry<StatusEffect> effect;
    protected final int duration;
    protected final int amplifier;
    protected final int cooldown;

    public PotionSpell(RegistryEntry<StatusEffect> effect, int color, int duration, int amplifier, int cooldown) {
        super(color, SpellTypes.GENERIC);
        this.effect = effect;
        this.duration = duration;
        this.amplifier = amplifier;
        this.cooldown = cooldown;
    }

    @Override
    protected void use(Spell.SpellContext ctx) {
        ctx.user().addStatusEffect(new StatusEffectInstance(effect, duration, amplifier));
    }
    @Override
    protected boolean canUse(Spell.SpellContext ctx) {
        return ctx.user() != null && super.canUse(ctx);
    }
    @Override
    public Spell.SpellCooldown getCooldown() {
        return new Spell.SpellCooldown(cooldown, BTC.identifierOf("potion"));
    }
}
