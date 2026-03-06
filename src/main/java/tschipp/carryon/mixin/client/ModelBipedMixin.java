package tschipp.carryon.mixin.client;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.client.render.ICarrying;

@Mixin(bbj.class)
public class ModelBipedMixin implements ICarrying {

    @Unique private boolean isCarryingBlock;
    @Unique private boolean isCarryingEntity;

    @Shadow public bcu f;
    @Shadow public bcu g;
    @Shadow public boolean n;

    @Override
    public boolean carryOn$isCarryingBlock() {
        return isCarryingBlock;
    }

    @Override
    public boolean carryOn$isCarryingEntity() {
        return isCarryingEntity;
    }

    @Override
    public void carryOn$setCarryingBlock(boolean block) {
        isCarryingBlock = block;
    }

    @Override
    public void carryOn$setCarryingEntity(boolean entity) {
        isCarryingEntity = entity;
    }

    @Inject(method = "a(FFFFFFLnet/minecraft/Entity;)V", at = @At("RETURN"))
    public void onSetAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entity, CallbackInfo info)
    {
        if (isCarryingBlock)
        {
            float x = -1F + (n ? 0f : 0.2f);
            f.f = x; f.g = 0f; f.h =  0f;
            g.f  = x; g.g  = 0f; g.h  =  0f;
        }

        else if (isCarryingEntity)
        {
            float x = -1.2F + (n ? 0f : 0.2f);
            f.f = x; f.g = 0f; f.h = -0.15f;
            g.f  = x; g.g  = 0f; g.h  =  0.15f;
        }
    }
}