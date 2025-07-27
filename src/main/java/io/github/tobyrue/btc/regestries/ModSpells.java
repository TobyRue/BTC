package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.spell.LeveledSpell;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.SpellScrollItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModSpells {
    public static final Map<Spell, SpellScrollItem> SCROLLS = new HashMap<>();

    public static <T extends Spell> T register(String name, T spell, boolean shouldRegisterItem) {
        if (shouldRegisterItem) {
            SCROLLS.put(spell, Registry.register(Registries.ITEM, Identifier.of(BTC.MOD_ID, name + "_scroll"), new SpellScrollItem(spell)));
        }

        return Registry.register(ModRegistries.SPELL, Identifier.of(BTC.MOD_ID, name), spell);
    }

    public static <T extends Spell> T register(String name, T spell) {
        return register(name, spell, true);
    }

    private static Spell fireballSpell(final int level) {
        return new Spell(0x0, SpellTypes.FIRE) {
            @Override
            public void use(final Spell.SpellContext ctx) {
                FireballEntity fireball = ctx.user() == null ? new FireballEntity(EntityType.FIREBALL, ctx.world()) : new FireballEntity(ctx.world(), ctx.user(), ctx.direction(), level);
                fireball.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
                fireball.setVelocity(ctx.direction().multiply(1.5));

                ctx.world().spawnEntity(fireball);
            }

            @Override
            public Spell.SpellCooldown getCooldown() {
                return new Spell.SpellCooldown(40 * level, BTC.identifierOf("fireball"));
            }
        };
    }

    public static final Spell WEAK_FIREBALL = register("weak_fireball", fireballSpell(1));

    public static final Spell STRONG_FIREBALL = register("strong_fireball", fireballSpell(5));

    private static final LeveledSpell GENERIC_FIRE_STORM = new LeveledSpell(0x0, SpellTypes.FIRE) {
        @Override
        public void use(final Spell.SpellContext ctx, final int level) {
            final var duration = 2 * level;
            final var maxRadius = 8d * level;
            Vec3d storedPos = ctx.user().getPos();
            ((Ticker.TickerTarget) ctx.user()).add(Ticker.forSeconds((ticks) -> {
                if (ctx.world() instanceof ServerWorld serverWorld) {
                    double progress = ticks / (double) (duration * 20);
                    double radius = maxRadius * progress;


                    int count = (int) (maxRadius / 64d * 1280d);
                    for (int i = 0; i < count; i++) {

                        double angle = (2 * Math.PI / count) * i;

                        double x = storedPos.getX() + Math.sin(angle) * radius;
                        double z = storedPos.getZ() + Math.cos(angle) * radius;

                        double yOffset = 0.2;
                        double y = storedPos.getY() + yOffset;

                        double xSpeed = Math.sin(angle) * 0.2;
                        double zSpeed = Math.cos(angle) * 0.2;

                        serverWorld.spawnParticles(ParticleTypes.FLAME, x, y, z, 0, xSpeed, 0.0, zSpeed, 0);
                    }

                    for (LivingEntity target : serverWorld.getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(maxRadius), e -> e.isAlive() && e != ctx.user())) {
                        double dist = target.getPos().distanceTo(storedPos);

                        double stepSize = maxRadius / duration;
                        if (dist <= radius && dist > (radius - stepSize)) {
                            target.setOnFireFor((float) ((radius * -1) + maxRadius));
                            target.damage(ctx.user().getDamageSources().inFire(), Math.min(8, (float) ((radius * -1) + maxRadius)));
                        }
                    }
                }
            }, duration));
        }

        @Override
        protected boolean canUse(Spell spell, Spell.SpellContext ctx, int level) {
            return ctx.user() != null && super.canUse(spell, ctx, level);
        }

        @Override
        public Spell.SpellCooldown getCooldown(final int level) {
            return new Spell.SpellCooldown(200 * level, BTC.identifierOf("fireball"));
        }
    };

    public static final Spell CONCENTRATED_FIRE_STORM = register("concentrated_fire_storm", GENERIC_FIRE_STORM.withLevel(1));
    public static final Spell FIRE_STORM = register("fire_storm", GENERIC_FIRE_STORM.withLevel(2));

    public static LeveledSpell potionSpell(RegistryEntry<StatusEffect> effect, int color, Function<Integer, Integer> duration, Function<Integer, Integer> cooldown) {
        return new LeveledSpell(color, SpellTypes.GENERIC) {
            @Override
            protected void use(Spell.SpellContext ctx, int level) {
                ctx.user().addStatusEffect(new StatusEffectInstance(effect, duration.apply(level), level - 1));
            }
            @Override
            protected boolean canUse(Spell spell, Spell.SpellContext ctx, int level) {
                return ctx.user() != null && super.canUse(spell, ctx, level);
            }
            @Override
            public Spell.SpellCooldown getCooldown(final int level) {
                return new Spell.SpellCooldown(cooldown.apply(level), BTC.identifierOf("potion"));
            }
        };
    }

    private static final LeveledSpell GENERIC_RESISTANCE = potionSpell(StatusEffects.RESISTANCE, 0x0, level -> 300, level -> level * 600);
    public static final Spell RESISTANCE_I = register("resistance_1", GENERIC_RESISTANCE.withLevel(1));
    public static final Spell RESISTANCE_II = register("resistance_2", GENERIC_RESISTANCE.withLevel(2));
    public static final Spell RESISTANCE_III = register("resistance_3", GENERIC_RESISTANCE.withLevel(3));

    private static final LeveledSpell GENERIC_STRENGTH = potionSpell(StatusEffects.STRENGTH, 0x0, level -> 300, level -> level * 400);
    public static final Spell STRENGTH_I = register("strength_1", GENERIC_STRENGTH.withLevel(1));
    public static final Spell STRENGTH_II = register("strength_2", GENERIC_STRENGTH.withLevel(2));
    public static final Spell STRENGTH_III = register("strength_3", GENERIC_STRENGTH.withLevel(3));

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            for (var item : SCROLLS.values()) {
                content.addAfter(ModItems.ENCHANTED_PAPER, item);
            }
        });
    }
}
