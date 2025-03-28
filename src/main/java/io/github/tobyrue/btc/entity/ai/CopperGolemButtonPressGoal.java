package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;

public class CopperGolemButtonPressGoal extends Goal {
    private final CopperGolemEntity golem;
    private final double speed;
    private BlockPos targetButtonPos;
    private int pressCooldown;

    public CopperGolemButtonPressGoal(CopperGolemEntity golem, double speed) {
        this.golem = golem;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (pressCooldown > 0) {
            pressCooldown--;
            return false;
        }

        BlockPos golemPos = golem.getBlockPos();
        for (BlockPos pos : BlockPos.iterateOutwards(golemPos, 2, 2, 2)) {
            BlockState state = golem.getWorld().getBlockState(pos);
            if (state.getBlock() instanceof ButtonBlock && !state.get(ButtonBlock.POWERED)) {
                targetButtonPos = pos;
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (targetButtonPos != null) {
            golem.getNavigation().startMovingTo(
                    targetButtonPos.getX() + 0.5, targetButtonPos.getY(), targetButtonPos.getZ() + 0.5, 1.0
            );
        }
    }

    @Override
    public boolean shouldContinue() {
        return targetButtonPos != null && golem.squaredDistanceTo(Vec3d.ofCenter(targetButtonPos)) > 1.5;
    }

    @Override
    public void tick() {
        if (targetButtonPos != null) {
//            this.golem.getLookControl().lookAt(this.targetButtonPos, (float)(this.golem.getMaxHeadRotation() + 20), (float)this.golem.getMaxLookPitchChange());
            if (golem.squaredDistanceTo(Vec3d.ofCenter(targetButtonPos)) <= 2.5) {
                BlockState state = golem.getWorld().getBlockState(targetButtonPos);
                if (state.getBlock() instanceof ButtonBlock button) {
                    button.powerOn(state, golem.getWorld(), targetButtonPos, null); // Correctly activating the button
                    golem.swingHand(Hand.MAIN_HAND);
                    pressCooldown = 100; // Prevent immediate reactivation
                    targetButtonPos = null;
                }
            } else {
                System.out.println("Moving towards button at " + targetButtonPos);
                // Only move toward the button if we haven't reached it yet
                golem.getNavigation().startMovingTo(targetButtonPos.getX(), targetButtonPos.getY(), targetButtonPos.getZ(), speed * this.golem.getSpeedMultiplier());
            }
        }
    }
}


