package io.github.tobyrue.btc.util;

import net.minecraft.nbt.NbtCompound;

public interface BonfirePlayerData {
    void bTC$setBonfireData(NbtCompound nbt);
    NbtCompound bTC$getBonfireData();
}