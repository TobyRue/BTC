package io.github.tobyrue.btc.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.nbt.NbtCompound;

public record UnlockSpellComponent(Identifier advancement, int textureInt, Identifier name, Identifier id, String args) {

    public NbtCompound argsAsNbt() {
        System.out.println("ARGS: " + args);
        var nbtArgs = args;
        if (!nbtArgs.endsWith("}")) {
            nbtArgs = "{" + args + "}";
        }
        System.out.println("NBT ARGS: " + nbtArgs);
        try {
            NbtCompound result = StringNbtReader.parse(nbtArgs);
            System.out.println("Args successfully parsed: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("Args failed to parse: " + nbtArgs);
            e.printStackTrace();
            return new NbtCompound();
        }
    }
    public static final Codec<UnlockSpellComponent> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Identifier.CODEC.fieldOf("advancement").forGetter(UnlockSpellComponent::advancement),
                Codec.INT.optionalFieldOf("texture_int", 0).forGetter(UnlockSpellComponent::textureInt),
                Identifier.CODEC.optionalFieldOf("name", Identifier.of("empty")).forGetter(UnlockSpellComponent::name),
                Identifier.CODEC.optionalFieldOf("id", Identifier.of("empty")).forGetter(UnlockSpellComponent::id),
                Codec.STRING.optionalFieldOf("args", "{}").forGetter(UnlockSpellComponent::args)

        ).apply(builder, UnlockSpellComponent::new);
    });
}
