package io.github.tobyrue.btc.item;

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

public class SpellBookItem extends MinimalPredefinedSpellsItem {

    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 100;
    }

    @Override
    public List<Spell.InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity) {
        List<Spell.InstancedSpell> s = new ArrayList<>();
        if (entity instanceof PlayerEntity player) {
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                put("level", 4);
            }})));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.BLAZE_STORM, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.WATER_BLAST, GrabBag.fromMap(new HashMap<>() {{
                put("noGravity", true);
            }})));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.ICE_BLOCK, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.DRAGON_FIREBALL, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.SHADOW_STEP, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.CLUSTER_WIND_CHARGE, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.WIND_TORNADO, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.EARTH_SPIKE_LINE, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.CREEPER_WALL_EXPLOSIVE_TRAP, GrabBag.empty()));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.POTION, GrabBag.fromMap(new HashMap<>() {{
                put("effect", "minecraft:regeneration");
                put("duration", 200);
                put("amplifier", 2);
                put("cooldown", 800);
                put("name", "regeneration");
            }})));
            addSpellToItem(player, s, null, new Spell.InstancedSpell(ModSpells.PURGE_BOLT, GrabBag.empty()));
        }
        return s;
    }

    @Override
    public List<Identifier> getSpellAdvancements(ItemStack stack, World world, LivingEntity entity) {
        return List.of();
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xEFBF04;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final var stack = user.getStackInHand(hand);
        final var data = this.getSpellDataStore(stack);
        if (data.getSpell() == null) {
            data.setSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                put("level", 4);
            }}));
        }
        if (this.tryUseSpell(world, user.getEyePos(), user.getRotationVec(1.0F).normalize(), user, stack)) {
            return TypedActionResult.success(stack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
    }
}

