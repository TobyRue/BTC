package io.github.tobyrue.btc.enums;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.BTC;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public enum WrenchType implements IWrenchType {
    ROTATE("rotate") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();

            var stateRotate = world.getBlockState(pos);
            if (!stateRotate.streamTags().anyMatch(t -> t == BTC.WRENCH_ROTATION_BLACKLIST) && !(stateRotate.getBlock() instanceof PistonBlock && stateRotate.get(PistonBlock.EXTENDED))) {
                Property<?> facingProperty = null;

                if (stateRotate.contains(Properties.FACING)) {
                    facingProperty = Properties.FACING;
                } else if (stateRotate.contains(Properties.HORIZONTAL_FACING)) {
                    facingProperty = Properties.HORIZONTAL_FACING;
                } else if (stateRotate.contains(Properties.HORIZONTAL_AXIS)) {
                    facingProperty = Properties.HORIZONTAL_AXIS;
                } else if (stateRotate.contains(Properties.ORIENTATION)) {
                    facingProperty = Properties.ORIENTATION;
                } else if (stateRotate.contains(Properties.AXIS)) {
                    facingProperty = Properties.AXIS;
                }

                if (facingProperty != null) {
                    world.setBlockState(pos, stateRotate.cycle(facingProperty));
                    return ActionResult.SUCCESS;
                } else {
                    world.setBlockState(pos, stateRotate.rotate(BlockRotation.CLOCKWISE_90));
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.FAIL;
        }
    },
    MIRROR("mirror") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            return null;
        }
    },
    COPY("copy") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            return null;
        }
    },
    PASTE("paste") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            return null;
        }
    },
    WIRE("wire") {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            return null;
        }
    };

    private final String name;
    public static final Codec<WrenchType> CODEC = Codec.STRING.xmap(
            WrenchType::valueOf,
            WrenchType::name
    );

    WrenchType(String name) {
        this.name = name;
    }
    public String asString() {
        return this.name;
    }
    public String toString() {
        return this.name;
    }

    public record WrenchClipboardComponent(BlockState state, @Nullable NbtComponent component) {
        public static final Codec<WrenchClipboardComponent> CODEC = RecordCodecBuilder.create(builder -> {
            return builder.group(
                    BlockState.CODEC.fieldOf("state").forGetter(WrenchClipboardComponent::state),
                    NbtComponent.CODEC.fieldOf("component").forGetter(WrenchClipboardComponent::component)
            ).apply(builder, WrenchClipboardComponent::new);
        });
    }

    public enum WireSubtype {
        CONNECTION("connection"), OPERATOR("operator"), DELAY("delay");

        private final String name;
        public static final Codec<WireSubtype> CODEC = Codec.STRING.xmap(
                WireSubtype::valueOf,
                WireSubtype::name
        );

        WireSubtype(String name) {
            this.name = name;
        }
        public String asString() {
            return this.name;
        }
        public String toString() {
            return this.name;
        }

    }
}
