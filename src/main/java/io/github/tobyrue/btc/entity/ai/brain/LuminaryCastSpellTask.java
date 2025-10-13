//package io.github.tobyrue.btc.entity.ai.brain;
//
//import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
//import io.github.tobyrue.btc.regestries.ModSpells;
//import io.github.tobyrue.btc.spell.Spell;
//import io.github.tobyrue.btc.spell.SpellDataStore;
//import io.github.tobyrue.btc.spell.SpellContext;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.ai.brain.MemoryModuleType;
//import net.minecraft.entity.ai.brain.task.MultiTickTask;
//import net.minecraft.server.world.ServerWorld;
//import net.minecraft.util.math.Vec3d;
//
//import java.util.Optional;
//
//public class LuminaryCastSpellTask extends MultiTickTask<EldritchLuminaryEntity> {
//    private final float speed;
//
//    public LuminaryCastSpellTask(float speed) {
//        super();
//        this.speed = speed;
//    }
//
//    @Override
//    protected boolean shouldRun(ServerWorld world, EldritchLuminaryEntity luminary) {
//        Optional<LivingEntity> target = luminary.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
//        return target.isPresent() && luminary.getSpellCooldown() <= 0;
//    }
//
//    @Override
//    protected void run(ServerWorld world, EldritchLuminaryEntity luminary, long time) {
//        Optional<LivingEntity> target = luminary.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
//        target.ifPresent(t -> {
//            // Get position and direction
//            Vec3d pos = luminary.getPos().add(0, luminary.getStandingEyeHeight(), 0);
//            Vec3d dir = t.getPos().add(0, t.getStandingEyeHeight() / 2, 0).subtract(pos).normalize();
//
//            // Create spell context
//            SpellDataStore data = new SpellDataStore(); // create an empty or default data store
//            Spell.SpellContext ctx = new Spell.SpellContext(world, pos, dir, data, luminary);
//
//            // Pick a spell — you could randomize this, or base it on luminary phase
//            Spell spell = ModSpells.FIREBALL; // e.g., always fireball for now
//
//            // Use the spell
//            spell.use(ctx);
//
//            // Cooldown to avoid spamming
//            luminary.setSpellCooldown(100);
//        });
//    }
//
//    @Override
//    protected boolean shouldKeepRunning(ServerWorld world, EldritchLuminaryEntity luminary, long time) {
//        return false; // only run once per trigger
//    }
//}
