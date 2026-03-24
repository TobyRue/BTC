package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.regestries.ModComponents;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeadItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

public class PetTotemItem extends Item {
    public PetTotemItem(Settings settings) {
        super(settings);
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        var stack = player.getStackInHand(hand);
        if (stack.contains(ModComponents.STORED_MOB_UUID) && world instanceof ServerWorld serverWorld) {
            UUID uuid = stack.get(ModComponents.STORED_MOB_UUID);
            var entity = serverWorld.getEntity(uuid);
            if (entity != null) {
                if (entity instanceof TameableEntity tameableEntity) {
                    tryTeleportToOwner(tameableEntity, player);
                    var nbt = tameableEntity.writeNbt(new NbtCompound());
                    stack.set(ModComponents.STORED_MOB_NBT, nbt);
                } else if (entity instanceof Tameable tameable && entity instanceof MobEntity mob) {
                    tryTeleportToOwner(mob, player);
                    var nbt = mob.writeNbt(new NbtCompound());
                    stack.set(ModComponents.STORED_MOB_NBT, nbt);
                }
            } else {
                var entityType = stack.get(ModComponents.STORED_ENTITY_TYPE);
                var nbt = stack.get(ModComponents.STORED_MOB_NBT);
                if (entityType != null && nbt != null) {
                    var newEntity = entityType.create(serverWorld);
                    if (newEntity instanceof TameableEntity tameableEntity) {
                        tameableEntity.readNbt(nbt);
                        tameableEntity.refreshPositionAndAngles(
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                player.getYaw(),
                                player.getPitch()
                        );
                        tameableEntity.setInvulnerable(true);
                        tameableEntity.setPersistent();
                        serverWorld.spawnEntity(tameableEntity);
                        stack.set(ModComponents.STORED_MOB_UUID, tameableEntity.getUuid());
                        player.sendMessage(Text.literal("Pet revived"), true);
                    } else if (newEntity instanceof Tameable tameable && newEntity instanceof MobEntity mob) {
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
                        player.sendMessage(Text.literal("Pet revived"), true);
                    }
                }
            }
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }
    public void tryTeleportToOwner(MobEntity entity, LivingEntity owner) {
        if (owner != null) {
            this.tryTeleportNear(owner.getBlockPos(), entity);
        }
    }

    private void tryTeleportNear(BlockPos pos, MobEntity entity) {
        for(int i = 0; i < 10; ++i) {
            int j = entity.getRandom().nextBetween(-3, 3);
            int k = entity.getRandom().nextBetween(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = entity.getRandom().nextBetween(-1, 1);
                if (this.tryTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k, entity)) {
                    return;
                }
            }
        }

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
}
