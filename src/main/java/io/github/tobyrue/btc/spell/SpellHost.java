package io.github.tobyrue.btc.spell;

import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface SpellHost<T> {
    void tickCooldowns(final T t);
    SpellDataStore getSpellDataStore(final T t);
    default boolean tryUseSpell(final World world, final Vec3d pos, final Vec3d direction, final @Nullable LivingEntity user, final @Nullable LivingEntity target, final T t) {
        final var data = this.getSpellDataStore(t);
        if (data.getSpell() instanceof Spell spell) {
            return spell.tryUse(new Spell.SpellContext(world, pos, direction, data, user, target), data.getArgs());
        }
        return false;
    }
    default boolean tryUseSpell(final World world, final Vec3d pos, final Vec3d direction, final @Nullable LivingEntity user, final T t) {
        final var data = this.getSpellDataStore(t);
        if (data.getSpell() instanceof Spell spell) {
            return spell.tryUse(new Spell.SpellContext(world, pos, direction, data, user, Spell.getEntityLookedAt(user, data.getArgs().getDouble("range", 32), data.getArgs().getDouble("aimingForgiveness", 0.5)) instanceof LivingEntity l2 ? l2 : null), data.getArgs());
        }
        return false;
    }
}
