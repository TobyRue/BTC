package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import io.github.tobyrue.btc.block.OminousBeaconBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OminousBeaconBlockEntity extends BlockEntity implements BlockEntityTicker<OminousBeaconBlockEntity> {
    static final int MAX_LENGTH = 32;
    public OminousBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, pos, state);
    }

    private int beamLength = 0;

    public int getBeamLength() {
        return this.beamLength;
    }

    private void updateBeam(World world, BlockPos pos, BlockState state) {
        var direction = state.get(OminousBeaconBlock.FACING);
        if(direction == Direction.UP || direction == Direction.NORTH || direction == Direction.EAST) {
            this.beamLength = 0;
            for(int l = 1; l < MAX_LENGTH + 2; l++) {
                var offsetPos = pos.offset(direction, l);
                var offsetState = world.getBlockState(offsetPos);
                if(offsetState.getBlock() == ModBlocks.OMINOUS_BEACON && offsetState.get(OminousBeaconBlock.FACING) == direction.getOpposite()) {
                    this.beamLength = l - 1;
                    break;
                }
            }
            for(int l = 1; l < this.beamLength + 1; l++) {
                var offsetPos = pos.offset(direction, l);
                var offsetState = world.getBlockState(offsetPos);
                if(offsetState.isOf(Blocks.BEDROCK) || offsetState.getOpacity(world, offsetPos) < 15) {
                    continue;
                } else {
                    world.breakBlock(offsetPos, true);
                }
            }
            world.getNonSpectatingEntities(LivingEntity.class, new Box(pos.toCenterPos(), pos.offset(direction, this.beamLength).toCenterPos()).expand(0.5)).forEach(entity -> entity.damage(ModDamageTypes.of(world, ModDamageTypes.BEACON_BURN), 2.0f));
        } else {
            this.beamLength = 0;
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, OminousBeaconBlockEntity blockEntity) {
        updateBeam(world, pos, state);
    }




    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        // Read custom data from NBT
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        // Write custom data to NBT
    }
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        System.out.println("Read Component");
    }

    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        System.out.println("Add Component");
    }
}
