package io.github.tobyrue.btc.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BTCSpawnQueue {

    private record PositionKey(ServerWorld world, BlockPos pos) {}
    private static final Set<PositionKey> TRACKED_POSITIONS = new HashSet<>();
    private static final List<Runnable> TASKS = new ArrayList<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (TASKS.isEmpty()) return;

            List<Runnable> tasksToRun = new ArrayList<>(TASKS);
            TASKS.clear();

            for (Runnable task : tasksToRun) {
                task.run();
            }
        });
    }

    public static void queue(ServerWorld world, BlockPos trackingPos, Runnable action) {
        PositionKey key = new PositionKey(world, trackingPos.toImmutable());
        if (TRACKED_POSITIONS.add(key)) {
            TASKS.add(action);
        }
    }
}