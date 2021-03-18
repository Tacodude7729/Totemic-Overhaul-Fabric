package net.fabricmc.totemicoverhaul.effects;

import java.util.Set;

import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.fabricmc.totemicoverhaul.utils.SetUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TotemEffectNourish implements ITotemEffect {

    private static final int[] materialCost = new int[] { 2, 4, 6, 8 };
    private static final Set<Item> ingredients = SetUtils.of(Items.COOKED_BEEF, Items.COOKED_CHICKEN, Items.COOKED_COD,
            Items.COOKED_MUTTON, Items.COOKED_PORKCHOP, Items.COOKED_RABBIT, Items.COOKED_SALMON, Items.BAKED_POTATO);

    @Override
    public Text getTooltip() {
        return new TranslatableText("item.totemicoverhaul.totem.effect.nourish");
    }

    @Override
    public TotemEffectType getType() {
        return TotemEffectType.ACTIVE_ONLY;
    }

    @Override
    public Set<Item> getIngredients() {
        return ingredients;
    }

    @Override
    public int getMaxLevel(TotemType type) {
        return 4;
    }

    @Override
    public int getMaterialForLevel(int level, TotemType type) {
        return materialCost[level];
    }

    @Override
    public void applyActive(LivingEntity entity, TotemEffectInstance effectInstance) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            int currentFoodLevel = player.getHungerManager().getFoodLevel();
            player.getHungerManager().setFoodLevel(currentFoodLevel + (effectInstance.getLevel() * 4));
        }
    }
}
