package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.enums.SpellTypes;

public abstract class LeveledSpell {
    protected final int color;
    protected final SpellTypes type;

    public LeveledSpell(final int color, final SpellTypes type) {
        this.color = color;
        this.type = type;
    }

    protected boolean canUse(final Spell spell, final Spell.SpellContext ctx, final int level) {
        return ctx.data().getCooldown(spell) == 0;
    }

    public Spell.SpellCooldown getCooldown(final int level) {
        return null;
    }

    protected abstract void use(final Spell.SpellContext ctx, final int level);

    public final Spell withLevel(final int level) {
        return new Spell(this.color, this.type) {
            @Override
            protected void use(final Spell.SpellContext ctx) {
                LeveledSpell.this.use(ctx, level);
            }

            @Override
            public SpellCooldown getCooldown() {
                return LeveledSpell.this.getCooldown(level);
            }

            @Override
            protected boolean canUse(final Spell.SpellContext ctx) {
                return LeveledSpell.this.canUse(this, ctx, level);
            }
        };
    }
}
