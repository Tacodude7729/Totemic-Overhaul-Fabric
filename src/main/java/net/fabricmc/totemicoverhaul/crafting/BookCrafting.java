package net.fabricmc.totemicoverhaul.crafting;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.totemicoverhaul.TotemicOverhaul;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class BookCrafting extends SpecialCraftingRecipe {

    public static SpecialRecipeSerializer<BookCrafting> BOOK_RECIPE_SERIALIZER = new SpecialRecipeSerializer<BookCrafting>(
            BookCrafting::new);

    public static void onInit() {
        Registry.register(Registry.RECIPE_SERIALIZER, TotemicOverhaul.ID_RECIPIE_BOOK, BOOK_RECIPE_SERIALIZER);
    }

    public BookCrafting(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        return getResult(inv) != null;
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        return getResult(inv);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean fits(int width, int height) {
        return width * height > 0;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BOOK_RECIPE_SERIALIZER;
    }

    private ItemStack getResult(CraftingInventory inv) {

        boolean book = false;
        boolean gold = false;

        for (int slot = 0; slot < inv.size(); slot++) {
            ItemStack stack = inv.getStack(slot);
            if (stack.getItem() == Items.BOOK) {
                if (book)
                    return null;
                book = true;
            } else if (stack.getItem() == Items.GOLD_INGOT) {
                if (gold)
                    return null;
                gold = true;
            } else if (!stack.isEmpty()) {
                return null;
            }
        }

        if (gold && book) {
            Item patchouliBook = (Item) Registry.ITEM.get(new Identifier("patchouli", "guide_book"));
            ItemStack stack = new ItemStack(patchouliBook, 1);
            CompoundTag tag = new CompoundTag();
            tag.putString("patchouli:book", "totemicoverhaul:tutorialbook");
            stack.setTag(tag);
            return stack;
        }
        return null;
    }

}
