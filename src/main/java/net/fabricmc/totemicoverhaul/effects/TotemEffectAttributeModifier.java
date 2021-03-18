package net.fabricmc.totemicoverhaul.effects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.fabricmc.totemicoverhaul.TotemItem.TotemEffectInstance;
import net.fabricmc.totemicoverhaul.TotemItem.TotemType;
import net.fabricmc.totemicoverhaul.utils.SetUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class TotemEffectAttributeModifier implements ITotemEffect {

    private final TranslatableText tooltip;
    private final Set<Item> ingredients;
    private final HashMap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    private final int[] upgradeCosts;

    private final HashSet<LivingEntity> appliedEntities;

    public TotemEffectAttributeModifier(String translationKey, Item ingredient, int[] upgradeCosts) {
        this.tooltip = new TranslatableText(translationKey);
        this.attributeModifiers = new HashMap<EntityAttribute, EntityAttributeModifier>();
        this.ingredients = SetUtils.of(ingredient);
        this.upgradeCosts = upgradeCosts;
        this.appliedEntities = new HashSet<LivingEntity>();
    }

    public TotemEffectAttributeModifier addAttributeModifier(EntityAttribute attribute, String uuid, double amount,
            EntityAttributeModifier.Operation operation) {
        EntityAttributeModifier entityAttributeModifier = new EntityAttributeModifier(UUID.fromString(uuid),
                tooltip.getKey(), amount, operation);
        attributeModifiers.put(attribute, entityAttributeModifier);
        return this;
    }

    @Override
    public Text getTooltip() {
        return tooltip;
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

    @Override
    public void metaPassive(MinecraftServer server) {
        for (ServerPlayerEntity entity : server.getPlayerManager().getPlayerList()) {
            if (!appliedEntities.contains(entity)) {
                if (entity.getHealth() > entity.getMaxHealth()) {
                    entity.setHealth(entity.getMaxHealth());
                }

                AttributeContainer attributes = entity.getAttributes();
                for (Entry<EntityAttribute, EntityAttributeModifier> entry : attributeModifiers.entrySet()) {
                    EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
                    if (entityAttributeInstance != null) {
                        entityAttributeInstance.removeModifier(entry.getValue());
                    }
                }
            }
        }
        appliedEntities.clear();
    }

    @Override
    public int tickPassive(LivingEntity entity, TotemEffectInstance effectInstance) {
        appliedEntities.add(entity);
        apply(entity, effectInstance);
        return effectInstance.getLevel() + 1;
    }

    private void apply(LivingEntity entity, TotemEffectInstance effectInstance) {
        AttributeContainer attributes = entity.getAttributes();
        for (Entry<EntityAttribute, EntityAttributeModifier> entry : attributeModifiers.entrySet()) {
            EntityAttributeInstance entityAttributeInstance = attributes.getCustomInstance(entry.getKey());
            if (entityAttributeInstance != null) {
                EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier) entry.getValue();
                entityAttributeInstance.removeModifier(entityAttributeModifier);
                entityAttributeInstance
                        .addPersistentModifier(new EntityAttributeModifier(entityAttributeModifier.getId(),
                                tooltip.getKey(), entry.getValue().getValue() * (effectInstance.getLevel() + 1),
                                entityAttributeModifier.getOperation()));
            }
        }
    }
}
