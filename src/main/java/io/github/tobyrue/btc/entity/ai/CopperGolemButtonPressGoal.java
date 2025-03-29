package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.golem.cantMove()) {
            return false;
        }
        if (this.golem.getOxidation() == CopperGolemEntity.Oxidation.OXIDIZED) {
            return false;
        }
        if (pressCooldown > 0) {
            pressCooldown--;
            return false;
        }

        BlockPos golemPos = golem.getBlockPos();
        for (BlockPos pos : BlockPos.iterateOutwards(golemPos, 16, 16, 16)) {
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
            golem.getNavigation().startMovingTo(targetButtonPos.getX(), targetButtonPos.getY(), targetButtonPos.getZ(), speed * this.golem.getSpeedMultiplier());
            lookAtPosition();
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.golem.cantMove() || this.golem.getOxidation() == CopperGolemEntity.Oxidation.OXIDIZED) {
            return false;
        }  else {
            return targetButtonPos != null;
//                    && golem.squaredDistanceTo(Vec3d.ofCenter(targetButtonPos)) > 1.5;
        }
    }

    @Override
    public void tick() {
        if (targetButtonPos != null) {
            lookAtPosition();

            if (golem.squaredDistanceTo(Vec3d.ofCenter(targetButtonPos)) <= 2) {
                BlockState state = golem.getWorld().getBlockState(targetButtonPos);
                if ((state.getBlock() instanceof ButtonBlock button) && pressCooldown <= 0) {
                    golem.setIfFirstSpawned(false);
                    button.powerOn(state, golem.getWorld(), targetButtonPos, null); // Correctly activating the button
                    golem.swingHand(Hand.MAIN_HAND);
                    pressCooldown = 180; // Prevent immediate reactivation
                    targetButtonPos = null;
                    golem.setButtonDirection(CopperGolemEntity.ButtonDirection.FRONT);
//                    if (targetButtonPos.getY() == golem.getPos().getY() + 1) {
//                        golem.setButtonDirection(CopperGolemEntity.ButtonDirection.UP);
//                    } else {
//                        golem.setButtonDirection(CopperGolemEntity.ButtonDirection.FRONT);
//                    }
                }
            }

            else {
                System.out.println("Moving towards button at " + targetButtonPos);
                golem.getNavigation().startMovingTo(targetButtonPos.getX(), targetButtonPos.getY(), targetButtonPos.getZ(), speed * this.golem.getSpeedMultiplier());
            }
        }
    }
    public void lookAtPosition() {
        golem.getLookControl().lookAt(targetButtonPos.getX() + 0.5, targetButtonPos.getY() + 0.5, targetButtonPos.getZ() + 0.5, (float)(this.golem.getMaxHeadRotation() + 20), (float)this.golem.getMaxLookPitchChange());
    }
}


