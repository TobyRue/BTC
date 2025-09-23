package io.github.tobyrue.btc.player_data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpellPersistentState extends PersistentState {
    private final Map<UUID, PlayerSpellData> playerSpellMap = new HashMap<>();

    public static final Codec<SpellPersistentState> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, PlayerSpellDataCodec.INSTANCE).fieldOf("players").forGetter(state -> {
                        Map<String, PlayerSpellData> map = new HashMap<>();
                        state.playerSpellMap.forEach((uuid, data) -> map.put(uuid.toString(), data));
                        return map;
                    })
            ).apply(instance, map -> {
                SpellPersistentState state = new SpellPersistentState();
                map.forEach((strUuid, data) -> state.playerSpellMap.put(UUID.fromString(strUuid), data));
                return state;
            })
    );


    private static class PlayerSpellDataCodec {
        public static final Codec<PlayerSpellData> INSTANCE = RecordCodecBuilder.create(instance ->
                instance.group(
                        Spell.InstancedSpell.CODEC.listOf().fieldOf("knownSpells").forGetter(d -> d.knownSpells),
                        Spell.InstancedSpell.CODEC.listOf().fieldOf("favoriteSpells").forGetter(d -> d.favoriteSpells)
                ).apply(instance, (known, fav) -> {
                    PlayerSpellData data = new PlayerSpellData();
                    data.knownSpells = new ArrayList<>(known);
                    data.favoriteSpells = new ArrayList<>(fav);
                    return data;
                })
        );
    }
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound playersTag = new NbtCompound();
        playerSpellMap.forEach((uuid, data) -> {
            NbtCompound playerTag = new NbtCompound();
            playerTag.put("knownSpells", Spell.InstancedSpell.CODEC.listOf().encodeStart(NbtOps.INSTANCE, data.knownSpells).result().orElse(new NbtCompound()));
            playerTag.put("favoriteSpells", Spell.InstancedSpell.CODEC.listOf().encodeStart(NbtOps.INSTANCE, data.favoriteSpells).result().orElse(new NbtCompound()));
            playersTag.put(uuid.toString(), playerTag);
        });
        nbt.put("players", playersTag);
        return nbt;
    }

    public static SpellPersistentState createFromNbt(NbtCompound nbt, net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        SpellPersistentState state = new SpellPersistentState();
        NbtCompound playersTag = nbt.getCompound("players");
        for (String key : playersTag.getKeys()) {
            UUID uuid = UUID.fromString(key);
            NbtCompound playerTag = playersTag.getCompound(key);
            var known = Spell.InstancedSpell.CODEC.listOf().parse(net.minecraft.nbt.NbtOps.INSTANCE, playerTag.get("knownSpells")).resultOrPartial(System.err::println).orElse(new ArrayList<>());
            var fav = Spell.InstancedSpell.CODEC.listOf().parse(net.minecraft.nbt.NbtOps.INSTANCE, playerTag.get("favoriteSpells")).resultOrPartial(System.err::println).orElse(new ArrayList<>());
            PlayerSpellData data = new PlayerSpellData();
            data.knownSpells = new ArrayList<>(known);
            data.favoriteSpells = new ArrayList<>(fav.subList(0, 12));

            while (data.favoriteSpells.size() < 12) {
                data.favoriteSpells.add(new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty()));
            }
            if (data.favoriteSpells.size() > 12) {
                data.favoriteSpells = new ArrayList<>(data.favoriteSpells.subList(0, 12));
            }

            state.playerSpellMap.put(uuid, data);
        }
        return state;
    }
    public static SpellPersistentState createNew() {
        return new SpellPersistentState();
    }
    private static final PersistentState.Type<SpellPersistentState> TYPE =
            new PersistentState.Type<>(
                    SpellPersistentState::createNew,
                    SpellPersistentState::createFromNbt,
                    null
            );

    public static SpellPersistentState get(MinecraftServer server) {
        if (server == null) {
            throw new IllegalStateException("Cannot get SpellPersistentState: server is null!");
        }

        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
        if (serverWorld == null) {
            throw new IllegalStateException("Cannot get SpellPersistentState: overworld is null!");
        }

        SpellPersistentState state = serverWorld.getPersistentStateManager().getOrCreate(TYPE, "spell_state");
        state.markDirty();
        return state;
    }


    public PlayerSpellData getPlayerData(LivingEntity player) {
        return playerSpellMap.computeIfAbsent(player.getUuid(), uuid -> new PlayerSpellData());
    }
}

