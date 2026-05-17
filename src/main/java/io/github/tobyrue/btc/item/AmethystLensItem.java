package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.util.ClientOreRadar;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class AmethystLensItem extends Item {
    public AmethystLensItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient) {
            BlockPos playerPos = user.getBlockPos();
            int radius = 16;
            List<BlockPos> foundOres = new ArrayList<>();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos targetPos = playerPos.add(x, y, z);
                        BlockState state = world.getBlockState(targetPos);

                        if (state.isIn(BlockTags.GOLD_ORES) || state.isIn(BlockTags.IRON_ORES) || state.isIn(BlockTags.DIAMOND_ORES) || state.isIn(BlockTags.REDSTONE_ORES) || state.isIn(BlockTags.LAPIS_ORES) || state.isIn(BlockTags.COAL_ORES) || state.isIn(BlockTags.EMERALD_ORES) || state.isIn(BlockTags.COPPER_ORES)) {
                            foundOres.add(targetPos);
                        }
                    }
                }
            }

            if (!foundOres.isEmpty()) {
                ClientOreRadar.startScanning(foundOres, 100);

                world.playSound(user.getX(), user.getY(), user.getZ(),
                        SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS,
                        1.0F, 1.3F, false);
            } else {
                world.playSound(user.getX(), user.getY(), user.getZ(),
                        SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS,
                        0.5F, 0.5F, false);
            }

            user.getItemCooldownManager().set(this, 120);
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}