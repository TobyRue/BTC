package io.github.tobyrue.btc.block.fluids;

import io.github.tobyrue.btc.BTC;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModFluids {
    public static FlowableFluid TOXIC_SLUDGE_SOURCE  = Registry.register(Registries.FLUID, BTC.identifierOf("toxic_sludge"), new ToxicSludgeFluid.Still());
    public static FlowableFluid FLOWING_TOXIC_SLUDGE =  Registry.register(Registries.FLUID, BTC.identifierOf("flowing_toxic_sludge"), new ToxicSludgeFluid.Flowing());
    public static void initialize() {
    }
}
