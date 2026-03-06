package tschipp.carryon.mixin.client;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.CarryOnHelper;

/**
 * Prevents digging and entity-attacking while the player is carrying a block or entity.
 */
@Mixin(bdc.class)
public class PlayerControllerMixin {

    /** Suppress initial left-click on block (starts digging). */
    @Inject(method = "clickBlock(IIILnet/minecraft/EnumFace;)V", at = @At("HEAD"), cancellable = true)
    private void carryon$blockClickBlock(int x, int y, int z, EnumFace face, CallbackInfo ci)
    {
        if (CarryOnHelper.isCarrying(Minecraft.w().h)) ci.cancel();
    }

    /** Suppress continued digging progress, crack animation and particles. */
    @Inject(method = "onPlayerDamageBlock(IIILnet/minecraft/EnumFace;)V", at = @At("HEAD"), cancellable = true)
    private void carryon$blockDamageBlock(int x, int y, int z, EnumFace face, CallbackInfo ci)
    {
        if (CarryOnHelper.isCarrying(Minecraft.w().h)) ci.cancel();
    }

    /** Suppress left-click entity attack (arm swing animation + attack). */
    @Inject(method = "leftClickEntity", at = @At("HEAD"), cancellable = true)
    private void carryon$blockLeftClickEntity(Entity target, CallbackInfo ci)
    {
        if (CarryOnHelper.isCarrying(Minecraft.w().h)) ci.cancel();
    }
}