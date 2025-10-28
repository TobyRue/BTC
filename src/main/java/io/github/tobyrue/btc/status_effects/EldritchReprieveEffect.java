//package io.github.tobyrue.btc.status_effects;
//
//import io.github.tobyrue.btc.regestries.ModStatusEffects;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.attribute.AttributeContainer;
//import net.minecraft.entity.damage.DamageSource;
//import net.minecraft.entity.effect.StatusEffect;
//import net.minecraft.entity.effect.StatusEffectCategory;
//import net.minecraft.entity.effect.StatusEffectInstance;
//import net.minecraft.server.world.ServerWorld;
//
//import java.util.WeakHashMap;
//
//public class EldritchReprieveEffect extends StatusEffect {
//    private static final WeakHashMap<LivingEntity, Float> STORED_DAMAGE = new WeakHashMap<>();
//
//    public EldritchReprieveEffect() {
//        super(StatusEffectCategory.HARMFUL, 0x4B0082); // deep eldritch purple
//    }
//
//
//    @Override
//    public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
//        if (entity.hasStatusEffect(ModStatusEffects.ELDRITCH_REPRIEVE)) {
//            STORED_DAMAGE.put(entity, STORED_DAMAGE.getOrDefault(entity, 0f) + amount);
//            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + amount));
//        }
//    }
//
//
//    @Override
//    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
//        float stored = STORED_DAMAGE.getOrDefault(entity, 0f);
//
//        // amplifier determines how much stored damage is applied per tick
//        // Example: amp 0 -> 10%, amp 1 -> 20%, amp 2 -> 30%, etc.
//        float portion = stored * (0.1f * (amplifier + 1));
//
//        // Ensure we don't over-damage or go negative
//        portion = Math.min(portion, stored);
//        STORED_DAMAGE.put(entity, stored - portion);
//
//        entity.damage(entity.getDamageSources().magic(), portion);
//        return super.applyUpdateEffect(entity, amplifier);
//    }
//
//    @Override
//    public boolean canApplyUpdateEffect(int duration, int amplifier) {
//        // amplifier scales timing: 1x, 5x, 10x, etc.
//        int interval = 5 * Math.max(1, amplifier + 1);
//
//        // Apply on these intervals
//        if (duration % interval == 0) {
//            return true;
//        }
//        // Cleanup when effect ends
//        else if (duration <= 1) {
//            STORED_DAMAGE.clear();
//        }
//
//        return false;
//    }
//}
