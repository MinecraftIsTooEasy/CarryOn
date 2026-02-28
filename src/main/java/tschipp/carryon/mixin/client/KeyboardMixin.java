package tschipp.carryon.mixin.client;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.CarryOnEvents;

@Mixin(value = Minecraft.class, priority = 10001)
public abstract class KeyboardMixin {

    @Shadow public EntityClientPlayerMP thePlayer;

    /** Checks whether the player is currently holding a CarryOn carry item. */
    @Unique
    private boolean carryon_isCarrying()
    {
        if (thePlayer == null) return false;

        ItemStack held = thePlayer.getHeldItemStack();

        return held != null && (held.getItem() == CarryOnEvents.TILE_ITEM || held.getItem() == CarryOnEvents.ENTITY_ITEM);
    }

    /**
     * Cancels inventory opening if the player is carrying something.
     */
    @Inject(method = "runTick", at = @At(value = "NEW", target = "net/minecraft/GuiInventory"), cancellable = true)
    private void cancelInventoryIfCarrying(CallbackInfo ci)
    {
        if (carryon_isCarrying())
        {
            ci.cancel();
        }
    }

    /**
     * Redirects inventory.changeCurrentItem(dwheel) — the scroll wheel path.
     * When carrying, we swallow the call entirely.
     */
    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "net/minecraft/InventoryPlayer.changeCurrentItem(I)V"))
    private void redirectScrollHotbar(InventoryPlayer inventory, int direction)
    {
        if (!carryon_isCarrying())
        {
            inventory.changeCurrentItem(direction);
        }
    }

    /** Saved hotbar slot captured at the start of each tick. Only valid when {@code carryon_wasCarrying} is true. */
    @Unique private int carryon_savedSlot    = 0;
    /** Whether the player was already carrying at the start of this tick. */
    @Unique private boolean carryon_wasCarrying = false;

    /** Capture the current hotbar slot at the very start of runTick. */
    @Inject(method = "runTick", at = @At("HEAD"))
    private void carryon_captureSlot(CallbackInfo ci)
    {
        carryon_wasCarrying = carryon_isCarrying();

        if (carryon_wasCarrying && thePlayer != null)
            carryon_savedSlot = thePlayer.inventory.currentItem;
    }

    /**
     * Restore the hotbar slot at the end of runTick — only if the player was
     * already carrying at the start of this tick. This prevents slot changes
     * made by number keys or scroll wheel from taking effect while carrying,
     * without interfering with the pickup tick itself.
     */
    @Inject(method = "runTick", at = @At("TAIL"))
    private void carryon_restoreSlot(CallbackInfo ci)
    {
        if (carryon_wasCarrying && thePlayer != null)
            thePlayer.inventory.currentItem = carryon_savedSlot;
    }

    /**
     * Bottom-level drop key suppression: intercept KeyBinding.onTick(keyCode) in the
     * keyboard event loop. When carrying, skip the call for keyBindDrop so pressTime
     * never increments — the key is completely inert regardless of how fast it is pressed.
     */
    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "net/minecraft/KeyBinding.onTick(I)V"))
    private void redirectOnTick(int keyCode)
    {
        if (carryon_isCarrying())
        {
            GameSettings settings = ((Minecraft) (Object) this).gameSettings;
            if (settings != null && keyCode == settings.keyBindDrop.keyCode) return;
        }

        KeyBinding.onTick(keyCode);
    }
}