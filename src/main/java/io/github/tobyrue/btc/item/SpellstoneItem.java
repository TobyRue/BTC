package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.SpellItem;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;

public class SpellstoneItem extends SpellItem {
    public SpellstoneItem(Settings settings) {
        super(settings);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final var stack = user.getStackInHand(hand);
        final var data = this.getSpellDataStore(stack);

        if (data.getSpell() == null) {
            data.setSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                put("level", 5);
            }}));
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
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        final var data = this.getSpellDataStore(stack);
        if (data.getSpell() != null) {
            tooltip.add(Text.literal(ModRegistries.SPELL.getId(data.getSpell()).toString()));
        }
    }
}
