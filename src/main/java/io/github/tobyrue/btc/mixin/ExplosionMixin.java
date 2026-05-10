package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.BTC;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public abstract class ExplosionMixin {

    @Shadow @Final private World world;
    @Shadow @Final private double x;
    @Shadow @Final private double y;
    @Shadow @Final private double z;
    @Shadow public abstract List<BlockPos> getAffectedBlocks();

    @Inject(method = "collectBlocksAndDamageEntities", at = @At("TAIL"))
    private void protectStructure(CallbackInfo ci) {
        if (this.world.isClient || !(this.world instanceof ServerWorld serverWorld)) return;

        BlockPos pos = BlockPos.ofFloored(this.x, this.y, this.z);

        StructureStart start = serverWorld.getStructureAccessor()
                .getStructureContaining(pos, BTC.BETTER_TRIAL_CHAMBERS_TAG);

        if (start.hasChildren()) {
            this.getAffectedBlocks().clear();
        }
    }
}