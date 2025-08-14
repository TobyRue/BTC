package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.tobyrue.btc.client.BTCClient;
import io.github.tobyrue.btc.packets.QuickElementPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "onKeyPressed", at = @At("HEAD"), cancellable = true)
    private static void onKeyPressMixin(InputUtil.Key key, CallbackInfo ci) {
        if (!(key instanceof InputUtil.Key keyboardKey)) return;

        int glfwKey = keyboardKey.getCode();

        // Check if the custom modifier keybinding is pressed
        boolean modifierDown = BTCClient.keyBinding1.isPressed();

        // Only intercept number keys 1–9 and 0
        if (modifierDown && (glfwKey >= GLFW.GLFW_KEY_1 && glfwKey <= GLFW.GLFW_KEY_9 || glfwKey == GLFW.GLFW_KEY_0)) {
            int slotNumber = (glfwKey == GLFW.GLFW_KEY_0)
                    ? 10
                    : glfwKey - GLFW.GLFW_KEY_1 + 1;

            // Send packet with player UUID
            ClientPlayNetworking.send(
                    new QuickElementPayload(slotNumber)
            );

            ci.cancel(); // Prevent normal hotbar switch
        }
    }
}
