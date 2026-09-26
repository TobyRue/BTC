package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.util.SummonableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.EnumSet;
import java.util.UUID;

public class ProtectOwnerGoal extends Goal {

    private static final double MIN_DISTANCE = 8.0D;
    private static final double MIN_DISTANCE_SQUARED = MIN_DISTANCE * MIN_DISTANCE;

    private final MobEntity mob;
    private LivingEntity owner;
    private final double speed;

    public ProtectOwnerGoal(MobEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;

        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        this.owner = findOwner();

        if (this.owner == null || !this.owner.isAlive()) {
            return false;
        }

        return this.mob.squaredDistanceTo(this.owner) > MIN_DISTANCE_SQUARED;
    }

    @Override
    public boolean shouldContinue() {
        if (this.owner == null || !this.owner.isAlive()) {
            return false;
        }

        return this.mob.squaredDistanceTo(this.owner) > MIN_DISTANCE_SQUARED;
    }

    @Override
    public void start() {
        moveToOwner();
    }

    @Override
    public void tick() {
        if (this.owner == null || !this.owner.isAlive()) {
            return;
        }

        if (this.mob.squaredDistanceTo(this.owner) > MIN_DISTANCE_SQUARED) {
            moveToOwner();
        } else {
            this.mob.getNavigation().stop();
        }
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.owner = null;
    }

    private void moveToOwner() {
        this.mob.getNavigation().startMovingTo(this.owner, this.speed);
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
}
