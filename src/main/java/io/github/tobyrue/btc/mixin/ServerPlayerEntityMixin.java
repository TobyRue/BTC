package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.block.entities.BonfireBlockEntity;
import io.github.tobyrue.btc.packets.BonfireSyncPayload;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

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