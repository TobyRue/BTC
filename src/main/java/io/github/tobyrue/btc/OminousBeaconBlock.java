package io.github.tobyrue.btc;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.client.BTCClient;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OminousBeaconBlock extends Block implements ModBlockEntityProvider<OminousBeaconBlockEntity>, ModTickBlockEntityProvider<OminousBeaconBlockEntity> {

    public OminousBeaconBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(FACING, Direction.UP)));
    }

    public static final MapCodec<OminousBeaconBlock> CODEC = createCodec(OminousBeaconBlock::new);

    public static final DirectionProperty FACING = FacingBlock.FACING;


    @Override
    public MapCodec<? extends OminousBeaconBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }

    @Override
    public BlockEntityType<OminousBeaconBlockEntity> getBlockEntityType() {
        return ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY;
    }
//    @Override
//    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        // Check if the player is holding the wrench
//        ItemStack heldItem = player.getStackInHand(hand);
//        if (heldItem.isOf(ModItems.WRENCH) && state.get(SURVIVAL)) {
//            if (BTCClient.leftAltKeyBinding.isPressed()) {
//                Direction currentFacing = state.get(FACING);
//                Direction newFacing;
//                // Cycle through the directions
//                newFacing = switch (currentFacing) {
//                    case NORTH -> Direction.EAST;
//                    case EAST -> Direction.SOUTH;
//                    case SOUTH -> Direction.WEST;
//                    case WEST -> Direction.UP;
//                    case UP -> Direction.DOWN;
//                    case DOWN -> Direction.NORTH;
//                    default -> Direction.NORTH; // Fallback to NORTH if something goes wrong
//                };
//
//                // Update the block state with the new facing direction
//                BlockState newState = state.with(FACING, newFacing);
//                world.setBlockState(pos, newState, Block.NOTIFY_ALL);
//
//                // Send a message to the player indicating the new facing direction
//                String directionMessage = "Facing direction changed to: " + newFacing.getName();
//                player.sendMessage(Text.literal(directionMessage), true);
//
//                // Optionally, play a sound
//                world.playSound(null, pos, SoundEvents.BLOCK_METAL_HIT, SoundCategory.BLOCKS, 8.0F, 1.0F);
//
//                return ItemActionResult.SUCCESS;
//            }
//        }
//        return ItemActionResult.FAIL;
//    }
}