package io.github.tobyrue.btc.util;

import net.minecraft.nbt.NbtCompound;

public interface BonfirePlayerData {
    void btc$setBonfireData(NbtCompound nbt);
    NbtCompound btc$getBonfireData();
}