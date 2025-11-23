package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.util.AdvancementUtils;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class UnlockScrollItem extends Item {
    public UnlockScrollItem() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(BTC.UNLOCK_SPELL_COMPONENT, new UnlockSpellComponent(BTC.identifierOf("adventure/enter_btc_trial_chamber"),  0, Identifier.of("empty"), Identifier.of("empty"), GrabBag.toNBT(GrabBag.empty()))).component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xFFFFFF, false)));
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
                && Objects.requireNonNull(stack.get(BTC.UNLOCK_SPELL_COMPONENT)).advancement() instanceof Identifier av
                && player.server.getAdvancementLoader().get(av) instanceof AdvancementEntry advancement) {
            if (!player.getAdvancementTracker().getProgress(advancement).isDone()) {
                player.getAdvancementTracker().grantCriterion(
                        advancement,
                        "unlock"
                );
                stack.decrementUnlessCreative(1, user);
            }
            if (Objects.requireNonNull(stack.get(BTC.UNLOCK_SPELL_COMPONENT)).id() instanceof Identifier id && Objects.requireNonNull(stack.get(BTC.UNLOCK_SPELL_COMPONENT)).args() instanceof NbtCompound args) {

                Spell.InstancedSpell spell = new Spell.InstancedSpell(ModRegistries.SPELL.get(id), GrabBag.fromNBT(args));
                MinecraftServer server = player.getServer();
                SpellPersistentState spellState = SpellPersistentState.get(server);
                PlayerSpellData playerData = spellState.getPlayerData(player);

                if (!playerData.knownSpells.stream().anyMatch(s -> s.spell().equals(spell.spell()) && s.args().equals(spell.args()))) {
                    System.out.println("Known Spells 1: " + playerData.knownSpells);
                    playerData.knownSpells.add(spell);
                    System.out.println("Known Spells: " + playerData.knownSpells.getLast());
                    System.out.println("Known Spells 2: " + playerData.knownSpells);
                }
            }
        }
        return super.finishUsing(stack, world, user);
    }



    @Override
    public Text getName(ItemStack stack) {
        if (Objects.requireNonNull(stack.get(BTC.UNLOCK_SPELL_COMPONENT)).name() instanceof Identifier id) {
            return Text.translatable(this.getTranslationKey(stack), Text.translatable("spell." + id.getNamespace() + "." + id.getPath()));
        }
        return Text.translatable(this.getTranslationKey() + ".empty");
    }
}
