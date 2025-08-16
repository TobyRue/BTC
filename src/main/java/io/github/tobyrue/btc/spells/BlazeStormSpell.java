package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlazeStormSpell extends Spell {

    public BlazeStormSpell() {
        super(SpellTypes.FIRE);
    }

    @Override
    public void use(final SpellContext ctx, final GrabBag args) {
        double deviation = args.getDouble("deviation", 0.2d);
        int duration = args.getInt("duration", 100);
        int amount = args.getInt("amount", 20);

        World world = ctx.world();
        LivingEntity user = ctx.user(); // can be null

        // Break the burst over duration using your Ticker
        int ticksPerShot = Math.max(1, duration / amount);

        ((Ticker.TickerTarget) (user)).add(
                Ticker.forTicks(tick -> {
                    Vec3d dir = ctx.user().getRotationVec(1).normalize();
                    if (tick % ticksPerShot == 0) {
                        // Spawn a small fireball with random deviation
                        Vec3d dev = new Vec3d(
                                world.getRandom().nextTriangular(dir.x, deviation),
                                world.getRandom().nextTriangular(dir.y, deviation),
                                world.getRandom().nextTriangular(dir.z, deviation)
                        ).normalize();

                        SmallFireballEntity fireball;
                        // Fired by a living entity
                        fireball = new SmallFireballEntity(world, user, dev);
                        fireball.setPosition(user.getX(), user.getBodyY(0.5) + 0.5, user.getZ());

                        world.spawnEntity(fireball);
                    }
                }, duration) // run for full duration
        );
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }


    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("blaze_storm"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFFF5400;
    }
}
