package io.github.tobyrue.btc.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class BTCChatTuner {

    public static void register() {
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message.startsWith("!btc")) {
                handleCommand(message.substring(4).trim());
                return false;
            }
            return true;
        });
    }

    private static void handleCommand(String input) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        String[] parts = input.split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            sendFeedback("Commands:\n" +
                    "!btc rx/ry/rz [val] - Set Handle Rotation X/Y/Z\n" +
                    "!btc tx/ty/tz [val] - Set Handle Translation X/Y/Z\n" +
                    "!btc fx/fy/fz [val] - Set Fire Translation X/Y/Z\n" +
                    "!btc print - Print current values");
            return;
        }

        String sub = parts[0].toLowerCase();

        if (sub.equals("print")) {
            sendFeedback(String.format("Current Offsets:\nHandle Rot: (%.2f, %.2f, %.2f)\nHandle Trans: (%.2f, %.2f, %.2f)\nFire Trans: (%.2f, %.2f, %.2f)",
                    FireStaffModelRenderer.rotX, FireStaffModelRenderer.rotY, FireStaffModelRenderer.rotZ,
                    FireStaffModelRenderer.transX, FireStaffModelRenderer.transY, FireStaffModelRenderer.transZ,
                    FireStaffModelRenderer.fireTransX, FireStaffModelRenderer.fireTransY, FireStaffModelRenderer.fireTransZ));
            return;
        }

        if (parts.length < 2) {
            sendFeedback("Missing value argument!");
            return;
        }

        try {
            float val = Float.parseFloat(parts[1]);
            switch (sub) {
                case "rx" -> FireStaffModelRenderer.rotX = val;
                case "ry" -> FireStaffModelRenderer.rotY = val;
                case "rz" -> FireStaffModelRenderer.rotZ = val;

                case "tx" -> FireStaffModelRenderer.transX = val;
                case "ty" -> FireStaffModelRenderer.transY = val;
                case "tz" -> FireStaffModelRenderer.transZ = val;

                case "fx" -> FireStaffModelRenderer.fireTransX = val;
                case "fy" -> FireStaffModelRenderer.fireTransY = val;
                case "fz" -> FireStaffModelRenderer.fireTransZ = val;


                case "fs" -> FireStaffModelRenderer.fireScale = val;
                case "s" -> FireStaffModelRenderer.scale = val;

                default -> {
                    sendFeedback("Unknown key: " + sub);
                    return;
                }
            }
            sendFeedback("Set " + sub + " to " + val);

        } catch (NumberFormatException e) {
            sendFeedback("Invalid float number: " + parts[1]);
        }
    }

    private static void sendFeedback(String msg) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§6[BTC Tuner] §r" + msg), false);
        }
    }
}