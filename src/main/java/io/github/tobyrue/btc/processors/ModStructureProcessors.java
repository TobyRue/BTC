package io.github.tobyrue.btc.processors;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.BTC;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.world.gen.structure.EndCityStructure;

public class ModStructureProcessors {

    public static final StructureProcessorType<RandomizeWoolColorProcessor> RANDOMIZE_WOOL_COLOR =
            register("randomize_wool_color", RandomizeWoolColorProcessor.CODEC);

    private static <P extends StructureProcessor> StructureProcessorType<P> register(String id, MapCodec<P> codec) {
        return Registry.register(Registries.STRUCTURE_PROCESSOR, BTC.identifierOf(id), () -> codec);
    }

    public static void registerProcessors() {
    }
}