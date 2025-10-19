package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;

import java.util.Comparator;
import java.util.List;

public class StormSurgeSpell extends Spell {

    public StormSurgeSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        if (user == null || world.isClient) return;

        int maxTargets = args.getInt("targetCount", 5);
        double range = args.getDouble("range", 20.0);

        // Find nearby hostile entities
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class,
                user.getBoundingBox().expand(range),
                e -> e != user && ((e instanceof HostileEntity && args.getBoolean("onlyHostile")) || !args.getBoolean("onlyHostile")));

        // Sort by distance and limit count
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
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("storm_surge"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFB5FFFF;
    }
}
