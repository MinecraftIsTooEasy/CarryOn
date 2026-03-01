package tschipp.carryon.mixin;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.CarryOnEvents;
import tschipp.carryon.items.ItemEntity;
import tschipp.carryon.items.ItemTile;

/**
 * Intercepts {@link InventoryPlayer#dropAllItems()} — the method called by
 * {@link EntityPlayer#onDeath} when {@code keepInventory} is off.
 *
 * <p>Before the items are scattered, any carry item that still holds data is
 * force-placed back into the world (block) or respawned (entity) near the player.
 * This prevents the carry item from landing on the ground as a mysterious empty
 * or data-bearing item entity.</p>
 */
@Mixin(InventoryPlayer.class)
public class InventoryPlayerMixin {

    @Shadow public EntityPlayer player;

    @Shadow public ItemStack[] mainInventory;

    @Inject(method = "dropAllItems", at = @At("HEAD"))
    private void carryon$placeOnDeath(CallbackInfo ci)
    {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return;

        World world = player.worldObj;

        for (int i = 0; i < mainInventory.length; i++)
        {
            ItemStack stack = mainInventory[i];

            if (stack == null) continue;

            if (stack.getItem() == CarryOnEvents.TILE_ITEM && ItemTile.hasTileData(stack))
            {
                carryon$placeTile(world, player, stack);
                mainInventory[i] = null;
            }
            else if (stack.getItem() == CarryOnEvents.ENTITY_ITEM && ItemEntity.hasEntityData(stack))
            {
                carryon$spawnEntity(world, player, stack);
                mainInventory[i] = null;
            }
        }
    }

    @Unique
    private static void carryon$placeTile(World world, EntityPlayer player, ItemStack stack)
    {
        Block block = ItemTile.getBlock(stack);
        int   meta  = ItemTile.getMeta(stack);

        if (block == null || block.blockID == 0)
        {
            ItemTile.clearTileData(stack);
            return;
        }

        int px = (int) Math.floor(player.posX);
        int py = (int) Math.floor(player.posY);
        int pz = (int) Math.floor(player.posZ);

        // Search a 3×2×3 area around the player for a free spot
        boolean placed = false;
        outer:
        for (int dy = 0; dy <= 1; dy++)
        {
            for (int dx = -1; dx <= 1; dx++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    int x = px + dx, y = py + dy, z = pz + dz;

                    if (world.getBlockId(x, y, z) != 0) continue;

                    world.setBlock(x, y, z, block.blockID, meta, 3);

                    NBTTagCompound tileData = ItemTile.getTileData(stack);

                    if (tileData != null && !tileData.hasNoTags())
                    {
                        TileEntity te = world.getBlockTileEntity(x, y, z);

                        if (te != null)
                        {
                            tileData.setInteger("x", x);
                            tileData.setInteger("y", y);
                            tileData.setInteger("z", z);
                            te.readFromNBT(tileData);
                        }
                    }

                    placed = true;
                    break outer;
                }
            }
        }

        if (!placed)
        {
            // No free spot — drop the block as an item entity at player position
            // so the player can recover it rather than losing it permanently.
            // TileEntity data will be lost, but the block itself is preserved.
            Item dropItem = Item.getItem(block);
            if (dropItem != null)
            {
                EntityItem ei = new EntityItem(world,
                        player.posX, player.posY, player.posZ,
                        new ItemStack(dropItem, 1, meta));
                ei.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld(ei);
            }
        }

        ItemTile.clearTileData(stack);
    }

    @Unique
    private static void carryon$spawnEntity(World world, EntityPlayer player, ItemStack stack)
    {
        Entity entity = ItemEntity.getEntity(stack, world);

        if (entity == null)
        {
            ItemEntity.clearEntityData(stack);
            return;
        }

        entity.setPosition(player.posX, player.posY, player.posZ);
        entity.rotationYaw   = player.rotationYaw;
        entity.rotationPitch = 0.0f;

        world.spawnEntityInWorld(entity);

        ItemEntity.clearEntityData(stack);
    }
}
