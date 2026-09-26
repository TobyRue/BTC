package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.util.SummonableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.world.ServerWorld;

import java.util.EnumSet;
import java.util.UUID;

public class SummonTargetGoal extends Goal {

    private final MobEntity mob;

    private LivingEntity owner;
    private LivingEntity target;

    private int lastOwnerAttackedTime;
    private int lastOwnerAttackTime;

    public SummonTargetGoal(MobEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        this.owner = findOwner();

        if (this.owner == null || !this.owner.isAlive()) {
            return false;
        }


        LivingEntity attacker = this.owner.getAttacker();

        if (attacker != null
                && attacker.isAlive()
                && this.owner.getLastAttackedTime() != this.lastOwnerAttackedTime) {

            this.lastOwnerAttackedTime = this.owner.getLastAttackedTime();

            if (isValidTarget(attacker)) {
                this.target = attacker;
                return true;
            }
        }

        LivingEntity ownerTarget = this.owner.getAttacking();

        if (ownerTarget != null
                && ownerTarget.isAlive()
                && this.owner.getLastAttackTime() != this.lastOwnerAttackTime) {

            this.lastOwnerAttackTime = this.owner.getLastAttackTime();

            if (isValidTarget(ownerTarget)) {
                this.target = ownerTarget;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null
                && this.target.isAlive()
                && isValidTarget(this.target);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
    }

    @Override
    public void stop() {
        this.target = null;
        this.mob.setTarget(null);
    }

    private LivingEntity findOwner() {
        if (!(this.mob instanceof SummonableEntity summonable)) {
            return null;
        }

        UUID ownerUuid = summonable.btc$getOwnerUuid();

        if (ownerUuid == null) {
            return null;
        }

        if (!(this.mob.getWorld() instanceof ServerWorld serverWorld)) {
            return null;
        }

        Entity entity = serverWorld.getEntity(ownerUuid);

        return entity instanceof LivingEntity livingEntity
                ? livingEntity
                : null;
    }

    private boolean isValidTarget(LivingEntity possibleTarget) {
        if (possibleTarget == null || !possibleTarget.isAlive()) {
            return false;
        }


        if (possibleTarget.getUuid().equals(this.owner.getUuid())) {
            return false;
        }

        if (possibleTarget instanceof SummonableEntity targetSummonable) {
            UUID targetOwnerUuid = targetSummonable.btc$getOwnerUuid();

            if (targetOwnerUuid != null
                    && targetOwnerUuid.equals(this.owner.getUuid())) {
                return false;
            }
        }


        if (possibleTarget instanceof TameableEntity tameable) {
            if (tameable.isTamed()
                    && tameable.getOwnerUuid() != null
                    && tameable.getOwnerUuid().equals(this.owner.getUuid())) {
                return false;
            }
        }


        AbstractTeam ownerTeam = this.owner.getScoreboardTeam();
        AbstractTeam targetTeam = possibleTarget.getScoreboardTeam();

        if (ownerTeam != null
                && targetTeam != null
                && ownerTeam.isEqual(targetTeam)) {
            return false;
        }


        if (possibleTarget instanceof SummonableEntity targetSummonable) {
            UUID targetOwnerUuid = targetSummonable.btc$getOwnerUuid();

            if (targetOwnerUuid != null) {
                if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
                    Entity targetOwnerEntity = serverWorld.getEntity(targetOwnerUuid);

                    if (targetOwnerEntity instanceof LivingEntity targetOwner) {
                        AbstractTeam targetOwnerTeam = targetOwner.getScoreboardTeam();

                        if (ownerTeam != null
                                && targetOwnerTeam != null
                                && ownerTeam.isEqual(targetOwnerTeam)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
