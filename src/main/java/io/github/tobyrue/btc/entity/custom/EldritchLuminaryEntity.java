package io.github.tobyrue.btc.entity.custom;


import io.github.tobyrue.btc.entity.ai.EldritchLuminaryStrafeGoal;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.SpellDataStore;
import io.github.tobyrue.btc.spell.SpellHost;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

public class EldritchLuminaryEntity extends HostileEntity implements Angerable, SpellHost<LivingEntity> {
    private static final TrackedData<NbtCompound> SPELL_COOLDOWN = DataTracker.registerData(
            EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND
    );
    private static final TrackedData<String> CURRENT_SPELL = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<NbtCompound> SPELL_ARGS = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<NbtCompound> SPELLS = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<Integer> SPELL_CAST_TIME = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ILLUSION_TIME = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> GLOBAL_CAST_DELAY =
            DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final int GLOBAL_DELAY = 20;

    private Spell.InstancedSpell activeCastingSpell = null;

    private LivingEntity target;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private final int castTime = 15;
    private final int illusionTime = 600;
    private final Vec3d[][] mirrorCopyOffsets;

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1D));
        this.goalSelector.add(6, new TemptGoal(this, 1.3D, Ingredient.ofItems(ModItems.STAFF, ModItems.DRAGON_STAFF, ModItems.FIRE_STAFF, ModItems.WIND_STAFF, ModItems.RUBY_TRIAL_KEY), false));
        this.goalSelector.add(1, new EldritchLuminaryStrafeGoal(this, 0.8, 16.0F));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, (entity) -> {
            return Math.abs(entity.getY() - this.getY()) <= 4.0;
        }));
        this.targetSelector.add(0, new TrackTargetGoal(this, true, false) {
            @Override
            public boolean canStart() {
                return true;
            }
        });
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SPELL_COOLDOWN, new NbtCompound());
        builder.add(CURRENT_SPELL, "empty");
        builder.add(SPELL_ARGS, new NbtCompound());
        builder.add(SPELLS, new NbtCompound());
        builder.add(SPELL_CAST_TIME, 0);
        builder.add(ILLUSION_TIME, 0);
        builder.add(GLOBAL_CAST_DELAY, 0);
    }

    public static DefaultAttributeContainer.Builder createEldritchLuminaryAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_ARMOR, 24f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,2f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.5)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.75f);
    }


    public EldritchLuminaryEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 20;
        this.mirrorCopyOffsets = new Vec3d[2][4];
        for (int i = 0; i < 4; ++i) {
            this.mirrorCopyOffsets[0][i] = Vec3d.ZERO;
            this.mirrorCopyOffsets[1][i] = Vec3d.ZERO;
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.INDIRECT_MAGIC)) {
            return false;
        }
        return super.damage(source, amount);
    }

    private void setupAnimationStates() {
        SpellDataStore data = getSpellDataStore(this);

        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.startIfNotRunning(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        // --- Attack / casting animation ---
        if (getCastTime() > 0 && getCastTime() <= castTime && getGlobalCastDelay() <= 0) {
            this.attackAnimationTimeout = 20;
            this.attackAnimationState.startIfNotRunning(this.age);
        } else {
            if (this.attackAnimationTimeout > 0) {
                --this.attackAnimationTimeout;
            } else {
                this.attackAnimationState.stop();
            }
        }
    }


    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    private Vec3d[] illusionOffsets;

    public Vec3d[] getIllusionOffsets() {
        return illusionOffsets;
    }

    public void setIllusionOffsets(Vec3d[] illusionOffsets) {
        this.illusionOffsets = illusionOffsets;
    }

    public Vec3d[] getMirrorCopyOffsets(float tickDelta) {
        if (this.getIllusionTime() <= 0) {
            return this.mirrorCopyOffsets[1];
        }

        double d = ((float) this.getIllusionTime() - tickDelta) / 3.0f;
        d = Math.pow(d, 0.25);

        Vec3d[] vec3ds = new Vec3d[4];

        for (int i = 0; i < 4; ++i) {
            Vec3d interpolated = this.mirrorCopyOffsets[1][i]
                    .multiply(1.0 - d)
                    .add(this.mirrorCopyOffsets[0][i].multiply(d));

            vec3ds[i] = interpolated.multiply(d);
        }

        return vec3ds;
    }

    @Override
    protected Box getHitbox() {
        return super.getHitbox();
    }

    @Override
    public boolean isInvisibleTo(PlayerEntity player) {
        if (getIllusionTime() > 0 && getIllusionTime() <= illusionTime) {
            return true;
        }
        return super.isInvisibleTo(player);
    }

    @Override
    public void tick() {
        super.tick();


        // --- Client illusion update ---
        if (this.getWorld().isClient()) {
            if (getIllusionTime() > 0 && getIllusionTime() <= illusionTime) {
                setIllusionTime(getIllusionTime() + 1);
            } else if (getIllusionTime() >= illusionTime) {
                setIllusionTime(0);
            }
        }

        // --- Global cast delay countdown ---

        SpellDataStore data = getSpellDataStore(this);
        Spell.InstancedSpell current = this.getCurrentSpellInstance();

        if (!this.getWorld().isClient() && this.getCurrentSpell() != null && this.getCurrentArgs() != null) {
//            System.out.println(
//                    "Delay=" + getGlobalCastDelay() +
//                            ", CastTime=" + getCastTime() +
//                            ", Active=" + (activeCastingSpell != null) +
//                            ", Target=" + (target != null) +
//                            ", Cooldowns=" + data.getCooldown(this.getCurrentSpell().getCooldown(getCurrentArgs(), this))
//            );
        }


        // --- Global cast delay countdown ---
        if (getGlobalCastDelay() > 0) {
            setGlobalCastDelay(getGlobalCastDelay() - 1);
        }

        if (this.target != null) {
            if (getGlobalCastDelay() <= 0) {

                // Step 1: Start casting
                if (activeCastingSpell == null && getCastTime() <= 0) {
                    activeCastingSpell = chooseRandomCurrentSpell();
                    setCastTime(1);
//                    System.out.println("Step 1 Cast spell: Active: " + activeCastingSpell);
                }

                // Step 2: Continue charging
                else if (activeCastingSpell != null && getCastTime() < castTime) {
                    setCastTime(getCastTime() + 1);
//                    System.out.println("Step 2 In charging: Active: " + activeCastingSpell);
                }

                // Step 3: Cast spell
                else if (activeCastingSpell != null && getCastTime() >= castTime) {
                    this.lookAtEntity(target, 90, 90);
                    castCurrentSpellAt(this.target);

                    // Now set delay for next cast
//                    System.out.println("Step 3 Cast spell: Spell: " + activeCastingSpell.spell());
                    setGlobalCastDelay(GLOBAL_DELAY);

                    activeCastingSpell = null;
                    setCastTime(0);
                    setSpellEmpty();
                }
            }
        } else {
            if (activeCastingSpell != null && getCastTime() >= castTime && (this.getHealth() / this.getMaxHealth()) * 100 <= 70) {
                this.lookAtEntity(target, 90, 90);
                activeCastingSpell = new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                    put("effect", "minecraft:regeneration");
                    put("duration", 150);
                    put("cooldown", 0);
                    put("amplifier", 4);
                }}));
                setCurrentSpellInstance(activeCastingSpell.spell(), activeCastingSpell.args());
                castCurrentSpellAt(this.target);

                // Apply cooldown
