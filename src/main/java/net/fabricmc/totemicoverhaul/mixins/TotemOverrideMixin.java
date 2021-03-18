package net.fabricmc.totemicoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.fabricmc.totemicoverhaul.TotemItem;
import net.fabricmc.totemicoverhaul.TotemicOverhaul;
import net.fabricmc.totemicoverhaul.TotemItem.TotemInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Mixin(LivingEntity.class)
public class TotemOverrideMixin {

    @Redirect(method = "damage",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;tryUseTotem(Lnet/minecraft/entity/damage/DamageSource;)Z"))
    private boolean tryUseTotem(LivingEntity entity, DamageSource source) {
        TotemInfo totem = null;
        ItemStack totemItem = null;
        Hand[] hands = Hand.values();
        
        for (Hand hand : hands) {
            ItemStack inHand = entity.getStackInHand(hand);
            if (inHand.getItem() == TotemItem.INSTANCE) {
                TotemInfo totemInHand = new TotemInfo(inHand);
                if (totemInHand.isActivatedBy(TotemicOverhaul.ID_TOTEM_ACTIVATOR_DEATH)) {
                    totem = totemInHand;
                    totemItem = inHand;
                    break;
                }
            }
        }

        if (totem != null) {
            entity.setHealth(1F);
            TotemItem.activateTotem(entity, totemItem);
            return true;
        }

        return false;
    }
}
