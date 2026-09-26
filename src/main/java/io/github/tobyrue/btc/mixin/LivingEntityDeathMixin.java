package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.SpellDataStore;
import io.github.tobyrue.btc.spell.SpellHost;
import io.github.tobyrue.btc.spell.SpellItem;
import io.github.tobyrue.btc.util.LivingEntityMixinAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDeathMixin {
//    @Shadow
//    public abstract ItemStack getEquippedStack(EquipmentSlot slot);
//
//    @Inject(method = "onDeath", at = @At("HEAD"))
//    private void btc$onEntityDeathCancelChanneledSpells(
//            DamageSource damageSource,
//            CallbackInfo ci
//    ) {
//        LivingEntity entity = (LivingEntity) (Object) this;
//
//        if (entity.getWorld().isClient()) {
//            return;
//        }
//
//        if (entity instanceof Ticker.TickerTarget tickerTarget
//                && entity instanceof LivingEntityMixinAccessor accessor) {
//
//            accessor.btc$getTickers().clear();
//        }
//
//
//        if (entity instanceof SpellHost<?> host) {
//            cancelHostSpell(host, entity, entity);
//        }
//
//
//        for (EquipmentSlot slot : EquipmentSlot.values()) {
//            ItemStack stack = this.getEquippedStack(slot);
//
//            if (!stack.isEmpty()) {
//                cancelItemSpell(stack, entity);
//            }
//        }
//
//
//        if (entity instanceof PlayerEntity player) {
//            for (ItemStack stack : player.getInventory().main) {
//                if (!stack.isEmpty()) {
//                    cancelItemSpell(stack, entity);
//                }
//            }
//
//            for (ItemStack stack : player.getInventory().offHand) {
//                if (!stack.isEmpty()) {
//                    cancelItemSpell(stack, entity);
//                }
//            }
//
//            for (ItemStack stack : player.getInventory().armor) {
//                if (!stack.isEmpty()) {
//                    cancelItemSpell(stack, entity);
//                }
//            }
//        }
//    }
//
//
//    @Unique
//    private void cancelItemSpell(ItemStack stack, LivingEntity entity) {
//        if (stack.isEmpty()) {
//            return;
//        }
//
//        if (!(stack.getItem() instanceof SpellItem spellItem)) {
//            return;
//        }
//
//        cancelHostSpell(spellItem, stack, entity);
//    }
//
//    @Unique
//    @SuppressWarnings("unchecked")
//    private <T> void cancelHostSpell(
//            SpellHost<T> host,
//            Object target,
//            LivingEntity entity
//    ) {
//        System.out.println("reee: ");
//
//        try {
//            SpellDataStore dataStore = host.getSpellDataStore((T) target);
//
//            if (dataStore == null) {
//                return;
//            }
//
//            Spell spell = dataStore.getSpell();
//
//            if (!(spell instanceof ChanneledSpell channeledSpell)) {
//                return;
//            }
//
//            GrabBag args = dataStore.getArgs();
//
//            if (args == null) {
//                return;
//            }
//
//
//            Spell.SpellContext ctx = new Spell.SpellContext(
//                    entity.getWorld(),
//                    entity.getPos(),
//                    entity.getRotationVector(),
//                    dataStore,
//                    entity,
//                    null
//            );
//
//            System.out.println("Ended: " + channeledSpell);
//            channeledSpell.onEnd(ctx, args, 0);
//
//        } catch (Exception ignored) {
//
//        }
//    }

}
