package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.MineEntity;
import io.github.tobyrue.btc.entity.custom.SuperHappyKillBallEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class SeaMineItem extends Item {
    public SeaMineItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var pos = context.getBlockPos();
        var hitPos = context.getHitPos();
        var hitSide = context.getSide();
        var world = context.getWorld();

        var spawnPos = BlockPos.ofFloored(hitPos.offset(hitSide, 1));
        MineEntity mineEntity = new MineEntity(spawnPos, world, CopperGolemEntity.Oxidation.UNOXIDIZED);
        mineEntity.setPos(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        world.spawnEntity(mineEntity);

        return ActionResult.PASS;
    }
}
