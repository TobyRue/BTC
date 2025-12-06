package io.github.tobyrue.btc.spell;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.stream.IntStream;

public abstract class Spell {
    protected final SpellTypes type;
    public static final Codec<Spell> CODEC = Identifier.CODEC.xmap(
            ModRegistries.SPELL::get,
            ModRegistries.SPELL::getId
    );

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

    public Text getDescription(final GrabBag args) {
        return Text.translatable(this.getTranslationKey() + ".description");
    }

    public Text getDescription() {
        return getDescription(GrabBag.empty());
    }

    public Text getName(final GrabBag args) {
        return Text.translatable(this.getTranslationKey());
    }

    public Text getName() {
        return this.getName(GrabBag.empty());
    }

    @Override
    public String toString() {
        return ModRegistries.SPELL.getEntry(this).getIdAsString();
    }
    @Deprecated
    public String getPureName() {
        return toString().substring(ModRegistries.SPELL.getId(this).getNamespace().length() + 1);
    }

    public String getTranslationKey() {
        return Util.createTranslationKey("spell", ModRegistries.SPELL.getId(this));
    }

    public static Identifier getId(Spell type) {
        return ModRegistries.SPELL.getId(type);
    }

    public record SpellContext(World world, Vec3d pos, Vec3d direction, SpellDataStore data, @Nullable LivingEntity user) {}
    public record SpellCooldown(int ticks, Identifier key) {}

    public record InstancedSpell(Spell spell, GrabBag args) {
        public static final Codec<InstancedSpell> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
                Spell.CODEC.fieldOf("spell").forGetter(InstancedSpell::spell),
                NbtCompound.CODEC.fieldOf("args").forGetter(s -> GrabBag.toNBT(s.args()))
        ).apply(instance, (spell, args) -> new InstancedSpell(spell, GrabBag.fromNBT(args)))));


//                Codec.INT_STREAM.comapFlatMap(stream -> Util.decodeFixedLengthArray(stream, 3).map(values -> new BlockPos(values[0], values[1], values[2])), pos -> IntStream.of(pos.getX(), pos.getY(), pos.getZ())).stable();
    }
}