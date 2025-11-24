package io.github.tobyrue.btc.spell;

import io.github.tobyrue.xml.util.Nullable;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

public interface GrabBag {

    Set<String> getKeys();

    default int size() {
        return this.getKeys().size();
    }

    @Nullable
    Class<?> getType(final String key);

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

    float getFloat(final String key, final float fallback);
    default float getFloat(final String key) {
        return getFloat(key, 0F);
    }

    double getDouble(final String key, final double fallback);
    default double getDouble(final String key) {
        return getDouble(key, 0D);
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
    default GrabBag getChild(final String key) {
        return getChild(key, GrabBag.empty());
    }

    default boolean equalsOther(@Nullable final Object o) {


        if (this == o) {
            return true;
        }

        if (o instanceof GrabBag other) {
            if (GrabBag.toNBT(this).equals(GrabBag.toNBT(other))) {
                return true;
            }
        }

        return o instanceof GrabBag other && this.getKeys().size() == other.getKeys().size() && this.getKeys().stream().allMatch(key -> {
            final var t1 = this.getType(key);
            final var t2 = other.getType(key);

            if (t1 == Byte.class && t2 == Byte.class) {
                return this.getByte(key) == other.getByte(key);
            } else if (t1 == Short.class && t2 == Short.class) {
                return this.getShort(key) == other.getShort(key);
            } else if (t1 == Integer.class && t2 == Integer.class) {
                return this.getInt(key) == other.getInt(key);
            } else if (t1 == Long.class && t2 == Long.class) {
                return this.getLong(key) == other.getLong(key);
            } else if (t1 == Float.class && t2 == Float.class) {
                return this.getFloat(key) == other.getFloat(key);
            } else if (t1 == Double.class && t2 == Double.class) {
                return this.getDouble(key) == other.getDouble(key);
            } else if (t1 == String.class && t2 == String.class) {
                return this.getString(key) == other.getString(key);
            } else if (t1 == Boolean.class && t2 == Boolean.class) {
                // Note: getType will return Byte.class for booleans, but let's handle it anyway
                return this.getBoolean(key) == other.getBoolean(key);
            } else if (t1 == GrabBag.class && t2 == GrabBag.class) {
                return this.getChild(key).equalsOther(other.getChild(key));
            } else {
                return false;
            }
        });
    }

    static GrabBag empty() {
        return new GrabBag() {
            @Override
            public Set<String> getKeys() {
                return Set.of();
            }

            @Override
            @Nullable
            public Class<?> getType(final String key) {
                return null;
            }

            @Override
            public int getInt(final String key, final int fallback) {
                return fallback;
            }

            @Override
            public short getShort(final String key, final short fallback) {
                return fallback;
            }

            @Override
            public byte getByte(final String key, final byte fallback) {
                return fallback;
            }

            @Override
            public long getLong(final String key, final long fallback) {
                return fallback;
            }

            @Override
            public float getFloat(final String key, final float fallback) {
                return fallback;
            }

            @Override
            public double getDouble(final String key, final double fallback) {
                return fallback;
            }

            @Override
            public boolean getBoolean(final String key, final boolean fallback) {
                return fallback;
            }

            @Override
            public String getString(final String key, final String fallback) {
                return fallback;
            }

            @Override
            public GrabBag getChild(final String key, final GrabBag fallback) {
                return fallback;
            }
        };
    }

    static GrabBag fromNBT(final NbtCompound nbt) {
        return new GrabBag() {
            @Override
            public Set<String> getKeys() {
                return nbt.getKeys();
            }

            @Override
            @Nullable
            public Class<?> getType(final String key) {
                return switch (nbt.getType(key)) {
                    case NbtCompound.BYTE_TYPE -> Byte.class; // Or boolean
                    case NbtCompound.SHORT_TYPE -> Short.class;
                    case NbtCompound.INT_TYPE -> Integer.class;
                    case NbtCompound.LONG_TYPE -> Long.class;
                    case NbtCompound.FLOAT_TYPE -> Float.class;
                    case NbtCompound.DOUBLE_TYPE -> Double.class;
                    case NbtCompound.STRING_TYPE -> String.class;
                    case NbtCompound.COMPOUND_TYPE -> GrabBag.class;
                    default -> null;
                };
            }

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
            public float getFloat(final String key, final float fallback) {
                return nbt.contains(key) && nbt.getType(key) == NbtCompound.FLOAT_TYPE ? nbt.getFloat(key) : fallback;
            }

            //TODO make work so that in json it is able to parse a double
//            @Override
//            public double getDouble(final String key, final double fallback) {
//                if (!nbt.contains(key)) return fallback;
//                switch(nbt.getType(key)) {
//                    case NbtCompound.BYTE_TYPE, NbtCompound.FLOAT_TYPE, NbtCompound.LONG_TYPE,
//                         NbtCompound.DOUBLE_TYPE, NbtCompound.INT_TYPE, NbtCompound.SHORT_TYPE -> { return nbt.getDouble(key); }
//                    default -> { return fallback; }
//                }
//            }

            @Override
            public double getDouble(final String key, final double fallback) {
                return nbt.contains(key) && nbt.getType(key) == NbtCompound.DOUBLE_TYPE ? nbt.getDouble(key) : fallback;
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
        };
    }

    static NbtCompound toNBT(final GrabBag args) {
        final var nbt = new NbtCompound();
        for (final var key : args.getKeys()) {
            final var t = args.getType(key);
            if (t == Byte.class) {
                nbt.putByte(key, args.getByte(key));
            } else if (t == Short.class) {
                nbt.putShort(key, args.getShort(key));
            } else if (t == Integer.class) {
                nbt.putInt(key, args.getInt(key));
            } else if (t == Long.class) {
                nbt.putLong(key, args.getLong(key));
            } else if (t == Float.class) {
                nbt.putFloat(key, args.getFloat(key));
            } else if (t == Double.class) {
                nbt.putDouble(key, args.getDouble(key));
            } else if (t == String.class) {
                nbt.putString(key, args.getString(key));
            } else if (t == Boolean.class) {
                // Note: getType will return Byte.class for booleans, but let's handle it anyway
                nbt.putBoolean(key, args.getBoolean(key));
            } else if (t == GrabBag.class) {
                nbt.put(key, GrabBag.toNBT(args.getChild(key)));
            }
        }
        return nbt;
    }

    static GrabBag fromMap(final Map<String, Object> map) {
        return new GrabBag() {
            @Override
            public Set<String> getKeys() {
                return map.keySet();
            }

            @Override
            @Nullable
            public Class<?> getType(final String key) {
                final var c = map.get(key);
                return switch (c) {
                    case Byte b -> Byte.class;
                    case Short i -> Short.class;
                    case Integer i -> Integer.class;
                    case Long l -> Long.class;
                    case Float v -> Float.class;
                    case Double v -> Double.class;
                    case String string -> String.class;
                    case Boolean b -> Byte.class;
                    case GrabBag grabBag -> GrabBag.class;
                    default -> null;
                };
            }

            @Override
            public int getInt(final String key, final int fallback) {
                if (map.get(key) instanceof Integer t) {
                    return t;
                }
                return fallback;
            }

            @Override
            public short getShort(final String key, final short fallback) {
                if (map.get(key) instanceof Short t) {
                    return t;
                }
                return fallback;
            }

            @Override
            public byte getByte(final String key, final byte fallback) {
                if (map.get(key) instanceof Byte t) {
                    return t;
                }
                return fallback;
            }

            @Override
            public long getLong(final String key, final long fallback) {
                if (map.get(key) instanceof Long t) {
                    return t;
                }
                return fallback;
            }

            @Override
            public float getFloat(final String key, final float fallback) {
                if (map.get(key) instanceof Float t) {
                    return t;
                }
                return fallback;
            }

            @Override
            public double getDouble(final String key, final double fallback) {
                if (map.get(key) instanceof Double t) {
                    return t;
                }
                return fallback;
            }

            @Override
            public boolean getBoolean(final String key, final boolean fallback) {
                if (map.get(key) instanceof Boolean t) {
                    return t;
                }
                return fallback;
            }

            @Override
            public String getString(final String key, final String fallback) {
                if (map.get(key) instanceof String t) {
                    return t;
                }
                return fallback;
            }

            @Override
            public GrabBag getChild(final String key, final GrabBag fallback) {
                if (map.get(key) instanceof GrabBag t) {
                    return t;
                }
                return fallback;
            }
        };
    }
}
