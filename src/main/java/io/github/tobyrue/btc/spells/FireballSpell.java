package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.UnlockScrollItem;
import io.github.tobyrue.btc.mixin.FireballEntityAccessor;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.function.Function;

public class FireballSpell extends Spell implements UpgradableSpell {

    public FireballSpell() {
        super(SpellTypes.FIRE);
    }

    @Override
    public void use(final Spell.SpellContext ctx, final GrabBag args) {
        FireballEntity fireball = ctx.user() == null ? new FireballEntity(EntityType.FIREBALL, ctx.world()) : new FireballEntity(ctx.world(), ctx.user(), ctx.direction(), args.getInt("level", 1));
        ((FireballEntityAccessor) fireball).btc$explosionPower(args.getInt("level", 1));
        fireball.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
        fireball.setVelocity(ctx.direction().multiply(1.5));
        ctx.world().spawnEntity(fireball);
    }

    @Override
    public Text getName(final GrabBag args) {
        return Text.translatable(this.getTranslationKey() + "." + (args.getInt("level") > 3 ? "strong" : "weak"));
    }

    @Override
    public Text getDescription(final GrabBag args) {
        return Text.translatable(this.getTranslationKey() + "." + (args.getInt("level") > 3 ? "strong" : "weak") + ".description");
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", Math.max(80, 60 * args.getInt("level", 1))), BTC.identifierOf("fireball"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFFF5400;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "level", 1, 1, 5, 1, BTC.identifierOf("amethyst_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", Math.max(200, 60 * args.getInt("level", 1)),60, 200, -20, BTC.identifierOf("gold_ingot_upgrade"));
        return upgrades;
    }

}
