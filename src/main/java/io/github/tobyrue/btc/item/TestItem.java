//package io.github.tobyrue.btc.item;
//
//import io.github.tobyrue.btc.client.screen.codex.Codex;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.fabricmc.loader.api.FabricLoader;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.text.Text;
//import net.minecraft.util.Formatting;
//import net.minecraft.util.Hand;
//import net.minecraft.util.TypedActionResult;
//import net.minecraft.world.World;
//
//import java.io.FileReader;
//
//public class TestItem extends Item {
//    public TestItem(Settings settings) {
//        super(settings);
//    }
////TODO Remove this from build as it is unstable and prevents servers from working
//
//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
//            if (user instanceof ServerPlayerEntity serverPlayerEntity) {
//                try (var reader = new FileReader("C:\\Users\\tobin\\IdeaProjects\\BTC\\spellbook.xml")) {
//                    var codex = Codex.parse(reader);
//                    for (var page : codex.children()) {
//                        Codex.sendPageToChat(page, serverPlayerEntity);
//                    }
//                } catch (Throwable t) {
//                    t.printStackTrace();
//                    user.sendMessage(Text.literal(String.format("[%s]: %s", t.getClass().getSimpleName(), t.getMessage())).formatted(Formatting.RED));
//                }
//            }
//
//            if (world.isClient()) {
//                openCodexScreen();
//            }
//        }
//        return TypedActionResult.success(user.getStackInHand(hand));
//    }
//
//    @Environment(EnvType.CLIENT)
//    private void openCodexScreen() {
//        try (var reader = new FileReader("C:\\Users\\tobin\\IdeaProjects\\BTC\\spellbook.xml")) {
//            net.minecraft.client.MinecraftClient.getInstance().setScreen(
//                    new io.github.tobyrue.btc.client.screen.codex.CodexScreen(Codex.parse(reader))
//            );
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//    }
//}