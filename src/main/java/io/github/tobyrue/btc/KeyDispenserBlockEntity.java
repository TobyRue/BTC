package io.github.tobyrue.btc;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;

import static io.github.tobyrue.btc.DungeonWireBlock.POWERED;

public class KeyDispenserBlockEntity extends BlockEntity {

    public final HashSet<String> HASH_SET = new HashSet<>();

    public KeyDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KEY_DISPENSER_ENTITY, pos, state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        var uuid = player.getUuid().toString();
        System.out.println("Clicked:" + uuid);
        ItemStack dropStack = new ItemStack(ModItems.RUBY_TRIAL_KEY);
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof DungeonWireBlock && neighborState.get(POWERED)) {
                if (!world.isClient) {
                    if (!HASH_SET.contains(uuid)) {
                        HASH_SET.add(uuid);
                        player.getInventory().offerOrDrop(dropStack);
                        markDirty();  // Mark the block entity as dirty to ensure data is saved
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        // Serialize the HashSet<String> to NbtList
        NbtList nbtList = new NbtList();
        for (String uuid : HASH_SET) {
            nbtList.add(NbtString.of(uuid)); // Directly add the string
        }
        nbt.put("CustomData", nbtList); // Keep the same key for reading
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        // Deserialize the NbtList back to HashSet<String>
        HASH_SET.clear();
        NbtList nbtList = nbt.getList("CustomData", NbtElement.STRING_TYPE); // Use STRING_TYPE
        for (int i = 0; i < nbtList.size(); i++) {
            String uuid = nbtList.getString(i);
            HASH_SET.add(uuid);
        }
    }


    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }
}
