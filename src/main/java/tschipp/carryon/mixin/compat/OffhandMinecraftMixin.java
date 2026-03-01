package tschipp.carryon.mixin.compat;

import net.minecraft.EntityClientPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.compat.OffhandCompat;

/**
 * Blocks offhand right-click use while the player is carrying a block or entity.
 *
 * <p>OffHand's MixinMinecraft injects {@code offhand$tryUseOffhandRightClick} into
 * {@code Minecraft.runTick} after every right-click. We inject into that same private
 * method (HEAD, cancellable) and return {@code false} to abort the whole pipeline.</p>
 */
@Mixin(targets = "com.m.offhand.mixin.MixinMinecraft", remap = false)
public abstract class OffhandMinecraftMixin {

    @Shadow public EntityClientPlayerMP thePlayer;

    @Inject(method = "offhand$tryUseOffhandRightClick", at = @At("HEAD"), cancellable = true)
    private void carryon_blockOffhandRightClick(CallbackInfoReturnable<Boolean> cir)
    {
        if (thePlayer == null) return;

        if (OffhandCompat.isCarrying(thePlayer.getHeldItemStack()))
            cir.setReturnValue(false);
    }
}
