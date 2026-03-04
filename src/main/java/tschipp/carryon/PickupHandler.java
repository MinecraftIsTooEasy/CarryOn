package tschipp.carryon;

import tschipp.carryon.api.CarryOnPluginLoader;

import net.minecraft.*;

/**
 * Determines which blocks and entities the player is allowed to carry.
 *
 * <h3>Block decision order (highest priority first)</h3>
 * <ol>
 *   <li>Plugin explicitly denies ({@code Boolean.FALSE}) → not carryable</li>
 *   <li>Built-in blacklist (spawners, end portal frames, …) → not carryable</li>
 *   <li>Plugin explicitly allows ({@code Boolean.TRUE}) → carryable</li>
 *   <li>Block has a {@link TileEntity} → carryable (whitelist default)</li>
 *   <li>Everything else → not carryable</li>
 * </ol>
 */
public class PickupHandler {

    public static boolean canPlayerPickUpBlock(EntityPlayer player, TileEntity tileEntity, World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        if (block == null) return false;
        int meta = world.getBlockMetadata(x, y, z);

        // 1. Plugin explicitly denies → reject immediately.
        Boolean pluginResult = CarryOnPluginLoader.queryBlock(player, block, meta);
        if (pluginResult == Boolean.FALSE) return false;

        // 2. Built-in blacklist.
        if (isBlackListedBlocks(block)) return false;

        // 3. Plugin explicitly allows.
        if (pluginResult == Boolean.TRUE) return true;

        // 4. Has a TileEntity → allow by default (whitelist core rule).
        return tileEntity != null;
    }

    public static boolean canPlayerPickUpEntity(EntityPlayer player, Entity entity) {
        Boolean pluginResult = CarryOnPluginLoader.queryEntity(player, entity);
        if (pluginResult == Boolean.FALSE) return false;
        if (pluginResult == Boolean.TRUE)  return true;

        // Built-in: all animals and child villagers are carryable.
        if (entity instanceof EntityAnimal) return true;

        if (entity instanceof EntityVillager && ((EntityVillager) entity).isChild()) return true;

        return false;
    }

    /**
     * Built-in blacklist — blocks that must never be carried regardless of
     * TileEntity presence or plugin votes.
     */
    public static boolean isBlackListedBlocks(Block block)
    {
        if (block instanceof BlockMobSpawner)      return true;

        if (block instanceof BlockEndPortal)       return true;
        if (block instanceof BlockEndPortalFrame)  return true;

        if (block instanceof BlockStrongbox)       return true;

        if (block instanceof BlockCommandBlock)    return true;

        return false;
    }
}