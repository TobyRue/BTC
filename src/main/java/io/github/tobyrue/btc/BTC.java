package io.github.tobyrue.btc;

import io.github.tobyrue.btc.block.*;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.component.BlockPosComponent;
import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.misc.OxidizeOnClick;
import io.github.tobyrue.btc.packets.ModPackets;
import io.github.tobyrue.btc.regestries.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.*;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.GustParticle;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.*;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.structure.Structure;

import java.util.Arrays;

public class BTC implements ModInitializer {

    public static String MOD_ID = "btc";

    public static final TagKey<Block> WRENCH_BLACKLIST = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "wrench_blacklist"));
    public static final TagKey<Item> WRENCHES = TagKey.of(RegistryKeys.ITEM,  Identifier.of(MOD_ID, "wrenches"));
    public static final TagKey<Block> BUTTONS = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "copper_buttons"));
    public static final TagKey<Block> PILASTER = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "pilaster"));
    public static final TagKey<Block> COLUMN = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "column"));
    public static final TagKey<Block> PANE = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "pane"));

    public static final ComponentType<Direction> WRENCH_DIRECTION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            identifierOf("wrench_direction"),
            ComponentType.<Direction>builder()
                    .codec(Direction.CODEC)
                    .build()
    );
    public static final ComponentType<WrenchType> WRENCH_TYPE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            identifierOf("wrench_type"),
            ComponentType.<WrenchType>builder()
                    .codec(WrenchType.CODEC)
                    .build()
    );

    public static final ComponentType<NbtComponent> SPELL_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("spell"),
            ComponentType.<NbtComponent>builder().codec(NbtComponent.CODEC).build()
    );
//    public static final ComponentType<Identifier> UNLOCK_SPELL_COMPONENT = Registry.register(
//            Registries.DATA_COMPONENT_TYPE,
//            BTC.identifierOf("unlock_spell"),
//            ComponentType.<Identifier>builder().codec(Identifier.CODEC).build()
//    );
//
    public static final ComponentType<UnlockSpellComponent> UNLOCK_SPELL_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("unlock_spell"),
            ComponentType.<UnlockSpellComponent>builder().codec(UnlockSpellComponent.CODEC).build()
    );

    public static final ComponentType<BlockPosComponent> CORNER_1_POSITION_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("corner_1_position"),
            ComponentType.<BlockPosComponent>builder().codec(BlockPosComponent.CODEC).build()
    );
    public static final ComponentType<BlockPosComponent> CORNER_2_POSITION_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("corner_2_position"),
            ComponentType.<BlockPosComponent>builder().codec(BlockPosComponent.CODEC).build()
    );
    //To add another map for a structure make a new tag like below and also add a new json file with the path in the tag below under the path: data/btc/tags/worldgen/structure. Look at better_trial_chambers_maps for the format change the structure in it to the name of the structure.
    public static final TagKey<Structure> BETTER_TRIAL_CHAMBERS_TAG = TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(MOD_ID, "better_trial_chambers_maps"));

    public static final SimpleParticleType WATER_BLAST = FabricParticleTypes.simple();
    public static final SimpleParticleType WATER_DROP = FabricParticleTypes.simple();



    // Register our custom particle type in the mod initializer.
    @Override
    public void onInitialize() {

//        System.out.println(SpellBookXMLParser.loadResource());
//
////        var xmlFilePath = "test.xml";
//
//        var builderFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder;
//        try {
//            builder = builderFactory.newDocumentBuilder();
//        } catch (ParserConfigurationException e) {
//            throw new RuntimeException(e);
//        }
//
//        var xmlString = SpellBookXMLParser.loadResource();
//        var reader = new StringReader(xmlString);
//        var inputSource = new InputSource(reader);
//        Document document;
//        try {
//            document = builder.parse(inputSource);
//        } catch (SAXException | IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        var root = document.getDocumentElement();
//        var pages = root.getElementsByTagName("page");
//
//        for (int i = 0; i < pages.getLength(); i++) {
//            var page = (Element) pages.item(i);
//
//            // Get attribute id
//
//            var id = page.getAttribute("id");
//            System.out.println("Page ID: " + id);
//
//            // Get all <line> elements inside this page
//            var lines = page.getElementsByTagName("line");
//
//            for (int j = 0; j < lines.getLength(); j++) {
//                var line = (Element) lines.item(j);
//                String align = line.hasAttribute("align") ? line.getAttribute("align") : "left";
//                String textContent = line.getTextContent().trim();
//                System.out.println("  Line (align=" + align + "): " + textContent);
//            }
//        }



        //Adds a trade in another class, BetterTrialChambersMapTrade
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

        FabricDefaultAttributeRegistry.register(ModEntities.ELDRITCH_LUMINARY, EldritchLuminaryEntity.createEldritchLuminaryAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.COPPER_GOLEM, CopperGolemEntity.createCopperGolemAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.TUFF_GOLEM, TuffGolemEntity.createTuffGolemAttributes());

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
            return ActionResult.PASS; // Other interactions (like opening chests, using tools) are allowed
        });

        //TODO GET RID OF WHEN BUILDING MOD FOR ANY RELEASE
        UseBlockCallback.EVENT.register(OxidizeOnClick::onUseBlock);



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

        //TODO COMMENT THESE BACK IN WHEN NOT DOING DATA GEN WITH THESE IT BREAKS IT
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "water_blast"), WATER_BLAST);
        ParticleFactoryRegistry.getInstance().register(BTC.WATER_BLAST, GustParticle.Factory::new);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "water_drop"), WATER_DROP);
        ParticleFactoryRegistry.getInstance().register(BTC.WATER_DROP, FlameParticle.Factory::new);
    }
    public static void println(Object... args) {
        System.out.println(String.join(" ", Arrays.stream(args).map(Object::toString).toArray(String[]::new)));
    }
    public static Identifier identifierOf(String id) {
        return Identifier.of(BTC.MOD_ID, id);
    }
}
