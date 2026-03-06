package tschipp.carryon.mixin;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.CarryOnHelper;
import tschipp.carryon.PickupHandler;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true)
    public void onBlockActivated(World world, int x, int y, int z, EntityPlayer player, EnumFace face, float offsetX, float offsetY, float offsetZ, CallbackInfoReturnable<Boolean> info)
    {
        if (!world.isRemote) return;

        ItemStack held = player.getHeldItemStack();

        if (CarryOnHelper.isCarryStack(held))
        {
            info.setReturnValue(false);
            info.cancel();
            return;
        }

        if (player.isSneaking() && held == null && PickupHandler.canPlayerPickUpBlock(player, world.getBlockTileEntity(x, y, z), world, x, y, z))
        {
            // Anti-fly: combined check — distance limit + no pickup while standing on functional block.
            // Use ceil so shift-sneaking on a block edge (posY=64.6) counts as level 65.
            int footY = MathHelper.ceiling_double_int(player.posY);
            if (y < footY - 1) return;

            int underX = MathHelper.floor_double(player.posX);
            int underZ = MathHelper.floor_double(player.posZ);
            Block underBlock = Block.blocksList[world.getBlockId(underX, footY - 1, underZ)];

            if (underBlock != null && PickupHandler.canPlayerPickUpBlock(player, world.getBlockTileEntity(underX, footY - 1, underZ), world, underX, footY - 1, underZ)) return;

            info.setReturnValue(false);
            info.cancel();
        }
    }
}