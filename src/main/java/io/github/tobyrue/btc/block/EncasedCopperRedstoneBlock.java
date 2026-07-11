package io.github.tobyrue.btc.block;

import net.minecraft.block.Oxidizable;

public class EncasedCopperRedstoneBlock extends EncasedRedstoneBlock implements Oxidizable {
    private final OxidationLevel oxidationLevel;

    public EncasedCopperRedstoneBlock(Settings settings, int redstoneLevel, OxidationLevel oxidationLevel) {
        super(settings, redstoneLevel);
        this.oxidationLevel = oxidationLevel;
    }

    @Override
    public OxidationLevel getDegradationLevel() {
        return oxidationLevel;
    }
}
