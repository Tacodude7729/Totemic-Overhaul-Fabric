package net.fabricmc.totemicoverhaul;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TotemActivatorRegistry {
    private static HashMap<Identifier, TotemActivator> map;

    public static void register(TotemActivator activator) {
        map.put(activator.id, activator);
    }

    public static TotemActivator get(Identifier id) {
        return map.get(id);
    }

    public static Collection<TotemActivator> getAll() {
        return map.values();
    }

    static {
        map = new HashMap<Identifier, TotemActivator>();

        register(new TotemActivator(TotemicOverhaul.ID_TOTEM_ACTIVATOR_CLICK, Items.LEVER));
        register(new TotemActivator(TotemicOverhaul.ID_TOTEM_ACTIVATOR_CLICK_ENTITY, Items.GUNPOWDER));
        register(new TotemActivator(TotemicOverhaul.ID_TOTEM_ACTIVATOR_VOID, Items.END_STONE));
        register(new TotemActivator(TotemicOverhaul.ID_TOTEM_ACTIVATOR_DEATH, Items.EMERALD_BLOCK));
        register(new TotemActivator(TotemicOverhaul.ID_TOTEM_ACTIVATOR_7_HEALTH, Items.NETHER_WART));
        register(new TotemActivator(TotemicOverhaul.ID_TOTEM_ACTIVATOR_DAMAGE, Items.NETHER_WART_BLOCK));
    }

    public static class TotemActivator {
        public final Identifier id;
        public final Item ingredient;
        public final String tooltipTranslationKey;

        public TotemActivator(Identifier id, Item ingredient) {
            this.id = id;
            this.tooltipTranslationKey = "item.totemicoverhaul.totem.activate."+(id.getPath());
            this.ingredient = ingredient;
        }
    }
}
