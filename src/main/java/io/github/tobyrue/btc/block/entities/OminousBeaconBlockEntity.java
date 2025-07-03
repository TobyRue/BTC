package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import io.github.tobyrue.btc.block.OminousBeaconBlock;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.fabricmc.fabric.mixin.screenhandler.ServerPlayerEntityMixin;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class OminousBeaconBlockEntity extends BlockEntity implements BlockEntityTicker<OminousBeaconBlockEntity> {
    static final int MAX_LENGTH = 32;
    public OminousBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, pos, state);
    }

    private int beamLength = 0;
    private final List<BeaconBlockEntity.BeamSegment> beamSegments = new ArrayList<>();

    public List<BeaconBlockEntity.BeamSegment> getBeamSegments() {
        return this.beamSegments;
    }

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
            //TODO
            double radiusL = 25.0;
            double radiusS = 15.0;
            for (int l = 0; l <= this.beamLength; l++) {
                BlockPos beamPos = pos.offset(direction, l);
                Vec3d beamCenter = Vec3d.ofCenter(beamPos);

                world.getNonSpectatingEntities(HostileEntity.class, new Box(
                                beamPos.getX() - radiusL, beamPos.getY() - radiusL, beamPos.getZ() - radiusL,
                                beamPos.getX() + radiusL, beamPos.getY() + radiusL, beamPos.getZ() + radiusL))
                        .forEach(entity -> {
                            if (entity.getPos().distanceTo(beamCenter) <= radiusL) {
                                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 0));
                                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 1));
                            }
                        });

                world.getNonSpectatingEntities(PlayerEntity.class, new Box(
                                beamPos.getX() - radiusS, beamPos.getY() - radiusS, beamPos.getZ() - radiusS,
                                beamPos.getX() + radiusS, beamPos.getY() + radiusS, beamPos.getZ() + radiusS))
                        .forEach(player -> {
                            if (player.getPos().distanceTo(beamCenter) <= radiusS && this.beamLength != 0) {
                                player.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(ModStatusEffects.NO_NATURAL_REGENERATION), 200, 0));                            }
                        });
            }


            world.getNonSpectatingEntities(LivingEntity.class, new Box(pos.toCenterPos(), pos.offset(direction, this.beamLength).toCenterPos()).expand(0.5)).forEach(entity -> entity.damage(ModDamageTypes.of(world, ModDamageTypes.BEACON_BURN), 2.0f));
            world.getNonSpectatingEntities(LivingEntity.class, new Box(pos.toCenterPos(), pos.offset(direction, this.beamLength).toCenterPos()).expand(0.5)).forEach(entity -> entity.setOnFireFor(3));
            if (this.beamLength > 0) {
                this.beamSegments.add(new BeaconBlockEntity.BeamSegment(0xFFFFFF));
            }
        } else {
            this.beamSegments.clear();
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
    }

    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
    }
}
