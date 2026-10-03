package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.ai.ProtectOwnerGoal;
import io.github.tobyrue.btc.entity.ai.SummonTargetGoal;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.btc.util.SummonableEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RaiseUndeadSpellInstant extends ChanneledSpell implements UpgradableSpell {

    private static final List<EntityType<? extends LivingEntity>> UNDEAD_TYPES = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.HUSK,
            EntityType.STRAY
    );

    public RaiseUndeadSpellInstant() {
        super(
                SpellTypes.EARTH, 35 * 20, 1,
                DisturbConfig.builder()
                        .level(DistributionLevels.NONE)
                        .disturbableTill(0)
                        .build(),
                true, ParticleTypes.ENCHANTED_HIT, ParticleAnimation.SPIRAL, 0, true
        );
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, final Start start) {
        var user = ctx.user();
        var world = ctx.world();

        if (!(world instanceof ServerWorld serverWorld) || user == null) return;

        killAllSummonsForOwner(serverWorld, user.getUuid());

        Random random = world.random;
        int count = args.getInt("count", 10);

        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.2F);

        for (int i = 0; i < count; i++) {
            EntityType<? extends LivingEntity> type = UNDEAD_TYPES.get(random.nextInt(UNDEAD_TYPES.size()));
            LivingEntity entity = type.create(world);
            if (!(entity instanceof MobEntity undead)) continue;

            Vec3d pos = user.getPos().add(
                    (random.nextDouble() - 0.5) * 5.0,
                    0,
                    (random.nextDouble() - 0.5) * 5.0
            );

            BlockPos ground = findSpawnableGround(world, user.getBlockPos(), 24);
            double spawnY = ground == null ? pos.getY() : ground.getY() + 1;

            undead.refreshPositionAndAngles(pos.x, spawnY, pos.z, random.nextFloat() * 360F, 0);

            ((SummonableEntity) undead).btc$setOwnerUuid(user.getUuid());

            undead.targetSelector.clear(g -> true);

            if (undead instanceof SkeletonEntity || undead instanceof StrayEntity) {
                undead.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                AbstractSkeletonEntity skeleton = (AbstractSkeletonEntity) undead;
                undead.goalSelector.add(2, new BowAttackGoal<>(skeleton, 1.0D, 20, 15.0F));
            } else if (undead instanceof ZombieEntity pathAwareEntity) {
                undead.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
                undead.goalSelector.add(2, new MeleeAttackGoal(pathAwareEntity, 1.25D, false));
            }

            undead.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));

            undead.goalSelector.add(1, new ProtectOwnerGoal(undead, 1.25D));
            undead.targetSelector.add(1, new SummonTargetGoal(undead));

            world.spawnEntity(undead);

            serverWorld.spawnParticles(
                    ParticleTypes.SOUL,
                    pos.x, pos.y + 1.0, pos.z,
                    10, 0.5, 0.5, 0.5, 0.02
            );
        }
    }

    @Override
    protected void runEnd(SpellContext ctx, GrabBag args, int tick) {
        var user = ctx.user();
        var world = ctx.world();

        if (world instanceof ServerWorld serverWorld && user != null) {
            killAllSummonsForOwner(serverWorld, user.getUuid());
        }
        super.runEnd(ctx, args, tick);
    }

    public static void killAllSummonsForOwner(ServerWorld serverWorld, UUID ownerUuid) {
        Box worldBox = new Box(-3.0E7, -3.0E7, -3.0E7, 3.0E7, 3.0E7, 3.0E7);

        serverWorld.getEntitiesByClass(MobEntity.class, worldBox, e -> {
            if (e instanceof SummonableEntity summonable) {
                return ownerUuid.equals(summonable.btc$getOwnerUuid());
            }
            return false;
        }).forEach(LivingEntity::kill);
    }

    @Nullable
    public BlockPos findSpawnableGround(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());

        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            if (world.getBlockState(pos).isSolidBlock(world, pos) && !world.getBlockState(pos.up()).isSolidBlock(world, pos.up()) && !world.getBlockState(pos.up()).isOf(Blocks.CHEST)) {
                return pos;
            }
        }
        return null;
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("raise_undead"));
    }

    @Override
    protected boolean canUse(SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF3CFF9B;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_summon_count")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_cast_time")));
        upgrades.add(new Pair<>(BTC.identifierOf("copper_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_moveable_distance")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 400, 200, 800, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "count", 10, 3, 25, 2, BTC.identifierOf("amethyst_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cast_time", 700, 200, 1200, 100, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "moveableDistance", 3, 0, 15, 2, BTC.identifierOf("copper_upgrade"));
        return upgrades;
    }
}