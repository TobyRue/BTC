package io.github.tobyrue.btc.player_data;

import io.github.tobyrue.btc.spell.Spell;

import java.util.ArrayList;
import java.util.List;

public class PlayerSpellData {
    public List<Spell.InstancedSpell> knownSpells = new ArrayList<>();
    public List<Spell.InstancedSpell> favoriteSpells = new ArrayList<>();
}
