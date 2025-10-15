package io.github.tobyrue.btc.entity.custom;


import io.github.tobyrue.btc.client.EldritchLuminaryModel;
import io.github.tobyrue.btc.client.EldritchLuminaryRenderer;
import io.github.tobyrue.btc.entity.ai.EldritchLuminaryStrafeGoal;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.SpellDataStore;
import io.github.tobyrue.btc.spell.SpellHost;
import net.minecraft.client.render.entity.IllusionerEntityRenderer;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

public class EldritchLuminaryEntity extends HostileEntity implements Angerable, SpellHost<LivingEntity> {
    private static final TrackedData<Integer> SPELL_COOLDOWN = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> CURRENT_SPELL = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<NbtCompound> SPELL_ARGS = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<NbtCompound> SPELLS = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<Integer> SPELL_CAST_TIME = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ILLUSION_TIME = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private Spell.InstancedSpell activeCastingSpell = null;

    private LivingEntity target;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private final int castTime = 30;
    private final int illusionTime = 400;
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
        builder.add(SPELL_COOLDOWN, 0);
        builder.add(CURRENT_SPELL, "empty");
        builder.add(SPELL_ARGS, new NbtCompound());
        builder.add(SPELLS, new NbtCompound());
        builder.add(SPELL_CAST_TIME, 0);
        builder.add(ILLUSION_TIME, 0);
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
        Spell.InstancedSpell current = this.activeCastingSpell != null
                ? this.activeCastingSpell
                : this.getCurrentSpellInstance();

