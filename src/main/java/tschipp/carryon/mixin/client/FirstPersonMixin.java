package tschipp.carryon.mixin.client;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.CarryOnItems;
import tschipp.carryon.client.render.BlockRendererLayer;
import tschipp.carryon.client.render.EntityRendererLayer;

@Mixin(bfj.class)
public class FirstPersonMixin {

    @Inject(method = "a(F)V", at = @At("HEAD"), cancellable = true)
    public void onRenderItem(float partialTicks, CallbackInfo info)
    {
        Minecraft mc = Minecraft.w();
        if (mc == null || mc.h == null) return;

        ItemStack stack = mc.h.getHeldItemStack();
        if (stack == null) return;

        if (stack.getItem() == CarryOnItems.TILE_ITEM)
        {
            info.cancel();
            BlockRendererLayer.renderFirstPerson(mc.h, stack, partialTicks);
        }
        else if (stack.getItem() == CarryOnItems.ENTITY_ITEM)
        {
            info.cancel();
            EntityRendererLayer.renderFirstPerson(mc.h, stack, partialTicks);
        }
    }

}