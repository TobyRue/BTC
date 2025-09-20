package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class PotionAreaEffectSpell extends Spell {

    public PotionAreaEffectSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();

        Identifier id = Identifier.tryParse(args.getString("effect", "minecraft:strength"));
        if (id == null) {
            return;
        }

        Optional<RegistryEntry.Reference<StatusEffect>> entry = Registries.STATUS_EFFECT.getEntry(id);
        if (entry.isEmpty()) {
            return;
        }
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(args.getDouble("radius", 8D)),
                entity -> (((entity != user) && !args.getBoolean("includeUser")) || args.getBoolean("includeUser")) && entity instanceof LivingEntity && ((entity instanceof HostileEntity && args.getBoolean("onlyHostile")) || !args.getBoolean("onlyHostile"))); // Only affect hostile mobs
        for (LivingEntity entity : entities) {
            entity.addStatusEffect(new StatusEffectInstance(entry.get(), args.getInt("duration", 60), args.getInt("amplifier")));
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }
    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown"), BTC.identifierOf("potion"));
    }

    @Override
    public Text getName(final GrabBag args) {
        return Text.translatable(this.getTranslationKey() + "." + (args.getString("name", "normal")));
    }

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
