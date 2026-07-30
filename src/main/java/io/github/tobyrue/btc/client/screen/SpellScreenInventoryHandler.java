package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.item.UnlockScrollItem;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModScreens;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.util.SpellScrollHelper;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class SpellScreenInventoryHandler extends ScreenHandler {

    public static final int FAVORITE_SLOTS_COUNT = 6;
    public static final int GRID_SLOTS_COUNT = 36;
    public static final int TOTAL_SPELL_SLOTS = FAVORITE_SLOTS_COUNT + GRID_SLOTS_COUNT;

    private final Inventory spellInventory = new SimpleInventory(TOTAL_SPELL_SLOTS);
    private final PlayerEntity player;
    private boolean isUpdatingInventory = false;

    public SpellScreenInventoryHandler(int syncId, PlayerInventory playerInventory) {
        super(ModScreens.SPELL_SCREEN_INVENTORY_HANDLER, syncId);
        this.player = playerInventory.player;

        for (int i = 0; i < FAVORITE_SLOTS_COUNT; i++) {
            this.addSlot(new FavoriteSpellSlot(spellInventory, i, 35 + i * 18, 19, i));
        }

        for (int i = 0; i < GRID_SLOTS_COUNT; i++) {
            int col = i % 9;
            int row = i / 9;
            int slotIdx = FAVORITE_SLOTS_COUNT + i;
            this.addSlot(new StandardSpellSlot(spellInventory, slotIdx, 8 + col * 18, 55 + row * 18));
        }

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 198));
        }

        populateSpellsFromData();
    }

    public void populateSpellsFromData() {
        if (player.getWorld().isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) return;

        this.isUpdatingInventory = true;
        this.spellInventory.clear();

        SpellPersistentState state = SpellPersistentState.get(serverPlayer.getServer());
        PlayerSpellData data = state.getPlayerData(serverPlayer);

        if (data != null) {
            if (data.favoriteSpells != null) {
                for (int i = 0; i < FAVORITE_SLOTS_COUNT && i < data.favoriteSpells.size(); i++) {
                    Spell.InstancedSpell fav = data.favoriteSpells.get(i);
                    if (fav != null && fav.spell() != ModSpells.EMPTY) {
                        this.spellInventory.setStack(i, SpellScrollHelper.createScrollFromSpell(fav, null, 0));
                    }
                }
            }

            if (data.knownSpells != null) {
                List<ItemStack> scrollList = SpellScrollHelper.parseKnownSpellsToScrolls(data);
                int gridIndex = 0;
                for (ItemStack scroll : scrollList) {
                    if (isStackInFavorites(data, scroll)) continue;

                    if (gridIndex < GRID_SLOTS_COUNT) {
                        this.spellInventory.setStack(FAVORITE_SLOTS_COUNT + gridIndex, scroll);
                        gridIndex++;
                    }
                }
            }
        }

        this.isUpdatingInventory = false;
        this.sendContentUpdates();
    }

    private boolean isStackInFavorites(PlayerSpellData data, ItemStack scroll) {
        UnlockSpellComponent comp = scroll.get(ModComponents.UNLOCK_SPELL_COMPONENT);
        if (comp == null || data.favoriteSpells == null) return false;

        GrabBag args = GrabBag.fromNBT(comp.argsAsNbt());
        return data.favoriteSpells.stream().anyMatch(fav ->
                fav != null && fav.spell() != ModSpells.EMPTY &&
                        ModRegistries.SPELL.getId(fav.spell()).equals(comp.id()) &&
                        fav.args().equalsOther(args)
        );
    }

    private void tryLearnSpell(ServerPlayerEntity serverPlayer, ItemStack scrollStack, boolean isFavorite, int favIndex) {
        UnlockSpellComponent component = scrollStack.get(ModComponents.UNLOCK_SPELL_COMPONENT);
        if (component == null) return;

        Identifier spellId = component.id();
        Spell registeredSpell = ModRegistries.SPELL.get(spellId);
        if (registeredSpell == null) return;

        GrabBag currentArgs = GrabBag.fromNBT(component.argsAsNbt());
        Spell.InstancedSpell spellInst = new Spell.InstancedSpell(registeredSpell, currentArgs);

        SpellPersistentState spellState = SpellPersistentState.get(serverPlayer.getServer());
        PlayerSpellData playerData = spellState.getPlayerData(serverPlayer);

        if (playerData != null) {
            boolean alreadyKnown = playerData.knownSpells.stream()
                    .anyMatch(inst -> inst.spell() == spellInst.spell() && inst.args().equalsOther(currentArgs));

            if (!alreadyKnown) {
                component.advancement().ifPresent(av -> {
                    AdvancementEntry advancement = serverPlayer.server.getAdvancementLoader().get(av);
                    if (advancement != null) {
                        serverPlayer.getAdvancementTracker().grantCriterion(advancement, "unlock");
                    }
                });
                playerData.knownSpells.add(spellInst);
            }

            if (isFavorite && favIndex >= 0 && favIndex < FAVORITE_SLOTS_COUNT) {
                while (playerData.favoriteSpells.size() <= favIndex) {
                    playerData.favoriteSpells.add(new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty()));
                }
                playerData.favoriteSpells.set(favIndex, spellInst);
            }

            spellState.markDirty();
        }
    }

    private void removeSpellFromPlayerData(ServerPlayerEntity serverPlayer, ItemStack scrollStack, boolean isFavorite, int favIndex) {
        UnlockSpellComponent component = scrollStack.get(ModComponents.UNLOCK_SPELL_COMPONENT);
        if (component == null) return;

        Spell registeredSpell = ModRegistries.SPELL.get(component.id());
        if (registeredSpell == null) return;

        GrabBag args = GrabBag.fromNBT(component.argsAsNbt());

        SpellPersistentState spellState = SpellPersistentState.get(serverPlayer.getServer());
        PlayerSpellData playerData = spellState.getPlayerData(serverPlayer);

        if (playerData != null) {
            if (playerData.knownSpells != null) {
                playerData.knownSpells.removeIf(inst -> inst.spell() == registeredSpell && inst.args().equalsOther(args));
            }

            if (isFavorite && favIndex >= 0 && favIndex < playerData.favoriteSpells.size()) {
                playerData.favoriteSpells.set(favIndex, new Spell.InstancedSpell(ModSpells.EMPTY, GrabBag.empty()));
            }

            spellState.markDirty();
        }
    }

    private boolean isDuplicateInInventory(ItemStack stack, int currentSlotIndex) {
        UnlockSpellComponent incomingComponent = stack.get(ModComponents.UNLOCK_SPELL_COMPONENT);
        if (incomingComponent == null) return true;

        Identifier incomingSpellId = incomingComponent.id();
        GrabBag incomingArgs = GrabBag.fromNBT(incomingComponent.argsAsNbt());

        for (int i = 0; i < TOTAL_SPELL_SLOTS; i++) {
            if (i == currentSlotIndex) continue;

            ItemStack existingStack = spellInventory.getStack(i);
            if (!existingStack.isEmpty() && existingStack.getItem() instanceof UnlockScrollItem) {
                UnlockSpellComponent existingComponent = existingStack.get(ModComponents.UNLOCK_SPELL_COMPONENT);
                if (existingComponent != null && existingComponent.id().equals(incomingSpellId)) {
                    GrabBag existingArgs = GrabBag.fromNBT(existingComponent.argsAsNbt());
                    if (existingArgs.equalsOther(incomingArgs)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < TOTAL_SPELL_SLOTS) {
                if (!this.insertItem(originalStack, TOTAL_SPELL_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                if (!player.getWorld().isClient() && player instanceof ServerPlayerEntity serverPlayer) {
                    boolean isFav = invSlot < FAVORITE_SLOTS_COUNT;
                    removeSpellFromPlayerData(serverPlayer, newStack, isFav, invSlot);
                }
            }
            else if (originalStack.getItem() instanceof UnlockScrollItem) {
                if (!this.insertItem(originalStack, 0, TOTAL_SPELL_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.spellInventory.canPlayerUse(player);
    }


    private class FavoriteSpellSlot extends Slot {
        private final int favIndex;

        public FavoriteSpellSlot(Inventory inventory, int index, int x, int y, int favIndex) {
            super(inventory, index, x, y);
            this.favIndex = favIndex;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof UnlockScrollItem && !isDuplicateInInventory(stack, this.getIndex());
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            super.onTakeItem(player, stack);
            if (!isUpdatingInventory && player instanceof ServerPlayerEntity serverPlayer) {
                removeSpellFromPlayerData(serverPlayer, stack, true, favIndex);
            }
        }

        @Override
        public void setStack(ItemStack stack, ItemStack previousStack) {
            super.setStack(stack, previousStack);
            if (!isUpdatingInventory && !stack.isEmpty() && player instanceof ServerPlayerEntity serverPlayer) {
                tryLearnSpell(serverPlayer, stack, true, favIndex);
            }
        }
    }

    private class StandardSpellSlot extends Slot {
        public StandardSpellSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.getItem() instanceof UnlockScrollItem && !isDuplicateInInventory(stack, this.getIndex());
        }

        @Override
        public void onTakeItem(PlayerEntity player, ItemStack stack) {
            super.onTakeItem(player, stack);
            if (!isUpdatingInventory && player instanceof ServerPlayerEntity serverPlayer) {
                removeSpellFromPlayerData(serverPlayer, stack, false, -1);
            }
        }

        @Override
        public void setStack(ItemStack stack, ItemStack previousStack) {
            super.setStack(stack, previousStack);
            if (!isUpdatingInventory && !stack.isEmpty() && player instanceof ServerPlayerEntity serverPlayer) {
                tryLearnSpell(serverPlayer, stack, false, -1);
            }
        }
    }
}