//                var cd = activeCastingSpell.spell().getCooldown(activeCastingSpell.args(), this);
//                getSpellDataStore(this).setCooldown(cd);

                // Reset
                activeCastingSpell = null;
                setCastTime(0);
                setSpellEmpty();
            }
        }


        // --- Particle effects (client) ---
        if (this.getWorld().isClient() && current.spell() != ModSpells.EMPTY && current.spell() != null && !this.isInvisible() && getIllusionTime() <= 0 && getCastTime() > 0 && getCastTime() <= castTime && getGlobalCastDelay() <= 0) {
            int colorInt = current.spell().getColor(current.args());
            float r = ((colorInt >> 16) & 0xFF) / 255.0F;
            float g = ((colorInt >> 8) & 0xFF) / 255.0F;
            float b = (colorInt & 0xFF) / 255.0F;

            float i = this.bodyYaw * 0.017453292F + MathHelper.cos((float) this.age * 0.6662F) * 0.25F;
            float j = MathHelper.cos(i);
            float k = MathHelper.sin(i);
            double d = 0.6 * this.getScale();
            double e = 1.8 * this.getScale();

            this.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                    this.getX() + j * d, this.getY() + e, this.getZ() + k * d, 0, 0, 0);
            this.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                    this.getX() - j * d, this.getY() + e, this.getZ() - k * d, 0, 0, 0);
        }

        if (getAllSpellInstances().isEmpty()) {
            // Base elemental attacks
            this.addSpell(new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(1));
                put("level", 2);
            }})), 4, 24, -1, -1, -1, -1, 0.6f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.WATER_BLAST, GrabBag.fromMap(new HashMap<>() {{
                put("noGravity", true);
                put("cooldown", getSpellWaitAmount(1));
            }})), 0, 24, -1, -1, -1, -1, 0.7f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.EARTH_SPIKE_LINE, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(1));
            }})), 0, 12, -1, 15, -1, -1, 0.8f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.ICE_BLOCK, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(3));
            }})), 6, 24, -1, -1, -1, -1, 0.6f);

            // Area or multi-attack spells
            this.addSpell(new Spell.InstancedSpell(ModSpells.FIRE_STORM, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(1));
            }})), 0, 8, -1, -1, -1, -1, 0.7f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.CREEPER_WALL_CIRCLE, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(2));
            }})), 5, 24, -1, -1, -1, -1, 0.8f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.LOCALIZED_STORM_PUSH, GrabBag.fromMap(new HashMap<>() {{
                put("shootStrength", 2d);
                put("verticalMultiplier", 1.2d);
                put("cooldown", getSpellWaitAmount(0));
            }})), 0, 24, -1, -1, -1, -1, 0.8f);

            // Summon / illusionary magic
            this.addSpell(new Spell.InstancedSpell(ModSpells.SHULKER_BULLET, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(1));
            }})), 0, 24, -1, -1, -1, -1, 0.5f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.ELDRITCH_ILLUSION, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(3));
            }})), 0, 24, -1, -1, -1, -1, 0.9f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.BLAZE_STORM, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(1));
            }})), 0, 16, -1, -1, -1, -1, 0.75f);

            // Buffs and debuffs
            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:invisibility");
                put("duration", 400);
                put("cooldown", getSpellWaitAmount(2));
            }})), 0, 48, -1, -1, -1, -1, 0.85f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:regeneration");
                put("duration", 150);
                put("amplifier", 3);
                put("cooldown", getSpellWaitAmount(0));
            }})), 0, 48, 80, -1, -1, -1, 1.3f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:regeneration");
                put("duration", 150);
                put("amplifier", 4);
                put("cooldown", getSpellWaitAmount(1));
            }})), 0, 48, 40, -1, -1, -1, 2f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION_AREA_EFFECT, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:darkness");
                put("duration", 200);
                put("amplifier", 3);
                put("cooldown", getSpellWaitAmount(2));
            }})), 0, 24, -1, -1, -1, -1, 0.9f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION_AREA_EFFECT, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:mining_fatigue");
                put("duration", 300);
                put("amplifier", 5);
                put("cooldown", getSpellWaitAmount(8));
            }})), 0, 24, 80, -1, -1, -1, 0.9f);

            // Movement / trick spells
            this.addSpell(new Spell.InstancedSpell(ModSpells.SHADOW_STEP, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(2));
            }})), 0, 24, -1, -1, -1, -1, 0.8f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.WIND_TORNADO, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(3));
            }})), 0, 20, -1, -1, -1, -1, 0.75f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.MIST_VEIL, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(2));
            }})), 0, 20, -1, -1, -1, -1, 0.5f);

            // Utility / advanced magic
            this.addSpell(new Spell.InstancedSpell(ModSpells.LIFE_STEAL, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(1));
            }})), 0, 24, 40, -1, -1, -1, 1f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.DRAGON_FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(1));
            }})), 0, 24, -1, -1, -1, -1, 0.8f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.DRAGONS_BREATH, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(5));
            }})), 0, 8, -1, -1, -1, -1, 0.7f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.FLAME_BURST, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(5));
            }})), 0, 10, -1, -1, -1, -1, 0.7f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.PURGE_BOLT, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(5));
            }})), 0, 24, -1, -1, -1, -1, 0f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.LIGHTNING_STRIKE, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", getSpellWaitAmount(3));
            }})), 4, 24, -1, -1, -1, -1, 0.8f);
            this.setSpellEmpty();
        }

        // --- Animation setup (client) ---
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }
        if (!this.getWorld().isClient()) {
            ((SpellHost<LivingEntity>) this).tickCooldowns(this);
        }
    }

    private int getSpellWaitAmount(int amount) {
        return ((amount) * (GLOBAL_DELAY + castTime)) + 1;
    }

    private void castCurrentSpellAt(LivingEntity target) {
        SpellDataStore data = getSpellDataStore(this);
        Spell spell = data.getSpell();
        GrabBag args = data.getArgs();

        if (spell == null) return;

        Vec3d origin = this.getPos().add(0, this.getStandingEyeHeight(), 0);
        Vec3d direction = target.getPos().add(0, target.getStandingEyeHeight() / 2, 0).subtract(origin).normalize();

        Spell.SpellContext ctx = new Spell.SpellContext(this.getWorld(), origin, direction, data, this);
        spell.tryUse(ctx, args);
    }

    public Spell.InstancedSpell getCurrentSpellInstance() {
        String spellIdString = this.dataTracker.get(CURRENT_SPELL);
        if (spellIdString == null || spellIdString.isEmpty()) {
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        Identifier id = Identifier.tryParse(spellIdString);
        Spell spell = ModRegistries.SPELL.get(id);

        NbtCompound argsNbt = this.dataTracker.get(SPELL_ARGS);
        GrabBag args = argsNbt != null ? GrabBag.fromNBT(argsNbt) : GrabBag.empty();

        return new Spell.InstancedSpell(spell, args);
    }

    public void setCurrentSpellInstance(Spell spell, @Nullable GrabBag args) {
        Identifier id = ModRegistries.SPELL.getId(spell);
        if (id == null) return; // invalid spell, do nothing

        // Check if this spell exists in the stored list
        boolean exists = this.getAllSpellInstances().stream()
                .anyMatch(inst -> ModRegistries.SPELL.getId(inst.spell()).equals(id));

        if (exists) {
            this.dataTracker.set(CURRENT_SPELL, id.toString());
            this.dataTracker.set(SPELL_ARGS, args != null ? GrabBag.toNBT(args) : new NbtCompound());
        }
    }

    public boolean canUseSpell(Spell.InstancedSpell spellInstance) {
        if (spellInstance == null || spellInstance.spell() == ModSpells.EMPTY) return false;

        SpellDataStore data = getSpellDataStore(this);
        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null) return false;

        Identifier id = ModRegistries.SPELL.getId(spellInstance.spell());
        if (id == null || !nbt.contains(id.toString())) return false;

        double distance = this.target != null ? this.distanceTo(this.target) : 0.0;
        double selfHealthPercent = (this.getHealth() / this.getMaxHealth()) * 100.0;
        double targetHealthPercent = (this.target != null && this.target.getMaxHealth() > 0)
                ? (this.target.getHealth() / this.target.getMaxHealth()) * 100.0
                : 100.0;

        boolean targetOnFire = (this.target != null && this.target.isOnFire());

        // --- Fire/water edge cases ---
        if (targetOnFire && (spellInstance.spell() == ModSpells.WATER_WAVE || spellInstance.spell() == ModSpells.WATER_BLAST))
            return false;
        if (this.target != null && this.target.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)
                && spellInstance.spell().getSpellType() == SpellTypes.FIRE)
            return false;

        NbtCompound spellData = nbt.getCompound(id.toString());

        double min = spellData.getDouble("minDistance");
        double max = spellData.getDouble("maxDistance");
        double belowLife = spellData.contains("belowLife") ? spellData.getDouble("belowLife") : -1;
        double aboveLife = spellData.contains("aboveLife") ? spellData.getDouble("aboveLife") : -1;
        double targetBelowLife = spellData.contains("targetBelowLife") ? spellData.getDouble("targetBelowLife") : -1;
        double targetAboveLife = spellData.contains("targetAboveLife") ? spellData.getDouble("targetAboveLife") : -1;

        if (belowLife == -1) belowLife = 100.0;
        if (aboveLife == -1) aboveLife = 0.0;
        if (targetBelowLife == -1) targetBelowLife = 100.0;
        if (targetAboveLife == -1) targetAboveLife = 0.0;

        boolean withinDistance = (max <= 0 || (distance >= min && distance <= max));
        boolean withinSelfHealth = (selfHealthPercent <= belowLife && selfHealthPercent >= aboveLife);
        boolean withinTargetHealth = (targetHealthPercent <= targetBelowLife && targetHealthPercent >= targetAboveLife);

        var cd = spellInstance.spell().getCooldown(spellInstance.args(), this);
        boolean cooldownReady = data.getCooldown(cd) <= 0;

        return withinDistance && withinSelfHealth && withinTargetHealth && cooldownReady;
    }

    public Spell.InstancedSpell chooseRandomCurrentSpell() {
        SpellDataStore data = getSpellDataStore(this);

        if (getGlobalCastDelay() > 0) {
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        List<Spell.InstancedSpell> spells = getAllSpellInstances();
//        System.out.println("Spell Instances: " + getAllSpellInstances());
        if (spells.isEmpty()) {
            setCurrentSpellInstance(ModSpells.EMPTY, GrabBag.empty());
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        double distance = this.target != null ? this.distanceTo(this.target) : 0.0;
        double selfHealthPercent = (this.getHealth() / this.getMaxHealth()) * 100.0;
        double targetHealthPercent = (this.target != null && this.target.getMaxHealth() > 0)
                ? (this.target.getHealth() / this.target.getMaxHealth()) * 100.0
                : 100.0;

        NbtCompound nbt = this.dataTracker.get(SPELLS);

        List<Spell.InstancedSpell> validSpells = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        if (this.isOnFire() && !this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            Spell.InstancedSpell fallback = new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:fire_resistance");
                put("duration", 240);
                put("cooldown", 100);
            }}));
            setCurrentSpellInstance(fallback.spell(), fallback.args());
            return fallback;
        }

        boolean targetOnFire = (this.target != null && this.target.isOnFire());

        var pb = new Spell.InstancedSpell(ModSpells.PURGE_BOLT, GrabBag.fromMap(new HashMap<>() {{
            put("cooldown", getSpellWaitAmount(5));
        }}));

        if (target.hasStatusEffect(StatusEffects.REGENERATION) || target.hasStatusEffect(StatusEffects.RESISTANCE) || target.hasStatusEffect(StatusEffects.ABSORPTION)) {
            if (canUseSpell(pb)) {
                setCurrentSpellInstance(pb.spell(), pb.args());
                return pb;
            }
        }

        for (Spell.InstancedSpell spellInstance : spells) {
            Identifier id = ModRegistries.SPELL.getId(spellInstance.spell());
            if (id == null || nbt == null || !nbt.contains(id.toString())) {
                continue;
            }

            if (targetOnFire && (spellInstance.spell() == ModSpells.WATER_WAVE && spellInstance.spell() == ModSpells.WATER_BLAST)) continue;
            if (target.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && spellInstance.spell().getSpellType() == SpellTypes.FIRE) {
                if (canUseSpell(pb)) {
                    setCurrentSpellInstance(pb.spell(), pb.args());
                    return pb;
                } else {
                    continue;
                }
            }

            NbtCompound spellData = nbt.getCompound(id.toString());

            double min = spellData.getDouble("minDistance");
            double max = spellData.getDouble("maxDistance");
            double belowLife = spellData.contains("belowLife") ? spellData.getDouble("belowLife") : -1;
            double aboveLife = spellData.contains("aboveLife") ? spellData.getDouble("aboveLife") : -1;
            double targetBelowLife = spellData.contains("targetBelowLife") ? spellData.getDouble("targetBelowLife") : -1;
            double targetAboveLife = spellData.contains("targetAboveLife") ? spellData.getDouble("targetAboveLife") : -1;
            float weight = spellData.contains("weight") ? spellData.getFloat("weight") : 1.0f;
            var cd = spellInstance.spell().getCooldown(spellInstance.args(), this);

            if (belowLife == -1) belowLife = 100.0;
            if (aboveLife == -1) aboveLife = 0.0;
            if (targetBelowLife == -1) targetBelowLife = 100.0;
            if (targetAboveLife == -1) targetAboveLife = 0.0;

            boolean withinDistance = (max <= 0 || (distance >= min && distance <= max));
            boolean withinSelfHealth = (selfHealthPercent <= belowLife && selfHealthPercent >= aboveLife);
            boolean withinTargetHealth = (targetHealthPercent <= targetBelowLife && targetHealthPercent >= targetAboveLife);

            if (withinDistance && withinSelfHealth && withinTargetHealth && data.getCooldown(cd) <= 0) {
                validSpells.add(spellInstance);
                weights.add(weight);
            }
        }

        if (validSpells.isEmpty()) {
            setCurrentSpellInstance(ModSpells.EMPTY, GrabBag.empty());
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        float totalWeight = 0.0f;
        for (float w : weights) totalWeight += w;

        float randomPoint = this.random.nextFloat() * totalWeight;
        for (int i = 0; i < validSpells.size(); i++) {
            randomPoint -= weights.get(i);
            if (randomPoint <= 0) {
                Spell.InstancedSpell chosen = validSpells.get(i);
                setCurrentSpellInstance(chosen.spell(), chosen.args());
                return chosen;
            }
        }

        Spell.InstancedSpell fallback = validSpells.getFirst();
        setCurrentSpellInstance(fallback.spell(), fallback.args());
        return fallback;
    }

    public void setSpellEmpty() {
        Identifier id = ModRegistries.SPELL.getId(ModSpells.EMPTY);
        if (id != null) {
            this.dataTracker.set(CURRENT_SPELL, id.toString());
            this.dataTracker.set(SPELL_ARGS, new NbtCompound());
        }
    }

    public int getGlobalCastDelay() {
        return this.dataTracker.get(GLOBAL_CAST_DELAY);
    }

    public void setGlobalCastDelay(int delay) {
        this.dataTracker.set(GLOBAL_CAST_DELAY, delay);
    }

    public int getCastTime() {
        return this.dataTracker.get(SPELL_CAST_TIME);
    }

    public void setCastTime(int time) {
        this.dataTracker.set(SPELL_CAST_TIME, time);
    }

    public int getIllusionTime() {
        return this.dataTracker.get(ILLUSION_TIME);
    }

    public void setIllusionTime(int time) {
        this.dataTracker.set(ILLUSION_TIME, time);
    }

    public Spell getCurrentSpell() {
        return getCurrentSpellInstance().spell();
    }

    public GrabBag getCurrentArgs() {
        return getCurrentSpellInstance().args();
    }

    public void setCurrentSpellNoArgs(Spell spell) {
        setCurrentSpellInstance(spell, GrabBag.empty());
    }


    public void addSpell(Spell.InstancedSpell instance, double minDistance, double maxDistance, double belowLife, double aboveLife, double targetBelowLife, double targetAboveLife, float weight) {
        if (instance == null || instance.spell() == null) return;

        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null) {
            nbt = new NbtCompound();
        } else {
            nbt = nbt.copy();
        }

        Identifier id = ModRegistries.SPELL.getId(instance.spell());
        if (id == null) return;

        NbtCompound spellData = new NbtCompound();
        spellData.put("args", GrabBag.toNBT(instance.args()));
        spellData.putDouble("minDistance", minDistance);
        spellData.putDouble("maxDistance", maxDistance);
        spellData.putDouble("belowLife", belowLife);
        spellData.putDouble("aboveLife", aboveLife);
        spellData.putDouble("targetBelowLife", targetBelowLife);
        spellData.putDouble("targetAboveLife", targetAboveLife);
        spellData.putFloat("weight", weight);

        nbt.put(id.toString(), spellData);

        this.dataTracker.set(SPELLS, nbt);
    }

    @Nullable
    public Spell.InstancedSpell getSpellInstance(int index) {
        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null || nbt.isEmpty()) return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());

        String spellId = nbt.getKeys().stream().skip(index).findFirst().orElse(null);
        if (spellId == null) return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());

        Spell spell = ModRegistries.SPELL.get(Identifier.tryParse(spellId));
        GrabBag args = GrabBag.fromNBT(nbt.getCompound(spellId));

        return new Spell.InstancedSpell(spell, args);
    }

    public List<Spell.InstancedSpell> getAllSpellInstances() {
        List<Spell.InstancedSpell> list = new ArrayList<>();
        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null || nbt.isEmpty()) return list;

        for (String key : nbt.getKeys()) {

            Spell spell = ModRegistries.SPELL.get(Identifier.tryParse(key));
            if (spell == null) continue;

            NbtCompound spellData = nbt.getCompound(key);
            GrabBag args;
            if (spellData.contains("args", NbtElement.COMPOUND_TYPE)) {
                args = GrabBag.fromNBT(spellData.getCompound("args"));
            } else {
                args = GrabBag.fromNBT(spellData);
            }

            list.add(new Spell.InstancedSpell(spell, args));
        }

        return list;
    }
    public void removeSpell(Spell.InstancedSpell instance) {
        if (instance == null || instance.spell() == null) return;

        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null) return;

        Identifier id = ModRegistries.SPELL.getId(instance.spell());
        if (id == null || !nbt.contains(id.toString())) return;

        nbt.remove(id.toString());
        this.dataTracker.set(SPELLS, nbt);
    }

    @Override
    public SpellDataStore getSpellDataStore(LivingEntity entity) {
        return new SpellDataStore() {
            @Override
            public Spell getSpell() {
                String spellName = dataTracker.get(CURRENT_SPELL);
                return ModRegistries.SPELL.get(Identifier.tryParse(spellName));
            }

            @Override
            public GrabBag getArgs() {
                NbtCompound argsNbt = dataTracker.get(SPELL_ARGS);

                if (argsNbt == null || argsNbt.isEmpty()) {
                    return GrabBag.empty();
                }

                if (argsNbt.contains("args", NbtElement.COMPOUND_TYPE)) {
                    argsNbt = argsNbt.getCompound("args");
                }

                return GrabBag.fromNBT(argsNbt);
            }

            @Override
            public void setSpell(Spell spell, GrabBag args) {
                dataTracker.set(CURRENT_SPELL, Objects.requireNonNull(ModRegistries.SPELL.getId(spell)).toString());
                dataTracker.set(SPELL_ARGS, args != null ? GrabBag.toNBT(args) : new NbtCompound());
            }
            @Override
            public int getCooldown(@Nullable Spell.SpellCooldown cooldown) {
                if (cooldown == null) return 0;

                NbtCompound cooldowns = dataTracker.get(SPELL_COOLDOWN);
                return cooldowns.contains(cooldown.key().toString())
                        ? cooldowns.getInt(cooldown.key().toString())
                        : 0;
            }


            @Override
            public float getCooldownPercent(@Nullable Spell.SpellCooldown cooldown) {
                int cd = getCooldown(cooldown);
                return cd / (float) (cooldown != null ? cooldown.ticks() : 1);
            }

            @Override
            public void setCooldown(@Nullable Spell.SpellCooldown cooldown) {
                if (cooldown == null) return;

                NbtCompound cooldowns = dataTracker.get(SPELL_COOLDOWN).copy();
                cooldowns.putInt(cooldown.key().toString(), cooldown.ticks());
                dataTracker.set(SPELL_COOLDOWN, cooldowns);
            }
        };
    }

    @Override
    public void tickCooldowns(LivingEntity entity) {
        NbtCompound cooldowns = dataTracker.get(SPELL_COOLDOWN).copy();
        boolean updated = false;

        for (String key : new HashSet<>(cooldowns.getKeys())) { // iterate over a copy
            int ticks = cooldowns.getInt(key);
            if (ticks > 0) {
                cooldowns.putInt(key, ticks - 1);
                updated = true;
            } else {
                cooldowns.remove(key); // safe now
                updated = true;
            }
        }

        if (updated) {
            dataTracker.set(SPELL_COOLDOWN, cooldowns);
        }
    }


    @Override
    public int getAngerTime() {
        return 0;
    }

    @Override
    public void setAngerTime(int angerTime) {

    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return null;
    }
    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }
    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {

    }

    @Override
    public void chooseRandomAngerTime() {
    }

}
