package net.fabricmc.totemicoverhaul.effects;

import java.util.Set;

import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.fabricmc.totemicoverhaul.utils.SetUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TotemEffectWeather implements ITotemEffect {

    private final Set<Item> ingredients;
    private final int[] upgradeCosts = new int[] { 8, 16, 32, 64, 96, 128, 160 };

    private final boolean raining, thundering;
    private final String translationKey;

    public TotemEffectWeather(String translationKey, boolean raining, boolean thundering, Item ingredient,
            int[] upgradeCosts) {
        this.raining = raining;
        this.thundering = thundering;
        this.translationKey = translationKey;
        this.ingredients = SetUtils.of(ingredient);
    }

    @Override
    public Text getTooltip() {
        return new TranslatableText(translationKey);
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
        int duration = effectInstance.getLevel() * 20 * 60 * 5;
        if (raining)
            ((ServerWorld) entity.getEntityWorld()).setWeather(0, duration, raining, thundering);
        else
            ((ServerWorld) entity.getEntityWorld()).setWeather(duration, 0, raining, thundering);
    }
}
