package tschipp.carryon.mixin;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.CarryOnEvents;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "canTakeStack", at = @At("HEAD"), cancellable = true)
    private void carryon$preventTakeCarryItem(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = ((Slot)(Object)this).getStack();
        if (stack == null) return;
        Item item = stack.getItem();
        if (item == CarryOnEvents.TILE_ITEM || item == CarryOnEvents.ENTITY_ITEM) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isItemValid", at = @At("HEAD"), cancellable = true)
    private void carryon$preventPlaceCarryItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) return;
        Item item = stack.getItem();
        if (item == CarryOnEvents.TILE_ITEM || item == CarryOnEvents.ENTITY_ITEM) {
            cir.setReturnValue(false);
        }
    }
}