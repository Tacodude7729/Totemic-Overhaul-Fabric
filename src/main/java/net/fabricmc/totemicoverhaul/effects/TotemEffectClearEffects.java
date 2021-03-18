package net.fabricmc.totemicoverhaul.effects;

import java.util.Set;

import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TotemEffectClearEffects implements ITotemEffect {

    private static final Set<Item> ingredients = Set.of(Items.MILK_BUCKET);

    @Override
    public Text getTooltip() {
        return new TranslatableText("item.totemicoverhaul.totem.effect.clear");
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
        return 0;
    }

    @Override
    public int getMaterialForLevel(int level, TotemType type) {
        throw new RuntimeException("Can't upgrade ClearEffects.");
    }

    @Override
    public void applyActive(LivingEntity entity, TotemEffectInstance effectInstance) {
        entity.clearStatusEffects();
    }

    @Override
    public int getPriority() {
        return -10;
    }
}
