package net.fabricmc.totemicoverhaul.crafting;

import com.google.gson.JsonObject;

import net.fabricmc.totemicoverhaul.TotemItem;
import net.fabricmc.totemicoverhaul.TotemItem.TotemInfo;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.fabricmc.totemicoverhaul.TotemicOverhaul;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TotemSmithing extends SmithingRecipe {

    public static final Item FIX_PASSIVE_TOTEM_ITEM = Items.DIAMOND;
    public static final Item FIX_ACTIVE_TOTEM_ITEM = Items.EMERALD;

    public static final TotemUpgradingSerializer TOTEM_UPGRADING_SERIALIZER = new TotemUpgradingSerializer();

    public static void onInit() {
        Registry.register(Registry.RECIPE_SERIALIZER, TotemicOverhaul.ID_RECIPIE_TOTEM_UPGRADE,
                TOTEM_UPGRADING_SERIALIZER);
    }

    public static class TotemUpgradingSerializer implements RecipeSerializer<TotemSmithing> {
        @Override
        public TotemSmithing read(Identifier identifier, JsonObject jsonObject) {
            return new TotemSmithing();
        }

        @Override
        public TotemSmithing read(Identifier identifier, PacketByteBuf packetByteBuf) {
            return new TotemSmithing();
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, TotemSmithing r) {
        }
    }

    public TotemSmithing() {
        super(null, null, null, null);
    }

    @Override
    public boolean matches(Inventory inv, World world) {
        ItemStack totemItem = inv.getStack(0);

        if (totemItem.getItem() != TotemItem.INSTANCE)
            return false;

        TotemInfo info = new TotemInfo(totemItem);

        if (info.getType() == TotemType.PASSIVE) {
            if (inv.getStack(1).getItem() == FIX_PASSIVE_TOTEM_ITEM && (info.getDamage() != 0 || info.isBroken()))
                return true;
            else if (inv.getStack(1).getItem() == Items.NETHERITE_INGOT && !info.isNetherite())
                return true;
        } else {
            if (info.getType() == TotemType.ACTIVE) {
                if (inv.getStack(1).getItem() == Items.NETHERITE_INGOT && !info.isNetherite()) {
                    return true;
                } else if (inv.getStack(1).getItem() == FIX_ACTIVE_TOTEM_ITEM && info.getDamage() != 0) {
                    return true;
                }
            }
        }
        return false;

    }

    @Override
    public ItemStack craft(Inventory inv) {
        ItemStack totemItem = inv.getStack(0);
        TotemInfo info = new TotemInfo(totemItem);

        if (inv.getStack(1).getItem() == Items.NETHERITE_INGOT) {
            info.setNetherite(true);
        } else {
            if (info.getType() == TotemType.PASSIVE) {
                if (info.isBroken()) {
                    info.setBroken(false);
                    info.setDamage(0);
                } else {
                    if (info.isNetherite()) {
                        info.setDamage(Math.max(0, info.getDamage() - 250));
                    } else {
                        info.setDamage(0);
                    }
                }
            } else {
                info.setDamage(info.getDamage() - 333);
            }
        }

        return info.generateStack();
    }

    @Override
    public Identifier getId() {
        return TotemicOverhaul.ID_RECIPIE_TOTEM_UPGRADE;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TOTEM_UPGRADING_SERIALIZER;
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return stack.getItem() == Items.NETHERITE_INGOT;
    }
}
