package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.CopperButtonBlock;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.regestries.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class CopperGolemButtonPressGoal extends Goal {
    private final CopperGolemEntity golem;
    private final double speed;
    private BlockPos targetButtonPos;
    private int pressCooldown;
    private int interest = 100;
    private Vec3d lastPosition;


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
            if (state.isIn(BTC.BUTTONS) && !state.get(ButtonBlock.POWERED)) {
                targetButtonPos = pos;
                lastPosition = golem.getPos();
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
            lastPosition = golem.getPos();
        }
    }

    @Override
    public boolean shouldContinue() {
        if (targetButtonPos == null) {
            return false;
        }

        BlockState state = golem.getWorld().getBlockState(targetButtonPos);

        if (this.golem.cantMove() || this.golem.getOxidation() == CopperGolemEntity.Oxidation.OXIDIZED || interest <= 0) {
            return false;
        } else if (state.getBlock() instanceof CopperButtonBlock && !state.get(ButtonBlock.POWERED)) {
            return true;
        }
        return false;
    }



    @Override
    public void tick() {
//        if (soundCooldown > 0) {
//            soundCooldown--;
//        }
        if (targetButtonPos != null) {
            lookAtPosition(targetButtonPos);
            Vec3d currentPos = golem.getPos();
            double distanceMoved = currentPos.distanceTo(lastPosition);
            if (distanceMoved < 0.1) {
                interest--;
            } else {
                if (interest >= 0) {
                    interest = Math.min(100, interest + 2); // Slightly regain interest when moving
                }
            }

//            if (interest <= 0) {
//                targetCooldown--;
//            }
            lastPosition = currentPos;
            if (interest > 0) {
                if (golem.squaredDistanceTo(Vec3d.ofCenter(targetButtonPos)) <= 2.5) {
                    BlockState state = golem.getWorld().getBlockState(targetButtonPos);
                    if ((state.getBlock() instanceof CopperButtonBlock button) && pressCooldown <= 0) {
                        button.powerOn(state, golem.getWorld(), targetButtonPos, null);
                        golem.setCanMoveDelayTwo(false);

                        interest = 100;
                        switch (golem.getOxidation()) {
                            case UNOXIDIZED -> pressCooldown = 120;
                            case EXPOSED -> pressCooldown = 160;
                            case WEATHERED -> pressCooldown = 200;
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

                        golem.getWorld().playSound(golem, golem.getBlockPos(), ModSounds.COPPER_ARM_MOVE, SoundCategory.NEUTRAL, 0.7f, 1f);
                    }
                } else {
                    golem.getNavigation().startMovingTo(targetButtonPos.getX(), targetButtonPos.getY(), targetButtonPos.getZ(), speed * this.golem.getSpeedMultiplier());
                }
            } else {
                golem.navigateAround(16, 7);
                if (interest <= -100) {
                    interest = 100;
                }
            }
        }
    }

    public void lookAtPosition(BlockPos pos) {
        golem.getLookControl().lookAt(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, (float)(this.golem.getMaxHeadRotation() + 20), (float)this.golem.getMaxLookPitchChange());
    }
}


