package net.fabricmc.totemicoverhaul.crafting;

import java.util.Collection;
import java.util.Set;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.totemicoverhaul.TotemActivatorRegistry;
import net.fabricmc.totemicoverhaul.TotemActivatorRegistry.TotemActivator;
import net.fabricmc.totemicoverhaul.TotemEffectRegistry;
import net.fabricmc.totemicoverhaul.TotemItem;
import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemInfo;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.fabricmc.totemicoverhaul.TotemicOverhaul;
import net.fabricmc.totemicoverhaul.effects.ITotemEffect;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TotemCrafting extends SpecialCraftingRecipe {

    public static final Item PASSIVE_TOTEM_ITEM = Items.DIAMOND;
    public static final Item ACTIVE_TOTEM_ITEM = Items.EMERALD;

    public static SpecialRecipeSerializer<TotemCrafting> TOTEM_RECIPE_SERIALIZER = new SpecialRecipeSerializer<TotemCrafting>(
            TotemCrafting::new);

    public static void onInit() {
        Registry.register(Registry.RECIPE_SERIALIZER, TotemicOverhaul.ID_RECIPIE_TOTEM, TOTEM_RECIPE_SERIALIZER);
    }

    public TotemCrafting(Identifier id) {
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
        return TOTEM_RECIPE_SERIALIZER;
    }

    private ItemStack getResult(CraftingInventory inv) {
        TotemInfo info = null;

        for (int slot = 0; slot < inv.size(); slot++) {
            ItemStack stack = inv.getStack(slot);
            if (stack != null) {
                if (stack.getItem() == TotemItem.INSTANCE) {
                    if (info != null)
                        return null; // Two totems
                    info = new TotemInfo(stack);
                }
            }
        }

        if (info == null)
            return null;
        switch (info.getType()) {
        case NONE:
            int foundIngredient = 0;
            Item ingredientType = null;
            for (int slot = 0; slot < inv.size(); slot++) {
                ItemStack stack = inv.getStack(slot);
                if (stack != null && !stack.isEmpty()) {
                    if (stack.getItem() == TotemItem.INSTANCE)
                        continue;
                    if (stack.getItem() == PASSIVE_TOTEM_ITEM || stack.getItem() == ACTIVE_TOTEM_ITEM) {
                        if (foundIngredient >= 2) {
                            return null; // More than two upgrade ingredients.
                        }
                        if (foundIngredient == 1) {
                            if (stack.getItem() != ingredientType)
                                return null;
                        }
                        ++foundIngredient;
                        ingredientType = stack.getItem();
                    } else {
                        return null;
                    }
                }
            }

            if (foundIngredient == 2) {
                if (ingredientType == PASSIVE_TOTEM_ITEM) {
                    info.setType(TotemType.PASSIVE);
                    return info.generateStack();
                } else if (ingredientType == ACTIVE_TOTEM_ITEM) {
                    info.setType(TotemType.ACTIVE);
                    return info.generateStack();
                }
            }
            return null;
        case ACTIVE:
            Set<ITotemEffect> allEffects = TotemEffectRegistry.getAll();
            Collection<TotemActivator> allActivators = TotemActivatorRegistry.getAll();
            boolean didSomething = false;
            for (int slot = 0; slot < inv.size(); slot++) {
                ItemStack stack = inv.getStack(slot);
                if (stack != null && !stack.isEmpty()) {
                    if (stack.getItem() == TotemItem.INSTANCE)
                        continue;
                    boolean found = false;

                    for (ITotemEffect effect : allEffects) {
                        if (effect.getIngredients().contains(stack.getItem())) {
                            TotemEffectInstance instance = info.getEffect(effect);
                            if (instance == null) {
                                if (info.addEffect(new TotemEffectInstance(effect, 0, 0, info.getType()))) {
                                    found = true;
                                }
                            } else {
                                if (instance.isAtMaxLevel())
                                    return null;
                                instance.setProgress(instance.getProgress() + 1);
                                found = true;
                            }
                        }
                    }

                    for (TotemActivator activator : allActivators) {
                        if (activator.ingredient == stack.getItem()) {
                            if (!info.addActivator(activator)) {
                                return null;
                            }
                            found = true;
                        }
                    }

                    if (!found) {
                        return null; // Invalid ingredient
                    } else {
                        didSomething = true;
                    }
                }
            }
            if (didSomething)
                return info.generateStack();
            else
                return null;
        case PASSIVE:
            allEffects = TotemEffectRegistry.getAll();
            didSomething = false;
            for (int slot = 0; slot < inv.size(); slot++) {
                ItemStack stack = inv.getStack(slot);
                if (stack != null && !stack.isEmpty()) {
                    if (stack.getItem() == TotemItem.INSTANCE)
                        continue;
                    boolean found = false;
                    for (ITotemEffect effect : allEffects) {
                        if (effect.getIngredients().contains(stack.getItem())) {
                            TotemEffectInstance instance = info.getEffect(effect);
                            if (instance == null)
                                info.addEffect(new TotemEffectInstance(effect, 0, 0, info.getType()));
                            else {
                                if (instance.isAtMaxLevel())
                                    return null;
                                instance.setProgress(instance.getProgress() + 1);
                            }
                            found = true;
                        }
                    }
                    if (!found) {
                        return null; // Invalid ingredient
                    } else {
                        didSomething = true;
                    }
                }
            }
            if (didSomething)
                return info.generateStack();
            else
                return null;
        default:
            return null;
        }
    }

}
