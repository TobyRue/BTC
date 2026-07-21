package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.regestries.ModComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class PetCharmItem extends Item {
    public PetCharmItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (stack.contains(ModComponents.STORED_MOB_UUID) && world instanceof ServerWorld serverWorld) {
            UUID uuid = stack.get(ModComponents.STORED_MOB_UUID);
            var entity = serverWorld.getEntity(uuid);

            if (entity != null) {
                if (entity instanceof MobEntity mob && mob.getType().isIn(BTC.PET_CHARM_WHITELIST)) {
                    if (tryTeleportToOwner(mob, player)) {
                        player.sendMessage(Text.translatable("item.btc.pet_charm.action.teleport"), true);
                    }

                    String petName = mob.hasCustomName() ? mob.getCustomName().getString() : mob.getType().getName().getString();
                    stack.set(ModComponents.STORED_ENTITY_NAME, petName);

                    String ownerName = player.getName().getString();
                    stack.set(ModComponents.STORED_OWNER_NAME, ownerName);

                    NbtCompound mobNbt = new NbtCompound();
                    mob.writeNbt(mobNbt);
                    mob.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 60));

                    stack.set(ModComponents.STORED_MOB_NBT, mobNbt);
                }
            } else {
                var entityType = stack.get(ModComponents.STORED_ENTITY_TYPE);
                var nbt = stack.get(ModComponents.STORED_MOB_NBT);

                if (entityType != null && nbt != null) {
                    var newEntity = entityType.create(serverWorld);

                    if (newEntity instanceof MobEntity mob && mob.getType().isIn(BTC.PET_CHARM_WHITELIST)) {
                        mob.readNbt(nbt);

                        mob.refreshPositionAndAngles(
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                player.getYaw(),
                                player.getPitch()
                        );

                        mob.setInvulnerable(true);
                        mob.setPersistent();
                        serverWorld.spawnEntity(mob);

                        stack.set(ModComponents.STORED_MOB_UUID, mob.getUuid());
                        player.sendMessage(Text.translatable("item.btc.pet_charm.action.revive"), true);
                    }
                }
            }
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }
    public boolean tryTeleportToOwner(MobEntity entity, LivingEntity owner) {
        if (owner != null) {
            return this.tryTeleportNear(owner.getBlockPos(), entity);
        }
        return false;
    }

    private boolean tryTeleportNear(BlockPos pos, MobEntity entity) {
        for(int i = 0; i < 10; ++i) {
            int j = entity.getRandom().nextBetween(-3, 3);
            int k = entity.getRandom().nextBetween(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = entity.getRandom().nextBetween(-1, 1);
                return this.tryTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k, entity);
            }
        }
        return false;
    }

    private boolean tryTeleportTo(int x, int y, int z, MobEntity entity) {
        if (!this.canTeleportTo(new BlockPos(x, y, z), entity)) {
            return false;
        } else {
            entity.refreshPositionAndAngles((double)x + 0.5, (double)y, (double)z + 0.5, entity.getYaw(), entity.getPitch());
            entity.getNavigation().stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos, MobEntity entity) {
        PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(entity, pos);
        if (pathNodeType != PathNodeType.WALKABLE) {
            return false;
        } else {
            BlockState blockState = entity.getWorld().getBlockState(pos.down());
            if (!this.canTeleportOntoLeaves() && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(entity.getBlockPos());
                return entity.getWorld().isSpaceEmpty(entity, entity.getBoundingBox().offset(blockPos));
            }
        }
    }
    protected boolean canTeleportOntoLeaves() {
        return true;
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        if (stack.contains(ModComponents.STORED_MOB_UUID)) {
            String petName = stack.getOrDefault(ModComponents.STORED_ENTITY_NAME, "Unknown");
            String ownerName = stack.getOrDefault(ModComponents.STORED_OWNER_NAME, "None");
            UUID mobUuid = stack.get(ModComponents.STORED_MOB_UUID);

            Text entityTypeName = Text.literal("Unknown Type");
            var entityType = stack.get(ModComponents.STORED_ENTITY_TYPE);
            if (entityType != null) {
                entityTypeName = entityType.getName();
            }

            tooltip.add(Text.translatable("item.btc.pet_charm.tooltip.stored_mob", petName, entityTypeName).formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("item.btc.pet_charm.tooltip.stored_owner", ownerName).formatted(Formatting.GRAY));

            if (type.isAdvanced() && mobUuid != null) {
                tooltip.add(Text.translatable("item.btc.pet_charm.tooltip.stored_uuid", mobUuid.toString()).formatted(Formatting.DARK_GRAY));
            }
        }
    }
}
