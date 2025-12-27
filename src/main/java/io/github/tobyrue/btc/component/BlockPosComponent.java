
package io.github.tobyrue.btc.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BlockPosComponent(int x, int y, int z) {

    public static final Codec<BlockPosComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.INT.optionalFieldOf("x", 0).forGetter(BlockPosComponent::x),
                Codec.INT.optionalFieldOf("y", 0).forGetter(BlockPosComponent::y),
                Codec.INT.optionalFieldOf("z", 0).forGetter(BlockPosComponent::z)
        ).apply(builder, BlockPosComponent::new);
    });
}
