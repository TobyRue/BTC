package io.github.tobyrue.btc.item;

import net.minecraft.block.Block;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class AliasedTranslatedBlockItem extends AliasedBlockItem {
    private final Identifier translation;

    public AliasedTranslatedBlockItem(Block block, Settings settings, Identifier translation) {
        super(block, settings);
        this.translation = translation;
    }

    @Override
    public String getTranslationKey() {
        return "item." + translation.getNamespace() + "." + translation.getPath();
    }
}
