package io.github.tobyrue.btc.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record UnlockSpellComponent(Optional<Identifier> advancement, int textureInt, Identifier id, String args) {

    public NbtCompound argsAsNbt() {
        var nbtArgs = args;
        if (!nbtArgs.endsWith("}")) {
            nbtArgs = "{" + args + "}";
        }

        try {
            return StringNbtReader.parse(nbtArgs);
        } catch (Exception e) {
            e.printStackTrace();
            return new NbtCompound();
        }
    }

    public static final Codec<UnlockSpellComponent> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    Identifier.CODEC.optionalFieldOf("advancement").forGetter(UnlockSpellComponent::advancement),
                    Codec.INT.optionalFieldOf("texture_int", 0).forGetter(UnlockSpellComponent::textureInt),
                    Identifier.CODEC.optionalFieldOf("id", Identifier.of("empty")).forGetter(UnlockSpellComponent::id),
                    Codec.STRING.optionalFieldOf("args", "{}").forGetter(UnlockSpellComponent::args)
            ).apply(builder, UnlockSpellComponent::new)
    );
}