package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.client.screen.ScrollTableScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ScrollTableBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<BlockPos>, Inventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(11, ItemStack.EMPTY);

    public ScrollTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SCROLL_TABLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Scroll Table");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ScrollTableScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inventory, registryLookup);
    }

    @Override public int size() { return this.inventory.size(); }
    @Override public boolean isEmpty() { return this.inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getStack(int slot) { return this.inventory.get(slot); }
    @Override public ItemStack removeStack(int slot, int amount) { return Inventories.splitStack(this.inventory, slot, amount); }
    @Override public ItemStack removeStack(int slot) { return Inventories.removeStack(this.inventory, slot); }
    @Override public void setStack(int slot, ItemStack stack) { this.inventory.set(slot, stack); }
    @Override public boolean canPlayerUse(PlayerEntity player) { return Inventory.canPlayerUse(this, player); }
    @Override public void clear() { this.inventory.clear(); }
}