package net.fabricmc.totemicoverhaul;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.totemicoverhaul.TotemActivatorRegistry.TotemActivator;
import net.fabricmc.totemicoverhaul.effects.ITotemEffect;
import net.fabricmc.totemicoverhaul.effects.ITotemEffect.TotemEffectType;
import net.fabricmc.totemicoverhaul.utils.NBTType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TotemItem extends Item {

    public static final TotemItem INSTANCE = new TotemItem(
            new FabricItemSettings().group(ItemGroup.MISC).maxCount(1).maxDamage(1000));

    public static final Random random = new Random();

    public static void onInit() {
        Registry.register(Registry.ITEM, TotemicOverhaul.ID_ITEM_TOTEM, INSTANCE);

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTicks() % 40 == 0) {
                for (ITotemEffect effect : TotemEffectRegistry.getAll())
                    effect.metaPassive(server);

                HashMap<ITotemEffect, TotemEffectInstance> effects = new HashMap<ITotemEffect, TotemEffectInstance>();
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

                    Inventory inv = player.getInventory();
                    for (int slot = 0; slot < inv.size(); slot++) {
                        ItemStack stack = inv.getStack(slot);
                        if (stack.getItem() == INSTANCE) {
                            TotemInfo info = new TotemInfo(stack);
                            if (info.getType() == TotemType.PASSIVE && !info.isBroken()) {
                                for (Entry<ITotemEffect, TotemEffectInstance> effectEntry : info.getEffects()
                                        .entrySet()) {
                                    TotemEffectInstance currentApplied = effects.get(effectEntry.getKey());
                                    if (currentApplied == null) {
                                        effects.put(effectEntry.getKey(), effectEntry.getValue());
                                    } else {
                                        if (currentApplied.getLevel() < effectEntry.getValue().getLevel()) {
                                            effects.put(effectEntry.getKey(), effectEntry.getValue());
                                        }
                                    }
                                }

                                int stackDamage = info.getDamage();
                                int damageAmount = info.getEffects().size();
                                if (damageAmount > 5)
                                    damageAmount = 5;
                                if (info.isNetherite()) {
                                    if (random.nextFloat() < 0.1)
                                        stackDamage += damageAmount;
                                } else {
                                    stackDamage += damageAmount;
                                }
                                if (stackDamage >= 999) {
                                    stack.getSubTag("Totem").putBoolean("Broken", true);
                                    stack.getTag().putInt("Damage", 0);
                                    player.getEntityWorld().playSound(null, player.getBlockPos(),
                                            SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1f, 1f);
                                } else {
                                    stack.getTag().putInt("Damage", stackDamage);
                                }

                            }
                        }
                    }
                    for (Entry<ITotemEffect, TotemEffectInstance> effectEntry : effects.entrySet()) {
                        effectEntry.getKey().tickPassive(player, effectEntry.getValue());
                    }
                    effects.clear();

                }
            }
            if (server.getTicks() % 10 == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (player.getY() <= 0) {
                        activateTotem(player, findTotem(TotemicOverhaul.ID_TOTEM_ACTIVATOR_VOID, player));
                    }
                    if (player.getHealth() < 8) {
                        activateTotem(player, findTotem(TotemicOverhaul.ID_TOTEM_ACTIVATOR_7_HEALTH, player));
                    }
                }
            }
        });
    }

    public static void onClientInit() {
        FabricModelPredicateProviderRegistry.register(INSTANCE, TotemicOverhaul.ID_MODEL_PREDICATE_TOTEM_TYPE,
                (itemStack, clientWorld, livingEntity, a) -> {
                    return new TotemInfo(itemStack).type.id;
                });
        FabricModelPredicateProviderRegistry.register(INSTANCE, TotemicOverhaul.ID_MODEL_PREDICATE_TOTEM_TYPE,
                (itemStack, clientWorld, livingEntity, a) -> {
                    return new TotemInfo(itemStack).type.id;
                });
        FabricModelPredicateProviderRegistry.register(INSTANCE, TotemicOverhaul.ID_MODEL_PREDICATE_TOTEM_NETHERITE,
                (itemStack, clientWorald, livingEntity, a) -> {
                    return new TotemInfo(itemStack).isNetherite() ? 1 : 0;
                });
        FabricModelPredicateProviderRegistry.register(INSTANCE, TotemicOverhaul.ID_MODEL_PREDICATE_TOTEM_BROKEN,
                (itemStack, clientWorld, livingEntity, a) -> {
                    return new TotemInfo(itemStack).isBroken() ? 1 : 0;
                });

        ClientPlayNetworking.registerGlobalReceiver(TotemicOverhaul.ID_NETWORKING_TOTEM_ANIMATION_PACKET,
                (client, handler, buf, responseSender) -> {
                    ItemStack totem = buf.readItemStack();
                    client.execute(() -> {
                        client.gameRenderer.showFloatingItem(totem);
                    });
                });

        ClientPlayNetworking.registerGlobalReceiver(TotemicOverhaul.ID_NETWORKING_TOTEM_EFFECT_PACKET,
                (client, handler, buf, responseSender) -> {
                    Entity entity = client.world.getEntityById(buf.readInt());
                    if (entity != null)
                        client.execute(() -> {
                            client.particleManager.addEmitter(entity, ParticleTypes.TOTEM_OF_UNDYING, 30);
                            client.world.playSound(entity.getX(), entity.getY(), entity.getZ(),
                                    SoundEvents.ITEM_TOTEM_USE, entity.getSoundCategory(), 1.0F, 1.0F, false);
                        });
                });
    }

    public static void activateTotem(LivingEntity entity, ItemStack totem) {
        if (entity.world.isClient())
            return;

        if (totem == null || totem.getItem() != INSTANCE)
            return;

        TotemInfo info = new TotemInfo(totem);
        if (info.type != TotemType.ACTIVE) {
            TotemicOverhaul.LOGGER.warn("Tried to activate a non-active totem!");
            return;
        }

        ArrayList<Entry<ITotemEffect, TotemEffectInstance>> effectsOrdered = new ArrayList<Entry<ITotemEffect, TotemEffectInstance>>();
        for (Entry<ITotemEffect, TotemEffectInstance> effectEntry : info.getEffects().entrySet())
            effectsOrdered.add(effectEntry);
        effectsOrdered.sort((a, b) -> Integer.compare(a.getKey().getPriority(), b.getKey().getPriority()));
        for (int i = 0; i < effectsOrdered.size(); i++) {
            Entry<ITotemEffect, TotemEffectInstance> entry = effectsOrdered.get(i);
            entry.getKey().applyActive(entity, entry.getValue());
        }

        for (ServerPlayerEntity player : PlayerLookup.tracking((ServerWorld) entity.world, entity.getBlockPos())) {
            PacketByteBuf packetBuffer = PacketByteBufs.create();
            packetBuffer.writeInt(entity.getId());
            ServerPlayNetworking.send(player, TotemicOverhaul.ID_NETWORKING_TOTEM_EFFECT_PACKET, packetBuffer);
        }

        if (entity instanceof ServerPlayerEntity) {
            PacketByteBuf packetBuffer = PacketByteBufs.create();
            packetBuffer.writeItemStack(totem);
            ServerPlayNetworking.send((ServerPlayerEntity) entity, TotemicOverhaul.ID_NETWORKING_TOTEM_ANIMATION_PACKET,
                    packetBuffer);
        }

        if (info.isNetherite) {
            int damage = totem.getTag().getInt("Damage") + 333;
            if (damage >= 999) {
                totem.decrement(1);
                entity.getEntityWorld().playSound(null, entity.getBlockPos(), SoundEvents.ENTITY_ITEM_BREAK,
                        SoundCategory.PLAYERS, 1f, 1f);
            } else {
                totem.getTag().putInt("Damage", damage);
            }
        } else {
            totem.decrement(1);
        }
    }

    public static ItemStack findTotem(Identifier activator, ServerPlayerEntity player) {
        Inventory inv = player.getInventory();
        for (int slot = 0; slot < inv.size(); slot++) {
            ItemStack stack = inv.getStack(slot);
            if (stack.getItem() == INSTANCE) {
                TotemInfo info = new TotemInfo(stack);
                if (info.isActivatedBy(activator))
                    return stack;
            }
        }
        return null;
    }

    public TotemItem(Settings settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        TotemInfo info = new TotemInfo(stack);

        if (info.activators.size() != 0) {
            tooltip.add(new LiteralText(""));
            tooltip.add(new TranslatableText("item.totemicoverhaul.totem.tooltip.activate_prefix")
                    .formatted(Formatting.BLUE, Formatting.UNDERLINE));
            for (TotemActivator a : info.activators) {
                tooltip.add(new LiteralText(" ").append(new TranslatableText(a.tooltipTranslationKey))
                        .formatted(Formatting.GRAY));
            }
        }

        tooltip.add(new LiteralText(""));
        if (info.effects.size() != 0 || info.isActivatedBy(TotemicOverhaul.ID_TOTEM_ACTIVATOR_DEATH)) {
            tooltip.add(new TranslatableText("item.totemicoverhaul.totem.tooltip.effect_prefix")
                    .formatted(Formatting.BLUE, Formatting.UNDERLINE));
            if (info.isActivatedBy(TotemicOverhaul.ID_TOTEM_ACTIVATOR_DEATH)) {
                tooltip.add(new LiteralText(" ")
                        .append(new TranslatableText("item.totemicoverhaul.totem.activate.death.effect")
                                .formatted(Formatting.GRAY)));
            }
            for (Entry<ITotemEffect, TotemEffectInstance> effectEntry : info.getEffects().entrySet()) {
                TotemEffectInstance inst = effectEntry.getValue();
                MutableText tooltipLine = new LiteralText(" ").append(effectEntry.getKey().getTooltip());
                if (inst.getEffect().getMaxLevel(info.getType()) != 0)
                    tooltipLine.append(" ")
                            .append(new TranslatableText("item.totemicoverhaul.totem.effect_level." + inst.getLevel()));
                if (!inst.isAtMaxLevel()) {
                    tooltipLine.append(" [" + inst.getProgress() + "/"
                            + inst.getEffect().getMaterialForLevel(inst.getLevel(), info.getType()) + "]");
                }
                tooltip.add(tooltipLine.formatted(Formatting.GRAY));
            }
        } else {
            tooltip.add(new TranslatableText("item.totemicoverhaul.totem.tooltip.no_effects").formatted(Formatting.GRAY,
                    Formatting.UNDERLINE));
        }
        tooltip.add(new LiteralText(""));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.world.isClient())
            return super.use(world, user, hand);

        ItemStack stack = user.getStackInHand(hand);
        TotemInfo info = new TotemInfo(stack);
        if (info.isActivatedBy(TotemicOverhaul.ID_TOTEM_ACTIVATOR_CLICK)) {
            activateTotem(user, stack);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.world.isClient())
            return super.useOnEntity(stack, user, entity, hand);
        TotemInfo info = new TotemInfo(stack);
        if (info.isActivatedBy(TotemicOverhaul.ID_TOTEM_ACTIVATOR_CLICK_ENTITY)) {
            activateTotem(entity, stack);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public Text getName(ItemStack stack) {
        TotemInfo info = new TotemInfo(stack);
        if (info.type == TotemType.NONE) {
            return new TranslatableText("item.totemicoverhaul.totem.name.none").formatted(Formatting.GOLD);
        } else if (info.type == TotemType.ACTIVE) {
            if (info.isNetherite) {
                return new TranslatableText("item.totemicoverhaul.totem.name.active_netherite")
                        .formatted(Formatting.LIGHT_PURPLE);
            } else {
                return new TranslatableText("item.totemicoverhaul.totem.name.active").formatted(Formatting.YELLOW);
            }
        } else if (info.type == TotemType.PASSIVE) {
            if (info.isNetherite) {
                if (info.isBroken()) {
                    return new TranslatableText("item.totemicoverhaul.totem.name.passive_netherite_broken")
                            .formatted(Formatting.LIGHT_PURPLE);
                } else {
                    return new TranslatableText("item.totemicoverhaul.totem.name.passive_netherite")
                            .formatted(Formatting.LIGHT_PURPLE);
                }
            } else {
                if (info.isBroken()) {
                    return new TranslatableText("item.totemicoverhaul.totem.name.passive_broken")
                            .formatted(Formatting.GOLD);
                } else {
                    return new TranslatableText("item.totemicoverhaul.totem.name.passive").formatted(Formatting.YELLOW);
                }
            }
        }
        return new LiteralText("This should never display.");
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    public static class TotemEffectInstance {
        private ITotemEffect effect;
        private int level;
        private int progress;
        private TotemType type;

        public TotemEffectInstance(ITotemEffect effect, int level, int progress, TotemType type) {
            this.effect = effect;
            this.level = level;
            this.progress = progress;
            this.type = type;
        }

        private TotemEffectInstance(NbtCompound tag, TotemType type) {
            Identifier effectKey = new Identifier(tag.getString("Effect"));
            this.effect = TotemEffectRegistry.get(effectKey);
            this.type = type;

            if (tag.contains("Level", NBTType.ANY_NUMBER.id))
                this.level = tag.getInt("Level");
            else
                this.level = 0;

            if (tag.contains("Progress", NBTType.ANY_NUMBER.id))
                this.progress = tag.getInt("Progress");
            else
                this.progress = 0;
        }

        private NbtCompound write() {
            NbtCompound tag = new NbtCompound();
            tag.putString("Effect", TotemEffectRegistry.get(effect).toString());
            tag.putInt("Level", level);
            tag.putInt("Progress", progress);
            return tag;
        }

        public ITotemEffect getEffect() {
            return effect;
        }

        public int getProgress() {
            return progress;
        }

        public int getLevel() {
            return level;
        }

        public TotemType getTotemType() {
            return type;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public void setProgress(int progress) {
            this.progress = progress;
            if (!isAtMaxLevel())
                if (progress >= effect.getMaterialForLevel(level, type)) {
                    this.progress = 0;
                    setLevel(level + 1);
                }
        }

        public boolean isAtMaxLevel() {
            return level == effect.getMaxLevel(type);
        }
    }

    public static class TotemInfo {
        private TotemType type;
        private ArrayList<TotemActivator> activators;
        private Map<ITotemEffect, TotemEffectInstance> effects;

        private boolean isNetherite, isBroken;
        private int damage;

        public TotemInfo(TotemType type) {
            this.type = type;
            this.effects = new HashMap<ITotemEffect, TotemEffectInstance>();
            this.activators = new ArrayList<TotemActivator>();
        }

        public TotemInfo(ItemStack stack) {
            if (!(stack.getItem() instanceof TotemItem))
                throw new IllegalArgumentException("Can't get totem info for a " + stack.getItem().toString());

            this.effects = new HashMap<ITotemEffect, TotemEffectInstance>();
            this.activators = new ArrayList<TotemActivator>();
            this.type = TotemType.NONE;

            if (stack.getTag().contains("Damage", NBTType.INT.id)) {
                this.damage = stack.getTag().getInt("Damage");
            } else {
                this.damage = 0;
            }

            NbtCompound totem = stack.getSubTag("Totem");
            if (totem == null)
                return;

            if (totem.contains("Activators", NBTType.LIST.id)) {
                type = TotemType.ACTIVE;
                NbtList activatorList = totem.getList("Activators", NBTType.STRING.id);
                for (int i = 0; i < activatorList.size(); i++) {
                    TotemActivator activator = TotemActivatorRegistry.get(new Identifier(activatorList.getString(i)));
                    if (activator == null) {
                        TotemicOverhaul.LOGGER
                                .warn("Invalid totem activator \"" + activatorList.getString(i) + "\". Ignoring.");
                        continue;
                    }
                    activators.add(activator);
                }
            }

            if (totem.contains("Effects", NBTType.LIST.id)) {
                if (type == TotemType.NONE)
                    type = TotemType.PASSIVE;
                NbtList effectsTag = totem.getList("Effects", NBTType.COMPOUND.id);
                for (int i = 0; i < effectsTag.size(); i++) {
                    NbtCompound effectTag = effectsTag.getCompound(i);
                    addEffect(new TotemEffectInstance(effectTag, type));
                }
            }

            if (totem.contains("Netherite", NBTType.BYTE.id)) {
                isNetherite = totem.getBoolean("Netherite");
            }

            if (totem.contains("Broken", NBTType.BYTE.id)) {
                setBroken(totem.getBoolean("Broken"));
            }
        }

        public ItemStack generateStack() {
            if (type == TotemType.NONE) {
                return new ItemStack(TotemItem.INSTANCE, 1);
            } else {
                NbtCompound totem = new NbtCompound();
                if (type == TotemType.ACTIVE) {
                    NbtList activatorsTag = new NbtList();
                    for (TotemActivator activator : activators) {
                        activatorsTag.add(NbtString.of(activator.id.toString()));
                    }
                    totem.put("Activators", activatorsTag);
                }
                NbtList effectsTag = new NbtList();
                for (Entry<ITotemEffect, TotemEffectInstance> effectEntry : getEffects().entrySet()) {
                    effectsTag.add(effectEntry.getValue().write());
                }
                totem.put("Effects", effectsTag);
                totem.put("Netherite", NbtByte.of(isNetherite));
                totem.put("Broken", NbtByte.of(isBroken));
                ItemStack stack = new ItemStack(TotemItem.INSTANCE, 1);
                stack.putSubTag("Totem", totem);
                if (damage != 0)
                    stack.getTag().putInt("Damage", damage);
                return stack;
            }
        }

        public TotemType getType() {
            return type;
        }

        public void setType(TotemType type) {
            this.type = type;
        }

        public ArrayList<TotemActivator> getActivators() {
            return activators;
        }

        public Map<ITotemEffect, TotemEffectInstance> getEffects() {
            return effects;
        }

        public boolean addActivator(TotemActivator activator) {
            if (activators.contains(activator))
                return false;
            activators.add(activator);
            return true;
        }

        public TotemEffectInstance getEffect(ITotemEffect effect) {
            return effects.get(effect);
        }

        public boolean addEffect(TotemEffectInstance effect) {
            if (effects.containsKey(effect.getEffect()))
                return false;
            if (checkEffect(effect)) {
                effects.put(effect.getEffect(), effect);
                return true;
            } else {
                return false;
            }
        }

        public boolean hasEffect(ITotemEffect effect) {
            return effects.containsKey(effect);
        }

        public boolean isNetherite() {
            return isNetherite;
        }

        public void setNetherite(boolean isNetherite) {
            this.isNetherite = isNetherite;
        }

        public boolean isBroken() {
            return isBroken;
        }

        public void setBroken(boolean isBroken) {
            if (isBroken) {
                if (type != TotemType.PASSIVE)
                    this.isBroken = false;
                else {
                    this.isBroken = true;
                    this.damage = 0;
                }
            } else {
                this.isBroken = false;
            }
        }

        public boolean isActivatedBy(Identifier id) {
            if (type == TotemType.ACTIVE) {
                for (TotemActivator activator : activators) {
                    if (activator.id.equals(id))
                        return true;
                }
                return false;
            } else {
                return false;
            }
        }

        public int getDamage() {
            return damage;
        }

        public void setDamage(int damage) {
            this.damage = damage;
        }

        private boolean checkEffect(TotemEffectInstance effect) {
            if (effect.getEffect() == null)
                return false;
            if (effect.getTotemType() != type)
                return false;
            if (effect.getEffect().getType() == TotemEffectType.ACTIVE_ONLY && type != TotemType.ACTIVE) {
                return false;
            }
            if (effect.getEffect().getType() == TotemEffectType.PASSIVE_ONLY & type != TotemType.PASSIVE) {
                return false;
            }
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TotemInfo))
                return false;
            TotemInfo info = (TotemInfo) obj;
            return type == info.type && isNetherite == info.isNetherite && activators.equals(info.activators)
                    && effects.equals(info.effects);
        }
    }

    public static enum TotemType {
        ACTIVE((byte) 1), PASSIVE((byte) 2), NONE((byte) 0);

        public final byte id;

        private TotemType(byte id) {
            this.id = id;
        }

        public static TotemType byId(byte id) {
            for (TotemType type : values())
                if (type.id == id)
                    return type;
            return NONE;
        }
    }
}
