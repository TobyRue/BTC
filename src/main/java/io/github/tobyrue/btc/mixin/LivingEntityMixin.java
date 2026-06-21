package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.Ticker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
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

    @Unique
    private final List<Identifier> btc$functions = new ArrayList<>();

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

        NbtList list = new NbtList();
        for (Identifier id : this.btc$functions) {
            list.add(NbtString.of(id.toString()));
        }
        nbt.put("Functions", list);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void btc$readDrops(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("DropsItems")) {
            this.btc$DropsItems = nbt.getBoolean("DropsItems");
        }
        if (nbt.contains("ExperienceDrops")) {
            this.experienceDroppingDisabled = !nbt.getBoolean("ExperienceDrops");
        }
        this.btc$functions.clear();
        if (nbt.contains("Functions", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("Functions", NbtElement.STRING_TYPE);
            for (int i = 0; i < list.size(); i++) {
                this.btc$functions.add(Identifier.of(list.getString(i)));
            }
        }
    }


    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void btc$checkDrops(ServerWorld world, DamageSource damageSource, CallbackInfo ci) {
        btc$runRandomDeathFunction(world);

        if (!this.btc$DropsItems) {
            this.dropXp(damageSource.getAttacker());
            ci.cancel();
        }
    }

    @Unique
    private void btc$runRandomDeathFunction(ServerWorld world) {
        if (this.btc$functions.isEmpty()) return;

        Entity entity = (Entity) (Object) this;

        Identifier selected = this.btc$functions.get(entity.getRandom().nextInt(this.btc$functions.size()));
        CommandFunctionManager manager = world.getServer().getCommandFunctionManager();

        manager.getFunction(selected).ifPresent(function -> {
            ServerCommandSource source = world.getServer().getCommandSource()
                    .withWorld(world)
                    .withPosition(entity.getPos())
                    .withRotation(entity.getRotationClient())
                    .withLevel(2)
                    .withSilent();

            manager.execute(function, source);
        });
    }
}
