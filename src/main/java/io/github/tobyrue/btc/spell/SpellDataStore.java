package io.github.tobyrue.btc.spell;

import io.github.tobyrue.xml.util.Nullable;

public interface SpellDataStore {

    @Nullable Spell getSpell();
    void setSpell(final Spell spell);
    int getCooldown(@Nullable final Spell spell);
    float getCooldownPercent(@Nullable final Spell spell);
    void setCooldown(final Spell spell);
}
