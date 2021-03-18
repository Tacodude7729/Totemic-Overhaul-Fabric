package net.fabricmc.totemicoverhaul.effects;

import java.util.Set;

import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.fabricmc.totemicoverhaul.utils.SetUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TotemEffectFire implements ITotemEffect {

    private static final Set<Item> ingredients = SetUtils.of(Items.FLINT_AND_STEEL, Items.FIRE_CHARGE);
    private static final int[] upgradeCosts = new int[] { 8, 16 };

    @Override
    public Text getTooltip() {
        return new TranslatableText("item.totemicoverhaul.totem.effect.fire");
    }

    @Override
    public TotemEffectType getType() {
        return TotemEffectType.VERSATILE;
    }

    @Override
    public Set<Item> getIngredients() {
        return ingredients;
    }

    @Override
    public int getMaxLevel(TotemType type) {
        if (type == TotemType.PASSIVE)
            return 0;
        return upgradeCosts.length;
    }

    @Override
    public int getMaterialForLevel(int level, TotemType type) {
        return upgradeCosts[level];
    }

    @Override
    public void applyActive(LivingEntity entity, TotemEffectInstance effectInstance) {
        entity.setFireTicks(20 * (20 * (effectInstance.getLevel() + 1)));
    }

    @Override
    public int tickPassive(LivingEntity entity, TotemEffectInstance effectInstance) {
        entity.setFireTicks(20 * 10);
        return 1;
    }
}
