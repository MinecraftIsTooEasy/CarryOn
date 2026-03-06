package tschipp.carryon.mixin.client;

import net.minecraft.*;

import tschipp.carryon.CarryOnHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, priority = 10001)
public abstract class KeyboardMixin {

    @Shadow public ClientPlayer h;

    /**
     * Redirects inventory.changeCurrentItem(dwheel) — the scroll wheel path.
     * When carrying, we swallow the call entirely.
     */
    @Redirect(method = "k", at = @At(value = "INVOKE", target = "net/minecraft/PlayerInventory.c(I)V"))
    private void redirectScrollHotbar(PlayerInventory inventory, int direction)
    {
        if (!CarryOnHelper.isCarrying(h))
            inventory.c(direction);
    }

    /** Saved hotbar slot captured at the start of each tick. Only valid when {@code carryon_wasCarrying} is true. */
    @Unique private int carryon_savedSlot = 0;
    /** Whether the player was already carrying at the start of this tick. */
    @Unique private boolean carryon_wasCarrying = false;

    /** Capture the current hotbar slot at the very start of runTick. */
    @Inject(method = "k", at = @At("HEAD"))
    private void carryon_captureSlot(CallbackInfo ci)
    {
        carryon_wasCarrying = CarryOnHelper.isCarrying(h);

        if (carryon_wasCarrying && h != null)
            carryon_savedSlot = h.inventory.currentItem;
    }

    /**
     * Restore the hotbar slot at the end of runTick — only if the player was
     * already carrying at the start of this tick. This prevents slot changes
     * made by number keys or scroll wheel from taking effect while carrying,
     * without interfering with the pickup tick itself.
     */
    @Inject(method = "k", at = @At("TAIL"))
    private void carryon_restoreSlot(CallbackInfo ci)
    {
        if (carryon_wasCarrying && h != null)
            h.inventory.currentItem = carryon_savedSlot;
    }

    /** Zero out drop, attack, and inventory key press-time while carrying. */
    @Redirect(method = "k", at = @At(value = "INVOKE", target = "net/minecraft/ats.a(I)V"))
    private void redirectOnTick(int d)
    {
        if (CarryOnHelper.isCarrying(h))
        {
            aul settings = ((Minecraft) (Object) this).u;

            if (settings != null && d == settings.O.d) return;

            if (settings != null && d == settings.R.d) return;

            if (settings != null && d == settings.N.d) return;
        }

        ats.a(d);
    }
}