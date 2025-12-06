package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class FireStormSpell extends Spell {


    public FireStormSpell() {
        super(SpellTypes.FIRE);
    }

    @Override
    public void use(final Spell.SpellContext ctx, final GrabBag args) {
        int duration = args.getInt("duration", 2);
        double maxRadius = args.getDouble("maxRadius", 8d);


        Vec3d storedPos = ctx.user().getPos();
        ((Ticker.TickerTarget) ctx.user()).add(Ticker.forSeconds((ticks) -> {
            if (ctx.world() instanceof ServerWorld serverWorld) {
                double progress = ticks / (double) (duration * 20);
                double radius = maxRadius * progress;


                int count = (int) (maxRadius / 64d * 1280d);
                for (int i = 0; i < count; i++) {

                    double angle = (2 * Math.PI / count) * i;

                    double x = storedPos.getX() + Math.sin(angle) * radius;
                    double z = storedPos.getZ() + Math.cos(angle) * radius;

                    double yOffset = 0.2;
                    double y = storedPos.getY() + yOffset;

                    double xSpeed = Math.sin(angle) * 0.2;
                    double zSpeed = Math.cos(angle) * 0.2;

                    serverWorld.spawnParticles(ParticleTypes.FLAME, x, y, z, 0, xSpeed, 0.0, zSpeed, 0);
                }

                for (LivingEntity target : serverWorld.getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(maxRadius), e -> e.isAlive() && e != ctx.user())) {
                    double dist = target.getPos().distanceTo(storedPos);

                    double stepSize = maxRadius / duration;
                    if (dist <= radius && dist > (radius - stepSize)) {
                        target.setOnFireFor((float) ((radius * -1) + maxRadius));
                        target.damage(ctx.user().getDamageSources().inFire(), Math.min(8, (float) ((radius * -1) + maxRadius)));
                    }
                }
            }
        }, duration));
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("fire_storm"));
    }

    @Override
    public Text getDescription(GrabBag args) {
        return Text.translatable(this.getTranslationKey() + "." + (args.getDouble("maxRadius", 8d) >= 8d ? (args.getDouble("maxRadius", 8d) == 8d ? "normal" : "strong") : "concentrated") + ".description");
    }

    @Override
    public Text getName(final GrabBag args) {
        return Text.translatable(this.getTranslationKey() + "." + (args.getDouble("maxRadius", 9d) >= 9d ? (args.getDouble("maxRadius", 9d) == 9d ? "normal" : "strong") : "concentrated"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFFF9400;
    }
}
