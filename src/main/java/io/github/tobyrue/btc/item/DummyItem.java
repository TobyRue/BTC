package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.SpellItem;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DummyItem extends SpellItem {
    public DummyItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final var stack = user.getStackInHand(hand);
        final var data = this.getSpellDataStore(stack);

        if (data.getSpell() == null) {
            data.setSpell(ModSpells.WEAK_FIREBALL);
        }

        if (!user.isSneaking()) {
            if (this.tryUseSpell(world, BlockPos.ofFloored(user.getEyePos()), user.getRotationVec(1.0F).normalize(), user, stack)) {
                return TypedActionResult.success(stack);
            } else {
                return TypedActionResult.fail(stack);
            }
        }
        return super.use(world, user, hand);
    }
}
