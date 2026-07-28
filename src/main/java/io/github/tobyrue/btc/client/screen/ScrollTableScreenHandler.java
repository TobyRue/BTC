package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.client.screen.recipe_book.ScrollTableRecipeInput;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.recipes.ScrollTableRecipe;
import io.github.tobyrue.btc.regestries.ModRecipes;
import io.github.tobyrue.btc.regestries.ModScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class ScrollTableScreenHandler extends AbstractRecipeScreenHandler<ScrollTableRecipeInput, ScrollTableRecipe> {
    private final Inventory inventory;
    protected final World world;

    public ScrollTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(11));
    }

    public ScrollTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory blockInventory) {
        super(ModScreens.SCROLL_TABLE_SCREEN_HANDLER, syncId);
        checkSize(blockInventory, 11);
        this.inventory = blockInventory;
        blockInventory.onOpen(playerInventory.player);
        this.world = playerInventory.player.getWorld();


        this.addSlot(new Slot(blockInventory, 0, 54, 18));  // 7
        this.addSlot(new Slot(blockInventory, 1, 79, 11));  // 0
        this.addSlot(new Slot(blockInventory, 2, 104, 18)); // 1
        this.addSlot(new Slot(blockInventory, 3, 47, 43));  // 6
        this.addSlot(new Slot(blockInventory, 4, 148, 46) {  // 9
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.EMPTY_SCROLL) || stack.isOf(ModItems.UNLOCK_SCROLL);
            }
        });
        this.addSlot(new Slot(blockInventory, 5, 111, 43)); // 2
        this.addSlot(new Slot(blockInventory, 6, 54, 68));  // 5
        this.addSlot(new Slot(blockInventory, 7, 80, 75));  // 4
        this.addSlot(new Slot(blockInventory, 8, 104, 68)); // 3

        this.addSlot(new Slot(blockInventory, 9, 79, 43) {  // 8
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });



        this.addSlot(new Slot(blockInventory, 10, 148, 69) { // 10
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.LAPIS_LAZULI);
            }
        });


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 118 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 176));
        }
    }

    @Override
    public boolean matches(RecipeEntry<ScrollTableRecipe> entry) {
        if (entry.value().getType() != ModRecipes.SCROLL_TABLE_RECIPE_TYPE) {
            return false;
        }
        ScrollTableRecipe scrollRecipe = entry.value();

        ItemStack[] inputs = new ItemStack[8];
        for (int i = 0; i < 8; i++) {
            inputs[i] = this.inventory.getStack(i);
        }
        return scrollRecipe.matches(new ScrollTableRecipeInput(inputs), this.world);
    }

    @Override
    public void populateRecipeFinder(RecipeMatcher finder) {
        for (int i = 11; i < 47; i++) {
            finder.addInput(this.getSlot(i).getStack());
        }
    }

    @Override
    public void clearCraftingSlots() {
        for (int i = 0; i < 8; i++) {
            this.inventory.setStack(i, ItemStack.EMPTY);
        }
    }
    public void dropOrMoveIngredients(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.inventory.getStack(i);
            if (!stack.isEmpty()) {
                boolean inserted = player.getInventory().insertStack(stack);

                if (!inserted || !stack.isEmpty()) {
                    player.dropItem(stack, false);
                }

                this.inventory.setStack(i, ItemStack.EMPTY);
            }
        }
        this.sendContentUpdates();
    }
    @Override
    public int getCraftingResultSlotIndex() {
        return 9;
    }

    @Override
    public int getCraftingWidth() {
        return 3;
    }

    @Override
    public int getCraftingHeight() {
        return 3;
    }

    @Override
    public int getCraftingSlotCount() {
        return 9;
    }

    @Override
    public RecipeBookCategory getCategory() {
        return RecipeBookCategory.BTC_SCROLL_TABLE;
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        return index != 9;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (slotIndex < 11) {
                if (!this.insertItem(originalStack, 11, 47, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(originalStack, newStack);
            } else {
                if (originalStack.isOf(ModItems.UNLOCK_SCROLL) || originalStack.isOf(ModItems.EMPTY_SCROLL)) {
                    if (!this.insertItem(originalStack, 9, 10, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (originalStack.isOf(Items.LAPIS_LAZULI)) {
                    if (!this.insertItem(originalStack, 10, 11, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.insertItem(originalStack, 0, 8, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (originalStack.getCount() == newStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, originalStack);
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void fillInputSlots(boolean syncId, RecipeEntry<?> recipe, ServerPlayerEntity player) {
        if (recipe.value() instanceof ScrollTableRecipe scrollRecipe) {
            this.dropOrMoveIngredients(player);

            new InputSlotFiller<>(this).fillInputSlots(player, (RecipeEntry<ScrollTableRecipe>) recipe, syncId);
        }
    }
}