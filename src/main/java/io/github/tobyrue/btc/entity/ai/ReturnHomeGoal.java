package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.KeyGolemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class ReturnHomeGoal extends Goal {
    private final KeyGolemEntity mob;
    private final double speed;

    public ReturnHomeGoal(KeyGolemEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return mob.getWorldHomePos() != null && mob.squaredDistanceTo(Vec3d.ofBottomCenter(mob.getWorldHomePos())) > 4.0;
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(mob.getWorldHomePos().getX(), mob.getWorldHomePos().getY(), mob.getWorldHomePos().getZ(), this.speed);
    }

    @Override
    public boolean shouldContinue() {
        return !mob.getNavigation().isIdle() && mob.squaredDistanceTo(Vec3d.ofBottomCenter(mob.getWorldHomePos())) > 1.0;
    }
}
