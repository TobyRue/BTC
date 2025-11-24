package io.github.tobyrue.btc.util;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public final class UnlockScrollCache {
    private static final Map<ItemStack, Spell.InstancedSpell> CACHE = new WeakHashMap<>();

    private UnlockScrollCache() {}

    public static Spell.InstancedSpell getCachedSpell(ItemStack stack) {
        Spell.InstancedSpell inst = CACHE.get(stack);
        if (inst != null) return inst;

        var comp = stack.get(BTC.UNLOCK_SPELL_COMPONENT);
        if (comp == null) return null;

        Identifier id = comp.id();
        if (id == null || id.equals(Identifier.of("empty"))) return null;

        NbtCompound args = comp.argsAsNbt();
        if (args == null) args = new NbtCompound();

        var spellType = ModRegistries.SPELL.get(id);
        if (spellType == null) return null;

        inst = new Spell.InstancedSpell(spellType, GrabBag.fromNBT(args));
        // cache it
        CACHE.put(stack, inst);
        return inst;
    }

    // call this when the stack's unlock component or args change
    public static void invalidate(ItemStack stack) {
        CACHE.remove(stack);
    }
}
