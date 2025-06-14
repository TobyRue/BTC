package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.entity.custom.CreeperPillarEntity;
import io.github.tobyrue.btc.entity.custom.EarthSpikeEntity;
import io.github.tobyrue.btc.enums.CreeperPillarType;
import io.github.tobyrue.btc.enums.SpellBookAttacks;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SpellBookItem extends Item {
    private static final Integer SPIKE_Y_RANGE = 12;
    private static final Integer SPIKE_COUNT = 8;

    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        SpellBookAttacks current = getElement(stack);
        SpellBookAttacks next = SpellBookAttacks.next(current);

        if (!player.isSneaking()) {
            switch (current) {
                case WATER_BLAST -> {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.8f + world.getRandom().nextFloat() * 0.4f);
                    Vec3d velocity = player.getRotationVec(1.0f).multiply(1.5f);
                    if (!world.isClient) {
                        WaterBlastEntity waterBlast = new WaterBlastEntity(player, world, player.getX(), player.getY() + 1.25, player.getZ(), velocity);
                        world.spawnEntity(waterBlast);
                        player.getItemCooldownManager().set(this, 15);
                    }
                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    return TypedActionResult.success(stack);
                }
                case FIREBALL -> {
                    Vec3d velocity = player.getRotationVec(1.0f).multiply(5.5f);
                    if (!world.isClient) {
                        FireballEntity fireball = new FireballEntity(world, player, velocity, 1);
                        world.spawnEntity(fireball);
                        player.getItemCooldownManager().set(this, 15);
                    }
                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    return TypedActionResult.success(stack);
                }
                case DRAGON_FIREBALL -> {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.8f + world.getRandom().nextFloat() * 0.4f);
                    Vec3d velocity = player.getRotationVec(1.0f).multiply(1.5f);
                    if (!world.isClient) {
                        DragonFireballEntity dragonFireball = new DragonFireballEntity(world, player, velocity);
                        world.spawnEntity(dragonFireball);
                        player.getItemCooldownManager().set(this, 15);
                    }
                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    return TypedActionResult.success(stack);
                }
                case WIND_CHARGE -> {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.8f + world.getRandom().nextFloat() * 0.4f);
                    WindChargeEntity windCharge = new WindChargeEntity(player, world, player.getX(), player.getY() + 1.0, player.getZ());
                    windCharge.setVelocity(player.getRotationVec(1.0f).multiply(1.5));
                    player.getItemCooldownManager().set(this, 10);
                    world.spawnEntity(windCharge);
                    return TypedActionResult.success(stack);
                }
                case EARTH_SPIKE -> {
                    spawnEarthSpikesTowardsYaw(world, player, SPIKE_Y_RANGE, SPIKE_COUNT);
                    return TypedActionResult.success(stack);
                }
                case REGENERATION -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2));
                    player.getItemCooldownManager().set(this, 150);
                    return TypedActionResult.success(stack);
                }
                case CREEPER_WALL_CIRCLE -> {
                    int spikes = Math.max(8, (int) (3 * 3) + 8);
                    double radius = 3;
                    for (int i = 0; i < spikes; i++) {
                        double angle = 2 * Math.PI * i / spikes;
                        double x = player.getX() + radius * Math.cos(angle);
                        double z = player.getZ() + radius * Math.sin(angle);
                        BlockPos groundPos = findSpawnableGroundPillar(world, new BlockPos((int) x, (int) player.getY(), (int) z), 8);
                        if (groundPos != null) {
                            CreeperPillarEntity pillar = new CreeperPillarEntity(world, x, groundPos.getY(), z, player.getYaw(), player, CreeperPillarType.NORMAL);
                            world.emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(player));
                            world.spawnEntity(pillar);
                        }
                    }
                    return TypedActionResult.success(stack);

                }
                case CREEPER_WALL_TRAP -> {
                    Entity entityLookedAt = getEntityLookedAt(player, 16);
                    if (entityLookedAt != null) {
                        System.out.println("Entity Looked at: " + entityLookedAt + ", Which pos is: " + entityLookedAt.getPos());
                        int spikes = Math.max(8, (int) (3 * 3) + 8);
                        double radius = 2;
                        for (int i = 0; i < spikes; i++) {
                            double angle = 2 * Math.PI * i / spikes;
                            double x = entityLookedAt.getX() + radius * Math.cos(angle);
                            double z = entityLookedAt.getZ() + radius * Math.sin(angle);
                            BlockPos groundPos = findSpawnableGroundPillar(world, new BlockPos((int) x, (int) entityLookedAt.getY(), (int) z), 8);
                            if (groundPos != null && entityLookedAt instanceof LivingEntity) {
                                CreeperPillarEntity pillar = new CreeperPillarEntity(world, x, groundPos.getY(), z, entityLookedAt.getYaw(), player, CreeperPillarType.NORMAL);
                                world.emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(player));
                                world.spawnEntity(pillar);
                            }
                        }
                        return TypedActionResult.success(stack);
                    }
                }
                case CREEPER_WALL_BLOCK -> {
                    Vec3d pillarPos = rayTrace(player, 24, 1.0F, true);
                    if (pillarPos != null) {
                        System.out.println("Pos is: " + pillarPos);
                        BlockPos groundPos = findSpawnableGroundPillar(world, new BlockPos((int) pillarPos.getX(), (int) pillarPos.getY(), (int) pillarPos.getZ()), 8);
                        if (groundPos != null) {
                            CreeperPillarEntity pillar = new CreeperPillarEntity(world, pillarPos.getX(), groundPos.getY(), pillarPos.getZ(), player.getYaw(), player, CreeperPillarType.NORMAL);
                            world.emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(pillarPos.getX(), groundPos.getY(), pillarPos.getZ()), GameEvent.Emitter.of(player));
                            world.spawnEntity(pillar);
                        }
                    }
                }
            }
        }

        if (!world.isClient && player.isSneaking()) {
            player.sendMessage(Text.translatable("item.btc.spell.book.set", Text.translatable("item.btc.spell.book." + next.asString())), true);
            setElement(stack, next);
            return TypedActionResult.success(stack);
        }

        return super.use(world, player, hand);
    }

    public static @Nullable Vec3d rayTrace(PlayerEntity player, double range, float tickDelta, boolean includeFluids) {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hitLong = client.cameraEntity.raycast(range, tickDelta, includeFluids);
        Entity entity = getEntityLookedAt(player, range);

        switch (hitLong.getType()) {
            case HitResult.Type.MISS:
                //nothing near enough
                if (entity instanceof LivingEntity) {
                    System.out.println("Entity found is: " + entity + ", which pos is: " + entity.getPos());
                    return entity.getPos();
                }
                return null;
            case HitResult.Type.BLOCK:
                if (entity instanceof LivingEntity) {
                        System.out.println("Entity found is: " + entity + ", which pos is: " + entity.getPos());
                        return entity.getPos();
                } else {
                    BlockHitResult blockHit = (BlockHitResult) hitLong;
                    BlockPos blockPos = blockHit.getBlockPos();
                    assert client.world != null;
                    BlockState blockState = client.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    System.out.println("Block found is: " + block + ", which pos is: " + blockPos);
                    return new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                }
        }
        return null;
    }


    public static @Nullable Vec3d getBlockLookedAt(PlayerEntity player, double range, float tickDelta, boolean includeFluids) {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hitLong = client.cameraEntity.raycast(range, tickDelta, includeFluids);
        Entity entity = getEntityLookedAt(player, range);

        switch (hitLong.getType()) {
            case HitResult.Type.MISS:
                return null;
            case HitResult.Type.BLOCK:
                BlockHitResult blockHit = (BlockHitResult) hitLong;
                BlockPos blockPos = blockHit.getBlockPos();
                assert client.world != null;
                BlockState blockState = client.world.getBlockState(blockPos);
                Block block = blockState.getBlock();
                System.out.println("Block found is: " + block + ", which pos is: " + blockPos);
                return new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        return null;
    }

    public static @Nullable Entity getEntityLookedAt(PlayerEntity player, double range) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));

        // Create a box from the eye position to the reach vector
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        // Find the closest entity intersecting that line
        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit()) /*Replace isAttackable() and canHit() in the predicate with any condition you like (e.g., specific entity types or tags)*/) {
            Box entityBox = entity.getBoundingBox().expand(0.3D); // slightly expanded hitbox
            Optional<Vec3d> optionalHit = entityBox.raycast(eyePos, reachVec);

            if (optionalHit.isPresent()) {
                double distanceSq = eyePos.squaredDistanceTo(optionalHit.get());
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    hitEntity = entity;
                }
            }
        }
        return hitEntity;
    }

    @Nullable
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
    @Nullable
    public BlockPos findSpawnableGroundPillar(World world, BlockPos centerPos, int yRange) {
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


    public void spawnEarthSpikesTowardsYaw(World world, LivingEntity caster, int yRange, int spikeCount) {
        float yaw = caster.getYaw();
        double rad = Math.toRadians(yaw);

        double stepX = -Math.sin(rad) * 1.5;
        double stepZ = Math.cos(rad) * 1.5;

        double startX = caster.getX();
        double startZ = caster.getZ();
        double startY = caster.getY();


        for (int i = 0; i < spikeCount; i++) {
            double x = startX + stepX * i;
            double z = startZ + stepZ * i;
            BlockPos searchPos = new BlockPos((int) x, (int) startY, (int) z);


            BlockPos groundPos = findSpawnableGround(world, searchPos, yRange);

            if (groundPos != null) {

                EarthSpikeEntity spike = new EarthSpikeEntity(world, groundPos.getX(), groundPos.getY(), groundPos.getZ(), yaw, caster);
                caster.getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(caster));
                world.spawnEntity(spike);
            }
        }
    }



    private SpellBookAttacks getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        String name = nbt.getString("Element");
        for (SpellBookAttacks attack : SpellBookAttacks.values()) {
            if (attack.asString().equals(name)) {
                return attack;
            }
        }
        return SpellBookAttacks.WATER_BLAST;
    }

    private void setElement(ItemStack stack, SpellBookAttacks attack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        nbt.putString("Element", attack.asString());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        SpellBookAttacks current = getElement(stack);
        tooltip.add(Text.translatable("item.btc.spell.context.current", Text.translatable("item.btc.spell.book." + current.asString())).formatted(Formatting.BLUE));
    }
}

