//package io.github.tobyrue.btc.mixin;
//
//import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
//import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.BlockEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.chunk.WorldChunk;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//
//@Mixin({WorldChunk.class})
//public class WorldChunkMixin {
//    @WrapOperation(
//            method = {"setBlockState"},
//            at = {@At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/block/entity/BlockEntity;onBlockReplaced(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"
//            )}
//    )
//    private void fixCrashHopefully(BlockEntity instance, BlockPos pos, BlockState oldState, Operation<Void> original) {
//        try {
//            original.call(instance, pos, oldState);
//        } catch (Throwable var6) {
//            instance.markRemoved();
//        }
//    }
//}
