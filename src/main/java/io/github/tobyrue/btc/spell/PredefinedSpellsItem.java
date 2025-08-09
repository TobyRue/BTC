package io.github.tobyrue.btc.spell;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public abstract class PredefinedSpellsItem extends SpellItem {
    public PredefinedSpellsItem(Settings settings) {
        super(settings);
    }

    public abstract List<InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity);

    public record InstancedSpell(Spell spell, GrabBag args) {}
}
