package io.github.tobyrue.btc.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class WeightedActiveTargetGoal extends TrackTargetGoal {
    private final Map<Class<? extends LivingEntity>, Integer> classWeights;
    private final Predicate<LivingEntity> overridePredicate;
    private final Predicate<LivingEntity> mustPredicate;
    private LivingEntity target;

    public WeightedActiveTargetGoal(MobEntity mob, Map<Class<? extends LivingEntity>, Integer> classWeights, Predicate<LivingEntity> overridePredicate, Predicate<LivingEntity> mustPredicate) {
        super(mob, true, true);
        this.classWeights = classWeights;
        this.overridePredicate = overridePredicate;
        this.mustPredicate = mustPredicate;
    }

    @Override
    public boolean canStart() {

        Box searchBox = this.mob.getBoundingBox().expand(this.getFollowRange(), 4.0, this.getFollowRange());
        List<LivingEntity> candidates = this.mob.getWorld().getEntitiesByClass(
                LivingEntity.class,
                searchBox,
                entity -> {
                    if (this.mustPredicate != null) {
                        return this.mustPredicate.test(entity);
                    }
                    return false;
                }
        );

        if (candidates.isEmpty()) {
            return false;
        }

        if (this.overridePredicate != null) {
            for (LivingEntity entity : candidates) {
                if (this.overridePredicate.test(entity)) {
                    this.target = entity;
                    return true;
                }
            }
        }

        List<LivingEntity> validCandidates = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();
        int totalWeight = 0;

        for (LivingEntity entity : candidates) {
            for (Map.Entry<Class<? extends LivingEntity>, Integer> entry : this.classWeights.entrySet()) {
                if (entry.getKey().isInstance(entity)) {
                    validCandidates.add(entity);
                    int weight = entry.getValue();
                    weights.add(weight);
                    totalWeight += weight;
                    break;
                }
            }
        }

        if (validCandidates.isEmpty() || totalWeight <= 0) {
            return false;
        }

        int randomWeight = this.mob.getRandom().nextInt(totalWeight);
        int currentWeightSum = 0;
        for (int i = 0; i < validCandidates.size(); i++) {
            currentWeightSum += weights.get(i);
            if (randomWeight < currentWeightSum) {
                this.target = validCandidates.get(i);
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }
}