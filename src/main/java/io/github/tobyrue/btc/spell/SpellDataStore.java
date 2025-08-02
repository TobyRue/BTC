package io.github.tobyrue.btc.spell;

import io.github.tobyrue.xml.util.Nullable;

public interface SpellDataStore {

    @Nullable Spell getSpell();
    GrabBag getArgs();

    default void setSpell(final Spell spell) {
        setSpell(spell, null);
    }

    void setSpell(final Spell spell, @Nullable final GrabBag args);
    int getCooldown(@Nullable final Spell.SpellCooldown cooldown);
    float getCooldownPercent(@Nullable final Spell.SpellCooldown cooldown);
    void setCooldown(@Nullable final Spell.SpellCooldown cooldown);
}
