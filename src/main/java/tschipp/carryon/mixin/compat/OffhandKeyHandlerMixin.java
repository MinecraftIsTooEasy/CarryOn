package tschipp.carryon.mixin.compat;

import com.m.offhand.client.OffhandKeyHandler;
import net.minecraft.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.compat.OffhandCompat;

/**
 * Prevents the offhand swap key from firing while the player is carrying a block or entity.
 *
 * <p>OffhandKeyHandler detects a fresh key-down each tick and calls {@code requestSwap()},
 * which sends an {@code OffhandSwapRequestPacket} to the server. By cancelling
 * {@code onClientTick} early when carrying, we stop the packet from ever being sent.</p>
 */
@Mixin(value = OffhandKeyHandler.class, remap = false)
public class OffhandKeyHandlerMixin {

    @Inject(method = "onClientTick", at = @At("HEAD"), cancellable = true)
    private void carryon_blockSwapWhileCarrying(Minecraft mc, CallbackInfo ci)
    {
        if (mc.thePlayer == null) return;

        if (OffhandCompat.isCarrying(mc.thePlayer.getHeldItemStack()))
            ci.cancel();
    }
}
