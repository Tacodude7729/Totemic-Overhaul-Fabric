package net.fabricmc.totemicoverhaul;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MiscItems {
        public static final Item FERMENTED_CARROT = new Item(new FabricItemSettings().group(ItemGroup.MISC)
                        .food(new FoodComponent.Builder().hunger(4).saturationModifier(0.1F)
                                        .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 0), 0.8F)
                                        .build()));
        public static final Item FERMENTED_GOLDEN_CARROT = new Item(new FabricItemSettings().group(ItemGroup.MISC)
                        .food(new FoodComponent.Builder().hunger(4).build()));
        public static final Item GOLDEN_RABBIT_FOOT = new Item(new FabricItemSettings().group(ItemGroup.MISC));

        public static void onInit() {
                Registry.register(Registry.ITEM, new Identifier("totemicoverhaul", "fermented_carrot"),
                                FERMENTED_CARROT);
                Registry.register(Registry.ITEM, new Identifier("totemicoverhaul", "fermented_golden_carrot"),
                                FERMENTED_GOLDEN_CARROT);
                Registry.register(Registry.ITEM, new Identifier("totemicoverhaul", "golden_rabbit_foot"),
                                GOLDEN_RABBIT_FOOT);
        }
}
