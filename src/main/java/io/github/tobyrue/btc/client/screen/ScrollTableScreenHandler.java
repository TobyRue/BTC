package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.screen.recipe_book.ScrollTableRecipeInput;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.recipes.ScrollTableRecipe;
import io.github.tobyrue.btc.regestries.ModScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
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

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 10, 8 + j * 18, 118 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 176));
        }

        this.addSlot(new Slot(blockInventory, 0, 79, 11));
        this.addSlot(new Slot(blockInventory, 1, 104, 18));
        this.addSlot(new Slot(blockInventory, 2, 111, 43));
        this.addSlot(new Slot(blockInventory, 3, 104, 68));
        this.addSlot(new Slot(blockInventory, 4, 80, 75));
        this.addSlot(new Slot(blockInventory, 5, 54, 68));
        this.addSlot(new Slot(blockInventory, 6, 47, 43));
        this.addSlot(new Slot(blockInventory, 7, 54, 18));

        this.addSlot(new Slot(blockInventory, 8, 79, 43) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

        this.addSlot(new Slot(blockInventory, 9, 148, 46) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Registries.ITEM.get(BTC.identifierOf("empty_scroll"))) || stack.isOf(Registries.ITEM.get(BTC.identifierOf("unlock_scroll")));
            }
        });

        this.addSlot(new Slot(blockInventory, 10, 148, 69) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Registries.ITEM.get(Identifier.of("minecraft", "lapis_lazuli")));
            }
        });
    }


    @Override
    public void populateRecipeFinder(RecipeMatcher finder) {
        for (int i = 0; i < this.inventory.size(); i++) {
            finder.addInput(this.inventory.getStack(i));
        }
    }

    @Override
    public void clearCraftingSlots() {
        for (int i = 36; i <= 43; i++) { 
            this.getSlot(i).setStackNoCallbacks(ItemStack.EMPTY);
        }
    }

    @Override
    public boolean matches(RecipeEntry<ScrollTableRecipe> entry) {
        ItemStack[] inputs = new ItemStack[8];
        for (int i = 0; i < 8; i++) {
            inputs[i] = this.inventory.getStack(i);
        }
        return entry.value().matches(new ScrollTableRecipeInput(inputs), this.world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 44;
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
        return RecipeBookCategory.CRAFTING;
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        return index != 44;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (slotIndex >= 36) {
                if (!this.insertItem(originalStack, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(originalStack, newStack);
            }
            else {
                if (originalStack.isOf(ModItems.UNLOCK_SCROLL)
                        || originalStack.isOf(ModItems.EMPTY_SCROLL)) {
                    if (!this.insertItem(originalStack, 45, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (originalStack.isOf(Items.LAPIS_LAZULI)) {
                    if (!this.insertItem(originalStack, 46, 47, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else {
                    if (!this.insertItem(originalStack, 36, 44, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}