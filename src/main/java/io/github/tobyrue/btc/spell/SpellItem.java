package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.DispenserBlock;
import net.minecraft.client.MinecraftClient;
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

import java.util.Objects;

public abstract class SpellItem extends Item implements SpellHost<ItemStack> {

    public SpellItem(Settings settings) {
        super(settings);
        DispenserBlock.registerBehavior(this, ((pointer, stack) -> {
            final var direction = new Vec3d(pointer.state().get(DispenserBlock.FACING).getUnitVector());
            final var data = SpellItem.this.getSpellDataStore(stack);
            //TODO dispenser sounds?
            data.getSpell().tryUse(new Spell.SpellContext(pointer.world(), pointer.pos().toCenterPos(), direction, data, null), data.getArgs());
            return stack;
        }));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        final var data = this.getSpellDataStore(stack);
        return data.getSpell() != null && data.getCooldown(data.getSpell().getCooldown(data.getArgs(), MinecraftClient.getInstance().player)) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        final var data = this.getSpellDataStore(stack);
        return data.getSpell() != null ? (int) (13 * data.getCooldownPercent(data.getSpell().getCooldown(data.getArgs(), MinecraftClient.getInstance().player))) : 0;
    }

    @Override
    public final SpellDataStore getSpellDataStore(final ItemStack stack) {
        return new SpellDataStore() {
            @Override
            public Spell getSpell() {
                final var nbt = stack.getOrDefault(BTC.SPELL_COMPONENT, NbtComponent.DEFAULT).copyNbt();
                return ModRegistries.SPELL.get(Identifier.tryParse(nbt.getString("name")));
            }

            @Override
            public GrabBag getArgs() {
                return GrabBag.fromNBT(stack.getOrDefault(BTC.SPELL_COMPONENT, NbtComponent.DEFAULT).copyNbt().getCompound("args"));
            }

            @Override
            public void setSpell(final Spell spell, @Nullable final GrabBag args) {
                final var nbt = stack.getOrDefault(BTC.SPELL_COMPONENT, NbtComponent.DEFAULT).copyNbt();

                nbt.putString("name", Objects.requireNonNull(ModRegistries.SPELL.getId(spell)).toString());

                if (args != null) {
                    nbt.put("args", GrabBag.toNBT(args));
                }

                stack.set(BTC.SPELL_COMPONENT, NbtComponent.of(nbt));
            }

            @Override
            public int getCooldown(final @Nullable Spell.SpellCooldown cooldown) {
                if (cooldown != null) {
                    final var nbt = stack.getOrDefault(BTC.SPELL_COMPONENT, NbtComponent.DEFAULT).copyNbt();
                    return nbt.getCompound("cooldowns").getCompound(cooldown.key().toString()).getInt("value");
                }
                return 0;
            }

            @Override
            public float getCooldownPercent(final @Nullable Spell.SpellCooldown cooldown) {
                if (cooldown != null) {
                    final var nbt = stack.getOrDefault(BTC.SPELL_COMPONENT, NbtComponent.DEFAULT).copyNbt();
                    return getCooldown(cooldown) / (float) nbt.getCompound("cooldowns").getCompound(cooldown.key().toString()).getInt("max");
                }
                return 0f;
            }

            @Override
            public void setCooldown(@Nullable final Spell.SpellCooldown cooldown) {
                if (cooldown != null) {
                    final var nbt = stack.getOrDefault(BTC.SPELL_COMPONENT, NbtComponent.DEFAULT).copyNbt();

                    final var cooldowns = nbt.getCompound("cooldowns");
                    nbt.put("cooldowns", cooldowns);

                    final var c = new NbtCompound();
                    c.putInt("value", cooldown.ticks());
                    c.putInt("max", cooldown.ticks());
                    cooldowns.put(cooldown.key().toString(), c);

                    stack.set(BTC.SPELL_COMPONENT, NbtComponent.of(nbt));
                }
            }
        };
    }

    @Override
    public void tickCooldowns(ItemStack stack) {
        final var nbt = stack.getOrDefault(BTC.SPELL_COMPONENT, NbtComponent.DEFAULT).copyNbt();
        final var cooldowns = nbt.getCompound("cooldowns");
        for (final var key : cooldowns.getKeys()) {
            final var c = cooldowns.getCompound(key);
            final var value = c.getInt("value") - 1;
            if (value > 0) {
                c.putInt("value", value);
            } else {
                cooldowns.remove(key);
            }
        }
        stack.set(BTC.SPELL_COMPONENT, NbtComponent.of(nbt));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient && entity instanceof LivingEntity) {
            this.tickCooldowns(stack);
        }
    }
}
