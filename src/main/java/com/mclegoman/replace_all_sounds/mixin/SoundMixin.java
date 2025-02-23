package com.mclegoman.replace_all_sounds.mixin;

import com.mclegoman.replace_all_sounds.ReplaceSoundManager;
import net.minecraft.client.sound.Sound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.FloatSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Sound.class)
public abstract class SoundMixin {
	@Mutable @Shadow @Final private Identifier id;
	@Inject(at = @At(value = "RETURN"), method = "<init>")
	private void replace_all_sounds$replaceSound(Identifier id, FloatSupplier volume, FloatSupplier pitch, int weight, Sound.RegistrationType registrationType, boolean stream, boolean preload, int attenuation, CallbackInfo ci) {
		Identifier replace = ReplaceSoundManager.getSound(ReplaceSoundManager.Type.sound, this.id);
		if (replace != null) this.id = replace;
	}
}