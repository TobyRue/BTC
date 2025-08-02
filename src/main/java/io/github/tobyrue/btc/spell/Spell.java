package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class Spell {
    protected final SpellTypes type;

    public Spell(final SpellTypes type) {
        this.type = type;
    }

    protected boolean canUse(final SpellContext ctx, final GrabBag args) {
        return ctx.data().getCooldown(this.getCooldown(args, ctx.user())) == 0;
    }

    public final boolean tryUse(final SpellContext ctx, final GrabBag args) {
        if (canUse(ctx, args)) {
            use(ctx, args);
            ctx.data().setCooldown(this.getCooldown(args, ctx.user()));
            return true;
        }
        return false;
    }

    @Nullable
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return null;
    }

    public abstract int getColor(GrabBag args);

    protected abstract void use(final SpellContext ctx, final GrabBag args);

    public SpellTypes getSpellType() {
        return this.type;
    }

    public Text getName() {
        return Text.translatable(this.getTranslationKey());
    }

    @Override
    public String toString() {
        return ModRegistries.SPELL.getEntry(this).getIdAsString();
    }
    public String getPureName() {
        return toString().substring(ModRegistries.SPELL.getId(this).getNamespace().length() + 1);
    }

    public String getTranslationKey() {
        return Util.createTranslationKey("spell", ModRegistries.SPELL.getId(this));
    }

    public record SpellContext(World world, Vec3d pos, Vec3d direction, SpellDataStore data, @Nullable LivingEntity user) {}
    public record SpellCooldown(int ticks, Identifier key) {}
}