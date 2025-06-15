//package io.github.tobyrue.btc.mixin;
//
//import io.github.tobyrue.btc.item.ModItems;
//import net.minecraft.block.EnchantingTableBlock;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.inventory.Inventory;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.screen.EnchantmentScreenHandler;
//import net.minecraft.screen.ScreenHandler;
//import net.minecraft.screen.ScreenHandlerContext;
//import net.minecraft.util.math.BlockPos;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.gen.Accessor;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.Arrays;
//
//@Mixin(EnchantmentScreenHandler.class)
//public abstract class EnchantingTableMixin {
//
//    @Shadow @Final private Inventory inventory;
//    @Shadow @Final public int[] enchantmentPower;
//    @Shadow @Final public int[] enchantmentId;
//    @Shadow @Final public int[] enchantmentLevel;
//
//    @Accessor("context") abstract ScreenHandlerContext getContext();
//
//    @Inject(method = "onContentChanged", at = @At("TAIL"))
//    private void onPaperPlaced(Inventory inventory, CallbackInfo ci) {
//        if (inventory != this.inventory) return;
//
//        ItemStack itemStack = inventory.getStack(0);
//        if (itemStack.isOf(Items.PAPER)) {
//            // Calculate bookshelf power
//            final int[] bookshelfPower = {0};
//            getContext().run((world, pos) -> {
//                int power = 0;
//                for (BlockPos offset : EnchantingTableBlock.POWER_PROVIDER_OFFSETS) {
//                    if (EnchantingTableBlock.canAccessPowerProvider(world, pos, offset)) {
//                        power++;
//                    }
//                }
//                bookshelfPower[0] = power;
//            });
//            System.out.println(Arrays.toString(bookshelfPower));
//            // If bookshelf power is max (24), offer the special enchant option
//            if (bookshelfPower[0] >= 24) {
//                enchantmentPower[0] = 30;  // XP cost shown
//                enchantmentId[0] = 39;     // arbitrary number — won't matter for paper
//                enchantmentLevel[0] = 30;
//
//                enchantmentPower[1] = 0;
//                enchantmentPower[2] = 0;
//                enchantmentId[1] = -1;
//                enchantmentId[2] = -1;
//                enchantmentLevel[1] = -1;
//                enchantmentLevel[2] = -1;
//            } else {
//                // Not enough power — no options
//                enchantmentPower[0] = 0;
//                enchantmentId[0] = -1;
//                enchantmentLevel[0] = -1;
//            }
//
//            ((ScreenHandler)(Object)this).sendContentUpdates();
//        }
//    }
//
//
//    @Inject(method = "onButtonClick", at = @At("HEAD"), cancellable = true)
//    private void onPaperEnchant(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
//        if (id != 0) return; // Only our custom slot
//
//        ItemStack input = inventory.getStack(0);
//        ItemStack lapis = inventory.getStack(1);
//
//        if (input.isOf(Items.PAPER)) {
//            if (enchantmentPower[0] != 30) {
//                cir.setReturnValue(false);
//                return;
//            }
//
//            if ((lapis.isEmpty() || lapis.getCount() < 3) && !player.isCreative()) {
//                cir.setReturnValue(false);
//                return;
//            }
//
//            if (player.experienceLevel < 30 && !player.getAbilities().creativeMode) {
//                cir.setReturnValue(false);
//                return;
//            }
//
//            if (!player.getAbilities().creativeMode) {
//                player.addExperienceLevels(-30);
//            }
//
//            lapis.decrementUnlessCreative(3, player);
//
//            inventory.setStack(0, new ItemStack(ModItems.ENCHANTED_PAPER));
//            cir.setReturnValue(true);
//        }
//    }
//}
