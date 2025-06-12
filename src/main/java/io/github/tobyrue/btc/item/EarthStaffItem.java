package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.entity.custom.EarthSpikeEntity;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EarthStaffItem extends StaffItem {
    private static final List<String> ATTACKS = List.of("Line of Earth Spikes", "2", "3", "4", "5", "6");
    private static final Integer SPIKE_Y_RANGE = 12;
    private static final Integer SPIKE_COUNT = 8;

    public EarthStaffItem(Settings settings) {
        super(settings);
    }
    @Nullable
    public BlockPos findSpawnableGround(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());
        // Start from top and go downwards
        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            // Improved block check to ensure solid block and air above or open space above
            if (world.getBlockState(pos).isSolidBlock(world, pos) && !world.getBlockState(pos.up()).isSolidBlock(world, pos.up()) && !world.getBlockState(pos.up()).isOf(Blocks.CHEST)) {
                return pos;
            }
        }

        // Fallback if no valid ground is found
        return null;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        ItemStack itemStack = user.getStackInHand(hand);
        String currentElement = getElement(stack);
        int nextIndex = (ATTACKS.indexOf(currentElement) + 1) % ATTACKS.size();
        String nextElement = ATTACKS.get(nextIndex);

        if (!world.isClient && user.isSneaking()) {
            user.sendMessage(Text.literal("Earth Staff set to - " + nextElement), true);
            setElement(stack, nextElement);
            return TypedActionResult.success(stack);
        }
        if (getElement(stack).equals("Line of Earth Spikes") && !user.isSneaking()) {
            spawnEarthSpikesTowardsYaw(world, user, SPIKE_Y_RANGE, SPIKE_COUNT);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("2") && !user.isSneaking()) {
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("3") && !user.isSneaking()) {
            return TypedActionResult.success(itemStack);
        } else if(getElement(stack).equals("4") && !user.isSneaking()){
            return TypedActionResult.success(itemStack);
        }
        return super.use(world, user, hand);
    }
    public void spawnEarthSpikesTowardsYaw(World world, LivingEntity caster, int yRange, int spikeCount) {
        float yaw = caster.getYaw();
        double rad = Math.toRadians(yaw);

        double stepX = -Math.sin(rad) * 1.5;
        double stepZ = Math.cos(rad) * 1.5;

        double startX = caster.getX();
        double startZ = caster.getZ();
        double startY = caster.getY();


        for (int i = 0; i < spikeCount; i++) {
            double x = startX + stepX * i;
            double z = startZ + stepZ * i;
            BlockPos searchPos = new BlockPos((int) x, (int) startY, (int) z);


            BlockPos groundPos = findSpawnableGround(world, searchPos, yRange);

            if (groundPos != null) {

                EarthSpikeEntity spike = new EarthSpikeEntity(world, groundPos.getX(), groundPos.getY(), groundPos.getZ(), yaw, caster);
                caster.getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(caster));
                world.spawnEntity(spike);
            }
        }
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
        tooltip.add(this.attackTypes().formatted(Formatting.GRAY));
    }
    public MutableText currentAttack(ItemStack stack) {return Text.literal("Current Spell: " + getElement(stack));}
    public MutableText attackTypes() {return Text.literal("Attack Types:");}

}
