package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent COPPER_STEP = registerSoundEvent("copper_step");
    public static final SoundEvent COPPER_HEAD_SPIN = registerSoundEvent("copper_head_spin");
    public static final SoundEvent COPPER_ARM_MOVE = registerSoundEvent("copper_arm_move");
    public static final SoundEvent COPPER_AMBIENT = registerSoundEvent("copper_ambient");
    public static final SoundEvent COPPER_DEATH = registerSoundEvent("copper_death");
    public static final SoundEvent COPPER_HURT = registerSoundEvent("copper_hurt");

    public static final SoundEvent MUSIC_DISC_CRYSTAL_FOREST = registerSoundEvent("crystal_forest");
    public static final RegistryKey<JukeboxSong> CRYSTAL_FOREST_KEY = RegistryKey.of(RegistryKeys.JUKEBOX_SONG, BTC.identifierOf("crystal_forest"));


    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(BTC.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    private static RegistryEntry.Reference<SoundEvent> registerReference(String id) {
        return Registry.registerReference(Registries.SOUND_EVENT, BTC.identifierOf(id), SoundEvent.of(BTC.identifierOf(id)));
    }

    public static void initialize() {
    }
}
