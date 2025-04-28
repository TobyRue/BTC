package io.github.tobyrue.btc.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class WaterStaffItem extends StaffItem {
    private static final List<String> ATTACKS = List.of("1", "2", "3", "4", "5", "6");

    public WaterStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        ItemStack itemStack = user.getStackInHand(hand);
        String currentElement = getElement(stack);
        int nextIndex = (ATTACKS.indexOf(currentElement) + 1) % ATTACKS.size();
        String nextElement = ATTACKS.get(nextIndex);

        if (!world.isClient && user.isSneaking()) {
            user.sendMessage(Text.literal("Water Staff set to - " + nextElement), true);
            setElement(stack, nextElement);
            return TypedActionResult.success(stack);
        }

        return super.use(world, user, hand);
    }

    private String getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt(); // Get a modifiable copy
        return nbt.contains("Attack") ? nbt.getString("Attack") : ATTACKS.get(0);
    }

    private void setElement(ItemStack stack, String attack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt(); // Get a modifiable copy
        nbt.putString("Attack", attack); // Update the element
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt)); // Create a new immutable NbtComponent
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        tooltip.add(this.currentAttack(stack).formatted(Formatting.BLUE));
        tooltip.add(this.attackTypes().formatted(Formatting.DARK_AQUA));
    }
    public MutableText currentAttack(ItemStack stack) {return Text.literal("Current Spell: " + getElement(stack));}
    public MutableText attackTypes() {return Text.literal("Attack Types:");}

}
