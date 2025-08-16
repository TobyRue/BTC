package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.Ticker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements Ticker.TickerTarget {

    @Unique
    final List<Ticker> tickers = new ArrayList<>();

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        tickers.removeIf(Ticker::tick);
    }

    @Override
    public void add(final Ticker ticker) {
        this.tickers.add(ticker);
    }
}
