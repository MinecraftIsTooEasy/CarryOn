package tschipp.carryon.mixin;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.CarryOnItems;
import tschipp.carryon.CarryOnHelper;
import tschipp.carryon.PickupHandler;
import tschipp.carryon.item.ItemEntity;
import tschipp.carryon.item.ItemTile;
import tschipp.carryon.mixin.accessor.Packet81RightClickAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Mixin(PlayerConnection.class)
public class NetServerHandlerMixin {

    @Shadow public ServerPlayer playerEntity;

    @Unique private static final Map<Integer, Long> pickupCooldown = new HashMap<>();

    @Unique private static final long COOLDOWN_MS = 500L;

    @Inject(method = "handleWindowClick", at = @At("HEAD"), cancellable = true)
    private void carryon$lockSlotOnWindowClick(Packet102WindowClick packet, CallbackInfo ci)
    {
        ServerPlayer player = this.playerEntity;
        if (player == null) return;

        if (!CarryOnHelper.isCarryStack(player.getHeldItemStack())) return;

        ci.cancel();
    }

    @Inject(method = "handleBlockItemSwitch", at = @At("HEAD"), cancellable = true)
    private void carryon$lockSlotOnHotbarSwitch(Packet16BlockItemSwitch packet, CallbackInfo ci)
    {
        ServerPlayer player = this.playerEntity;

        if (player == null) return;

        if (!CarryOnHelper.isCarryStack(player.getHeldItemStack())) return;
        ci.cancel();
    }

    @Inject(method = "handleRightClick", at = @At("HEAD"), cancellable = true)
    private void carryon$handleRightClick(Packet81RightClick packet, CallbackInfo ci)
    {
        ServerPlayer player = this.playerEntity;

        if (player == null) return;

        World world = player.worldObj;

        if (world == null) return;

        ItemStack held = player.getHeldItemStack();

        // While carrying, only allow the item's own right-click (which places the carried
        // block/entity back into the world). Block activation, entity interaction, ingestion
        // and all other right-click actions are cancelled.
        if (CarryOnHelper.isCarryStack(held))
        {
            RightClickFilter filter = ((Packet81RightClickAccessor) packet).getFilter();

            // allowsOnItemRightClick → this is the placement path, let it through
            if (filter != null && filter.allowsOnItemRightClick()) return;

            // Everything else (block activation, entity interaction, ingestion, …) → cancel
            ci.cancel();
            return;
        }

        // ---- pickup logic (sneaking, empty hand) ----
        if (!player.isSneaking() || player.hasHeldItem()) return;

        // Record which hotbar slot is currently empty — the carry item must always
        // land here, and the client must be forced back to this slot after pickup.
        // This prevents a scroll-wheel packet arriving after pickup from silently
        // moving focus away from the carry item.
        final int carrySlot = player.inventory.currentItem;

        // Entity-only packet — animal/villager right-click
        if (((Packet81RightClickAccessor) packet).getFilter().allowsEntityInteractionOnly())
        {
            Entity entity = world.getEntityByID(packet.entity_id);

            if (entity != null && !entity.isDead && PickupHandler.canPlayerPickUpEntity(player, entity)
                    && checkCooldown(player.entityId))
            {
                ItemStack stack = new ItemStack(CarryOnItems.ENTITY_ITEM);

                if (ItemEntity.storeEntityData(entity, world, stack))
                {
                    entity.setDead();
                    forceCarrySlot(player, carrySlot, stack);
                    ci.cancel();
                }
            }
            return;
        }

        if (!packet.requiresRaycasting()) return;

        // Temporarily apply packet position for server-side raycast
        RaycastCollision raycastCollision = withPacketPos(player, packet, () -> player.getSelectedObject(((Packet81RightClickAccessor) packet).getPartial_tick(), false, false, null));

        if (raycastCollision == null) return;

        if (raycastCollision.isBlock()) {

            Block block = raycastCollision.getBlockHit();

            int x = raycastCollision.block_hit_x, y = raycastCollision.block_hit_y, z = raycastCollision.block_hit_z;

            if (block == null || !PickupHandler.canPlayerPickUpBlock(player, world.getBlockTileEntity(x, y, z), world, x, y, z)) return;

            if (block.getBlockHardness(world.getBlockMetadata(x, y, z)) < 0) return;

            // Anti-fly: two combined checks.
            // 1. Target block must be within 1 block below the player's feet.
            // 2. The block directly under the player's feet must NOT be a functional block.
            //    (Prevents picking up while jumping off a functional block to carry it upward.)
            {
                // Use ceil so that edge-clinging (posY=64.6) counts as being at level 65,
                // closing the 1% gap where shift-sneaking on a block edge bypassed floor().
                int footY = MathHelper.ceiling_double_int(player.posY);
                if (y < footY - 1) return;

                int underX = MathHelper.floor_double(player.posX);
                int underZ = MathHelper.floor_double(player.posZ);
                Block underBlock = Block.blocksList[world.getBlockId(underX, footY - 1, underZ)];

                if (underBlock != null && PickupHandler.canPlayerPickUpBlock(player, world.getBlockTileEntity(underX, footY - 1, underZ), world, underX, footY - 1, underZ)) return;
            }

            if (ItemTile.isLocked(x, y, z, world)) return;

            if (!checkCooldown(player.entityId)) {
                ci.cancel(); return;
            }

            ItemStack stack = new ItemStack(CarryOnItems.TILE_ITEM);

            TileEntity tileEntityToPickup = world.getBlockTileEntity(x, y, z);
            if (ItemTile.storeTileData(tileEntityToPickup, world, x, y, z, stack))
            {
                if (tileEntityToPickup instanceof IInventory) {
                    IInventory iInventory = (IInventory) tileEntityToPickup;
                    for (int i = 0; i < iInventory.getSizeInventory(); i++) {
                        iInventory.setInventorySlotContents(i, null);
                    }
                }
                world.setBlockToAir(x, y, z);
                forceCarrySlot(player, carrySlot, stack);
                ci.cancel();
            }
        }
        else if (raycastCollision.isEntity())
        {
            Entity entity = raycastCollision.getEntityHit();

            if (entity == null || entity.isDead) return;

            if (!PickupHandler.canPlayerPickUpEntity(player, entity)) return;

            if (!checkCooldown(player.entityId))
            {
                ci.cancel(); return;
            }

            ItemStack stack = new ItemStack(CarryOnItems.ENTITY_ITEM);

            if (ItemEntity.storeEntityData(entity, world, stack))
            {
                entity.setDead();
                forceCarrySlot(player, carrySlot, stack);
                ci.cancel();
            }
        }
    }

