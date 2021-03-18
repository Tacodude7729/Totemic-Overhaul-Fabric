package net.fabricmc.totemicoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

@Mixin(ModelPredicateProviderRegistry.class)
public interface ModelPredicateMixin {
    @Invoker("register")
    public static void register(Item item, Identifier id, ModelPredicateProvider provider) {
        throw new AssertionError();
    }
}
