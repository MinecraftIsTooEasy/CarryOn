package tschipp.carryon.mixin;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.CarryOnHelper;

/**
 * Intercepts Packet14BlockDig.processPacket before it reaches NetServerHandler.
 * NetServerHandler does not override handleBlockDig, so we hook the packet itself.
 */
@Mixin(Packet14BlockDig.class)
public class BlockDigPacketMixin {

    @Inject(method = "processPacket", at = @At("HEAD"), cancellable = true)
    private void carryon$cancelDigWhileCarrying(NetHandler handler, CallbackInfo ci)
    {
        if (!handler.isServerHandler()) return;

        if (!(handler instanceof NetServerHandler)) return;

        ServerPlayer player = ((NetServerHandler) handler).playerEntity;
        if (player == null) return;

        ItemStack held = player.getHeldItemStack();
        if (!CarryOnHelper.isCarryStack(held)) return;

        ci.cancel();
    }
}