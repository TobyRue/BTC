package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent COPPER_STEP = registerSoundEvent("copper_step");
    public static final SoundEvent COPPER_HEAD_SPIN = registerSoundEvent("copper_head_spin");
    public static final SoundEvent COPPER_ARM_MOVE = registerSoundEvent("copper_arm_move");
    public static final SoundEvent COPPER_AMBIENT = registerSoundEvent("copper_ambient");
    public static final SoundEvent COPPER_DEATH = registerSoundEvent("copper_death");
    public static final SoundEvent COPPER_HURT = registerSoundEvent("copper_hurt");


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(BTC.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void initialize() {
    }
}
