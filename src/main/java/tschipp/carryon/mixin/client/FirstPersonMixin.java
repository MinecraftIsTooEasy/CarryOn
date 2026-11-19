package tschipp.carryon.mixin.client;

import net.minecraft.ItemRenderer;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.CarryOnEvents;
import tschipp.carryon.client.render.BlockRendererLayer;
import tschipp.carryon.client.render.EntityRendererLayer;

@Mixin(ItemRenderer.class)
public class FirstPersonMixin {

    @Inject(method = "renderItemInFirstPerson", at = @At("HEAD"), cancellable = true)
    private void onRenderItem(float partialTicks, CallbackInfo info) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        ItemStack stack = mc.thePlayer.getHeldItemStack();
        if (stack == null) return;

        boolean carryingBlock = stack.getItem() == CarryOnEvents.TILE_ITEM;
        if (!carryingBlock && stack.getItem() != CarryOnEvents.ENTITY_ITEM) return;

        info.cancel();
        if (carryingBlock) {
            BlockRendererLayer.renderFirstPerson(mc.thePlayer, stack, partialTicks);
        } else {
            EntityRendererLayer.renderFirstPerson(mc.thePlayer, stack, partialTicks);
        }
    }
}
