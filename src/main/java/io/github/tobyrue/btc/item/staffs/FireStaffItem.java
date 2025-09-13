package io.github.tobyrue.btc.item.staffs;

import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.PredefinedSpellsItem;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FireStaffItem extends PredefinedSpellsItem {
    public FireStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final var stack = user.getStackInHand(hand);
        final var data = this.getSpellDataStore(stack);
        if (user instanceof ServerPlayerEntity serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            SpellPersistentState spellState = SpellPersistentState.get(server);
            PlayerSpellData playerData = spellState.getPlayerData(serverPlayer);
            if (getKnownSpells(playerData).isEmpty()) {
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.empty()), null);
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                    put("level", 4);
                }})), null);
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.FIRE_STORM, GrabBag.empty()), null);
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.FIRE_STORM, GrabBag.fromMap(new HashMap<>() {{
                    put("duration", 4);
                    put("maxRadius", 16);
                    put("cooldown", 600);
                }})), null);
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.BLAZE_STORM, GrabBag.empty()), null);
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.FLAME_BURST, GrabBag.empty()), null);
            }
        }
        if (data.getSpell() == null) {
            data.setSpell(ModSpells.WIND_CHARGE, GrabBag.empty());
        }
        if (!user.isSneaking()) {
            if (this.tryUseSpell(world, user.getEyePos(), user.getRotationVec(1.0F).normalize(), user, stack)) {
                return TypedActionResult.success(stack);
            } else {
                return TypedActionResult.fail(stack);
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xE5531D;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        final var data = this.getSpellDataStore(stack);
        if (data.getSpell() != null) {
            tooltip.add(Text.literal(ModRegistries.SPELL.getId(data.getSpell()).toString()));
        }
    }
    @Override
    public List<Spell.InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity) {
        List<Spell.InstancedSpell> s = new ArrayList<>();
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                put("level", 4);
            }})));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.FIRE_STORM, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.FIRE_STORM, GrabBag.fromMap(new HashMap<>() {{
                put("duration", 4);
                put("maxRadius", 16);
                put("cooldown", 600);
            }})));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.BLAZE_STORM, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.FLAME_BURST, GrabBag.empty()));
        }
        return s;
    }
}
