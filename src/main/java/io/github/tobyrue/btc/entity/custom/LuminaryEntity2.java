package io.github.tobyrue.btc.entity.custom;


import io.github.tobyrue.btc.entity.ai.EldritchLuminaryStrafeGoal;
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
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LuminaryEntity2 extends HostileEntity implements Angerable, SpellHost<LivingEntity> {
    private static final TrackedData<Integer> SPELL_COOLDOWN = DataTracker.registerData(LuminaryEntity2.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> CURRENT_SPELL = DataTracker.registerData(LuminaryEntity2.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<NbtCompound> SPELL_ARGS = DataTracker.registerData(LuminaryEntity2.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<NbtCompound> SPELLS = DataTracker.registerData(LuminaryEntity2.class, TrackedDataHandlerRegistry.NBT_COMPOUND);
    private static final TrackedData<Integer> SPELL_CAST_TIME = DataTracker.registerData(LuminaryEntity2.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> SPELL_CAST_COOLDOWN = DataTracker.registerData(LuminaryEntity2.class, TrackedDataHandlerRegistry.INTEGER);

    private LivingEntity target;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;
    private final int castCooldown = 30;
    private final int castTime = 15;


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


    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.INDIRECT_MAGIC)) {
            return false;
        }
        return super.damage(source, amount);
    }

    public LuminaryEntity2(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 15;
    }

    public int getFireballStrength() {
        return 3;
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.startIfNotRunning(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
        if (getCastTime() > 0 && getCastTime() < castTime) {
            this.attackAnimationTimeout = 40;
            this.attackAnimationState.startIfNotRunning(this.age);
        } else {
            // Fade or stop animation when not in casting phase
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

    public void triggerAttackAnimation() {
        this.attackAnimationState.start(this.age);
    }

    public int attackTick = 100; // Default to no action scheduled.


    @Override
    public void tick() {
        super.tick();

        while (getCastCooldown() < castCooldown) {
            setCastCooldown(getCastCooldown() + 1);
        }

        if (getCastCooldown() >= castCooldown) {
            while (getCastTime() < castTime) {
                setCastTime(getCastTime() + 1);
            }
            if (getCastTime() >= castTime) {
                castCurrentSpellAt(this.target);
                setCastTime(0);
                setCastCooldown(0);
            }
        }

        if (getAllSpellInstances().isEmpty()) {
            this.addSpell(new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.empty()));
        }

        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }

        // Server-side spell cooldown tick
        if (!this.getWorld().isClient()) {
            ((SpellHost<LivingEntity>) this).tickCooldowns(this);
        }

        if (this.getWorld().isClient && this.getCurrentSpell() != ModSpells.EMPTY) {
            Spell.InstancedSpell spell = this.getCurrentSpellInstance();
            var color = Color.decode(Integer.toString(spell.spell().getColor(spell.args())));

            float r = (float)color.getRed();
            float g = (float)color.getGreen();
            float b = (float)color.getBlue();
            float i = this.bodyYaw * 0.017453292F + MathHelper.cos((float)this.age * 0.6662F) * 0.25F;
            float j = MathHelper.cos(i);
            float k = MathHelper.sin(i);
            double d = 0.6 * (double)this.getScale();
            double e = 1.8 * (double)this.getScale();
            this.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b), this.getX() + (double) j * d, this.getY() + e, this.getZ() + (double) k * d, 0.0, 0.0, 0.0);
            this.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b), this.getX() - (double) j * d, this.getY() + e, this.getZ() - (double) k * d, 0.0, 0.0, 0.0);

        }

        // Example: auto-cast spell if cooldown is 0 and a target exists
        if (!this.getWorld().isClient() && getTarget() != null && getSpellDataStore(this).getCooldown(null) <= 0) {
            castCurrentSpellAt(getTarget());
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

    public void scheduleAction(int ticks) {
        this.attackTick = ticks;
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(SPELL_COOLDOWN, 0);
        builder.add(CURRENT_SPELL, "empty");
        builder.add(SPELL_ARGS, new NbtCompound());
        builder.add(SPELLS, new NbtCompound());
        builder.add(SPELL_CAST_TIME, 0);
        builder.add(SPELL_CAST_COOLDOWN, 0);
    }

    public static DefaultAttributeContainer.Builder createEldritchLuminaryAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_ARMOR, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,2f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.5)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.75f);
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
        if (id != null) {
            this.dataTracker.set(CURRENT_SPELL, id.toString());
            this.dataTracker.set(SPELL_ARGS, args != null ? GrabBag.toNBT(args) : new NbtCompound());
        }
    }

    public int getCastCooldown() {
        return this.dataTracker.get(SPELL_CAST_COOLDOWN);
    }

    public void setCastCooldown(int cooldown) {
        this.dataTracker.set(SPELL_CAST_COOLDOWN, cooldown);
    }

    public int getCastTime() {
        return this.dataTracker.get(SPELL_CAST_TIME);
    }

    public void setCastTime(int time) {
        this.dataTracker.set(SPELL_CAST_TIME, time);
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
                return GrabBag.fromNBT(argsNbt != null ? argsNbt : new NbtCompound());
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

    public void addSpell(Spell.InstancedSpell instance) {
        if (instance == null || instance.spell() == null) return;

        NbtCompound nbt = this.dataTracker.get(SPELLS);
        if (nbt == null) {
            nbt = new NbtCompound();
        } else {
            nbt = nbt.copy();
        }

        Identifier id = ModRegistries.SPELL.getId(instance.spell());
        if (id == null) return;

        nbt.put(id.toString(), GrabBag.toNBT(instance.args()));
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
            GrabBag args = GrabBag.fromNBT(nbt.getCompound(key));
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

}
