package io.github.tobyrue.btc.entity.custom;

import net.minecraft.block.ChainBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class MineEntity extends Entity {
    public MineEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        var pos = this.getBlockPos();
        this.setNoGravity(false);
        for(Direction direction : Direction.values()) {
            if (this.getWorld().getBlockState(pos.offset(direction)).getBlock() instanceof ChainBlock) {
                this.setPosition(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
                this.setNoGravity(true);
                break;
            }
        }
        this.applyGravity();
        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.isOnGround()) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return super.getPickBlockStack();
    }

    @Override
    protected double getGravity() {
        return 0.07d;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }
}
