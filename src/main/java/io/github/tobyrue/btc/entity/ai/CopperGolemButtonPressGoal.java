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
    private Queue<BlockPos> buttonQueue = new LinkedList<>();  // Queue to store button positions
    private BlockPos currentButtonPos;  // The current button to press
    private int pressCooldown = 0;  // Cooldown to prevent immediate reactivation
    private final double speed;

    public CopperGolemButtonPressGoal(CopperGolemEntity golem, double speed) {
        this.golem = golem;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        System.out.println("Checking if Copper Golem can start button press goal...");

        // If the golem can't move or is oxidized, return false
        if (this.golem.cantMove()) {
            System.out.println("Golem can't move, aborting goal.");
            return false;
        }
        if (this.golem.getOxidation() == CopperGolemEntity.Oxidation.OXIDIZED) {
            System.out.println("Golem is oxidized, aborting goal.");
            return false;
        }
        if (pressCooldown > 0) {
            pressCooldown--;
            System.out.println("Cooldown active: " + pressCooldown + " ticks remaining.");
            return false;
        }

        // Add nearby unpressed buttons to the queue
        if (buttonQueue.isEmpty()) {
            BlockPos golemPos = golem.getBlockPos();
            for (BlockPos pos : BlockPos.iterateOutwards(golemPos, 8, 8, 8)) {
                BlockState state = golem.getWorld().getBlockState(pos);
                if (state.getBlock() instanceof ButtonBlock && !state.get(ButtonBlock.POWERED)) {
                    buttonQueue.add(pos);  // Add position to the queue
                    System.out.println("Added button at " + pos + " to the queue.");
                }
            }
        }

        boolean canStart = !buttonQueue.isEmpty();
        System.out.println("Can start goal: " + canStart);
        return canStart;  // Only start if there are buttons to press
    }

    @Override
    public void start() {
        System.out.println("Goal started. Moving Copper Golem to press the next button.");

        // Get the next button from the queue
        if (!buttonQueue.isEmpty()) {
            currentButtonPos = buttonQueue.poll();
            System.out.println("Moving to button at " + currentButtonPos);
            golem.getNavigation().startMovingTo(
                    currentButtonPos.getX() + 0.5, currentButtonPos.getY(), currentButtonPos.getZ() + 0.5, 1.0
            );
        }
    }

    @Override
    public boolean shouldContinue() {
        System.out.println("Checking if Copper Golem should continue goal...");

        if (this.golem.cantMove() || this.golem.getOxidation() == CopperGolemEntity.Oxidation.OXIDIZED) {
            System.out.println("Golem can't move or is oxidized. Goal will not continue.");
            return false;
        } else {
            boolean shouldContinue = currentButtonPos != null && golem.squaredDistanceTo(Vec3d.ofCenter(currentButtonPos)) > 1.5;
            System.out.println("Should continue: " + shouldContinue);
            return shouldContinue;
        }
    }

    @Override
    public void tick() {
        if (currentButtonPos != null) {
            double distanceSquared = golem.squaredDistanceTo(Vec3d.ofCenter(currentButtonPos));
            System.out.println("Current distance to button: " + distanceSquared);

            if (distanceSquared <= 1.5) {
                BlockState state = golem.getWorld().getBlockState(currentButtonPos);
                if (state.getBlock() instanceof ButtonBlock button) {
                    System.out.println("Pressing button at " + currentButtonPos);
                    button.powerOn(state, golem.getWorld(), currentButtonPos, null); // Simulate the button press
                    golem.swingHand(Hand.MAIN_HAND); // Swing hand to simulate interaction
                    pressCooldown = 100; // Cooldown before the next press (100 ticks)
                    System.out.println("Cooldown set to " + pressCooldown);
                    currentButtonPos = null;  // Reset current button to allow golem to find the next
                }
            } else {
                System.out.println("Moving towards button at " + currentButtonPos);
                // Only move toward the button if we haven't reached it yet
                golem.getNavigation().startMovingTo(currentButtonPos.getX(), currentButtonPos.getY(), currentButtonPos.getZ(), speed);
            }
        }
    }
}

