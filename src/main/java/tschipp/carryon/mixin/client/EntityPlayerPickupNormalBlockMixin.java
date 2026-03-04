package tschipp.carryon.mixin.client;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.CarryOnHelper;
import tschipp.carryon.PickupHandler;

@Mixin(Minecraft.class)
public abstract class EntityPlayerPickupNormalBlockMixin {

    @Shadow public EntityClientPlayerMP thePlayer;
    @Shadow public WorldClient theWorld;
    @Shadow public RaycastCollision objectMouseOver;
    @Shadow private Timer timer;
    @Shadow public int right_click_counter;

    @Inject(method = "clickMouse(I)V", at = @At("TAIL"), remap = false)
    private void carryon$onClickMouseTail(int button, CallbackInfo ci)
    {
        if (button != 1) return;
        if (right_click_counter > 0) return;

        EntityClientPlayerMP player = thePlayer;

        if (player == null) return;

        if (theWorld == null) return;

        if (!player.isSneaking()) return;

        if (player.getHeldItemStack() != null) return;

        if (CarryOnHelper.isCarrying(player)) return;

        RaycastCollision collision = objectMouseOver;
        if (collision == null || !collision.isBlock()) return;

        int x = collision.block_hit_x, y = collision.block_hit_y, z = collision.block_hit_z;
        Block block = Block.blocksList[theWorld.getBlockId(x, y, z)];

        if (block == null) return;

        if (!PickupHandler.canPlayerPickUpBlock(player, theWorld.getBlockTileEntity(x, y, z), theWorld, x, y, z)) return;

        int footY = MathHelper.ceiling_double_int(player.posY - player.ySize);

        if (y < footY - 2) return;

        int underX = MathHelper.floor_double(player.posX);
        int underZ = MathHelper.floor_double(player.posZ);

        Block underBlock = Block.blocksList[theWorld.getBlockId(underX, footY - 1, underZ)];

        if (underBlock != null && PickupHandler.canPlayerPickUpBlock(player, theWorld.getBlockTileEntity(underX, footY - 1, underZ), theWorld, underX, footY - 1, underZ)) return;

        RightClickFilter pickupFilter = new RightClickFilter();
        pickupFilter.setExclusive(RightClickFilter.BLOCK_ACTIVATION);
        player.sendPacket(new Packet81RightClick(player, timer.renderPartialTicks, pickupFilter));
        right_click_counter = 10;
    }
}