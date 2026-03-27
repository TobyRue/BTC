package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.Ticker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements Ticker.TickerTarget {

    @Shadow public abstract boolean isExperienceDroppingDisabled();

    @Shadow private boolean experienceDroppingDisabled;

    @Shadow protected abstract void dropXp(@Nullable Entity attacker);

    @Unique
    final List<Ticker> tickers = new ArrayList<>();
    @Unique
    private boolean btc$DropsItems = true;

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        tickers.removeIf(Ticker::tick);
    }

    @Override
    public void bTC$add(final Ticker ticker) {
        this.tickers.add(ticker);
    }


    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void btc$writeDrops(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("DropsItems", this.btc$DropsItems);
        nbt.putBoolean("ExperienceDrops", !this.isExperienceDroppingDisabled());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void btc$readDrops(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("DropsItems")) {
            this.btc$DropsItems = nbt.getBoolean("DropsItems");
        }
        if (nbt.contains("ExperienceDrops")) {
            this.experienceDroppingDisabled = !nbt.getBoolean("ExperienceDrops");
        }
    }


    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void btc$checkDrops(ServerWorld world, DamageSource damageSource, CallbackInfo ci) {
        if (!this.btc$DropsItems) {
            this.dropXp(damageSource.getAttacker());
            ci.cancel();
        }
    }
}
