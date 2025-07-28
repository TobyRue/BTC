package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class Spell {
    protected final int color;
    protected final SpellTypes type;

    public Spell(final int color, final SpellTypes type) {
        this.color = color;
        this.type = type;
    }

    protected boolean canUse(final SpellContext ctx) {
        return ctx.data().getCooldown(this) == 0;
    }

    public final boolean tryUse(final SpellContext ctx) {
        if (canUse(ctx)) {
            use(ctx);
            ctx.data().setCooldown(this);
            return true;
        }
        return false;
    }

    @Nullable
    public SpellCooldown getCooldown() {
        return null;
    }

    protected abstract void use(final SpellContext ctx);

    public SpellTypes getSpellType() {
        return this.type;
    }

    public record SpellContext(World world, Vec3d pos, Vec3d direction, SpellDataStore data, @Nullable LivingEntity user) {}
    public record SpellCooldown(int ticks, Identifier key) {}
}