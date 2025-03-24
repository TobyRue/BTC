package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ai.CopperGolemWander;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CopperGolemEntity extends GolemEntity {
    private int oxidationTimer = 0;
    private static final int OXIDATION_INTERVAL = 20
//            * 60
            * 5; // Every 5 minutes (6000 ticks)

    public enum Oxidation {
        UNOXIDIZED, EXPOSED, WEATHERED, OXIDIZED
    }

    private static final TrackedData<Byte> OXIDATION_STATE;
    private static final TrackedData<Boolean> WAXED_STATE; // New waxed state

    static {
        OXIDATION_STATE = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
        WAXED_STATE = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }


    public CopperGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }
    public static DefaultAttributeContainer.Builder createCopperGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.8f)
                .add(EntityAttributes.GENERIC_ARMOR, 8f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20f)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1f);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OXIDATION_STATE, (byte) Oxidation.UNOXIDIZED.ordinal());
        builder.add(WAXED_STATE, false);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new CopperGolemWander(this, 0.3D));
        this.goalSelector.add(2, new TemptGoal(this, 0.4D, Ingredient.ofItems(Items.COPPER_INGOT), false));
    }

    @Override
    public void tick() {
        super.tick();
        System.out.println("Oxidation Timer: " + this.oxidationTimer + " Oxidation: " + this.getOxidation());

        if (!this.getWorld().isClient) {
            if (!this.isWaxed()) { // Only oxidize if NOT waxed
                this.oxidationTimer++;

                if (this.oxidationTimer >= OXIDATION_INTERVAL) {
                    this.oxidationTimer = 0;
                    this.advanceOxidation();
                    System.out.println("Oxidation State Changed: " + this.getOxidation());
                }
            }
        }
    }

    public boolean isWaxed() {
        return this.dataTracker.get(WAXED_STATE);
    }

    public void setWaxed(boolean waxed) {
        this.dataTracker.set(WAXED_STATE, waxed);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Check if the player is using honeycomb
        if (itemStack.isOf(Items.HONEYCOMB)) {
            if (!this.isWaxed()) {
                this.setWaxed(true); // Mark as waxed
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Reduce honeycomb count
                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }

    private void advanceOxidation() {
        Oxidation currentState = this.getOxidation();
        Oxidation nextState = switch (currentState) {
            case UNOXIDIZED -> Oxidation.EXPOSED;
            case EXPOSED -> Oxidation.WEATHERED;
            case WEATHERED, OXIDIZED -> Oxidation.OXIDIZED;
        };
        this.setOxidation(nextState);
    }

    public void setOxidation(Oxidation oxidation) {
        this.dataTracker.set(OXIDATION_STATE, (byte) oxidation.ordinal());
    }
    public Oxidation getOxidation() {
        return Oxidation.values()[this.dataTracker.get(OXIDATION_STATE)];
    }



    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("OxidationState", (byte) this.getOxidation().ordinal());  // Save the oxidation state
        nbt.putBoolean("Waxed", this.isWaxed());  // Save the waxed state
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("OxidationState", NbtElement.BYTE_TYPE)) {
            this.setOxidation(Oxidation.values()[nbt.getByte("OxidationState")]);
        }
        if (nbt.contains("Waxed")) {  // No type check needed for booleans
            this.setWaxed(nbt.getBoolean("Waxed"));
        }
    }
}
