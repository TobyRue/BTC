package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.entity.BlockEntity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BlockKeyItem extends Item {
    public BlockKeyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient) {
            String uuid = player.getUuidAsString();
            stack.set(BTC.KEY_NAME, Text.literal(uuid));
            stack.set(BTC.PLAYER_UUID, Text.literal(uuid));
            stack.set(BTC.PLAYER_NAME, player.getName());

            Text prettyName = player.getName().copy().append(Text.translatable("item.btc.block_key.of"));

            stack.set(DataComponentTypes.CUSTOM_NAME, prettyName);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {

        if (world instanceof ServerWorld serverWorld) {
            if (entity instanceof PlayerEntity player && !stack.contains(BTC.KEY_NAME) && !stack.contains(BTC.PLAYER_UUID) && !stack.contains(BTC.PLAYER_NAME)) {
                String uuid = player.getUuidAsString();
                stack.set(BTC.KEY_NAME, Text.literal(uuid));
                stack.set(BTC.PLAYER_UUID, Text.literal(uuid));
                stack.set(BTC.PLAYER_NAME, player.getName());

                Text prettyName = player.getName().copy().append(Text.translatable("item.btc.block_key.of"));

                stack.set(DataComponentTypes.CUSTOM_NAME, prettyName);
            }

            Text customName = stack.get(DataComponentTypes.CUSTOM_NAME);
            Text playerName = stack.get(BTC.PLAYER_NAME);


            if (customName != null && playerName != null) {
                String nameStr = customName.getString();
                String playerStr = playerName.getString();
                String supposed = playerStr + Text.translatable("item.btc.block_key.of").getString();
                if (!nameStr.equals(supposed)) {
                    stack.set(BTC.KEY_NAME, Objects.requireNonNull(stack.get(BTC.PLAYER_UUID)).copy().append("." + nameStr));
                }
            }
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();

        if (player != null && player.isSneaking()) {
            BlockEntity be = world.getBlockEntity(context.getBlockPos());
            NbtCompound nbt = be.createNbtWithId(world.getRegistryManager());
            ItemStack key = context.getStack();

            if (!nbt.contains("Lock") && be instanceof LockableContainerBlockEntity lockable && key.contains(BTC.KEY_NAME)) {
                Text keyText = key.get(BTC.KEY_NAME);
                if (keyText == null) return ActionResult.PASS;

                String lockString = keyText.getString();

                if (!world.isClient) {
                    nbt.putString("Lock", lockString);
                    be.readNbt(nbt, world.getRegistryManager());
                    be.markDirty();

                    player.sendMessage(Text.literal("Block Secured.").formatted(Formatting.GREEN), true);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        Text keyText = stack.get(BTC.KEY_NAME);
        if (keyText != null) {
            String fullKey = keyText.getString();
            String displayedKey = truncateBeforeFirstDot(fullKey);

            if (!displayedKey.isEmpty()) {
                tooltip.add(Text.literal("Key: " + displayedKey).formatted(Formatting.GRAY));
                tooltip.add(Text.literal("Player: " + Objects.requireNonNull(stack.get(BTC.PLAYER_NAME)).getString()).formatted(Formatting.GRAY));
            }

            if (type.isAdvanced()) {
                tooltip.add(Text.literal("Full ID: " + fullKey).formatted(Formatting.DARK_GRAY));
            }
        }
    }

    public static String truncateAtFirstDot(String input) {
        if (input == null) return "";
        int dotIndex = input.indexOf('.');
        if (dotIndex == -1) return input;
        return input.substring(0, dotIndex);
    }

    public static String truncateBeforeFirstDot(String input) {
        if (input == null) return "";
        int dotIndex = input.indexOf('.');
        if (dotIndex == -1) return "";
        return input.substring(dotIndex + 1);
    }
}