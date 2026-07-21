package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ClusterWindChargeSpell extends Spell {

    public ClusterWindChargeSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(final SpellContext ctx, final GrabBag args) {
        int count = args.getInt("count", 8);
        double spreadFactor = args.getDouble("spreadFactor", 0.2d);

        for (int i = 0; i < count; i++) {
            WindChargeEntity windCharge = new WindChargeEntity(EntityType.WIND_CHARGE, ctx.world());
            if (ctx.user() != null) {
                windCharge.setOwner(ctx.user());
            }

            double randomPitch = (Math.random() - 0.5) * spreadFactor;
            double randomYaw = (Math.random() - 0.5) * spreadFactor;

            Vec3d scatterDirection = ctx.direction().add(randomYaw, randomPitch, randomYaw).normalize();

            Vec3d spawnPosition = new Vec3d(ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ()).add(ctx.direction().multiply(1.5));
            windCharge.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);

            windCharge.setVelocity(scatterDirection.multiply(2.0));
            ctx.world().spawnEntity(windCharge);
        }
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 80), BTC.identifierOf("cluster_wind_charge"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFA8FFF9;
    }
}
