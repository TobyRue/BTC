package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.TrialCoreBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrialCoreBlockEntity extends BlockEntity {
    private final List<Identifier> functions = new ArrayList<>();
    private final Random random = new Random();

    public TrialCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRIAL_CORE_BLOCK_ENTITY, pos, state);
    }

    public void addFunction(Identifier id) {
        functions.add(id);
        markDirty();
    }

    public void runRandomFunction(ServerWorld world, BlockPos pos) {
        if (functions.isEmpty()) return;

        Identifier selected = functions.get(random.nextInt(functions.size()));
        CommandFunctionManager manager = world.getServer().getCommandFunctionManager();

        manager.getFunction(selected).ifPresent(function -> {
            BlockState state = this.getCachedState();
            Direction facing = state.contains(TrialCoreBlock.FACING) ? state.get(TrialCoreBlock.FACING) : Direction.NORTH;


            float yaw = switch (facing) {
                case SOUTH -> 0.0F;
                case WEST -> 90.0F;
                case NORTH -> 180.0F;
                case EAST -> 270.0F;
                default -> 0.0F;
            };

            float pitch = switch (facing) {
                case UP -> -90.0F;
                case DOWN -> 90.0F;
                default -> 0.0F;
            };
            ServerCommandSource source = world.getServer().getCommandSource()
                    .withWorld(world)
                    .withPosition(Vec3d.ofCenter(pos))
                    .withRotation(new Vec2f(pitch, yaw)) // Integrates direction vectors directly into the source
                    .withLevel(2)
                    .withSilent();

            manager.execute(function, source);
        });
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        NbtList list = new NbtList();
        for (Identifier id : functions) {
            list.add(NbtString.of(id.toString()));
        }
        nbt.put("Functions", list);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        functions.clear();
        NbtList list = nbt.getList("Functions", NbtElement.STRING_TYPE);
        for (int i = 0; i < list.size(); i++) {
            functions.add(Identifier.of(list.getString(i)));
        }
    }
}