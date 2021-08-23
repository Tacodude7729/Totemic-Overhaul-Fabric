package net.fabricmc.totemicoverhaul;

import java.util.HashMap;
import java.util.Set;

import net.fabricmc.totemicoverhaul.effects.ITotemEffect;
import net.fabricmc.totemicoverhaul.effects.ITotemEffect.TotemEffectType;
import net.fabricmc.totemicoverhaul.effects.TotemEffectAttributeModifier;
import net.fabricmc.totemicoverhaul.effects.TotemEffectClearEffects;
import net.fabricmc.totemicoverhaul.effects.TotemEffectExplode;
import net.fabricmc.totemicoverhaul.effects.TotemEffectFire;
import net.fabricmc.totemicoverhaul.effects.TotemEffectHarm;
import net.fabricmc.totemicoverhaul.effects.TotemEffectHeal;
import net.fabricmc.totemicoverhaul.effects.TotemEffectNourish;
import net.fabricmc.totemicoverhaul.effects.TotemEffectPotion;
import net.fabricmc.totemicoverhaul.effects.TotemEffectSpawnTP;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TotemEffectRegistry {

    private static HashMap<Identifier, ITotemEffect> idToEffects;
    private static HashMap<ITotemEffect, Identifier> effectsToId;

    public static ITotemEffect register(Identifier id, ITotemEffect effect) {
        if (idToEffects.containsKey(id))
            throw new RuntimeException("You fucked up. Duplicate totem effect identifier "+id+".");
        idToEffects.put(id, effect);
        effectsToId.put(effect, id);
        return effect;
    }

    public static ITotemEffect get(Identifier id) {
        return idToEffects.get(id);
    }

    public static Identifier get(ITotemEffect effect) {
        return effectsToId.get(effect);
    }

    public static Set<ITotemEffect> getAll() {
        return effectsToId.keySet();
    }

    static {
        idToEffects = new HashMap<Identifier, ITotemEffect>();
        effectsToId = new HashMap<ITotemEffect, Identifier>();

        register(new Identifier("totemicoverhaul", "speed"), new TotemEffectPotion(StatusEffects.SPEED, TotemEffectType.VERSATILE, Items.SUGAR, new int[] {4, 8, 16}, new int[] {32, 64, 128}));
        register(new Identifier("totemicoverhaul", "haste"), new TotemEffectPotion(StatusEffects.HASTE, TotemEffectType.VERSATILE, Items.GOLDEN_PICKAXE, new int[] {2, 3, 4, 5}, new int[] {2, 8, 16, 32}));
        register(new Identifier("totemicoverhaul", "wither"), new TotemEffectPotion(StatusEffects.WITHER, TotemEffectType.ACTIVE_ONLY, Items.WITHER_ROSE, new int[] {2, 3, 5}));
        register(new Identifier("totemicoverhaul", "strength"), new TotemEffectPotion(StatusEffects.STRENGTH, TotemEffectType.ACTIVE_ONLY, Items.BLAZE_POWDER, new int[] {16, 32}));
        register(new Identifier("totemicoverhaul", "regeneration"), new TotemEffectPotion(StatusEffects.REGENERATION, TotemEffectType.ACTIVE_ONLY, Items.GHAST_TEAR, new int[] {3, 6, 9}));
        register(new Identifier("totemicoverhaul", "mining_fatigue"), new TotemEffectPotion(StatusEffects.MINING_FATIGUE, TotemEffectType.VERSATILE, Items.PRISMARINE_SHARD, new int[] {8, 16, 32}));
        register(new Identifier("totemicoverhaul", "night_vision"), new TotemEffectPotion(StatusEffects.NIGHT_VISION, TotemEffectType.VERSATILE, Items.GOLDEN_CARROT, new int[] {}));
        register(new Identifier("totemicoverhaul", "blindess"), new TotemEffectPotion(StatusEffects.BLINDNESS, TotemEffectType.VERSATILE, MiscItems.FERMENTED_CARROT, new int[] {}));
        register(new Identifier("totemicoverhaul", "invisibility"), new TotemEffectPotion(StatusEffects.INVISIBILITY, TotemEffectType.ACTIVE_ONLY, MiscItems.FERMENTED_GOLDEN_CARROT, new int[] {}));
        register(new Identifier("totemicoverhaul", "absorption"), new TotemEffectPotion(StatusEffects.ABSORPTION, TotemEffectType.ACTIVE_ONLY, Items.IRON_CHESTPLATE, new int[] {4, 8}));
        register(new Identifier("totemicoverhaul", "poision"), new TotemEffectPotion(StatusEffects.POISON, TotemEffectType.ACTIVE_ONLY, Items.SPIDER_EYE, new int[] {8, 16, 32}));
        register(new Identifier("totemicoverhaul", "weakness"), new TotemEffectPotion(StatusEffects.WEAKNESS, TotemEffectType.VERSATILE, Items.FERMENTED_SPIDER_EYE, new int[] {8, 16, 32}));
        register(new Identifier("totemicoverhaul", "jump_boost"), new TotemEffectPotion(StatusEffects.JUMP_BOOST, TotemEffectType.VERSATILE, Items.RABBIT_FOOT, new int[] {2, 3}, new int[] {8, 16, 32}));
        register(new Identifier("totemicoverhaul", "glowing"), new TotemEffectPotion(StatusEffects.GLOWING, TotemEffectType.VERSATILE, Items.GLOWSTONE_DUST, new int[] {}));
        register(new Identifier("totemicoverhaul", "water_breathing"), new TotemEffectPotion(StatusEffects.WATER_BREATHING, TotemEffectType.VERSATILE, Items.PUFFERFISH, new int[] {}));
        register(new Identifier("totemicoverhaul", "fire_resistance"), new TotemEffectPotion(StatusEffects.FIRE_RESISTANCE, TotemEffectType.VERSATILE, Items.MAGMA_CREAM, new int[] {}));
        
        register(new Identifier("totemicoverhaul", "health_boost"), new TotemEffectAttributeModifier(StatusEffects.HEALTH_BOOST.getTranslationKey(), Items.GOLDEN_APPLE, new int[] {8, 16, 24, 32, 40, 48}).addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, "b7cf1fed-d993-482f-89c5-8be5605f6b31", 4, Operation.ADDITION));
        register(new Identifier("totemicoverhaul", "luck"), new TotemEffectAttributeModifier(StatusEffects.LUCK.getTranslationKey(), MiscItems.GOLDEN_RABBIT_FOOT, new int[] {8, 16}).addAttributeModifier(EntityAttributes.GENERIC_LUCK, "e941389e-0e68-4183-bb09-210e37009414", 1, Operation.ADDITION));
        
        register(new Identifier("totemicoverhaul", "clear_effects"), new TotemEffectClearEffects());
        register(new Identifier("totemicoverhaul", "heal"), new TotemEffectHeal());
        register(new Identifier("totemicoverhaul", "harm"), new TotemEffectHarm());
        register(new Identifier("totemicoverhaul", "spawnpoint"), new TotemEffectSpawnTP());
        register(new Identifier("totemicoverhaul", "nourish"), new TotemEffectNourish());
        register(new Identifier("totemicoverhaul", "expode"), new TotemEffectExplode());
        register(new Identifier("totemicoverhaul", "fire"), new TotemEffectFire());
    }
}
