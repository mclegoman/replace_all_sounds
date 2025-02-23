package com.mclegoman.replace_all_sounds.mixin;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;<init>(Lnet/minecraft/client/MinecraftClient;Ljava/io/File;)V"))
	private void replace_all_sounds$clientInit(RunArgs runArgs, CallbackInfo ci) {
		if (FabricLoader.getInstance().isModLoaded("fabric-resource-loader-v0")) FabricLoader.getInstance().getModContainer("replace_all_sounds").ifPresent(container -> ResourceManagerHelper.registerBuiltinResourcePack(Identifier.of("low_battery"), container, Text.translatable("replace_all_sounds.resource_pack.low_battery"), ResourcePackActivationType.DEFAULT_ENABLED));
	}
}