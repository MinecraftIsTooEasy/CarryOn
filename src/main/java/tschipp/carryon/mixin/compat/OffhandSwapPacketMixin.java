package tschipp.carryon.mixin.compat;

import com.m.offhand.network.OffhandSwapRequestPacket;
import net.minecraft.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.compat.OffhandCompat;

/**
 * Server-side guard: cancels the offhand swap packet while the player is carrying.
 *
 * <p>Even if the client somehow sends the swap request, the server will not execute it
 * while a CarryOn carry item is held in the main hand.</p>
 */
@Mixin(value = OffhandSwapRequestPacket.class, remap = false)
public class OffhandSwapPacketMixin {

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void carryon_blockSwapWhileCarrying(EntityPlayer player, CallbackInfo ci)
    {
        if (player == null) return;

        if (OffhandCompat.isCarrying(player.getHeldItemStack()))
            ci.cancel();
    }
}
