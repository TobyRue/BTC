package io.github.tobyrue.btc.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WindChargeItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DungeonFireBlock extends Block {
    private static final VoxelShape BOTTOM_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE2;
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape COAL_SHAPE1;
    private static final VoxelShape COAL_SHAPE2;
    private static final VoxelShape COAL_SHAPE3;
    private static final VoxelShape COAL_SHAPE4;

    private static final VoxelShape SHAPE;

    public static final IntProperty DAMAGE = IntProperty.of("damage", 0, 31);
    public static final BooleanProperty INFERNAL = BooleanProperty.of("infernal");
    public static final IntProperty FIRE_TIME = IntProperty.of("fire_time", 0, 31);

    public DungeonFireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(INFERNAL, false)
                .with(FIRE_TIME, 4)
                .with(DAMAGE, 2));
    }

    static {
        BOTTOM_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 2.0, 13.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 6.0, 12.0);
        MIDDLE_SHAPE2 = Block.createCuboidShape(3.0, 6.0, 3.0, 13.0, 7.0, 13.0);
        TOP_SHAPE = Block.createCuboidShape(2.0, 7.0, 2.0, 14.0, 10.0, 14.0);
        COAL_SHAPE1 = Block.createCuboidShape(3.0, 8.0, 3.0, 13.0, 11.0, 13.0);
        COAL_SHAPE2 = Block.createCuboidShape(4.0, 11.0, 4.0, 12.0, 12.0, 12.0);
        COAL_SHAPE3 = Block.createCuboidShape(5.0, 12.0, 6.0, 11.0, 13.0, 10.0);
        COAL_SHAPE4 = Block.createCuboidShape(7.0, 13.0, 7.0, 10.0, 14.0, 9.0);
        SHAPE = VoxelShapes.union(BOTTOM_SHAPE, MIDDLE_SHAPE, MIDDLE_SHAPE2, TOP_SHAPE, COAL_SHAPE1, COAL_SHAPE2, COAL_SHAPE3, COAL_SHAPE4);
    }

    public void incrementDamage(World world, BlockPos pos, BlockState state, int incrementD) {
        // Check if the block has the FIRE_TIME property
        if (state.contains(DungeonFireBlock.DAMAGE)) {
            // Get the current fire time value
            int currentDamage = state.get(DungeonFireBlock.DAMAGE);

            // Increment the fire time value by 1, ensuring it does not exceed the maximum allowed value
            int newDamage = Math.min(31, currentDamage + incrementD); // Ensure fire time does not exceed 31

            // Create a new BlockState with the updated FIRE_TIME value
            BlockState newState = state.with(DungeonFireBlock.DAMAGE, newDamage);

            // Update the block state in the world
            world.setBlockState(pos, newState);
        }
    }

    public void decrementDamage(World world, BlockPos pos, BlockState state, int decrementD) {
        // Check if the block has the FIRE_TIME property
        if (state.contains(DungeonFireBlock.DAMAGE)) {
            // Get the current fire time value
            int currentDamage = state.get(DungeonFireBlock.DAMAGE);

            // Decrease the fire time value by 1, ensuring it does not go below 0
            int newDamage = Math.max(0, currentDamage - decrementD); // Ensure fire time does not go below 0
            // Create a new BlockState with the updated FIRE_TIME value
            BlockState newState = state.with(DungeonFireBlock.DAMAGE, newDamage);

            // Update the block state in the world
            world.setBlockState(pos, newState);
        }
    }

    public void incrementFireTime(World world, BlockPos pos, BlockState state) {
        // Check if the block has the FIRE_TIME property
        if (state.contains(DungeonFireBlock.FIRE_TIME)) {
            // Get the current fire time value
            int currentFireTime = state.get(DungeonFireBlock.FIRE_TIME);

            // Increment the fire time value by 1, ensuring it does not exceed the maximum allowed value
            int newFireTime = Math.min(31, currentFireTime + 1); // Ensure fire time does not exceed 31

            // Create a new BlockState with the updated FIRE_TIME value
            BlockState newState = state.with(DungeonFireBlock.FIRE_TIME, newFireTime);

            // Update the block state in the world
            world.setBlockState(pos, newState);
        }
    }

    public void decrementFireTime(World world, BlockPos pos, BlockState state, int decrementF) {
        // Check if the block has the FIRE_TIME property
        if (state.contains(DungeonFireBlock.FIRE_TIME)) {
            // Get the current fire time value
            int currentFireTime = state.get(DungeonFireBlock.FIRE_TIME);

            // Decrease the fire time value by 1, ensuring it does not go below 0
            int newFireTime = Math.max(0, currentFireTime - decrementF); // Ensure fire time does not go below 0
            // Create a new BlockState with the updated FIRE_TIME value
            BlockState newState = state.with(DungeonFireBlock.FIRE_TIME, newFireTime);

            // Update the block state in the world
            world.setBlockState(pos, newState);
        }
    }

    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() == Items.OMINOUS_BOTTLE && !state.get(INFERNAL)) {
            BlockState newState1 = state.with(DungeonFireBlock.INFERNAL, true);
            player.playSound(SoundEvents.ITEM_FIRECHARGE_USE);
            world.setBlockState(pos, newState1);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            return ItemActionResult.SUCCESS;
        } else if (stack.getItem() == Items.BLUE_ICE && state.get(INFERNAL)) {
            player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH);
            BlockState newState2 = state.with(DungeonFireBlock.INFERNAL, false).with(DungeonFireBlock.FIRE_TIME, 4).with(DungeonFireBlock.DAMAGE, 2);
            world.setBlockState(pos, newState2);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            return ItemActionResult.SUCCESS;
        }
        if (state.get(INFERNAL)) {
            if (stack.getItem() == Items.STICK && state.get(FIRE_TIME) <= 5) {
                player.playSound(SoundEvents.ITEM_FIRECHARGE_USE);
                incrementFireTime(world, pos, state);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ItemActionResult.SUCCESS;
            } else if (stack.getItem() == Items.COAL && state.get(FIRE_TIME) <= 10 && state.get(FIRE_TIME) > 5) {
                player.playSound(SoundEvents.ITEM_FIRECHARGE_USE);
                incrementFireTime(world, pos, state);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ItemActionResult.SUCCESS;
            } else if (stack.getItem() == Items.BLAZE_POWDER && state.get(FIRE_TIME) > 10 && state.get(FIRE_TIME) <= 20) {
                player.playSound(SoundEvents.ITEM_FIRECHARGE_USE);
                incrementFireTime(world, pos, state);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ItemActionResult.SUCCESS;
            } else if (stack.getItem() == Items.LAVA_BUCKET && state.get(FIRE_TIME) > 20 && state.get(FIRE_TIME) < 31) {
                player.playSound(SoundEvents.ITEM_FIRECHARGE_USE);
                incrementFireTime(world, pos, state);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                ItemStack emptyBucket1 = new ItemStack(Items.BUCKET);
                if (!world.isClient) {
                    player.getInventory().offerOrDrop(emptyBucket1);
                }
                return ItemActionResult.SUCCESS;
            }
        }
        if (stack.getItem() == Items.POTION) {
            player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH);
            decrementFireTime(world, pos, state, 1);
            if (!player.isCreative()) {
                stack.decrement(1);
                ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!world.isClient) {
                    player.getInventory().offerOrDrop(emptyBottle);
                }
            }
            // Remove one water bottle from the player's inventory

            return ItemActionResult.SUCCESS;
        } else if (stack.getItem() == Items.WATER_BUCKET) {
            player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH);
            decrementFireTime(world, pos, state, 2);
            if (!player.isCreative()) {
                stack.decrement(1);
                ItemStack emptyBucket = new ItemStack(Items.BUCKET);
                if (!world.isClient) {
                    player.getInventory().offerOrDrop(emptyBucket);
                }
            }
            return ItemActionResult.SUCCESS;
        } else if (stack.getItem() == Items.ICE && state.get(FIRE_TIME) >= 2) {
            player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH);
            decrementFireTime(world, pos, state, 4);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            return ItemActionResult.SUCCESS;
        } else if (stack.getItem() == Items.PACKED_ICE && state.get(FIRE_TIME) >= 4) {
            player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH);
            decrementFireTime(world, pos, state, 8);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            return ItemActionResult.SUCCESS;
        }
        if (state.get(INFERNAL)) {
            if (stack.getItem() == Items.BLAZE_ROD && state.get(DAMAGE) < 10) {
                player.playSound(SoundEvents.ITEM_FIRECHARGE_USE);
                incrementDamage(world, pos, state, 1);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ItemActionResult.SUCCESS;
            } else if (stack.getItem() == Items.END_CRYSTAL && state.get(DAMAGE) >= 8 && state.get(DAMAGE) < 17) {
                player.playSound(SoundEvents.ITEM_FIRECHARGE_USE);
                incrementDamage(world, pos, state, 3);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ItemActionResult.SUCCESS;
            } else if (stack.getItem() == Items.NETHER_STAR && state.get(DAMAGE) >= 12 && state.get(DAMAGE) < 31) {
                player.playSound(SoundEvents.ITEM_FIRECHARGE_USE);
                incrementDamage(world, pos, state, 8);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
                return ItemActionResult.SUCCESS;
            }
        }
        if (stack.getItem() == Items.GLOW_LICHEN) {
            player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH);
            decrementDamage(world, pos, state, 1);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            return ItemActionResult.SUCCESS;
        } else if (stack.getItem() == Items.PRISMARINE_SHARD || stack.getItem() == Items.PRISMARINE_CRYSTALS) {
            player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH);
            decrementDamage(world, pos, state, 2);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            return ItemActionResult.SUCCESS;
        } else if (stack.getItem() == Items.NAUTILUS_SHELL) {
            player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH);
            decrementDamage(world, pos, state, 4);
            if (!player.isCreative()) {
                stack.decrement(1);
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(INFERNAL, false)
                .with(FIRE_TIME, 4)
                .with(DAMAGE, 2);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(INFERNAL);
        builder.add(FIRE_TIME);
        builder.add(DAMAGE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.bypassesSteppingEffects() && entity instanceof LivingEntity) {
            int fireTimeValue = state.get(FIRE_TIME);
            int damageValue = state.get(DAMAGE);
            // Convert the damage value to float
            float fireTimeFloat = (float) fireTimeValue;
            float damageFloat = (float) damageValue;
            entity.damage(world.getDamageSources().hotFloor(), damageFloat);
            entity.setOnFireFor(fireTimeFloat);
        }
        super.onSteppedOn(world, pos, state, entity);
    }
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(8) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 2.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }

        int i;
        double d2;
        double e2;
        double f2;
        for(i = 0; i < 3; ++i) {
            d2 = (double)pos.getX() + random.nextDouble() * 0.35 + 0.35;
            e2 = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
            f2 = (double)pos.getZ() + random.nextDouble() * 0.35 + 0.35;
            world.addParticle(ParticleTypes.SMOKE, d2, e2, f2, 0.0, 0.0, 0.0);
            if(state.get(INFERNAL)) {
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d2, e2, f2, 0.0, 0.0, 0.0);
            } else if(!state.get(INFERNAL)) {
                world.addParticle(ParticleTypes.FLAME, d2, e2, f2, 0.0, 0.0, 0.0);
            }
        }
        super.randomDisplayTick(state, world, pos, random);
    }
}