package tschipp.carryon.mixin.client;

import net.minecraft.Minecraft;
import net.minecraft.WorldClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.client.render.CarriedBlockRenderer;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "loadWorld(Lnet/minecraft/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void clearCarriedRenderCache(WorldClient world, String message, CallbackInfo ci) {
        CarriedBlockRenderer.clearRenderCache();
    }
}
