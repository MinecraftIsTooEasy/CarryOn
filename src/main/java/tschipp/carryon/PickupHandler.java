package tschipp.carryon;

import net.minecraft.*;

/**
 * Determines which blocks and entities the player is allowed to carry.
 */
public class PickupHandler {

    public static boolean canPlayerPickUpBlock(EntityPlayer player, TileEntity te, World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        if (block == null) return false;
        return isFunctionalBlock(block);
    }

    public static boolean canPlayerPickUpEntity(EntityPlayer player, Entity entity) {
        // Allow carrying animals (all EntityAnimal subclasses)
        if (entity instanceof EntityAnimal) return true;
        // Allow carrying baby villagers only
        if (entity instanceof EntityVillager && ((EntityVillager) entity).isChild()) return true;
        return false;
    }

    public static boolean isFunctionalBlock(Block block) {

        if (block instanceof BlockChest) return true;

        if (block instanceof BlockEnderChest) return true;

        if (block instanceof BlockStrongbox) return true;

        String className = block.getClass().getSimpleName().toLowerCase();
        if (className.contains("chest") || className.contains("locker")) return true;

        if (block instanceof BlockFurnace) return true;

        if (block instanceof BlockWorkbench) return true;

        if (block instanceof BlockAnvil) return true;

        if (block instanceof BlockHopper) return true;

        if (block instanceof BlockDispenser) return true;

        if (block instanceof BlockEnchantmentTable) return true;

        if (block instanceof BlockBrewingStand) return true;

        if (block instanceof BlockBeacon) return true;

        if (block instanceof BlockJukeBox) return true;

        return false;
    }
}