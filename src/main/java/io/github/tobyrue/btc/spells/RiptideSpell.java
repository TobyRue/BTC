package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.enchantment.effect.entity.PlaySoundEnchantmentEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;

import java.util.List;

public class RiptideSpell extends Spell {

    public RiptideSpell() {
        super(SpellTypes.WATER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0x3FD0FF; // watery blue
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        LivingEntity user = ctx.user();

        int duration = args.getInt("duration", 20); // ticks
        float damage = args.getFloat("damage", 2.0f);

        Vec3d dir = ctx.direction().normalize();
        double speed = args.getDouble("speed", 2.0d); // configurable speed
        Vec3d velocity = dir.multiply(speed);

        user.addVelocity(velocity.x, velocity.y, velocity.z);
        user.velocityModified = true; // makes sure velocity syncs with client

        // Trigger riptide animation/sound/etc.
        var sound = List.of(SoundEvents.ITEM_TRIDENT_RIPTIDE_1, SoundEvents.ITEM_TRIDENT_RIPTIDE_2, SoundEvents.ITEM_TRIDENT_RIPTIDE_3);
        var random = ctx.world().getRandom();
        // If player, play trident riptide sound (optional flavor)
        if (user instanceof PlayerEntity player) {
            player.useRiptide(duration, damage, user.getMainHandStack());

            ctx.world().playSound(
                    player,
                    player.getBlockPos(),
                    sound.get(random.nextBetween(0, 2)).value(),
                    user.getSoundCategory(),
                    1.0F,
                    1.0F
            );
        }
    }
    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }
    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 100), BTC.identifierOf("riptide"));
    }
}
