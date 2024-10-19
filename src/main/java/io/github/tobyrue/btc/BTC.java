package io.github.tobyrue.btc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.RailBlock;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;

public class BTC implements ModInitializer {
    public static String MOD_ID = "btc";

    public static final StatusEffect ANTI_PLACE;
    public static final StatusEffect DRAGON_SCALES;
    public static final TagKey<Block> WRENCH_BLACKLIST = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "wrench_blacklist"));

    static {
        ANTI_PLACE = Registry.register(Registries.STATUS_EFFECT, Identifier.of("btc", "anti_place"), new AntiPlaceEffect());
        DRAGON_SCALES = Registry.register(Registries.STATUS_EFFECT, Identifier.of("btc", "dragon_scales"), new DragonScalesEffect());
    }

    @Override
    public void onInitialize() {
        ModBlocks.initialize();
        ModItems.initialize();
        ModBlockEntities.initialize();
        ModPotions.initialize();
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if(player instanceof PlayerEntity && player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(BTC.ANTI_PLACE)) && !player.isCreative()) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        //INGREDIENTS
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.RUBY_TRIAL_KEY));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModBlocks.CHISELED_COPPER_BRICKS));

        //FUNCTIONAL
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.OMINOUS_BEACON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.PEDESTAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.COPPER_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.ANTIER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_FIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.KEY_DISPENSER_BLOCK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.FIRE_DISPENSER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModItems.WRENCH));

        //COMBAT
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.WIND_STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.FIRE_STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.DRAGON_STAFF));

        //REDSTONE
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.COPPER_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.FIRE_DISPENSER));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_DOOR));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_PRESSURE_PLATE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.KEY_DISPENSER_BLOCK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModItems.WRENCH));

        //BUILDING BLOCKS
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((itemGroup) -> itemGroup.add(ModBlocks.CHISELED_COPPER_BRICKS));
    }
    public static void println(Object... args) {
        System.out.println(String.join(" ", java.util.Arrays.stream(args).map(Object::toString).toArray(String[]::new)));
    }
}
