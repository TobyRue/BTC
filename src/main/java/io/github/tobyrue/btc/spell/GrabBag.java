package io.github.tobyrue.btc.spell;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public interface GrabBag {
    int getInt(final String key, final int fallback);
    default int getInt(final String key) {
        return getInt(key, 0);
    }

    short getShort(final String key, final short fallback);
    default short getShort(final String key) {
        return getShort(key, (short) 0);
    }

    byte getByte(final String key, final byte fallback);
    default byte getByte(final String key) {
        return getByte(key, (byte) 0);
    }

    long getLong(final String key, final long fallback);
    default long getLong(final String key) {
        return getLong(key, 0L);
    }

    boolean getBoolean(final String key, final boolean fallback);
    default boolean getBoolean(final String key) {
        return getBoolean(key, false);
    }

    String getString(final String key, final String fallback);
    default String getString(final String key) {
        return getString(key, "");
    }

    GrabBag getChild(final String key, final GrabBag fallback);
    GrabBag getChild(final String key);

    static GrabBag fromNBT(final NbtCompound nbt) {
        return new GrabBag() {
            @Override
            public int getInt(final String key, final int fallback) {
                return nbt.contains(key) && nbt.getType(key) == NbtCompound.INT_TYPE ? nbt.getInt(key) : fallback;
            }

            @Override
            public short getShort(final String key, final short fallback) {
                return nbt.contains(key) && nbt.getType(key) == NbtCompound.SHORT_TYPE ? nbt.getShort(key) : fallback;
            }

            @Override
            public byte getByte(final String key, final byte fallback) {
                return nbt.contains(key) && nbt.getType(key) == NbtCompound.BYTE_TYPE ? nbt.getByte(key) : fallback;
            }

            @Override
            public long getLong(final String key, final long fallback) {
                return nbt.contains(key) && nbt.getType(key) == NbtCompound.LONG_TYPE ? nbt.getLong(key) : fallback;
            }

            @Override
            public boolean getBoolean(final String key, final boolean fallback) {
                return nbt.contains(key) && nbt.getType(key) == NbtCompound.BYTE_TYPE ? nbt.getBoolean(key) : fallback;
            }

            @Override
            public String getString(final String key, final String fallback) {
                return nbt.contains(key) && nbt.getType(key) == NbtCompound.STRING_TYPE ? nbt.getString(key) : fallback;
            }

            @Override
            public GrabBag getChild(final String key, final GrabBag fallback) {
                return nbt.contains(key) && nbt.getType(key) == NbtElement.COMPOUND_TYPE ? fromNBT(nbt.getCompound(key)) : fallback;
            }

            @Override
            public GrabBag getChild(final String key) {
                return getChild(key, fromNBT(new NbtCompound()));
            }
        };
    }
}
