package io.github.tobyrue.btc.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
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
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class SpellBookItem extends Item {
    private static final List<String> ELEMENTS = List.of("Water Blast", "Fireball", "Dragon Fireball", "Wind Charge", "Regeneration");


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

