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
            sendFeedback(String.format("Current Offsets:\nHandle Rot: (%.2f, %.2f, %.2f)\nHandle Trans: (%.2f, %.2f, %.2f)\nItem Trans: (%.2f, %.2f, %.2f)",
                    DragonStaffModelRenderer.rotX, DragonStaffModelRenderer.rotY, DragonStaffModelRenderer.rotZ,
                    DragonStaffModelRenderer.transX, DragonStaffModelRenderer.transY, DragonStaffModelRenderer.transZ,
                    DragonStaffModelRenderer.itemTransX, DragonStaffModelRenderer.itemTransY, DragonStaffModelRenderer.itemTransZ));
            return;
        }

        if (parts.length < 2) {
            sendFeedback("Missing value argument!");
            return;
        }

        try {
            float val = Float.parseFloat(parts[1]);
            switch (sub) {
                case "rx" -> DragonStaffModelRenderer.rotX = val;
                case "ry" -> DragonStaffModelRenderer.rotY = val;
                case "rz" -> DragonStaffModelRenderer.rotZ = val;

                case "tx" -> DragonStaffModelRenderer.transX = val;
                case "ty" -> DragonStaffModelRenderer.transY = val;
                case "tz" -> DragonStaffModelRenderer.transZ = val;

                case "fx" -> DragonStaffModelRenderer.itemTransX = val;
                case "fy" -> DragonStaffModelRenderer.itemTransY = val;
                case "fz" -> DragonStaffModelRenderer.itemTransZ = val;


                case "fs" -> DragonStaffModelRenderer.itemScale = val;
                case "s" -> DragonStaffModelRenderer.scale = val;

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