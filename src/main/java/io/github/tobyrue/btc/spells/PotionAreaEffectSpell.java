package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class PotionAreaEffectSpell extends Spell implements UpgradableSpell {

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

        double radius = args.getDouble("textRadius", 8D);
        int duration = args.getInt("duration", 60);
        int amplifier = args.getInt("amplifier", 0);

        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(radius),
                entity -> (((entity != user) && !args.getBoolean("includeUser")) || args.getBoolean("includeUser")) && entity instanceof LivingEntity && ((entity instanceof HostileEntity && args.getBoolean("onlyHostile")) || !args.getBoolean("onlyHostile"))); // Only affect hostile mobs
        for (LivingEntity entity : entities) {
            entity.addStatusEffect(new StatusEffectInstance(entry.get(), duration, amplifier));
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown"), Identifier.tryParse(args.getString("cooldown_name", "btc:potion")));
    }

    @Override
    public Text getDescription(GrabBag args) {
        return Text.translatable(this.getTranslationKey() + "." + (args.getString("name", "normal")) + ".description");
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

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_duration")));
        upgrades.add(new Pair<>(BTC.identifierOf("netherite_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_potency")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", args.getInt("cooldown", 200), 40, 1000, -20, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "textRadius", 8.0, 3.0, 20.0, 1.5, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "duration", 60, 20, 600, 20, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "amplifier", 0, 0, 5, 1, BTC.identifierOf("netherite_upgrade"));
        return upgrades;
    }
}