    @Unique
    private static void forceCarrySlot(ServerPlayer player, int slot, ItemStack stack)
    {
        player.inventory.mainInventory[slot] = stack;
        player.inventory.currentItem = slot;
        // Tell the client to select this slot.
        player.sendPacket(new Packet16BlockItemSwitch(slot));
        // Sync the full inventory so the client sees the item in the correct slot.
        player.sendContainerToPlayer(player.inventoryContainer);
    }

    /** Returns true and records timestamp if outside cooldown window; false if still cooling down. */
    @Unique
    private static boolean checkCooldown(int playerId)
    {
        long now = System.currentTimeMillis();
        Long last = pickupCooldown.get(playerId);

        if (last != null && now - last < COOLDOWN_MS)
            return false;

        pickupCooldown.put(playerId, now);
        return true;
    }

    /** Temporarily applies packet position/rotation to the player for a server-side raycast. */
    @Unique
    private static RaycastCollision withPacketPos(ServerPlayer player, Packet81RightClick pkt, Supplier<RaycastCollision> fn)
    {
        double ox = player.posX, oy = player.posY, oz = player.posZ;
        float oyaw = player.rotationYaw, opitch = player.rotationPitch;
        double opx = player.prevPosX, opy = player.prevPosY, opz = player.prevPosZ;
        float opyaw = player.prevRotationYaw, oppitch = player.prevRotationPitch;
        float oySize = player.ySize;
        AxisAlignedBB obb = player.boundingBox.copy();

        player.posX = pkt.pos_x; player.posY = pkt.pos_y; player.posZ = pkt.pos_z;
        player.rotationYaw = pkt.rotation_yaw; player.rotationPitch = pkt.rotation_pitch;
        player.prevPosX = pkt.prev_pos_x; player.prevPosY = pkt.prev_pos_y; player.prevPosZ = pkt.prev_pos_z;
        player.prevRotationYaw = pkt.prev_rotation_yaw; player.prevRotationPitch = pkt.prev_rotation_pitch;
        player.ySize = pkt.y_size;
        player.boundingBox.setBB(pkt.bb);

        RaycastCollision rc = fn.get();

        player.posX = ox; player.posY = oy; player.posZ = oz;
        player.rotationYaw = oyaw; player.rotationPitch = opitch;
        player.prevPosX = opx; player.prevPosY = opy; player.prevPosZ = opz;
        player.prevRotationYaw = opyaw; player.prevRotationPitch = oppitch;
        player.ySize = oySize;
        player.boundingBox.setBB(obb);

        return rc;
    }
}