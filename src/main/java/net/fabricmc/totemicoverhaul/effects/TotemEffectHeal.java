package net.fabricmc.totemicoverhaul.effects;

import java.util.Set;

import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TotemEffectHeal implements ITotemEffect {

    private static final Set<Item> ingredients = Set.of(Items.GLISTERING_MELON_SLICE);
    private static final int[] upgradeCosts = new int[] {8, 16, 32, 64, 96, 128, 160};

    @Override
    public Text getTooltip() {
        return new TranslatableText("item.totemicoverhaul.totem.effect.health");
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
        return upgradeCosts.length;
    }

    @Override
    public int getMaterialForLevel(int level, TotemType type) {
        return upgradeCosts[level];
    }

    @Override
    public void applyActive(LivingEntity entity, TotemEffectInstance effectInstance) {
        entity.heal((effectInstance.getLevel() + 1) * 4);
    }
}
