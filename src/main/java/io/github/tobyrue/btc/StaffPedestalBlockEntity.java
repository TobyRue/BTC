package io.github.tobyrue.btc;

import com.google.common.base.Ticker;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.Vibrations;
import org.jetbrains.annotations.Nullable;
import static io.github.tobyrue.btc.BTC.println;

import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Stream;


public class StaffPedestalBlockEntity extends BlockEntity implements BlockEntityTicker<StaffPedestalBlockEntity> {

    public final HashSet<String> HASH_SET = new HashSet<>();

    public StaffPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STAFF_PEDESTAL_BLOCK_ENTITY, pos, state);
    }
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        var uuid = player.getUuid().toString();

        if(!HASH_SET.contains(uuid)) {
            HASH_SET.add(uuid);
            this.markDirty();
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
            System.out.println("clicked once");
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }


    @Override
    public void tick(World world, BlockPos pos, BlockState state, StaffPedestalBlockEntity blockEntity) {



    }


    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        // Serialize HashSet<String> to NbtList
        NbtList nbtList = new NbtList();
        for (String s : HASH_SET) {
            nbtList.add(NbtString.of(s));
        }
        nbt.put("CustomData", nbtList);
        System.out.println("NBT data written: " + nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        // Deserialize NbtList to HashSet<String>
        HASH_SET.clear();
        NbtList nbtList = nbt.getList("CustomData", NbtElement.STRING_TYPE);
        for (int i = 0; i < nbtList.size(); i++) {
            HASH_SET.add(nbtList.getString(i));
        }
        System.out.println("NBT data read: " + nbt);

    }
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        println("Read Component");
    }

    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        println("Add Component");
    }
}
