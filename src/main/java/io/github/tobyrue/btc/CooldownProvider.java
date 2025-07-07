package io.github.tobyrue.btc;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;


public interface CooldownProvider {

    // Set a cooldown with option to make it visible
    default void setCooldown(LivingEntity entity, ItemStack stack, String key, int durationTicks, boolean visible) {
        if (entity.getWorld() instanceof ServerWorld) {
            NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
            NbtCompound nbt = component.copyNbt();
            NbtCompound cooldowns = nbt.getCompound("Cooldowns");

            // Remove visibility from any other cooldowns if this one will be visible
            if (visible) {
                for (String existingKey : cooldowns.getKeys()) {
                    NbtCompound entry = cooldowns.getCompound(existingKey);
                    entry.remove("visible");
                    cooldowns.put(existingKey, entry);
                }
            }

            // Create new cooldown entry
            NbtCompound entry = new NbtCompound();
            entry.putInt("ticks", 0);
            entry.putInt("max", durationTicks);
            if (visible) entry.putBoolean("visible", true);
            cooldowns.put(key, entry);

            nbt.put("Cooldowns", cooldowns);
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

            // Schedule ticking task
            ((Ticker.TickerTarget) entity).add(ticks -> {
                NbtComponent comp = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
                NbtCompound data = comp.copyNbt();
                NbtCompound cds = data.getCompound("Cooldowns");

                if (!cds.contains(key)) return true;

                NbtCompound e = cds.getCompound(key);
                int current = e.getInt("ticks") + 1;
                int max = e.getInt("max");

                if (current >= max) {
                    cds.remove(key);
                    if (cds.getKeys().isEmpty()) {
                        data.remove("Cooldowns");
                    } else {
                        data.put("Cooldowns", cds);
                    }
                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(data));
                    return true;
                } else {
                    e.putInt("ticks", current);
                    cds.put(key, e);
                    data.put("Cooldowns", cds);
                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(data));
                    return false;
                }
            });
        }
    }

    // Check if a cooldown is active
    default boolean isCooldownActive(ItemStack stack, String key) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound cooldowns = component.copyNbt().getCompound("Cooldowns");
        return cooldowns.contains(key);
    }

    // Get progress (0.0 to 1.0) for a specific cooldown
    default float getCooldownProgress(ItemStack stack, String key) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound cooldowns = component.copyNbt().getCompound("Cooldowns");
        if (cooldowns.contains(key)) {
            NbtCompound entry = cooldowns.getCompound(key);
            int ticks = entry.getInt("ticks");
            int max = entry.getInt("max");
            return max > 0 ? (float) ticks / max : 0f;
        }
        return 0f;
    }
    default float getCooldownProgressInverse(ItemStack stack, String key) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound cooldowns = component.copyNbt().getCompound("Cooldowns");
        if (cooldowns.contains(key)) {
            NbtCompound entry = cooldowns.getCompound(key);
            int ticks = entry.getInt("ticks");
            int max = entry.getInt("max");
            if (max > 0) {
                float progress = (float) ticks / max;
                return 1.0f - progress; // inverted progress
            }
        }
        return 0f;
    }
    // Get the key of the currently visible cooldown, if any
    default String getVisibleCooldownKey(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound cooldowns = component.copyNbt().getCompound("Cooldowns");
        for (String key : cooldowns.getKeys()) {
            if (cooldowns.getCompound(key).getBoolean("visible")) {
                return key;
            }
        }
        return null;
    }
}
