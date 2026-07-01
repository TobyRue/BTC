package io.github.tobyrue.btc.processors;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RandomizeWoolColorProcessor extends StructureProcessor {
    public static final MapCodec<RandomizeWoolColorProcessor> CODEC = MapCodec.unit(RandomizeWoolColorProcessor::new);

    private static final String[] COLORS = {
            "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "green", "red", "black", "white"
    };

    public RandomizeWoolColorProcessor() {}

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(
            WorldView world, BlockPos pos, BlockPos pivot,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo currentBlockInfo,
            StructurePlacementData data
    ) {
        BlockState currentState = currentBlockInfo.state();
        Identifier blockId = Registries.BLOCK.getId(currentState.getBlock());
        String path = blockId.getPath();

        if (blockId.getNamespace().equals("minecraft") && path.startsWith("white_")) {

            long seed = (long) pivot.getX() * 3129871L ^ (long) pivot.getZ() * 116129781L ^ (long) pivot.getY();
            Random seededRandom = Random.create(seed);
            String chosenColor = COLORS[seededRandom.nextInt(COLORS.length)];

            String suffix = path.substring("white_".length());
            Identifier targetBlockId = Identifier.of("minecraft", chosenColor + "_" + suffix);
            Block targetBlock = Registries.BLOCK.get(targetBlockId);

            BlockState newState = targetBlock.getDefaultState();

            for (net.minecraft.state.property.Property<?> property : currentState.getProperties()) {
                newState = copyProperty(currentState, newState, property);
            }

            return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), newState, currentBlockInfo.nbt());
        }

        return currentBlockInfo;
    }

    @SuppressWarnings("unchecked")
    private <T extends Comparable<T>> BlockState copyProperty(BlockState src, BlockState dest, net.minecraft.state.property.Property<T> property) {
        if (dest.contains(property)) {
            return dest.with(property, src.get(property));
        }
        return dest;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructureProcessors.RANDOMIZE_WOOL_COLOR;
    }
}