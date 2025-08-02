package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;

public class EnderChestSpell extends Spell {

    public EnderChestSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF4B0082;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        PlayerEntity player = (PlayerEntity) ctx.user();
        if (player == null || player.getEnderChestInventory() == null) return;

        EnderChestInventory inventory = player.getEnderChestInventory();
        NamedScreenHandlerFactory factory = new SimpleNamedScreenHandlerFactory((syncId, playerInventory, playerx) ->
                GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, inventory),
                Text.translatable("container.enderchest"));

        player.openHandledScreen(factory);
    }



    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }
}
