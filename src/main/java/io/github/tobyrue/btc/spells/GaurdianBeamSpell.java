package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GaurdianBeamSpell extends Spell {
    public GaurdianBeamSpell() {
        super(SpellTypes.WATER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {

    }
    //TODO other methods needed
}
