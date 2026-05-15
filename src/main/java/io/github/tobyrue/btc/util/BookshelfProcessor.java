package io.github.tobyrue.btc.util;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.BTC;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.util.math.random.Random;

import java.util.List;

public class BookshelfProcessor extends StructureProcessor {
    public static final MapCodec<BookshelfProcessor> CODEC = MapCodec.unit(BookshelfProcessor::new);

    private record EnchantEntry(String id, int minLvl, int maxLvl) {}

    private static final List<EnchantEntry> ENCHANT_POOL = List.of(
            new EnchantEntry("minecraft:unbreaking", 1, 3),
            new EnchantEntry("minecraft:sharpness", 1, 5),
            new EnchantEntry("minecraft:protection", 1, 4),
            new EnchantEntry("minecraft:efficiency", 2, 5),
            new EnchantEntry("minecraft:mending", 1, 1),
            new EnchantEntry("minecraft:fortune", 1, 3)
    );

    @Override
    public StructureTemplate.StructureBlockInfo process(
            WorldView world, BlockPos pivot, BlockPos pos,
            StructureTemplate.StructureBlockInfo original,
            StructureTemplate.StructureBlockInfo current,
            StructurePlacementData data
    ) {
        if (current.state().isOf(Blocks.CHISELED_BOOKSHELF)) {
            Random random = data.getRandom(pos);

            BlockState state = current.state();
            NbtCompound nbt = new NbtCompound();
            NbtList items = new NbtList();

            int bookCount = random.nextInt(4) + 1;

            for (int i = 0; i < bookCount; i++) {
                NbtCompound item = new NbtCompound();
                item.putByte("Slot", (byte) random.nextInt(6));
                item.putString("id", "minecraft:enchanted_book");
                item.putInt("count", 1);

                EnchantEntry selection = ENCHANT_POOL.get(random.nextInt(ENCHANT_POOL.size()));
                int level = selection.minLvl == selection.maxLvl ?
                        selection.minLvl :
                        random.nextInt((selection.maxLvl - selection.minLvl) + 1) + selection.minLvl;

                NbtCompound tag = new NbtCompound();
                NbtList enchants = new NbtList();
                NbtCompound ench = new NbtCompound();

                ench.putString("id", selection.id);
                ench.putShort("lvl", (short) level);

                enchants.add(ench);
                tag.put("StoredEnchantments", enchants);
                item.put("tag", tag);

                items.add(item);
            }

            nbt.put("Items", items);

            return new StructureTemplate.StructureBlockInfo(current.pos(), state, nbt);
        }
        return current;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        // This must match your registry entry in the main class
        return BTC.BOOKSHELF_PROCESSOR;
    }
}