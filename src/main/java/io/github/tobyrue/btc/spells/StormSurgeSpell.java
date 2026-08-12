package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class StormSurgeSpell extends Spell implements UpgradableSpell {

    public StormSurgeSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        if (user == null || world.isClient) return;

        int maxTargets = args.getInt("targetCount", 5);
        double range = args.getDouble("range", 20.0d);

        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class,
                user.getBoundingBox().expand(range),
                e -> e != user && ((e instanceof HostileEntity && args.getBoolean("onlyHostile")) || !args.getBoolean("onlyHostile")));

        entities.sort(Comparator.comparingDouble(e -> e.squaredDistanceTo(user)));
        if (entities.size() > maxTargets) {
            entities = entities.subList(0, maxTargets);
        }

        if (!world.isClient && world instanceof ServerWorld serverWorld) {
            for (LivingEntity target : entities) {
                LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(serverWorld);
                if (lightning != null) {
                    lightning.refreshPositionAfterTeleport(target.getPos());
                    lightning.setCosmetic(false);
                    serverWorld.spawnEntity(lightning);
                }
            }
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("storm_surge"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFB5FFFF;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_target_count")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 400, 150, 600, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "targetCount", 5, 1, 15, 1, BTC.identifierOf("amethyst_shard_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 20.0, 10.0, 45.0, 2.5, BTC.identifierOf("ender_pearl_upgrade"));
        return upgrades;
    }
}