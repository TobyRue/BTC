package io.github.tobyrue.btc.enums;

import com.mojang.serialization.Codec;

public enum WrenchType {
    ROTATE("rotate"), WIRE("wire"), WIRE_DELAY("wire_delay"), WIRE_COMPLEX("wire_complex");

    private final String name;
    public static final Codec<WrenchType> CODEC = Codec.STRING.xmap(
            WrenchType::valueOf,
            WrenchType::name
    );

    WrenchType(String name) {
        this.name = name;
    }
    public String asString() {
        return this.name;
    }
    public String toString() {
        return this.name;
    }

}
