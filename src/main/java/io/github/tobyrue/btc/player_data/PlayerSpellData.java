package io.github.tobyrue.btc.player_data;

import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;

import java.util.ArrayList;
import java.util.List;

public class PlayerSpellData {
    public List<Spell.InstancedSpell> knownSpells = new ArrayList<>();
    public List<Spell.InstancedSpell> favoriteSpells = new ArrayList<>();

    public PlayerSpellData() {
        for (int i = 0; i < 10; i++) {
            favoriteSpells.add(new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty()));
        }
    }
}
