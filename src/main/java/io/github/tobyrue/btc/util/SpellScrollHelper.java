package io.github.tobyrue.btc.util;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellScrollHelper {

    /**
     * Converts a single InstancedSpell into an Unlock Scroll ItemStack.
     *
     * @param instancedSpell The spell instance containing the Spell type and its GrabBag args.
     * @param advancement    The optional Identifier of the advancement associated with this scroll.
     * @param textureInt     Texture variation index for the component.
     * @return An ItemStack of ModItems.UNLOCK_SCROLL with the appropriate UnlockSpellComponent applied.
     */
    public static ItemStack createScrollFromSpell(Spell.InstancedSpell instancedSpell, @Nullable Identifier advancement, int textureInt) {
        if (instancedSpell == null || instancedSpell.spell() == null) {
            return ItemStack.EMPTY;
        }

        Identifier spellId = ModRegistries.SPELL.getId(instancedSpell.spell());
        if (spellId == null) {
            spellId = BTC.identifierOf("empty");
        }

        GrabBag args = instancedSpell.args();
        NbtCompound argsNbt = (args != null) ? GrabBag.toNBT(args) : new NbtCompound();
        String argsString = argsNbt.asString();

        UnlockSpellComponent component = new UnlockSpellComponent(
                Optional.ofNullable(advancement),
                textureInt,
                spellId,
                argsString
        );

        ItemStack scrollStack = new ItemStack(ModItems.UNLOCK_SCROLL);
        scrollStack.set(ModComponents.UNLOCK_SPELL_COMPONENT, component);

        UnlockScrollCache.invalidate(scrollStack);

        return scrollStack;
    }

    /**
     * Exports a single specific spell from the player's knownSpells list into a scroll stack
     * and removes it from their known spells.
     *
     * @param player         The server player who is removing the spell.
     * @param targetSpell    The InstancedSpell instance to export and unlearn.
     * @param advancement    Optional advancement to attach to the created scroll.
     * @param textureInt     Texture variation index for the scroll component.
     * @return The created scroll ItemStack (or ItemStack.EMPTY if the player doesn't know the spell).
     */
    public static ItemStack exportAndRemoveSingleSpell(ServerPlayerEntity player, Spell.InstancedSpell targetSpell, @Nullable Identifier advancement, int textureInt) {
        if (player == null || targetSpell == null || player.getServer() == null) {
            return ItemStack.EMPTY;
        }

        SpellPersistentState spellState = SpellPersistentState.get(player.getServer());
        PlayerSpellData playerData = spellState.getPlayerData(player);

        if (playerData == null || playerData.knownSpells == null) {
            return ItemStack.EMPTY;
        }

        Spell.InstancedSpell matchingSpell = null;
        for (Spell.InstancedSpell known : playerData.knownSpells) {
            if (known.spell() == targetSpell.spell() && known.args().equalsOther(targetSpell.args())) {
                matchingSpell = known;
                break;
            }
        }

        if (matchingSpell != null) {
            ItemStack scrollStack = createScrollFromSpell(matchingSpell, advancement, textureInt);

            playerData.knownSpells.remove(matchingSpell);
            spellState.markDirty();

            return scrollStack;
        }

        return ItemStack.EMPTY;
    }

    /**
     * Convenience overload: Exports and removes a spell by list index in knownSpells.
     *
     * @param player The server player.
     * @param index  The index in player's knownSpells list to export.
     * @return The created scroll ItemStack (or ItemStack.EMPTY if index is invalid).
     */
    public static ItemStack exportAndRemoveSingleSpellByIndex(ServerPlayerEntity player, int index) {
        if (player == null || player.getServer() == null) {
            return ItemStack.EMPTY;
        }

        SpellPersistentState spellState = SpellPersistentState.get(player.getServer());
        PlayerSpellData playerData = spellState.getPlayerData(player);

        if (playerData == null || playerData.knownSpells == null || index < 0 || index >= playerData.knownSpells.size()) {
            return ItemStack.EMPTY;
        }

        Spell.InstancedSpell targetSpell = playerData.knownSpells.get(index);
        ItemStack scrollStack = createScrollFromSpell(targetSpell, null, 0);

        playerData.knownSpells.remove(index);
        spellState.markDirty();

        return scrollStack;
    }

    /**
     * Exports ALL known spells from the player as Unlock Scroll ItemStacks and clears
     * their knownSpells list entirely.
     *
     * @param player The server player.
     * @return A List of ItemStacks representing all converted scrolls.
     */
    public static List<ItemStack> exportAndRemoveAllSpells(ServerPlayerEntity player) {
        List<ItemStack> scrollList = new ArrayList<>();

        if (player == null || player.getServer() == null) {
            return scrollList;
        }

        SpellPersistentState spellState = SpellPersistentState.get(player.getServer());
        PlayerSpellData playerData = spellState.getPlayerData(player);

        if (playerData == null || playerData.knownSpells == null || playerData.knownSpells.isEmpty()) {
            return scrollList;
        }

        for (Spell.InstancedSpell instancedSpell : playerData.knownSpells) {
            ItemStack scrollStack = createScrollFromSpell(instancedSpell, null, 0);
            if (!scrollStack.isEmpty()) {
                scrollList.add(scrollStack);
            }
        }

        playerData.knownSpells.clear();
        spellState.markDirty();

        return scrollList;
    }

    /**
     * Converts all known spells in a PlayerSpellData object into a list of Unlock Scroll ItemStacks
     * without removing them.
     *
     * @param playerData The player's spell data containing knownSpells.
     * @return A list of ItemStacks, one for each known spell.
     */
    public static List<ItemStack> parseKnownSpellsToScrolls(PlayerSpellData playerData) {
        List<ItemStack> scrollList = new ArrayList<>();

        if (playerData == null || playerData.knownSpells == null) {
            return scrollList;
        }

        for (Spell.InstancedSpell instancedSpell : playerData.knownSpells) {
            ItemStack scrollStack = createScrollFromSpell(
                    instancedSpell,
                    null,
                    0
            );

            if (!scrollStack.isEmpty()) {
                scrollList.add(scrollStack);
            }
        }

        return scrollList;
    }
}