package tschipp.carryon.mixin;

import net.minecraft.Minecraft;

import tschipp.carryon.CarryOnItems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class CraftingManagerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void afterInit(CallbackInfo ci) {
        CarryOnItems.register();
    }
}