package net.fabricmc.totemicoverhaul.effects;

import java.util.Set;

import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.fabricmc.totemicoverhaul.utils.SetUtils;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TotemEffectBlankPassive implements ITotemEffect {
    
    private Set<Item> ingredients;
    private int[] upgradeCosts;
    private String translationKey;

    public TotemEffectBlankPassive(String translationKey, Item upgradeItem, int[] costs) {
        this.ingredients = SetUtils.of(upgradeItem);
        this.upgradeCosts = costs;
        this.translationKey = translationKey;
    }


    @Override
    public Text getTooltip() {
        return new TranslatableText(translationKey);
    }

    @Override
    public TotemEffectType getType() {
        return TotemEffectType.PASSIVE_ONLY;
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
}
