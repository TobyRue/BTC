package io.github.tobyrue.btc.processors;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.enums.StructureBlockMode;
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
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapPopulatorProcessor extends StructureProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger("BTC-MapProcessor");
    public static final MapCodec<MapPopulatorProcessor> CODEC = MapCodec.unit(MapPopulatorProcessor::new);

    public MapPopulatorProcessor() {}

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(
            WorldView world, BlockPos pos, BlockPos pivot,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo currentBlockInfo,
            StructurePlacementData data
    ) {
        if (!currentBlockInfo.state().isOf(Blocks.AIR) && !currentBlockInfo.state().getBlock().getTranslationKey().contains("spruce") && !currentBlockInfo.state().getBlock().getTranslationKey().contains("dark_oak")) {
            System.out.println("Current Block: " + currentBlockInfo.state().getBlock() + " Pos: " + currentBlockInfo.pos());
        }
        if (currentBlockInfo.state().isOf(Blocks.STRUCTURE_BLOCK) && currentBlockInfo.nbt() != null) {
            String mode = currentBlockInfo.nbt().getString("mode");
            String metadata = currentBlockInfo.nbt().getString("metadata");

            if (currentBlockInfo.state().get(StructureBlock.MODE) == StructureBlockMode.DATA && "Map Frame".equals(metadata)) {
                BlockPos targetPos = currentBlockInfo.pos();
                LOGGER.info("[Map Processor] 'process' method matched target data block at {}", targetPos.toShortString());

                if (world instanceof ServerWorldAccess serverWorldAccess) {
                    ServerWorld serverWorld = serverWorldAccess.toServerWorld();

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
                    LOGGER.info("[Map Processor] Spawned item frame successfully via 'process' loop.");
                }

                return new StructureTemplate.StructureBlockInfo(targetPos, Blocks.AIR.getDefaultState(), null);
            }
        }

        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.MAP_POPULATOR;
    }
}