        if (current != null && current.spell() != null) {
            var spellCooldown = current.spell().getCooldown(current.args(), this);

            // Only animate while casting
            if (getCastTime() > 0 && getCastTime() <= castTime) {
                this.attackAnimationTimeout = 20;
                this.attackAnimationState.startIfNotRunning(this.age);
            } else {
                if (this.attackAnimationTimeout > 0) {
                    --this.attackAnimationTimeout;
                } else {
                    this.attackAnimationState.stop();
                }
            }
        } else {
            this.attackAnimationState.stop();
        }
    }


    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    public Vec3d[] getMirrorCopyOffsets(float tickDelta) {
        if (this.getIllusionTime() <= 0) {
            return this.mirrorCopyOffsets[1];
        }
        double d = ((float)this.getIllusionTime() - tickDelta) / 3.0f;
        d = Math.pow(d, 0.25);
        Vec3d[] vec3ds = new Vec3d[4];
        for (int i = 0; i < 4; ++i) {
            vec3ds[i] = this.mirrorCopyOffsets[1][i].multiply(1.0 - d).add(this.mirrorCopyOffsets[0][i].multiply(d));
        }
        return vec3ds;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            if (getIllusionTime() > 0 && getIllusionTime() <= illusionTime) {
                this.setIllusionTime(getIllusionTime() + 1);
            } else if (getIllusionTime() >= illusionTime) {
                this.setIllusionTime(0);
            }
        }

        if (this.getCurrentSpellInstance() != null && this.getCurrentSpellInstance().spell() != null && this.getCurrentSpellInstance().args() != null) {
            SpellDataStore data = getSpellDataStore(this);
            Spell.InstancedSpell current = this.getCurrentSpellInstance();
            var spellCooldown = current.spell().getCooldown(current.args(), this);


             if (!this.getWorld().isClient()) {

                 ((SpellHost<LivingEntity>) this).tickCooldowns(this);

                 if (this.target != null) {
                     // Step 1: If not currently charging, start a new spell
                     if (activeCastingSpell == null && getCastTime() <= 0) {
                         // Only start if all spells are ready
                         Spell.InstancedSpell random = chooseRandomCurrentSpell();
                         var cd = random.spell().getCooldown(random.args(), this);
                         if (getSpellDataStore(this).getCooldown(cd) <= 0) {
                             activeCastingSpell = random;
                             setCastTime(1);
                         }
                     }
                     // Step 2: Continue charging
                     else if (activeCastingSpell != null && getCastTime() < castTime) {
                         setCastTime(getCastTime() + 1);
                     }
                     // Step 3: Finished casting
                     else if (activeCastingSpell != null && getCastTime() >= castTime) {
                         this.lookAtEntity(target, 90, 90);
                         castCurrentSpellAt(this.target);

                         // Apply cooldown
                         var cd = activeCastingSpell.spell().getCooldown(activeCastingSpell.args(), this);
                         getSpellDataStore(this).setCooldown(cd);

                         // Reset
                         activeCastingSpell = null;
                         setCastTime(0);
                         setSpellEmpty();
                     }
                 } else {
                     if (activeCastingSpell != null && getCastTime() >= castTime && (this.getHealth() / this.getMaxHealth()) * 100 <= 30) {
                         this.lookAtEntity(target, 90, 90);
                         activeCastingSpell = new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                             put("effect", "minecraft:regeneration");
                             put("duration", 150);
                             put("cooldown", 100);
                             put("amplifier", 3);
                         }}));
                         setCurrentSpellInstance(activeCastingSpell.spell(), activeCastingSpell.args());
                         castCurrentSpellAt(this.target);

                         // Apply cooldown
                         var cd = activeCastingSpell.spell().getCooldown(activeCastingSpell.args(), this);
                         getSpellDataStore(this).setCooldown(cd);

                         // Reset
                         activeCastingSpell = null;
                         setCastTime(0);
                         setSpellEmpty();
                     }
                 }
             }
             System.out.println("Cooldown: " + data.getCooldown(spellCooldown));


            if (this.getWorld().isClient() && current.spell() != ModSpells.EMPTY && current.spell() != null && data.getCooldown(spellCooldown) <= 0) {

                int colorInt = current.spell().getColor(current.args());

                float r = ((colorInt >> 16) & 0xFF) / 255.0F;
                float g = ((colorInt >> 8) & 0xFF) / 255.0F;
                float b = (colorInt & 0xFF) / 255.0F;

                float i = this.bodyYaw * 0.017453292F + MathHelper.cos((float) this.age * 0.6662F) * 0.25F;
                float j = MathHelper.cos(i);
                float k = MathHelper.sin(i);
                double d = 0.6 * (double) this.getScale();
                double e = 1.8 * (double) this.getScale();

                this.getWorld().addParticle(
                        EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                        this.getX() + j * d, this.getY() + e, this.getZ() + k * d,
                        0.0, 0.0, 0.0
                );

                this.getWorld().addParticle(
                        EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                        this.getX() - j * d, this.getY() + e, this.getZ() - k * d,
                        0.0, 0.0, 0.0
                );
            }
        }
        if (getAllSpellInstances().isEmpty()) {
            this.addSpell(new Spell.InstancedSpell(ModSpells.SHULKER_BULLET, GrabBag.empty()), 4, 16, -1, 6, -1, 75, 0.5f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", 100);
            }})), 8, 24, -1, -1, -1, -1, 0.6f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.EARTH_SPIKE_LINE, GrabBag.empty()), 0, 12, -1, 15, -1, -1, 0.7f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.LOCALIZED_STORM_PUSH, GrabBag.fromMap(new HashMap<>() {{
                put("shootStrength", 2d);
                put("verticalMultiplier", 1.2d);
                put("cooldown", 100);
            }})), 0, 4, 40, -1, -1, -1, 0.8f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.CREEPER_WALL_BLOCK, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", 100);
            }})), 2, 10, 30, 4, -1, -1, 0.7f);
            this.addSpell(new Spell.InstancedSpell(ModSpells.CREEPER_WALL_CIRCLE, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", 100);
            }})), 5, 12, 8, -1, -1, -1, 0.8f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:invisibility");
                put("duration", 400);
                put("cooldown", 100);
            }})), 0, 24, 50, -1, -1, -1, 0.85f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:regeneration");
                put("duration", 150);
                put("cooldown", 100);
                put("amplifier", 3);
            }})), 0, 24, 30, -1, -1, -1, 1f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:blindness");
                put("duration", 200);
                put("cooldown", 100);
                put("amplifier", 3);
            }})), 0, 24, 60, -1, -1, -1, 0.9f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.WATER_BLAST, GrabBag.fromMap(new HashMap<>() {{
                put("noGravity", true);
                put("cooldown", 100);
            }})), 0, 24, -1, -1, -1, -1, 0.7f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.ICE_BLOCK, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", 100);
            }})), 6, 24, -1, -1, -1, -1, 0.6f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.FIRE_STORM, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", 100);
            }})), 0, 8, -1, -1, -1, -1, 0.7f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.SHADOW_STEP, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", 100);
            }})), 0, 18, -1, -1, 40, -1, 0.8f);

            this.addSpell(new Spell.InstancedSpell(ModSpells.ELDRITCH_ILLUSION, GrabBag.fromMap(new HashMap<>() {{
                put("cooldown", 100);
            }})), 0, 24, -1, -1, -1, -1, 1.1f);

            this.setSpellEmpty();
        }
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }

        if (!this.getWorld().isClient()) {
            ((SpellHost<LivingEntity>) this).tickCooldowns(this);
        }
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

        data.setCooldown(spell.getCooldown(data.getArgs(), this));
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

    @Nullable
    public Spell.InstancedSpell getRandomSpellInstance() {
        List<Spell.InstancedSpell> spells = this.getAllSpellInstances();
        if (spells.isEmpty()) return null;

        return spells.get(this.getWorld().random.nextInt(spells.size()));
    }

    public Spell.InstancedSpell chooseRandomCurrentSpell() {
        List<Spell.InstancedSpell> spells = getAllSpellInstances();
        if (spells.isEmpty()) {
            setCurrentSpellInstance(ModSpells.EMPTY, GrabBag.empty());
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        double distance = this.target != null ? this.distanceTo(this.target) : 0.0;
        double selfHealthPercent = (this.getHealth() / this.getMaxHealth()) * 100.0;
        double targetHealthPercent = (this.target != null && this.target.getMaxHealth() > 0)
                ? (this.target.getHealth() / this.target.getMaxHealth()) * 100.0
                : 100.0; // Assume full HP if no target

        NbtCompound nbt = this.dataTracker.get(SPELLS);


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

        // Create a weighted pool of valid spells
        List<Spell.InstancedSpell> validSpells = new ArrayList<>();
        List<Float> weights = new ArrayList<>();

        for (Spell.InstancedSpell spellInstance : spells) {
            Identifier id = ModRegistries.SPELL.getId(spellInstance.spell());
            if (id == null || nbt == null || !nbt.contains(id.toString())) continue;

            // Skip water-type spells if the target is burning
            if (targetOnFire && (spellInstance.spell() == ModSpells.WATER_WAVE && spellInstance.spell() == ModSpells.WATER_BLAST)) continue;
            if (target.hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && spellInstance.spell().getSpellType() == SpellTypes.FIRE) continue;


            NbtCompound spellData = nbt.getCompound(id.toString());

            double min = spellData.getDouble("minDistance");
            double max = spellData.getDouble("maxDistance");
            double belowLife = spellData.contains("belowLife") ? spellData.getDouble("belowLife") : -1;
            double aboveLife = spellData.contains("aboveLife") ? spellData.getDouble("aboveLife") : -1;
            double targetBelowLife = spellData.contains("targetBelowLife") ? spellData.getDouble("targetBelowLife") : -1;
            double targetAboveLife = spellData.contains("targetAboveLife") ? spellData.getDouble("targetAboveLife") : -1;
            float weight = spellData.contains("weight") ? spellData.getFloat("weight") : 1.0f;

            if (belowLife == -1) belowLife = 100.0;
            if (aboveLife == -1) aboveLife = 0.0;
            if (targetBelowLife == -1) targetBelowLife = 100.0;
            if (targetAboveLife == -1) targetAboveLife = 0.0;

            boolean withinDistance = (max <= 0 || (distance >= min && distance <= max));
            boolean withinSelfHealth = (selfHealthPercent <= belowLife && selfHealthPercent >= aboveLife);
            boolean withinTargetHealth = (targetHealthPercent <= targetBelowLife && targetHealthPercent >= targetAboveLife);

            if (withinDistance && withinSelfHealth && withinTargetHealth) {
                validSpells.add(spellInstance);
                weights.add(weight);
            }
        }

        if (validSpells.isEmpty()) {
            setCurrentSpellInstance(ModSpells.EMPTY, GrabBag.empty());
            return new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty());
        }

        // Weighted random selection
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

        // Fallback
        Spell.InstancedSpell chosen = validSpells.getFirst();
        setCurrentSpellInstance(chosen.spell(), chosen.args());
        return chosen;
    }

    public void setSpellEmpty() {
        Identifier id = ModRegistries.SPELL.getId(ModSpells.EMPTY);
        if (id != null) {
            this.dataTracker.set(CURRENT_SPELL, id.toString());
            this.dataTracker.set(SPELL_ARGS, new NbtCompound());
        }
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
                return dataTracker.get(SPELL_COOLDOWN);
            }

            @Override
            public float getCooldownPercent(@Nullable Spell.SpellCooldown cooldown) {
                int cd = getCooldown(cooldown);
                return cd / (float) (cooldown != null ? cooldown.ticks() : 1);
            }

            @Override
            public void setCooldown(@Nullable Spell.SpellCooldown cooldown) {
                if (cooldown != null) {
                    dataTracker.set(SPELL_COOLDOWN, cooldown.ticks());
                }
            }
        };
    }

    @Override
    public void tickCooldowns(LivingEntity entity) {
        int cd = dataTracker.get(SPELL_COOLDOWN);
        if (cd > 0) {
            dataTracker.set(SPELL_COOLDOWN, cd - 1);
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
