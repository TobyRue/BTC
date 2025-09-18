package io.github.tobyrue.btc.item.staffs;

import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class WindStaffItem extends MinimalPredefinedSpellsItem {
    public WindStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final var stack = user.getStackInHand(hand);
        final var data = this.getSpellDataStore(stack);
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
        return 0xA0F4C5;
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
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.WIND_CHARGE, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.CLUSTER_WIND_CHARGE, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.WIND_TORNADO, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.STORM_PUSH, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.TEMPESTS_CALL, GrabBag.empty()));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.LOCALIZED_STORM_PUSH, GrabBag.empty()));
        }
        return s;
    }
}
