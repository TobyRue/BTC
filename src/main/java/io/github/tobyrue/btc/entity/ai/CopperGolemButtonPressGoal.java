package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

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
            lookAtPosition(targetButtonPos);
        }
    }

    @Override
    public boolean shouldContinue() {
        if (this.golem.cantMove() || this.golem.getOxidation() == CopperGolemEntity.Oxidation.OXIDIZED) {
            return false;
        }  else {
            return targetButtonPos != null;
        }
    }



    @Override
    public void tick() {
        if (targetButtonPos != null) {
            lookAtPosition(targetButtonPos);
            if (golem.squaredDistanceTo(Vec3d.ofCenter(targetButtonPos)) <= 2.5) {
                BlockState state = golem.getWorld().getBlockState(targetButtonPos);
                if ((state.getBlock() instanceof ButtonBlock button) && pressCooldown <= 0) {
                    golem.setCanMoveDelayTwo(false);
                    button.powerOn(state, golem.getWorld(), targetButtonPos, null); // Correctly activating the button
                    golem.swingHand(Hand.MAIN_HAND);
                    if (golem.getOxidation() == CopperGolemEntity.Oxidation.UNOXIDIZED) {
                        pressCooldown = 120;
                    } else if (golem.getOxidation() == CopperGolemEntity.Oxidation.EXPOSED) {
                        pressCooldown = 160;
                    } else if (golem.getOxidation() == CopperGolemEntity.Oxidation.WEATHERED) {
                        pressCooldown = 200;
                    }
                    if (targetButtonPos != null && targetButtonPos.getY() == golem.getBlockPos().getY() + 1) {
                        golem.setButtonDirection(CopperGolemEntity.ButtonDirection.UP);
                    } else if (targetButtonPos != null && targetButtonPos.getY() < golem.getBlockPos().getY()) {
                        golem.setButtonDirection(CopperGolemEntity.ButtonDirection.DOWN);
                    } else if (targetButtonPos != null && targetButtonPos.getY() == golem.getBlockPos().getY()) {
                        golem.setButtonDirection(CopperGolemEntity.ButtonDirection.FRONT);
                    }
                    if (pressCooldown > 0) {
                        pressCooldown--;
                    }
                    targetButtonPos = null;
                }
            }

            else {
                System.out.println("Moving towards button at " + targetButtonPos);
                golem.getNavigation().startMovingTo(targetButtonPos.getX(), targetButtonPos.getY(), targetButtonPos.getZ(), speed * this.golem.getSpeedMultiplier());
            }
        }
    }
    public void lookAtPosition(BlockPos pos) {
        golem.getLookControl().lookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, (float)(this.golem.getMaxHeadRotation() + 20), (float)this.golem.getMaxLookPitchChange());
    }
}


