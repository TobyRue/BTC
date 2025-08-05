package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EarthSpikeEntity;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class EarthSpikeLineSpell extends Spell {
    public EarthSpikeLineSpell() {
        super(SpellTypes.EARTH);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        int spikeCount = args.getInt("spikeCount", 8);
        int yRange = args.getInt("yRange", 12);


        var user = ctx.user();
        float yaw = user.getYaw();
        double rad = Math.toRadians(yaw);

        double stepX = -Math.sin(rad) * 1.5;
        double stepZ = Math.cos(rad) * 1.5;

        double startX = user.getX();
        double startZ = user.getZ();
        double startY = user.getY();


        for (int i = 0; i < spikeCount; i++) {
            double x = startX + stepX * i;
            double z = startZ + stepZ * i;
            BlockPos searchPos = new BlockPos((int) x, (int) startY, (int) z);


            BlockPos groundPos = findSpawnableGround(ctx.world(), searchPos, yRange);

            if (groundPos != null) {

                EarthSpikeEntity spike = new EarthSpikeEntity(ctx.world(), groundPos.getX(), groundPos.getY(), groundPos.getZ(), yaw, user);
                user.getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(user));
                ctx.world().spawnEntity(spike);
            }
        }
    }

    @org.jetbrains.annotations.Nullable
    public BlockPos findSpawnableGround(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());
        // Start from top and go downwards
        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            // Improved block check to ensure solid block and air above or open space above
            if (world.getBlockState(pos).isSolidBlock(world, pos) && !world.getBlockState(pos.up()).isSolidBlock(world, pos.up()) && !world.getBlockState(pos.up()).isOf(Blocks.CHEST)) {
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
        return new Spell.SpellCooldown(args.getInt("cooldown", 200), BTC.identifierOf("earth_spike_line"));
    }
}
