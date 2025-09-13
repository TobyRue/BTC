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

public class DragonStaffItem extends PredefinedSpellsItem {
    public DragonStaffItem(Settings settings) {
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
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.ENDER_PEARL, GrabBag.empty()), null);
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.ENDER_CHEST, GrabBag.empty()), null);
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.DRAGONS_BREATH, GrabBag.empty()), null);
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.LIFE_STEAL, GrabBag.empty()), null);
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.SHULKER_BULLET, GrabBag.empty()), null);
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
        } else {
            if (user instanceof ServerPlayerEntity serverPlayer) {
                MinecraftServer server = serverPlayer.getServer();
                SpellPersistentState spellState = SpellPersistentState.get(server);
                PlayerSpellData playerData = spellState.getPlayerData(serverPlayer);
                //TODO make something that swaps the spell
            }
        }
        return super.use(world, user, hand);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x9E00ED;
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
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.ENDER_PEARL, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.ENDER_CHEST, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.DRAGONS_BREATH, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.LIFE_STEAL, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.SHULKER_BULLET, GrabBag.empty()));
        }
        return s;
    }
}
