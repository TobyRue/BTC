package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.TrialCoreBlock;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StructureCoreBlockEntity extends BlockEntity {


    private final List<Identifier> functions = new ArrayList<>();


    private String note = "";

    public int getDataSetCount() {
        return dataSetCount;
    }

    public void setDataSetCount(int dataSetCount) {
        this.dataSetCount = dataSetCount;
    }

    private int dataSetCount = 0;
    private final Random random = new Random();

    public StructureCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STRUCTURE_CORE_BLOCK_ENTITY, pos, state);
    }

    public List<Identifier> getFunctions() {
        return functions;
    }

    public void addFunction(Identifier id) {
        functions.add(id);
        markDirty();
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
                    .withRotation(new Vec2f(pitch, yaw))
                    .withLevel(2)
                    .withSilent();

            manager.execute(function, source);
        });
    }
    public void parseAndSetData(String input) {
        this.functions.clear();
        this.note = "";

        if (input.contains("#")) {
            int commentIdx = input.indexOf('#');
            this.note = input.substring(commentIdx + 1).trim();
            input = input.substring(0, commentIdx);
        }

        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String insideBrackets = matcher.group(1);
            String[] entries = insideBrackets.split(",");

            for (String entry : entries) {
                String trimmed = entry.trim();
                if (!trimmed.isEmpty()) {
                    Identifier id = Identifier.tryParse(trimmed);
                    if (id != null) {
                        this.functions.add(id);
                    }
                }
            }
        }

        this.dataSetCount = 1;
        markDirty();

        if (this.world != null && !this.world.isClient) {
            this.world.updateListeners(this.pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        NbtList list = new NbtList();
        for (Identifier id : functions) {
            list.add(NbtString.of(id.toString()));
        }
        nbt.put("Functions", list);
        nbt.putString("Note", note);
        nbt.putInt("DataSetCount", dataSetCount);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        functions.clear();
        NbtList list = nbt.getList("Functions", NbtElement.STRING_TYPE);
        for (int i = 0; i < list.size(); i++) {
            functions.add(Identifier.of(list.getString(i)));
        }
        if (nbt.contains("Note")) {
            this.note = nbt.getString("Note");
        }
        if (nbt.contains("DataSetCount")) {
            this.dataSetCount = nbt.getInt("DataSetCount");
        }

        if (this.world instanceof ServerWorld serverWorld && !this.functions.isEmpty() && this.dataSetCount == 1) {
            serverWorld.scheduleBlockTick(this.pos, this.getCachedState().getBlock(), 1);
        }
        if (this.dataSetCount == 0) {
            this.dataSetCount = 1;
        }
    }
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}