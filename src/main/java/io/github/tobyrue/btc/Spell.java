package io.github.tobyrue.btc;

import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class Spell {
    public Spell(int color, SpellTypes type, @Nullable SpellCooldown cooldown) {

    }

    public record SpellCooldown(int ticks, Identifier key) {}
}