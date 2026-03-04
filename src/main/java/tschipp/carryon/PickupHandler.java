package tschipp.carryon;

import tschipp.carryon.api.CarryOnPluginLoader;

import net.minecraft.*;

/**
 * Determines which blocks and entities the player is allowed to carry.
 */
public class PickupHandler {

    public static boolean canPlayerPickUpBlock(EntityPlayer player, TileEntity tileEntity, World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        if (block == null) return false;
        int meta = world.getBlockMetadata(x, y, z);
        return isFunctionalBlock(block) || CarryOnPluginLoader.anyPluginAllowsBlock(player, block, meta);
    }

    public static boolean canPlayerPickUpEntity(EntityPlayer player, Entity entity) {
        // Allow carrying animals (all EntityAnimal subclasses)
        if (entity instanceof EntityAnimal) return true;
        // Allow carrying baby villagers only
        if (entity instanceof EntityVillager && ((EntityVillager) entity).isChild()) return true;

        return CarryOnPluginLoader.anyPluginAllowsEntity(player, entity);
    }

    public static boolean isFunctionalBlock(Block block) {

        if (block instanceof BlockChest) return true;
        if (block instanceof BlockEnderChest) return true;
        if (block instanceof BlockStrongbox) return true;
        String className = block.getClass().getSimpleName().toLowerCase();
        if (className.contains("chest") || className.contains("locker")) return true;

        if (block instanceof BlockFurnace) return true;

        if (block instanceof BlockWorkbench) return true;
        if (className.contains("workbench")) return true;

        if (block instanceof BlockAnvil) return true;

        if (block instanceof BlockHopper) return true;

        if (block instanceof BlockNote) return true;
        if (block instanceof BlockJukeBox) return true;

        if (block instanceof BlockRedstoneLight) return true;

        if (block instanceof BlockPistonBase) return true;

        if (block instanceof BlockDispenser) return true;

        if (block instanceof BlockEnchantmentTable) return true;

        if (block instanceof BlockBrewingStand) return true;

        if (block instanceof BlockBeacon) return true;

        return false;
    }
}