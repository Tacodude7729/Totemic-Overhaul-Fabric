package net.fabricmc.totemicoverhaul.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityRespawnMixin {
    @Invoker("moveToSpawn")
    public void invokeMoveToSpawn(ServerWorld world);
}
