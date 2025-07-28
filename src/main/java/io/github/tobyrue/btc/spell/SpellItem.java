package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.DispenserBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class SpellItem extends Item implements SpellHost<ItemStack> {

    public SpellItem(Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, ((pointer, stack) -> {
            final var direction = new Vec3d(pointer.state().get(DispenserBlock.FACING).getUnitVector());
            final var data = SpellItem.this.getSpellDataStore(stack);
            //TODO dispenser sounds?
            data.getSpell().tryUse(new Spell.SpellContext(pointer.world(), pointer.pos().toCenterPos(), direction, data, null));
            return stack;
        }));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        final var ctx = this.getSpellDataStore(stack);
        return ctx.getCooldown(ctx.getSpell()) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        final var ctx = this.getSpellDataStore(stack);
        return (int) (13 * ctx.getCooldownPercent(ctx.getSpell()));
    }

    @Override
    public final SpellDataStore getSpellDataStore(final ItemStack stack) {
        return new SpellDataStore() {
            @Override
            public Spell getSpell() {
                final var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                return ModRegistries.SPELL.get(Identifier.tryParse(nbt.getString("BTCSpell")));
            }

            @Override
            public void setSpell(final Spell spell) {
                final var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                nbt.putString("BTCSpell", ModRegistries.SPELL.getId(spell).toString());
                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            }

            @Override
            public int getCooldown(final @Nullable Spell spell) {
                if (spell instanceof Spell && spell.getCooldown() instanceof Spell.SpellCooldown cooldown) {
                    final var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                    return nbt.getCompound("BTCCooldowns").getCompound(cooldown.key().toString()).getInt("value");
                }
                return 0;
            }

            @Override
            public float getCooldownPercent(final @Nullable Spell spell) {
                if (spell instanceof Spell && spell.getCooldown() instanceof Spell.SpellCooldown cooldown) {
                    final var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
                    return getCooldown(spell) / (float) nbt.getCompound("BTCCooldowns").getCompound(cooldown.key().toString()).getInt("max");
                }
                return 0f;
            }

            @Override
            public void setCooldown(final Spell spell) {
                final var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();

                final var cooldowns = nbt.getCompound("BTCCooldowns");
                nbt.put("BTCCooldowns", cooldowns);

                final var cooldown = new NbtCompound();
                cooldown.putInt("value", spell.getCooldown().ticks());
                cooldown.putInt("max", spell.getCooldown().ticks());
                cooldowns.put(spell.getCooldown().key().toString(), cooldown);

                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            }
        };
    }

    @Override
    public void tickCooldowns(ItemStack stack) {
        final var nbt = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt();
        final var cooldowns = nbt.getCompound("BTCCooldowns");
        for (final var key : cooldowns.getKeys()) {
            final var c = cooldowns.getCompound(key);
            final var value = c.getInt("value") - 1;
            if (value > 0) {
                c.putInt("value", value);
            } else {
                cooldowns.remove(key);
            }
        }
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient && entity instanceof LivingEntity) {
            this.tickCooldowns(stack);
        }
    }
}
