package net.fabricmc.totemicoverhaul.effects;

import java.util.Set;

import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TotemEffectPotion implements ITotemEffect {

    public final StatusEffect statusEffect;

    private final TotemEffectType effectType;
    private final Set<Item> ingredients;

    private final int[] passiveUpgradeCosts, activeUpgradeCosts;

    public TotemEffectPotion(StatusEffect statusEffect, TotemEffectType effectType, Item ingredient, int[] upgradeCosts) {
        this(statusEffect, effectType, ingredient, upgradeCosts, upgradeCosts);
    }

    public TotemEffectPotion(StatusEffect statusEffect, TotemEffectType effectType, Item ingredient, int[] passiveUpgradeCosts, int[] activeUpgradeCosts) {
        this.statusEffect = statusEffect;
        this.effectType = effectType;
        this.ingredients = Set.of(ingredient);
        this.passiveUpgradeCosts = passiveUpgradeCosts;
        this.activeUpgradeCosts = activeUpgradeCosts;
    }

    @Override
    public Text getTooltip() {
        return new TranslatableText(statusEffect.getTranslationKey());
    }

    @Override
    public TotemEffectType getType() {
        return effectType;
    }

    @Override
    public void applyActive(LivingEntity entity, TotemEffectInstance effectInstance) {
        entity.applyStatusEffect(new StatusEffectInstance(statusEffect, 20 * 60, effectInstance.getLevel()));
    }

    @Override
    public int tickPassive(LivingEntity entity, TotemEffectInstance effectInstance) {
        entity.applyStatusEffect(new StatusEffectInstance(statusEffect, 200, effectInstance.getLevel(), true, false, false));
        return effectInstance.getLevel() + 1;
    }

    @Override
    public int getMaxLevel(TotemType type) {
        if (type == TotemType.ACTIVE) {
            return activeUpgradeCosts.length;
        } else {
            return passiveUpgradeCosts.length;
        }
    }

    @Override
    public int getMaterialForLevel(int level, TotemType type) {
        if (type == TotemType.ACTIVE) {
            return activeUpgradeCosts[level];
        } else {
            return passiveUpgradeCosts[level];
        }
    }

    @Override
    public Set<Item> getIngredients() {
        return ingredients;
    }

}
