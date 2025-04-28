package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.entity.custom.EarthSpikeEntity;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EvokerEntity;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellBookItem extends Item {
    private static final List<String> ELEMENTS = List.of("Water Blast", "Fireball", "Dragon Fireball", "Wind Charge", "Earth Spike", "Regeneration");

    private static final Integer SPIKE_Y_RANGE = 12;
    private static final Integer SPIKE_COUNT = 8;

    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Get the current element
        String currentElement = getElement(stack);
        int nextIndex = (ELEMENTS.indexOf(currentElement) + 1) % ELEMENTS.size();
        String nextElement = ELEMENTS.get(nextIndex);
        if (!player.isSneaking()) {
            if (getElement(stack).equals("Water Blast")) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                Vec3d velocity = player.getRotationVec(1.0f).multiply(1.5f);
                if (!world.isClient) {
                    // Spawn the entity 1 block higher
                    WaterBlastEntity waterBlast = new WaterBlastEntity(player, world, player.getX(), player.getY() + 1.25, player.getZ(), velocity);
                    world.spawnEntity(waterBlast);
                    player.getItemCooldownManager().set(this, 15);
                }
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(stack);
            } else if (getElement(stack).equals("Fireball")) {
                Vec3d velocity = player.getRotationVec(1.0f).multiply(5.5f);

                if (!world.isClient) {
                    FireballEntity fireballEntity = new FireballEntity(world, player, velocity, 1);
                    world.spawnEntity(fireballEntity);
                    player.getItemCooldownManager().set(this, 15);
                }
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(stack);
            } if (getElement(stack).equals("Dragon Fireball")) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                Vec3d velocity = player.getRotationVec(1.0f).multiply(1.5f);
                if (!world.isClient) {
                    // Spawn the entity 1 block higher
                    DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(world, player, velocity);
                    world.spawnEntity(dragonFireballEntity);
                    player.getItemCooldownManager().set(this, 15);
                }
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(stack);
            } if (getElement(stack).equals("Wind Charge")) {
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
                WindChargeEntity windCharge = new WindChargeEntity(player, world, player.getX(), player.getY() + 1.0, player.getZ());
                Vec3d direction = player.getRotationVec(1.0f);
                windCharge.setVelocity(direction.multiply(1.5)); // Adjust speed as needed
                player.getItemCooldownManager().set(this, 10);
                world.spawnEntity(windCharge);
                return TypedActionResult.success(stack);
            } if (getElement(stack).equals("Earth Spike")) {
                spawnEarthSpikesTowardsYaw(world, player, SPIKE_Y_RANGE, SPIKE_COUNT);
                return TypedActionResult.success(stack);
            } if (getElement(stack).equals("Regeneration")) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2));
                player.getItemCooldownManager().set(this, 150);
                return TypedActionResult.success(stack);
            }
        }
        // Show message above the hotbar
        if (!world.isClient && player.isSneaking()) {
            player.sendMessage(Text.literal("Spellbook set to: " + nextElement), true);
            setElement(stack, nextElement);
            return TypedActionResult.success(stack);
        }

        return super.use(world, player, hand);
    }

    @Nullable
    public BlockPos findSpawnableGround(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());

        System.out.println("Searching from Y " + topY + " to " + bottomY + " at XZ: " + centerPos.getX() + ", " + centerPos.getZ());

        // Start from top and go downwards
        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            // Improved block check to ensure solid block and air above or open space above
            if (world.getBlockState(pos).isSolidBlock(world, pos) && !world.getBlockState(pos.up()).isSolidBlock(world, pos.up()) && !world.getBlockState(pos.up()).isOf(Blocks.CHEST)) {
                System.out.println("Found spawnable ground at: " + pos);
                return pos;
            }
        }

        // Fallback if no valid ground is found
        System.out.println("No ground found at XZ: " + centerPos.getX() + ", " + centerPos.getZ());
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

        System.out.println("Spawning Earth Spikes — Yaw: " + yaw + ", rad: " + rad);
        System.out.println("StepX: " + stepX + ", StepZ: " + stepZ);
        System.out.println("Y Range: " + yRange + ", Max Spike Count: " + spikeCount);

        for (int i = 0; i < spikeCount; i++) {
            double x = startX + stepX * i;
            double z = startZ + stepZ * i;
            BlockPos searchPos = new BlockPos((int) x, (int) startY, (int) z);

            System.out.println("Checking position: " + searchPos);

            BlockPos groundPos = findSpawnableGround(world, searchPos, yRange);

            if (groundPos != null) {
                System.out.println("Found ground at: " + groundPos);

                EarthSpikeEntity spike = new EarthSpikeEntity(world, groundPos.getX(), groundPos.getY(), groundPos.getZ(), yaw, caster);
                caster.getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(caster));
                world.spawnEntity(spike);
            } else {
                System.out.println("No valid ground found for spike at: " + searchPos);
            }
        }
    }



    private String getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt(); // Get a modifiable copy
        return nbt.contains("Element") ? nbt.getString("Element") : ELEMENTS.get(0); // Default to "Water"
    }

    private void setElement(ItemStack stack, String element) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt(); // Get a modifiable copy

        nbt.putString("Element", element); // Update the element
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt)); // Create a new immutable NbtComponent
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(getDescription1(stack).formatted(Formatting.BLUE));
    }

    public MutableText getDescription1(ItemStack stack) {
        return Text.literal("Current Spell: " + getElement(stack));
    }
}

