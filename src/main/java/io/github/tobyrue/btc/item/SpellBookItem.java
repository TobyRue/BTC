package io.github.tobyrue.btc.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class SpellBookItem extends Item {
    private static final List<String> ELEMENTS = List.of("Water", "Fire", "Dragon", "Wind");

    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        // Get the current element
        String currentElement = getElement(stack);
        int nextIndex = (ELEMENTS.indexOf(currentElement) + 1) % ELEMENTS.size();
        String nextElement = ELEMENTS.get(nextIndex);

        // Update the item
        setElement(stack, nextElement);

        // Show message above the hotbar
        if (!world.isClient && player.isSneaking()) {
            player.sendMessage(Text.literal("Spellbook set to: " + nextElement), true);
            return TypedActionResult.success(stack);
        }

        return super.use(world, player, hand);
    }

    private String getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt(); // Get a modifiable copy
        return nbt.contains("Element") ? nbt.getString("Element") : ELEMENTS.get(0); // Default to "Water"
    }

    private void setElement(ItemStack stack, String element) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt(); // Get a modifiable copy

        nbt.putString("Element", element); // Update the element
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt)); // Create a new immutable NbtComponent
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(getDescription1(stack).formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.DARK_AQUA));
    }

    public MutableText getDescription1(ItemStack stack) {
        return Text.literal("Current Spell: " + getElement(stack));
    }
}

