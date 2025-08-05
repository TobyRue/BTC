package io.github.tobyrue.btc.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

public record UnlockSpellComponent(Identifier advancement, Identifier texture) {
    public static final Codec<UnlockSpellComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Identifier.CODEC.fieldOf("advancement").forGetter(UnlockSpellComponent::advancement),
                Identifier.CODEC.optionalFieldOf("texture", null).forGetter(UnlockSpellComponent::texture)
        ).apply(builder, UnlockSpellComponent::new);
    });
}
