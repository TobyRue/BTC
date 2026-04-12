package io.github.tobyrue.btc.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker("writeCustomDataToNbt")
    void callWriteCustomDataToNbt(NbtCompound nbt);

    @Invoker("readCustomDataFromNbt")
    void callReadCustomDataFromNbt(NbtCompound nbt);
}
