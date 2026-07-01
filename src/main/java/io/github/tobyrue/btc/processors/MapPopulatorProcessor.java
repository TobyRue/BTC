package io.github.tobyrue.btc.processors;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ServerWorldAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MapPopulatorProcessor extends StructureProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger("BTC-MapProcessor");
    public static final MapCodec<MapPopulatorProcessor> CODEC = MapCodec.unit(MapPopulatorProcessor::new);

    public MapPopulatorProcessor() {}

    @Override
    public List<StructureTemplate.StructureBlockInfo> reprocess(
            ServerWorldAccess world, BlockPos pos, BlockPos pivot,
            List<StructureTemplate.StructureBlockInfo> originalBlockInfos,
            List<StructureTemplate.StructureBlockInfo> currentBlockInfos,
            StructurePlacementData data
    ) {
        ServerWorld serverWorld = world.toServerWorld();
        if (serverWorld == null) {
            LOGGER.error("[Map Processor] Reprocess failed: ServerWorld is null!");
            return currentBlockInfos;
        }

        if (currentBlockInfos.isEmpty()) {
            LOGGER.warn("[Map Processor] Reprocess called, but currentBlockInfos list is completely empty.");
            return currentBlockInfos;
        }

        LOGGER.info("[Map Processor] Reprocess triggered! Scanning {} total blocks in this structure piece...", currentBlockInfos.size());
        List<StructureTemplate.StructureBlockInfo> modifiedBlocks = new ArrayList<>();
        int dataBlocksFound = 0;

        for (StructureTemplate.StructureBlockInfo info : currentBlockInfos) {
            if (info.state().isOf(Blocks.STRUCTURE_BLOCK)) {
                dataBlocksFound++;

                if (info.nbt() == null) {
                    LOGGER.warn("[Map Processor] Found Structure Block at {}, but its NBT data is NULL!", info.pos().toShortString());
                    modifiedBlocks.add(info);
                    continue;
                }

                String mode = info.nbt().contains("mode") ? info.nbt().getString("mode") : "UNKNOWN";
                String metadata = info.nbt().contains("metadata") ? info.nbt().getString("metadata") : "";

                LOGGER.info("[Map Processor] Inspecting Structure Block at {} -> Mode: '{}', Metadata Tag: '{}'",
                        info.pos().toShortString(), mode, metadata);

                if ("DATA".equalsIgnoreCase(mode) && "Map Frame".equals(metadata)) {
                    BlockPos targetPos = info.pos();
                    LOGGER.info("[Map Processor] CRITICAL TARGET MATCHED! Initializing map frame generation at {}", targetPos.toShortString());

                    ChunkPos centerChunk = new ChunkPos(targetPos);
                    for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                            serverWorld.getChunkManager().addTicket(
                                    ChunkTicketType.START,
                                    new ChunkPos(centerChunk.x + x, centerChunk.z + z),
                                    2,
                                    net.minecraft.util.Unit.INSTANCE
                            );
                        }
                    }

                    MapIdComponent mapId = serverWorld.increaseAndGetMapId();
                    MapState mapState = MapState.of(targetPos.getX(), targetPos.getZ(), (byte) 3, true, false, serverWorld.getRegistryKey());
                    serverWorld.putMapState(mapId, mapState);

                    ItemFrameEntity frame = new ItemFrameEntity(serverWorld, targetPos, Direction.DOWN);
                    ItemStack filledMap = new ItemStack(Items.FILLED_MAP);
                    filledMap.set(DataComponentTypes.MAP_ID, mapId);
                    frame.setHeldItemStack(filledMap);

                    serverWorld.spawnEntityAndPassengers(frame);

                    modifiedBlocks.add(new StructureTemplate.StructureBlockInfo(targetPos, Blocks.AIR.getDefaultState(), null));
                    continue;
                }
            }

            modifiedBlocks.add(info);
        }

        LOGGER.info("[Map Processor] Scan Finished. Out of {} blocks, checked {} structure blocks.", currentBlockInfos.size(), dataBlocksFound);
        return modifiedBlocks;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.MAP_POPULATOR;
    }
}