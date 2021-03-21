package net.fabricmc.totemicoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public interface TryUseTotemMixin {
    @Invoker("tryUseTotem")
    public boolean invokeTryUseTotem(DamageSource source);

}
