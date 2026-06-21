package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FancyPotBlockEntity extends BlockEntity implements LootableInventory, SingleStackInventory.SingleStackBlockEntityInventory {
    private ItemStack stack;
    @Nullable
    protected RegistryKey<LootTable> lootTableId;
    protected long lootTableSeed;

    public FancyPotBlockEntity(BlockPos pos, BlockState state) {
        super(switch (state.getBlock()) {
            case Block block when state.isOf(ModBlocks.FANCY_RED_POT) -> ModBlockEntities.FANCY_RED_BLOCK_ENTITY;
            case Block block when state.isOf(ModBlocks.FANCY_GREEN_POT) -> ModBlockEntities.FANCY_GREEN_BLOCK_ENTITY;
            case Block block when state.isOf(ModBlocks.FANCY_BLUE_POT) -> ModBlockEntities.FANCY_BLUE_BLOCK_ENTITY;
            default -> throw new IllegalStateException("Unexpected value: " + state.getBlock());
        }, pos, state);
        this.stack = ItemStack.EMPTY;
    }


    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt) && !this.stack.isEmpty()) {
            nbt.put("item", this.stack.encode(registryLookup));
        }

    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (!this.readLootTable(nbt)) {
            if (nbt.contains("item", NbtElement.COMPOUND_TYPE)) {
                this.stack = ItemStack.fromNbt(registryLookup, nbt.getCompound("item")).orElse(ItemStack.EMPTY);
            }
        }

    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    @Nullable
    public RegistryKey<LootTable> getLootTable() {
        return this.lootTableId;
    }

    @Override
    public void setLootTable(@Nullable RegistryKey<LootTable> lootTable) {
        this.lootTableId = lootTable;
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        componentMapBuilder.add(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(List.of(this.stack)));
    }

    @Override
    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        this.stack = components.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).copyFirstStack();
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove("sherds");
        nbt.remove("item");
    }

    public ItemStack getStack() {
        this.generateLoot((PlayerEntity)null);
        return this.stack;
    }

    public ItemStack decreaseStack(int count) {
        this.generateLoot((PlayerEntity)null);
        ItemStack itemStack = this.stack.split(count);
        if (this.stack.isEmpty()) {
            this.stack = ItemStack.EMPTY;
        }

        return itemStack;
    }

    public void setStack(ItemStack stack) {
        this.generateLoot((PlayerEntity)null);
        this.stack = stack;
    }

    public ItemStack asStack(BlockState state) {
        ItemStack itemStack = state.getBlock().asItem().getDefaultStack();
        itemStack.applyComponentsFrom(this.createComponentMap());
        return itemStack;
    }


    @Override
    public BlockEntity asBlockEntity() {
        return this;
    }
}
