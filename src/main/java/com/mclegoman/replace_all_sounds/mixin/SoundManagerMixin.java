package com.mclegoman.replace_all_sounds.mixin;

import com.mclegoman.replace_all_sounds.ReplaceSoundManager;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin extends SinglePreparationResourceReloader<SoundManager.SoundList> {
	@Inject(method = "prepare(Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)Lnet/minecraft/client/sound/SoundManager$SoundList;", at = @At("HEAD"))
	private void replace_all_sounds$prepare(ResourceManager resourceManager, Profiler profiler, CallbackInfoReturnable<SoundManager.SoundList> cir) {
		new ReplaceSoundManager().reload();
	}
}