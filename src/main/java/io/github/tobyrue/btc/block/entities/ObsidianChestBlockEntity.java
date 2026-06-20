package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.SpellDataStore;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class ObsidianChestBlockEntity extends BlockEntity implements LidOpenable, BlockEntityTicker<ObsidianChestBlockEntity> {
    private final ChestLidAnimator lidAnimator = new ChestLidAnimator();
    private Identifier lootTableId = null;
    private final Map<UUID, SimpleInventory> playerInventories = new HashMap<>();
    private final Set<UUID> lootedPlayers = new HashSet<>();

    private final ViewerCountManager stateManager = new ViewerCountManager() {
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
        }

        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
        }

        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            world.addSyncedBlockEvent(pos, state.getBlock(), 1, newViewerCount);
        }

        protected boolean isPlayerViewing(PlayerEntity player) {
            return player.currentScreenHandler instanceof GenericContainerScreenHandler;
        }
    };

    public ObsidianChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OBSIDIAN_CHEST_BLOCK_ENTITY, pos, state);
    }

    public void onOpen(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public void onClose(PlayerEntity player) {
        if (!this.removed && !player.isSpectator()) {
            this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
        }
    }
    public SimpleInventory getInventoryForPlayer(UUID uuid) {
        return playerInventories.computeIfAbsent(uuid, k -> {
            SimpleInventory inv = new SimpleInventory(27);
            inv.addListener(sender -> this.markDirty());
            return inv;
        });
    }
    public boolean hasPlayerLooted(UUID uuid) {
        return lootedPlayers.contains(uuid);
    }

    public void markPlayerLooted(UUID uuid) {
        lootedPlayers.add(uuid);
        this.markDirty();
        if (this.world != null && !this.world.isClient) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.lidAnimator.setOpen(data > 0);
            return true;
        }

        if (type == 2) {
            if (this.world != null && this.world.isClient()) {
                if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT) {
                    addLootedPlayers();
                }
            }
            return true;
        }

        return super.onSyncedBlockEvent(type, data);
    }

    @Environment(EnvType.CLIENT)
    private void addLootedPlayers() {
        PlayerEntity localPlayer = MinecraftClient.getInstance().player;
        if (localPlayer != null && !lootedPlayers.contains(localPlayer.getUuid())) {
            this.lootedPlayers.add(localPlayer.getUuid());
        }
    }


    public void tick(World world, BlockPos pos, BlockState state, ObsidianChestBlockEntity blockEntity) {
        this.stateManager.updateViewerCount(world, pos, state);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, ObsidianChestBlockEntity blockEntity) {
        blockEntity.lidAnimator.step();
    }

    public float getAnimationProgress(float tickDelta) {
        return this.lidAnimator.getProgress(tickDelta);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        NbtCompound invsNbt = new NbtCompound();
        playerInventories.forEach((uuid, inv) -> {
            invsNbt.put(uuid.toString(), inv.toNbtList(registryLookup));
        });
        nbt.put("PlayerInventories", invsNbt);

        NbtList lootedList = new NbtList();
        for (UUID uuid : lootedPlayers) {
            lootedList.add(net.minecraft.nbt.NbtString.of(uuid.toString()));
        }
        nbt.put("LootedPlayers", lootedList);

        if (this.lootTableId != null) {
            nbt.putString("LootTable", this.lootTableId.toString());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        if (nbt.contains("PlayerInventories", NbtElement.COMPOUND_TYPE)) {
            NbtCompound invsNbt = nbt.getCompound("PlayerInventories");
            for (String key : invsNbt.getKeys()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    SimpleInventory inv = getInventoryForPlayer(uuid);
                    inv.readNbtList(invsNbt.getList(key, NbtElement.COMPOUND_TYPE), registryLookup);
                } catch (IllegalArgumentException ignored) {}
            }
        }

        if (nbt.contains("LootedPlayers", NbtElement.LIST_TYPE)) {
            NbtList lootedList = nbt.getList("LootedPlayers", NbtElement.STRING_TYPE);
            for (int i = 0; i < lootedList.size(); i++) {
                try {
                    lootedPlayers.add(UUID.fromString(lootedList.getString(i)));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        if (nbt.contains("LootTable", NbtElement.STRING_TYPE)) {
            this.lootTableId = Identifier.of(nbt.getString("LootTable"));
        }
    }

    public Identifier getLootTableId() { return lootTableId; }
}