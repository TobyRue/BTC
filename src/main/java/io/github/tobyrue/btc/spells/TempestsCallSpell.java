package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class TempestsCallSpell extends Spell {

    public TempestsCallSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(final SpellContext ctx, final GrabBag args) {
        double pull_radius = args.getDouble("pull_radius", 25d);
        double pull_strength = args.getDouble("pull_strength", 3d);
        List<LivingEntity> entities = ctx.world().getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(pull_radius), entity -> entity != ctx.user());
        // Log the number of entities found
        // Pull all mobs towards the player
        for (LivingEntity entity : entities) {
            // Calculate the direction towards the player
            double dx = ctx.user().getX() - entity.getX();
            double dy = ctx.user().getY() - entity.getY();
            double dz = ctx.user().getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance != 0) {
                entity.setVelocity(dx / distance * pull_strength, dy / distance * pull_strength, dz / distance * pull_strength);
            }
        }
    }

    @Override
    protected boolean canUse(final Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 160), BTC.identifierOf("tempests_call"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF84A1FF;
    }
}
