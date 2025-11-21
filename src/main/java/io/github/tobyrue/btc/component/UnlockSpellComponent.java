package io.github.tobyrue.btc.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public record UnlockSpellComponent(Identifier advancement, int textureInt, Identifier name) {
    public static final Codec<UnlockSpellComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Identifier.CODEC.fieldOf("advancement").forGetter(UnlockSpellComponent::advancement),
                Codec.INT.optionalFieldOf("texture_int", 0).forGetter(UnlockSpellComponent::textureInt),
                Identifier.CODEC.optionalFieldOf("name", Identifier.of("empty")).forGetter(UnlockSpellComponent::name)

        ).apply(builder, UnlockSpellComponent::new);
    });
}
