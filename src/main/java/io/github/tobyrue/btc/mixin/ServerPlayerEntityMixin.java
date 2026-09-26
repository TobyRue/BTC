package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.block.entities.BonfireBlockEntity;
import io.github.tobyrue.btc.packets.BonfireSyncPayload;
import io.github.tobyrue.btc.spell.*;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import io.github.tobyrue.btc.util.LivingEntityMixinAccessor;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {


    @Inject(method = "onDisconnect", at = @At("HEAD"))
    private void btc$clearTickersOnDisconnect(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player instanceof LivingEntityMixinAccessor accessor) {
            accessor.btc$getTickers().clear();
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void btc$onDeath(
            DamageSource damageSource,
            CallbackInfo ci
    ) {
        PlayerEntity entity = (PlayerEntity) (Object) this;
        if (entity.getWorld().isClient()) {
            return;
        }

        if (entity instanceof PlayerEntity player) {


            for (ItemStack stack : player.getInventory().main) {
                cancelItemSpell(stack, entity);
            }


            for (ItemStack stack : player.getInventory().offHand) {
                cancelItemSpell(stack, entity);
            }


            for (ItemStack stack : player.getInventory().armor) {
                cancelItemSpell(stack, entity);
            }
        }
    }

    @Unique
    private void cancelItemSpell(
            ItemStack stack,
            LivingEntity entity
    ) {
        if (stack == null || stack.isEmpty()) {
            return;
        }


        if (!(stack.getItem() instanceof SpellItem spellItem)) {
            return;
        }

        cancelHostSpell(spellItem, stack, entity);
    }


    @Unique
    @SuppressWarnings("unchecked")
    private <T> void cancelHostSpell(
            SpellHost<T> host,
            Object target,
            LivingEntity entity
    ) {

        try {
            SpellDataStore dataStore =
                    host.getSpellDataStore((T) target);

            if (dataStore == null) {
                return;
            }

            Spell spell = dataStore.getSpell();

            if (spell == null) {
                return;
            }

            if (!(spell instanceof ChanneledSpell channeledSpell)) {
                return;
            }

            GrabBag args = dataStore.getArgs();

            if (args == null) {
                return;
            }

            Spell.SpellContext ctx =
                    new Spell.SpellContext(
                            entity.getWorld(),
                            entity.getPos(),
                            entity.getRotationVector(),
                            dataStore,
                            entity,
                            null
                    );


            channeledSpell.onEnd(
                    ctx,
                    args,
                    0
            );


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Inject(method = "getRespawnTarget", at = @At("HEAD"), cancellable = true)
    private void checkBonfirePriority(boolean keepInventory, TeleportTarget.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<TeleportTarget> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        NbtCompound bonfire = ((BonfirePlayerData) player).bTC$getBonfireData();

        if (bonfire != null && bonfire.contains("pos")) {
            BlockPos bonfirePos = BlockPos.fromLong(bonfire.getLong("pos"));
            String dimId = bonfire.getString("dim");
            if (player.getWorld().getBlockEntity(bonfirePos) instanceof BonfireBlockEntity block) {
                double distSq = player.getPos().squaredDistanceTo(bonfirePos.toCenterPos());
                double configRadius = block.getRadius();

                if (distSq <= (configRadius * configRadius)) {
                    ServerWorld targetWorld = player.getServer().getWorld(
                            RegistryKey.of(RegistryKeys.WORLD, Identifier.of(dimId))
                    );

                    if (targetWorld != null) {
                        cir.setReturnValue(new TeleportTarget(targetWorld, bonfirePos.toCenterPos().add(0, 1, 0), Vec3d.ZERO, 0.0f, 0.0f, postDimensionTransition));
                    }
                }
            }
        }
    }

    @Inject(method = "copyFrom", at = @At("RETURN"))
    private void copyBonfireData(ServerPlayerEntity player, boolean alive, CallbackInfo ci) {
        NbtCompound oldData = ((BonfirePlayerData) player).bTC$getBonfireData();

        ((BonfirePlayerData) this).bTC$setBonfireData(oldData);
        BonfireSyncPayload payload = new BonfireSyncPayload(oldData);

        ServerPlayNetworking.send(player, payload);
    }
}