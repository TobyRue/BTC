//package io.github.tobyrue.btc.entity.ai.brain;
//
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableSet;
//import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
//import net.minecraft.entity.ai.brain.Activity;
//import net.minecraft.entity.ai.brain.Brain;
//import net.minecraft.entity.ai.brain.MemoryModuleType;
//import net.minecraft.entity.ai.brain.sensor.Sensor;
//import net.minecraft.entity.ai.brain.sensor.SensorType;
//import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
//import net.minecraft.entity.ai.brain.task.WanderAroundTask;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.math.GlobalPos;
//
//import java.util.Optional;
//
//public class LuminaryBrain {
//    public static Brain<?> create(EldritchLuminaryEntity luminary, Brain<EldritchLuminaryEntity> brain) {
//        // Register the memory modules and sensors this entity can use
//        brain = brain.copyWithActivities(
//                ImmutableList.of(Activity.CORE, Activity.IDLE, Activity.FIGHT),
//                Activity.IDLE
//        );
//
//        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
//
//        brain.setTaskList(
//                Activity.CORE,
//                0,
//                ImmutableList.of(
//                        new LookAtTargetTask(45, 90),
//                        new WanderAroundTask()
//                )
//        );
//
//        brain.setTaskList(
//                Activity.IDLE,
//                ImmutableList.of(
//                        new WanderAroundTask()
//                )
//        );
//
//        brain.setTaskList(
//                Activity.FIGHT,
//                ImmutableList.of(
//                        new LuminaryCastSpellTask(1.0f)
//                )
//        );
//
//        brain.setDefaultActivity(Activity.IDLE);
//        brain.switchTo(Activity.IDLE);
//        return brain;
//    }
//
//    public static void tick(ServerWorld world, EldritchLuminaryEntity luminary) {
//        Brain<EldritchLuminaryEntity> brain = luminary.getBrain();
//
//        brain.tick(world, luminary);
//        Optional<PlayerEntity> player = brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET)
//                .filter(p -> p.isAlive());
//
//        if (player.isPresent()) {
//            luminary.lookAtEntity(player.get(), 90.0F, 90.0F);
//        }
//    }
//}
