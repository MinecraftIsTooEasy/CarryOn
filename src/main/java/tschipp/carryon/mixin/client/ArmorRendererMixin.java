package tschipp.carryon.mixin.client;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.CarryOnItems;
import tschipp.carryon.client.render.ICarrying;

@Mixin(bhj.class)
public abstract class ArmorRendererMixin {

    @Shadow private bbj g;
    @Shadow private bbj h;

    @Inject(method = "a(Lnet/minecraft/beu;IF)I", at = @At("RETURN"))
    private void onSetArmorModel(beu par1AbstractClientPlayer, int par2, float par3, CallbackInfoReturnable<Integer> cir)
    {
        ItemStack stack = par1AbstractClientPlayer.getHeldItemStack();

        boolean carryBlock = stack != null && stack.getItem() == CarryOnItems.TILE_ITEM;

        boolean carryEntity = stack != null && stack.getItem() == CarryOnItems.ENTITY_ITEM;

        if (g instanceof ICarrying) {
            ICarrying carrying = (ICarrying) g;
            carrying.carryOn$setCarryingBlock(carryBlock); carrying.carryOn$setCarryingEntity(carryEntity);
        }

        if (h instanceof ICarrying) {
            ICarrying carrying = (ICarrying) h;
            carrying.carryOn$setCarryingBlock(carryBlock); carrying.carryOn$setCarryingEntity(carryEntity);
        }
    }
}