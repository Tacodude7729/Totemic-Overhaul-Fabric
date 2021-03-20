package net.fabricmc.totemicoverhaul.effects;

import java.util.Set;

import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.fabricmc.totemicoverhaul.utils.SetUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TotemEffectHarm implements ITotemEffect {

    private static final Set<Item> ingredients = SetUtils.of(Items.STONE_SWORD);
    private static final int[] upgradeCosts = new int[] {8, 16};

    @Override
    public Text getTooltip() {
        return new TranslatableText("item.totemicoverhaul.totem.effect.harming");
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
        entity.damage(DamageSource.MAGIC, (effectInstance.getLevel() + 1) * 4);
    }
}
