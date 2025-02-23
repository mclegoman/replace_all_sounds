package com.mclegoman.replace_all_sounds.mixin;

import com.mclegoman.replace_all_sounds.ReplaceSoundManager;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin {
	@Mutable @Shadow @Final protected Identifier id;
	@Inject(at = @At(value = "RETURN"), method = "<init>(Lnet/minecraft/util/Identifier;Lnet/minecraft/sound/SoundCategory;Lnet/minecraft/util/math/random/Random;)V")
	private void replace_all_sounds$replaceSound(Identifier soundId, SoundCategory category, Random random, CallbackInfo ci) {
		if (!this.id.getPath().contains("music") && !this.id.getPath().contains("loop")) {
			Identifier replace = ReplaceSoundManager.getSound(ReplaceSoundManager.Type.event, this.id);
			if (replace != null) this.id = replace;
		}
	}
}