package io.github.tobyrue.btc;

import io.github.tobyrue.btc.block.*;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.config.BTCConfig;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.entity.custom.KeyGolemEntity;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.packets.ModPackets;
import io.github.tobyrue.btc.recipes.KeyDuplicateRecipe;
import io.github.tobyrue.btc.recipes.UnbreakableSmithingRecipe;
import io.github.tobyrue.btc.regestries.*;
import io.github.tobyrue.btc.util.BookshelfProcessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;

import java.util.Arrays;
import java.util.UUID;

public class BTC implements ModInitializer {

    public static String MOD_ID = "btc";

    public static final TagKey<Block> WRENCH_ROTATION_BLACKLIST = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "wrench_rotation_blacklist"));
    public static final TagKey<Block> WRENCH_CLIPBOARD_BLACKLIST = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "wrench_clipboard_blacklist"));
    public static final TagKey<Item> WRENCHES = TagKey.of(RegistryKeys.ITEM,  Identifier.of(MOD_ID, "wrenches"));
    public static final TagKey<Block> BUTTONS = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "copper_buttons"));
    public static final TagKey<Block> PILASTER = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "pilaster"));
    public static final TagKey<Block> PILLAR = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "pillar"));
    public static final TagKey<Block> PANE = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "pane"));
    public static final TagKey<Block> STOPS_OMINOUS_BEACON = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "stops_ominous_beacon"));
    public static final TagKey<Block> OMINOUS_BEACON_IGNORES = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "ominous_beacon_ignores"));

    public static final StructureProcessorType<BookshelfProcessor> BOOKSHELF_PROCESSOR =
            Registry.register(Registries.STRUCTURE_PROCESSOR, Identifier.of("btc", "bookshelf_processor"), () -> BookshelfProcessor.CODEC);

    public static final RecipeSerializer<KeyDuplicateRecipe> KEY_DUPLICATE_SERIALIZER =
            Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    BTC.identifierOf("key_duplicate"),
                    new SpecialRecipeSerializer<>(KeyDuplicateRecipe::new)
            );
    public static final RecipeSerializer<UnbreakableSmithingRecipe> UNBREAKABLE_SMITHING =
            Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    BTC.identifierOf("unbreakable_smithing"),
                    new UnbreakableSmithingRecipe.Serializer()
            );
    //To add another map for a structure make a new tag like below and also add a new json file with the path in the tag below under the path: data/btc/tags/worldgen/structure. Look at better_trial_chambers_maps for the format change the structure in it to the name of the structure.
    public static final TagKey<Structure> BETTER_TRIAL_CHAMBERS_TAG = TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(MOD_ID, "better_trial_chambers"));

    public static final TagKey<Structure> SURFACE_INDICATOR_TAG = TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(MOD_ID, "surface_indicator"));


    @Override
    public void onInitialize() {


        TradeOfferHelper.registerWanderingTraderOffers( 1, factories -> {
            factories.add(new BetterTrialChambersMapTrade());
        });

        ModCommands.initialize();
        ModMapDecorationTypes.initialize();
        ModBlocks.initialize();
        ModItems.initialize();
        ModBlockEntities.initialize();
        ModPotions.initialize();
        ModSounds.initialize();
        ModWaxings.initialize();
        ModInventoryItemRegistry.initialize();
        ModPackets.initialize();
        ModSpells.initialize();
        ModEvents.init();
        BTCConfig.load();

        FabricDefaultAttributeRegistry.register(ModEntities.ELDRITCH_LUMINARY, EldritchLuminaryEntity.createEldritchLuminaryAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.COPPER_GOLEM, CopperGolemEntity.createCopperGolemAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.TUFF_GOLEM, TuffGolemEntity.createTuffGolemAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.KEY_GOLEM, KeyGolemEntity.createKeyGolemAttributes());

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);
            BlockPos blockPos = hitResult.getBlockPos();
            BlockState state = world.getBlockState(blockPos);
            Block block = state.getBlock();
            if (player.hasStatusEffect(ModStatusEffects.BUILDER_BLUNDER) && !player.isCreative()) {
                boolean b = (block instanceof ChestBlock) || (block instanceof CraftingTableBlock) ||
                        (block instanceof CrafterBlock) || (block instanceof AnvilBlock) || (block instanceof DispenserBlock) ||
                        (block instanceof DropperBlock) || (block instanceof TrappedChestBlock) || (block instanceof BarrelBlock) ||
                        (block instanceof GrindstoneBlock) || (block instanceof StonecutterBlock) || (block instanceof EnchantingTableBlock) ||
                        (block instanceof FurnaceBlock) || (block instanceof BlastFurnaceBlock) || (block instanceof SmokerBlock) ||
                        (block instanceof SmithingTableBlock) || (block instanceof LoomBlock) || (block instanceof ComposterBlock) ||
                        (block instanceof CampfireBlock) || (block instanceof JukeboxBlock) || (block instanceof PedestalBlock) ||
                        (block instanceof VaultBlock) || (block instanceof KeyAcceptorBlock) || (block instanceof KeyDispenserBlock)||
                        (block instanceof DungeonDoorBlock) || (block instanceof LecternBlock) || (block instanceof EndPortalFrameBlock) ||
                        (block instanceof DragonEggBlock) || (block instanceof BedBlock) || (block instanceof BeaconBlock) ||
                        (block instanceof BellBlock) || (block instanceof BrewingStandBlock) || (block instanceof CauldronBlock) ||
                        (block instanceof NoteBlock) || (block instanceof DecoratedPotBlock) || (block instanceof FlowerPotBlock) ||
                        (block instanceof BeehiveBlock) || (block instanceof SignBlock) || (block instanceof HangingSignBlock) ||
                        (block instanceof EnderChestBlock) || (block instanceof ShulkerBoxBlock)  || (block instanceof CartographyTableBlock) ||
                        (block instanceof FletchingTableBlock) || (block instanceof ChiseledBookshelfBlock) || (block instanceof RepeaterBlock) ||
                        (block instanceof ComparatorBlock) || (block instanceof LeverBlock) || (block instanceof ButtonBlock) ||
                        (block instanceof HopperBlock) || (block instanceof TrapdoorBlock) || (block instanceof DoorBlock) ||
                        (block instanceof FenceGateBlock) || (block instanceof CakeBlock) || (block instanceof FarmlandBlock);

                if ((stack.getItem() instanceof BlockItem && !b) || ((b && player.isSneaking()) && stack.getItem() instanceof BlockItem) || stack.getItem() instanceof BoneMealItem || stack.getItem() instanceof BucketItem || stack.getItem() instanceof PowderSnowBucketItem || stack.getItem() instanceof EndCrystalItem || stack.getItem() instanceof BoatItem || stack.getItem() instanceof MinecartItem) {
                    return ActionResult.FAIL;
                } else if (stack.getItem() instanceof BlockItem && !player.isSneaking()) {
                    return ActionResult.PASS;
                }
            }
            return ActionResult.PASS;
        });
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            var stack = player.getStackInHand(hand);
            if (stack.isOf(ModItems.PET_TOTEM)) {
                if (entity instanceof TameableEntity tameableEntity) {
                    if (tameableEntity.getOwner() == player && !stack.contains(ModComponents.STORED_MOB_UUID)) {
                        UUID uuid = tameableEntity.getUuid();
                        stack.set(ModComponents.STORED_MOB_UUID, uuid);
                        tameableEntity.setInvulnerable(true);
                        tameableEntity.setPersistent();
                        var nbt = tameableEntity.writeNbt(new NbtCompound());
                        stack.set(ModComponents.STORED_MOB_NBT, nbt);
                        stack.set(ModComponents.STORED_ENTITY_TYPE, tameableEntity.getType());
                        player.sendMessage(Text.literal("Pet bound to totem"), true);
                        return ActionResult.SUCCESS;
                    }
                } else if (entity instanceof Tameable tameable && entity instanceof MobEntity mob) {
                    System.out.println("Owner: " + tameable.getOwner() + " Player: " + player);
                    if (tameable.getOwner() == player && !stack.contains(ModComponents.STORED_MOB_UUID)) {
                        UUID uuid = mob.getUuid();
                        stack.set(ModComponents.STORED_MOB_UUID, uuid);
                        mob.setInvulnerable(true);
                        mob.setPersistent();
                        var nbt = mob.writeNbt(new NbtCompound());
                        stack.set(ModComponents.STORED_MOB_NBT, nbt);
                        stack.set(ModComponents.STORED_ENTITY_TYPE, mob.getType());
                        player.sendMessage(Text.literal("Pet bound to totem"), true);
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack stack = player.getStackInHand(hand);

            if (stack.isOf(Items.GUNPOWDER) && BTCConfig.placableGunpowder) {
                BlockPos pos = hitResult.getBlockPos().offset(hitResult.getSide());

                if (world.getBlockState(pos).isAir()) {
                    BlockState state = ModBlocks.GUNPOWDER_DUST.getDefaultState();

                    if (state.canPlaceAt(world, pos)) {
                        if (!world.isClient) {
                            BlockState connectedState = ((GunpowderDustBlock)state.getBlock())
                                    .getConnectionState(world, state, pos);

                            world.setBlockState(pos, connectedState, Block.NOTIFY_ALL);

                            world.playSound(null, pos, SoundEvents.BLOCK_SAND_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                            if (!player.getAbilities().creativeMode) {
                                stack.decrement(1);
                            }
                        }
                        return ActionResult.success(world.isClient);
                    }
                }
            }
            return ActionResult.PASS;
        });

        //TODO GET RID OF WHEN BUILDING MOD FOR ANY RELEASE
//        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
//            UseBlockCallback.EVENT.register(OxidizeOnClick::onUseBlock);
//        }
//        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
//            if (!world.isClient && hand == Hand.MAIN_HAND) {
//                if (player.getStackInHand(hand).getItem() == ModItems.COPPER_WRENCH) {
//                    // Your custom logic here
//                    System.out.println("Left-clicked block at: " + pos);
//
//                    // Example: change block or print properties
//                    if (world.getBlockState(pos).contains(Properties.FACING)) {
//                        var stateRotate = world.getBlockState(pos);
//                        world.setBlockState(pos, stateRotate.cycle(Properties.FACING));
//                        System.out.println("Block facing: " + world.getBlockState(pos).get(Properties.FACING));
//                    }
//
//                    // Cancel normal attack
//                    return ActionResult.SUCCESS;
//                }
//            }
//            return ActionResult.PASS;
//        });

    }
    public static void println(Object... args) {
        System.out.println(String.join(" ", Arrays.stream(args).map(Object::toString).toArray(String[]::new)));
    }
    public static Identifier identifierOf(String id) {
        return Identifier.of(BTC.MOD_ID, id);
    }



    public static final TagKey<Block> PISTONS_CAN_MOVE_BLOCK_ENTITY;


    public static void HandleBlockEntityShenanigans(BlockEntity input, BlockEntity output, World world) {
        if (input != null && output != null && world != null) {
            output.readComponentlessNbt(input.createComponentlessNbt(world.getRegistryManager()), world.getRegistryManager());
            output.setComponents(input.getComponents());
            output.markDirty();
        }
    }

    static {
        PISTONS_CAN_MOVE_BLOCK_ENTITY = TagKey.of(RegistryKeys.BLOCK, identifierOf("pistons_can_move_block_entity"));
    }
}
