package io.github.tobyrue.btc.dataholders;

import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.util.PistonReactable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class PistonPushManager {

    public record TrackedData(int count, long lastTime) {}

    private static final Map<BlockPos, TrackedData> ACTIVE_TRACKERS = new HashMap<>();

    public static BlockState onBlockMovedByPiston(World world, BlockPos fromPos, BlockPos toPos, BlockState movedState, BlockEntity movedBlockEntity) {
        if (world.isClient()) {
            return movedState;
        }

        BlockPos immutableFrom = fromPos.toImmutable();
        BlockPos immutableTo = toPos.toImmutable();
        long currentTime = world.getTime();

        TrackedData current = ACTIVE_TRACKERS.remove(immutableFrom);

        if (movedState.getBlock() instanceof PistonReactable reactable) {
            int requiredPushes = reactable.getRequiredPushes(movedState);
            long maxDelay = reactable.getMaxTickDelay(movedState);

            int count = getUpdatedCount(current, currentTime, maxDelay);

            if (count >= requiredPushes) {
                return reactable.onPistonReaction(world, immutableTo, movedState, count) == null ? movedState : reactable.onPistonReaction(world, immutableTo, movedState, count);
            } else {
                ACTIVE_TRACKERS.put(immutableTo, new TrackedData(count, currentTime));
            }
            return movedState;
        }

        return handleReactions(world, immutableTo, movedState, movedBlockEntity, current, currentTime);
    }

    private static BlockState handleReactions(World world, BlockPos toPos, BlockState movedState, BlockEntity movedBlockEntity, TrackedData current, long currentTime) {
        if (movedState.isOf(Blocks.COBBLESTONE)) {
            int count = getUpdatedCount(current, currentTime, 40L);
            if (count >= 10) {
                return Blocks.STONE.getDefaultState();
            } else {
                ACTIVE_TRACKERS.put(toPos, new TrackedData(count, currentTime));
            }
            return movedState;
        }

        if (movedBlockEntity instanceof Inventory inventory) {
            int count = getUpdatedCount(current, currentTime, 40L);

            if (count >= 5) {
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stack = inventory.getStack(i);
                    if (!stack.isEmpty() && stack.isOf(Items.MILK_BUCKET)) {
                        inventory.setStack(i, new ItemStack(ModItems.BUTTER, stack.getCount()));
                    }
                }
                inventory.markDirty();
            } else {
                ACTIVE_TRACKERS.put(toPos, new TrackedData(count, currentTime));
            }
        }

        return movedState;
    }

    private static int getUpdatedCount(TrackedData current, long currentTime, long maxDelay) {
        if (current != null) {
            long timeDiff = currentTime - current.lastTime();
            if (timeDiff > 0 && timeDiff <= maxDelay) {
                return current.count() + 1;
            } else if (timeDiff == 0) {
                return current.count();
            }
        }
        return 1;
    }
}