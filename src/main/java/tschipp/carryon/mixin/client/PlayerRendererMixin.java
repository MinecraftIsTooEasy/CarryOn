package tschipp.carryon.mixin.client;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.CarryOnItems;
import tschipp.carryon.client.render.BlockRendererLayer;
import tschipp.carryon.client.render.EntityRendererLayer;
import tschipp.carryon.client.render.ICarrying;

@Mixin(bhj.class)
public abstract class PlayerRendererMixin {

    @Shadow private bbj f;

    @Inject(method = "a(Lnet/minecraft/beu;F)V", at = @At("RETURN"))
    private void onRenderSpecials(beu player, float partialTick, CallbackInfo info)
    {
        ItemStack stack  = player.getHeldItemStack();
        ICarrying model  = (ICarrying) this.f;

        if (stack != null && stack.getItem() == CarryOnItems.TILE_ITEM)
        {
            model.carryOn$setCarryingBlock(true);
            model.carryOn$setCarryingEntity(false);
            BlockRendererLayer.renderThirdPerson(player, partialTick);

        }
        else if (stack != null && stack.getItem() == CarryOnItems.ENTITY_ITEM)
        {
            model.carryOn$setCarryingBlock(false);
            model.carryOn$setCarryingEntity(true);
            EntityRendererLayer.renderThirdPerson(player, partialTick);
        }
        else
        {
            model.carryOn$setCarryingBlock(false);
            model.carryOn$setCarryingEntity(false);
        }
    }
}