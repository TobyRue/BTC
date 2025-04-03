package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.FluidTags;

import java.util.EnumSet;

public class CopperSwimGoal extends Goal {
        private final CopperGolemEntity mob;

        public CopperSwimGoal(CopperGolemEntity mob) {
            this.mob = mob;
            this.setControls(EnumSet.of(Control.JUMP));
            mob.getNavigation().setCanSwim(true);
        }

        public boolean canStart() {
            if (mob.getOxidation() != CopperGolemEntity.Oxidation.OXIDIZED) {
                return this.mob.isTouchingWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getSwimHeight() || this.mob.isInLava();
            } else {
                return false;
            }
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

    @Override
    public boolean shouldContinue() {
        if (mob.getOxidation() != CopperGolemEntity.Oxidation.OXIDIZED) {
            return this.mob.isTouchingWater() && this.mob.getFluidHeight(FluidTags.WATER) > this.mob.getSwimHeight() || this.mob.isInLava();
        } else {
            return false;
        }
    }

    public void tick() {
        if (this.mob.getRandom().nextFloat() < 0.8F) {
            this.mob.getJumpControl().setActive();
        }
    }
}
