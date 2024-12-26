package io.github.tobyrue.btc.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class EldritchLuminaryStrafeGoal extends Goal {
    private final MobEntity luminary;
    private LivingEntity target;
    private final double speed;
    private final float range;
    private boolean strafingLeft;
    private boolean movingBackward;
    private int combatTicks = -1;
    private double closeRangeThreshold;

    public EldritchLuminaryStrafeGoal(MobEntity luminary, double speed, float range, double closeRangeThreshold) {
        this.luminary = luminary;
        this.speed = speed;
        this.range = range;
        this.closeRangeThreshold = closeRangeThreshold;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.target = this.luminary.getTarget();
        // Check if the target is valid and set by the ActiveTargetGoal
        return this.target != null && this.target.isAlive() && this.luminary.getTarget() instanceof LivingEntity;
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null && this.target.isAlive() && this.luminary.getTarget() instanceof LivingEntity;
    }

    @Override
    public void start() {
        this.combatTicks = 0;
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        double distance = this.luminary.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());

        // Reevaluate strafing directions every 20 ticks
        if (this.combatTicks >= 20) {
            this.strafingLeft = this.luminary.getRandom().nextBoolean();
            this.movingBackward = this.luminary.getRandom().nextBoolean();
            this.combatTicks = 0;
        }

        // Move backward if the target is within 5 blocks
        if (distance < this.closeRangeThreshold * this.closeRangeThreshold) {
            this.movingBackward = true;
        } else if (distance > (double) (this.range * 0.75F)) {
            this.movingBackward = false;
        }

        // Strafing and climbing logic
        double strafeX = this.movingBackward ? -0.5 : 0.5;
        double strafeZ = this.strafingLeft ? 0.5 : -0.5;

        // Pathfinding for climbing
        BlockPos targetPos = this.target.getBlockPos();
        BlockPos luminaryPos = this.luminary.getBlockPos();
        boolean shouldClimb = luminaryPos.getY() < targetPos.getY(); // Target is higher up

        if (shouldClimb) {
            this.luminary.getNavigation().startMovingTo(
                    targetPos.getX() + strafeX,
                    targetPos.getY(),
                    targetPos.getZ() + strafeZ,
                    this.speed
            );
        } else {
            this.luminary.getMoveControl().strafeTo(
                    (float) strafeX,
                    (float) strafeZ
            );
        }

        // Keep the luminary focused on the target
        this.luminary.lookAtEntity(this.target, 30.0F, 30.0F);

        this.combatTicks++;
    }
}
