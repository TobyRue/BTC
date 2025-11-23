package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.CreeperPillarEntity;
import io.github.tobyrue.btc.enums.CreeperPillarType;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CreeperWallCircleSpell extends Spell {
    public CreeperWallCircleSpell() {
        super(SpellTypes.EARTH);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        int spikes = args.getInt("spikes", 20);
        double radius = args.getDouble("radius", 3d);

        user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 120, 2));
        for (int i = 0; i < spikes; i++) {
            double angle = 2 * Math.PI * i / spikes;
            double x = user.getX() + radius * Math.cos(angle);
            double z = user.getZ() + radius * Math.sin(angle);
            BlockPos groundPos = findSpawnableGroundPillar(world, new BlockPos((int) x, (int) user.getY(), (int) z), 10);
            if (groundPos != null) {
                CreeperPillarEntity pillar = new CreeperPillarEntity(world, x, groundPos.getY(), z, user.getYaw(), user, CreeperPillarType.NORMAL);
                world.emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(user));
                world.spawnEntity(pillar);
            }
        }
    }

    @org.jetbrains.annotations.Nullable
    public static BlockPos findSpawnableGroundPillar(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());


        // Start from top and go downwards
        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            // Improved block check to ensure solid block and air above or open space above
            if (!(world.getBlockState(pos).getBlock() instanceof AirBlock) && world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) {
                return pos;
            }
        }

        // Fallback if no valid ground is found
        return null;
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 1200), BTC.identifierOf("creeper_wall_circle"));
    }
}
