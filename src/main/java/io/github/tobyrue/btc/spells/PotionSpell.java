package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class PotionSpell extends Spell {


    public PotionSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    protected void use(Spell.SpellContext ctx, final GrabBag args) {
        Identifier id = Identifier.tryParse(args.getString("effect", "minecraft:strength"));
        if (id == null) {
            return;
        }

        Optional<RegistryEntry.Reference<StatusEffect>> entry = Registries.STATUS_EFFECT.getEntry(id);
        if (entry.isEmpty()) {
            return;
        }

        ctx.user().addStatusEffect(new StatusEffectInstance(entry.get(), args.getInt("duration", 60), args.getInt("amplifier")));

//        var effect = Registries.STATUS_EFFECT.getEntry(Registries.STATUS_EFFECT.get(Identifier.tryParse(args.getString("effect", "minecraft:strength"))));
//        ctx.user().addStatusEffect(new StatusEffectInstance(effect, args.getInt("duration", 60), args.getInt("amplifier")));
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }
    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown"), BTC.identifierOf("potion"));
    }

//    @Override
//    public int getColor(final GrabBag args) {
//        var effect = Registries.STATUS_EFFECT.get(Identifier.tryParse(args.getString("effect", "minecraft:strength")));
//        return effect != null ? effect.getColor() : 0;
//    }
    @Override
    public int getColor(final GrabBag args) {
        Identifier id = Identifier.tryParse(args.getString("effect", "minecraft:strength"));
        StatusEffect effect = Registries.STATUS_EFFECT.get(id);
        if (effect == null) {
            return 0xFFFFFFFF; // fallback: white and fully opaque
        }

        int rgb = effect.getColor(); // usually 0xRRGGBB
        int argb = 0xFF000000 | rgb; // prepend FF as alpha
        return argb;
    }
}
