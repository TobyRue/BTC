package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

public class TestSpell extends ChanneledSpell {
    public TestSpell() {
        super(SpellTypes.GENERIC, 20, 2, new Disturb(DistributionLevels.DAMAGE_CROUCH_AND_MOVE, 20, 4), true, ParticleTypes.ENCHANTED_HIT, ParticleAnimation.SPIRAL);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xffffff;
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick) {
        System.out.println("Test Spell: " + tick);
    }
}
