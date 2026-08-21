package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.client.screen.codex.Codex;
import io.github.tobyrue.btc.client.screen.codex.CodexScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.io.FileReader;

public class TestItem extends Item {
    public TestItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            try (var reader = new FileReader("C:\\Users\\tobin\\IdeaProjects\\BTC\\spellbook.xml")) {
                var codex = Codex.parse(reader);
                for (var page : codex.children()) {
                    Codex.sendPageToChat(page, serverPlayerEntity);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                user.sendMessage(Text.literal(String.format("[%s]: %s", t.getClass().getSimpleName(), t.getMessage())).formatted(Formatting.RED));
            }
        }

        if (world.isClient()) {
            try (var reader = new FileReader("C:\\Users\\tobin\\IdeaProjects\\BTC\\spellbook.xml")) {
                MinecraftClient.getInstance().setScreen(new CodexScreen(Codex.parse(reader)));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}