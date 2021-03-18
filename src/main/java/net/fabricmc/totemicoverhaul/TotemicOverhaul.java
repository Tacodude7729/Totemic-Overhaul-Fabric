package net.fabricmc.totemicoverhaul;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.totemicoverhaul.crafting.TotemCrafting;
import net.fabricmc.totemicoverhaul.crafting.TotemSmithing;
import net.minecraft.util.Identifier;

public class TotemicOverhaul implements ModInitializer, ClientModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("TotemicOverhaul");

	@Override
	public void onInitialize() {
		TotemItem.onInit();
		MiscItems.onInit();
		TotemCrafting.onInit();
		TotemSmithing.onInit();
		LOGGER.info("Totemic Overhaul by Tacodude Initalized.");
	}

	@Override
	public void onInitializeClient() {
		TotemItem.onClientInit();		
	}

	public static Identifier ID_NETWORKING_TOTEM_ANIMATION_PACKET = new Identifier("totemicoverhaul", "totem_animation");
	public static Identifier ID_NETWORKING_TOTEM_EFFECT_PACKET = new Identifier("totemicoverhaul", "totem_effect");

	public static Identifier ID_ITEM_TOTEM = new Identifier("totemicoverhaul", "totem");

	public static Identifier ID_RECIPIE_TOTEM = new Identifier("totemicoverhaul", "crafting_special_totem");
	public static Identifier ID_RECIPIE_TOTEM_UPGRADE = new Identifier("totemicoverhaul", "crafting_special_totem_upgrade");

	public static Identifier ID_MODEL_PREDICATE_TOTEM_TYPE = new Identifier("totem"); // Dosn't work when domain is set
	public static Identifier ID_MODEL_PREDICATE_TOTEM_NETHERITE = new Identifier("totem_netherite");
	public static Identifier ID_MODEL_PREDICATE_TOTEM_BROKEN = new Identifier("totem_broken");

	public static Identifier ID_TOTEM_ACTIVATOR_CLICK = new Identifier("totemicoverhaul", "click");
	public static Identifier ID_TOTEM_ACTIVATOR_CLICK_ENTITY = new Identifier("totemicoverhaul", "click_entity");
	public static Identifier ID_TOTEM_ACTIVATOR_DEATH = new Identifier("totemicoverhaul", "death");
	public static Identifier ID_TOTEM_ACTIVATOR_QUARTER_HEALTH = new Identifier("totemicoverhaul", "quarter_health");
}
