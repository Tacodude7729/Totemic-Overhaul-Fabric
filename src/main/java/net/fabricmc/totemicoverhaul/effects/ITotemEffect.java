package net.fabricmc.totemicoverhaul.effects;

import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

public interface ITotemEffect {

    public Text getTooltip();

    public TotemEffectType getType();

    public default void applyActive(LivingEntity entity, TotemEffectInstance effectInstance) {
        if (getType() == TotemEffectType.PASSIVE_ONLY) {
            throw new NotImplementedException("Cannot apply active effect on a non-active effect type. " + this);
        }
    };

    public default void metaPassive(MinecraftServer server) {}

    public default int tickPassive(LivingEntity entity, TotemEffectInstance effectInstance) {
        if (getType() == TotemEffectType.ACTIVE_ONLY) {
            throw new NotImplementedException("Cannot apply passive effect on a active only effect type. " + this);
        }
        return 1;
    }

    public Set<Item> getIngredients();

    public int getMaxLevel(TotemType type);

    public int getMaterialForLevel(int level, TotemType type);

    public default int getPriority() {
        return 0;
    }

    public static enum TotemEffectType {
        ACTIVE_ONLY, PASSIVE_ONLY, VERSATILE
    }
}
