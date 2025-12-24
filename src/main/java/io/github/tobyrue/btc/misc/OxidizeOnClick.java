package io.github.tobyrue.btc.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.Items;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class OxidizeOnClick {

    public static ActionResult onUseBlock(
            PlayerEntity player,
            World world,
            Hand hand,
            BlockHitResult hitResult
    ) {

        if (world.isClient) {
            return ActionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        boolean wasWaxed = HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().containsKey(block);
        Block baseBlock = wasWaxed
                ? HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().get(block)
                : block;

        if (!(baseBlock instanceof Oxidizable)) {
            return ActionResult.PASS;
        }

        Optional<Block> next = Oxidizable.getIncreasedOxidationBlock(baseBlock);
        Optional<Block> prev = Oxidizable.getDecreasedOxidationBlock(baseBlock);

        Block target;

        if (!player.isSneaking()) {
            if (next.isEmpty()) return ActionResult.PASS;
            target = next.get();
        } else {
            if (prev.isEmpty()) return ActionResult.PASS;
            target = prev.get();
        }

        if (wasWaxed) {
            target = HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().getOrDefault(target, target);
        }

        BlockState newState = copyBlockState(state, target);
        world.setBlockState(pos, newState, Block.NOTIFY_ALL);

        return ActionResult.SUCCESS;
    }

    private static BlockState copyBlockState(BlockState oldState, Block newBlock) {
        BlockState newState = newBlock.getDefaultState();

        for (Property<?> property : oldState.getProperties()) {
            if (newState.contains(property)) {
                newState = copyProperty(newState, oldState, property);
            }
        }

        return newState;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState copyProperty(
            BlockState newState,
            BlockState oldState,
            Property<T> property
    ) {
        return newState.with(property, oldState.get(property));
    }
}
