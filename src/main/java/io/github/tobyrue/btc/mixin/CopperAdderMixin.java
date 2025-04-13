//package io.github.tobyrue.btc.mixin;
//
//import com.google.common.base.Suppliers;
//import com.google.common.collect.BiMap;
//import io.github.tobyrue.btc.block.ModBlocks;
//import net.minecraft.block.Block;
//import net.minecraft.item.HoneycombItem;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.function.Supplier;
//
//
//@Mixin(HoneycombItem.class)
//public class CopperAdderMixin {
//    @Inject(method = "<clinit>", at = @At("TAIL"))
//    private static void injectWaxables(CallbackInfo ci) {
//        BiMap<Block, Block> map = HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get();
//        map.put(ModBlocks.UNOXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON);
//        map.put(ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
//        map.put(ModBlocks.WEATHERED_COPPER_BUTTON, ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
//        map.put(ModBlocks.OXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);
//    }
//}
