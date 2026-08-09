package io.github.tobyrue.btc.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record ScrollDefinitionComponent(Identifier name, int color) {


    public static final Codec<ScrollDefinitionComponent> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Identifier.CODEC.fieldOf("name").forGetter(ScrollDefinitionComponent::name),
                    Codec.INT.fieldOf("color").forGetter(ScrollDefinitionComponent::color)
            ).apply(builder, ScrollDefinitionComponent::new)
    );
}