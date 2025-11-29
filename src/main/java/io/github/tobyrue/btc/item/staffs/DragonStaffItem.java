package io.github.tobyrue.btc.item.staffs;

import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.MinimalPredefinedSpellsItem;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DragonStaffItem extends MinimalPredefinedSpellsItem {
    public DragonStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 100;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final var stack = user.getStackInHand(hand);
        final var data = this.getSpellDataStore(stack);
        if (data.getSpell() == null) {
            data.setSpell(ModSpells.ENDER_PEARL, GrabBag.empty());
        }
        if (this.tryUseSpell(world, user.getEyePos(), user.getRotationVec(1.0F).normalize(), user, stack)) {
            return TypedActionResult.success(stack);
        } else {
            return TypedActionResult.consume(stack);
        }
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x9E00ED;
    }

    @Override
    public List<Spell.InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity) {
        List<Spell.InstancedSpell> s = new ArrayList<>();
        if (entity instanceof PlayerEntity player) {
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.ENDER_PEARL, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.ENDER_CHEST, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.DRAGON_FIREBALL, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.LIFE_STEAL, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.SHULKER_BULLET, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.SHADOW_STEP, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.DRAGONS_BREATH, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "btc:dragon_scales");
                put("name", "dragon_scales");
                put("duration", 400);
                put("amplifier", 1);
                put("cooldown", 1200);
            }})));
        }
        return s;
    }

    @Override
    public List<Identifier> getSpellAdvancements(ItemStack stack, World world, LivingEntity entity) {
        return List.of();
    }
}
