package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.MeltingIceBlock;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

public class IceBlockSpell extends ChanneledSpell implements UpgradableSpell {
    private static final WeakHashMap<LivingEntity, LivingEntity> STORED_TARGET = new WeakHashMap<>();
    private static final WeakHashMap<LivingEntity, BlockPos> STORED_BLOCK = new WeakHashMap<>();

    public IceBlockSpell() {
        super(
                SpellTypes.WATER,
                800,
                1,
                DisturbConfig.builder()
                        .level(DistributionLevels.CLICK)
                        .disturbableTill(80)
                        .moveableDistance(1)
                        .hold(20)
                        .build(),
                true, ParticleTypes.ENCHANTED_HIT, ParticleAnimation.SPIRAL,
                80,
                true
        );
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF53AFD6;
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, final Start start) {
        freezeTargetArea(ctx, args, ctx.user(), ctx.world());
    }

    @Override
    protected void runEnd(SpellContext ctx, GrabBag args, int tick) {
        var world = ctx.world();
        var user = ctx.user();
        var target = STORED_TARGET.get(user);
        var targetPos = STORED_BLOCK.get(user);

        if (target != null && targetPos != null) {
            double entityWidth = target.getWidth();
            double entityHeight = target.getHeight();
            double entityLength = target.getWidth();

            int rangeX = (int) Math.ceil(entityWidth / 2.0);
            int rangeY = (int) Math.ceil(entityHeight / 2.0);
            int rangeZ = (int) Math.ceil(entityLength / 2.0);

            BlockPos.Mutable mutablePos = new BlockPos.Mutable();

            for (int x = -rangeX; x <= rangeX; x++) {
                for (int y = -rangeY; y <= rangeY; y++) {
                    for (int z = -rangeZ; z <= rangeZ; z++) {
                        mutablePos.set(targetPos.getX() + x, targetPos.getY() + y + 1, targetPos.getZ() + z);

                        BlockState state = world.getBlockState(mutablePos);
                        if (state.getBlock() instanceof MeltingIceBlock) {
                            world.setBlockState(mutablePos, Blocks.AIR.getDefaultState());
                        }
                    }
                }
            }
        }
        super.runEnd(ctx, args, tick);
    }

    public void freezeTargetArea(SpellContext ctx, GrabBag args, LivingEntity player, World world) {
        int duration = args.getInt("duration", 200);
        int amplifier = args.getInt("amplifier", 4);
        int durationM = args.getInt("durationM", 200);
        int amplifierM = args.getInt("amplifierM", 1);

        if (!world.isClient) {
            LivingEntity target = isTargetInRange(ctx.user(), ctx.target(), args.getDouble("range", 32d));

            if (target != null) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, amplifier));
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, durationM, amplifierM));
                STORED_TARGET.put(player, target);
                STORED_BLOCK.put(player, target.getBlockPos());

                BlockPos targetPos = target.getBlockPos();
                double entityWidth = target.getWidth();
                double entityHeight = target.getHeight();
                double entityLength = target.getWidth();

                int rangeX = (int) Math.ceil(entityWidth / 2.0);
                int rangeY = (int) Math.ceil(entityHeight / 2.0);
                int rangeZ = (int) Math.ceil(entityLength / 2.0);

                BlockPos.Mutable mutablePos = new BlockPos.Mutable();

                for (int x = -rangeX; x <= rangeX; x++) {
                    for (int y = -rangeY; y <= rangeY; y++) {
                        for (int z = -rangeZ; z <= rangeZ; z++) {
                            mutablePos.set(targetPos.getX() + x, targetPos.getY() + y + 1, targetPos.getZ() + z);

                            BlockState state = world.getBlockState(mutablePos);
                            if (state.isReplaceable() || state.getFluidState().isStill()) {
                                world.setBlockState(mutablePos, ModBlocks.MELTING_ICE.getDefaultState());
                            }
                        }
                    }
                }

                world.playSound(null, targetPos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        assert ctx.user() != null;
        Entity target = getEntityLookedAt(ctx.user(), args.getDouble("range", 24d), args.getDouble("aimingForgiveness", 0.3D));
        return target != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("ice_block"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_duration")));
        upgrades.add(new Pair<>(BTC.identifierOf("netherite_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_slowness_potency")));
        upgrades.add(new Pair<>(BTC.identifierOf("copper_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_moveable_distance")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 600, 300, 900, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 24.0, 12.0, 48.0, 3.0, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "duration", 200, 80, 400, 20, BTC.identifierOf("quartz_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "amplifier", 4, 1, 6, 1, BTC.identifierOf("netherite_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "moveableDistance", 1, 0, 10, 2, BTC.identifierOf("copper_upgrade"));
        return upgrades;
    }
}