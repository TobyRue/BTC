package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.regestries.ModRegistries;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.Objects;

public class UnlockScrollItem extends Item {
    public UnlockScrollItem() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(BTC.UNLOCK_SPELL_COMPONENT, new UnlockSpellComponent(BTC.identifierOf("adventure/enter_btc_trial_chamber"),  0)).component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xFFFFFF, false)));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 30;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Start using the item
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {

        if ((user instanceof ServerPlayerEntity player)
                && Objects.requireNonNull(stack.get(BTC.UNLOCK_SPELL_COMPONENT)).advancement() instanceof Identifier id
                && player.server.getAdvancementLoader().get(id) instanceof AdvancementEntry advancement) {
            if (!player.getAdvancementTracker().getProgress(advancement).isDone()) {
                player.getAdvancementTracker().grantCriterion(
                        advancement,
                        "unlock"
                );
                stack.decrementUnlessCreative(1, user);
            }
        }
        return super.finishUsing(stack, world, user);
    }



    @Override
    public Text getName(ItemStack stack) {
        if (Objects.requireNonNull(stack.get(BTC.UNLOCK_SPELL_COMPONENT)).advancement() instanceof Identifier id) {
            return Text.translatable(this.getTranslationKey(stack), Text.translatable("advancements." + id.toShortTranslationKey().replace("/", ".")));
        }
        return Text.translatable(this.getTranslationKey() + ".empty");
    }
}
