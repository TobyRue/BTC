package io.github.tobyrue.btc;

import net.minecraft.block.PistonBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(BTC.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }
    public static final Item RUBY_TRIAL_KEY = register(
            new Item(new Item.Settings()),
            "ruby_trial_key"
    );
    public static final Item WRENCH = register(new Item(new Item.Settings().maxCount(1)) {
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            var state = context.getWorld().getBlockState(context.getBlockPos());
            if(!state.streamTags().anyMatch(t->t == BTC.WRENCH_BLACKLIST) && !(state.getBlock() instanceof PistonBlock && state.get(PistonBlock.EXTENDED))) {
                context.getWorld().setBlockState(context.getBlockPos(), state.rotate(context.getPlayer().isSneaking() ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90));
                return ActionResult.SUCCESS;
            }
            return super.useOnBlock(context);
        }
    },
    "wrench"
    );

    public static final Item STAFF = register(
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "staff"
    );
    public static final Item WIND_STAFF = register(
        new WindStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "wind_staff"
    );
    public static final Item FIRE_STAFF = register(
            new FireStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "fire_staff"
    );
    public static final Item DRAGON_STAFF = register(
            new DragonStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "dragon_staff"
    );

    public static void initialize() {
    }
}