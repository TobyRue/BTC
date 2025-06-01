package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.block.DungeonWireBlock;
import io.github.tobyrue.btc.wires.WireBlock;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashSet;

import static io.github.tobyrue.btc.block.DungeonWireBlock.POWERED;

public class KeyDispenserBlockEntity extends BlockEntity {

    public final HashSet<String> HASH_SET = new HashSet<>();

    public KeyDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KEY_DISPENSER_ENTITY, pos, state);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        var uuid = player.getUuid().toString();
        ItemStack dropStack = new ItemStack(ModItems.RUBY_TRIAL_KEY);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            Direction dirFromNeighborToThis = direction.getOpposite();
            if (neighborState.getBlock() instanceof WireBlock) {
                var property = neighborState.get(WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(dirFromNeighborToThis));
                if (property == WireBlock.ConnectionType.OUTPUT && neighborState.get(WireBlock.POWERED)) {
                    if (!HASH_SET.contains(uuid)) {
                        HASH_SET.add(uuid);
                        world.addParticle(ParticleTypes.GUST, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0, 0);
                        if (!world.isClient) {
                            player.getInventory().offerOrDrop(dropStack);
                        }
                        markDirty();
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
