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

    @Shadow public ClientPlayer h;
    @Shadow public bdd f;
    @Shadow public RaycastCollision t;
    @Shadow private aur S;
    @Shadow public int right_click_counter;

    @Inject(method = "c(I)V", at = @At("TAIL"), remap = false)
    private void carryon$onClickMouseTail(int button, CallbackInfo ci)
    {
        if (button != 1) return;
        if (right_click_counter > 0) return;

        ClientPlayer player = h;

        if (player == null) return;

        if (f == null) return;

        if (!player.isSneaking()) return;

        if (player.getHeldItemStack() != null) return;

        if (CarryOnHelper.isCarrying(player)) return;

        RaycastCollision collision = t;
        if (collision == null || !collision.isBlock()) return;

        int x = collision.block_hit_x, y = collision.block_hit_y, z = collision.block_hit_z;
        Block block = Block.blocksList[f.getBlockId(x, y, z)];

        if (block == null) return;

        if (!PickupHandler.canPlayerPickUpBlock(player, f.getBlockTileEntity(x, y, z), f, x, y, z)) return;

        int footY = MathHelper.ceiling_double_int(player.posY - player.ySize);

        if (y < footY - 2) return;

        int underX = MathHelper.floor_double(player.posX);
        int underZ = MathHelper.floor_double(player.posZ);

        Block underBlock = Block.blocksList[f.getBlockId(underX, footY - 1, underZ)];

        if (underBlock != null && PickupHandler.canPlayerPickUpBlock(player, f.getBlockTileEntity(underX, footY - 1, underZ), f, underX, footY - 1, underZ)) return;

        RightClickFilter pickupFilter = new RightClickFilter();
        pickupFilter.setExclusive(RightClickFilter.BLOCK_ACTIVATION);
        player.sendPacket(new Packet81RightClick(player, S.c, pickupFilter));
        right_click_counter = 10;
    }
}