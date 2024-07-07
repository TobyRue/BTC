package io.github.tobyrue.btc;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

public class StaffPedestalBlock extends Block {

    public static final EnumProperty<StateVariant> VARIANT = EnumProperty.of("pedestal_state", StateVariant.class);

    public StaffPedestalBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, StateVariant.ACTIVE));
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    public enum StateVariant implements StringIdentifiable {
        ACTIVE("active"),
        DISPENSING("dispensing"),
        INACTIVE("inactive");

        private final String name;

        StateVariant(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
