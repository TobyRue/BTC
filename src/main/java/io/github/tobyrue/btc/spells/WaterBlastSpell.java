package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.Vec3d;

public class WaterBlastSpell extends Spell {

    public WaterBlastSpell() {
        super(SpellTypes.FIRE);
    }

    @Override
    public void use(final SpellContext ctx, final GrabBag args) {
        WaterBlastEntity waterBlast = new WaterBlastEntity(ModEntities.WATER_BLAST, ctx.world());
        waterBlast.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
        waterBlast.setVelocity(ctx.direction().multiply(1.5));
        if (ctx.user() != null) {
            waterBlast.setOwner(ctx.user());
        }
        waterBlast.setNoGravity(args.getBoolean("noGravity", false));
        ctx.world().spawnEntity(waterBlast);
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 200), BTC.identifierOf("water_blast"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0;
        //TODO
    }
}
