package io.github.tobyrue.btc.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.NbtCompound;

public record UnlockSpellComponent(Identifier advancement, int textureInt, Identifier name, Identifier id, NbtCompound args) {
    public static final Codec<UnlockSpellComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Identifier.CODEC.fieldOf("advancement").forGetter(UnlockSpellComponent::advancement),
                Codec.INT.optionalFieldOf("texture_int", 0).forGetter(UnlockSpellComponent::textureInt),
                Identifier.CODEC.optionalFieldOf("name", Identifier.of("empty")).forGetter(UnlockSpellComponent::name),
                Identifier.CODEC.optionalFieldOf("id", Identifier.of("empty")).forGetter(UnlockSpellComponent::id),
                NbtCompound.CODEC.optionalFieldOf("args", new NbtCompound()).forGetter(UnlockSpellComponent::args)

        ).apply(builder, UnlockSpellComponent::new);
    });
}
