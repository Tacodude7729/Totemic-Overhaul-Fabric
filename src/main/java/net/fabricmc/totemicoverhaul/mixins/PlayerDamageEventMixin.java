package net.fabricmc.totemicoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.totemicoverhaul.TotemItem;
import net.fabricmc.totemicoverhaul.TotemicOverhaul;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(PlayerEntity.class)
public class PlayerDamageEventMixin {

    @Inject(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (((Object) this) instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) ((Object) this);
            TotemItem.activateTotem(player, TotemItem.findTotem(TotemicOverhaul.ID_TOTEM_ACTIVATOR_DAMAGE, player));
        }
    }
}
