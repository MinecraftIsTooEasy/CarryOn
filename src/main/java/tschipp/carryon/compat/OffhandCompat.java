package tschipp.carryon.compat;

import com.m.offhand.api.compat.IOffhandActionFilter;
import com.m.offhand.api.compat.OffhandCompatRegistry;

import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;

import net.xiaoyu233.fml.FishModLoader;

import tschipp.carryon.CarryOnEvents;
import tschipp.carryon.items.ItemEntity;
import tschipp.carryon.items.ItemTile;

public class OffhandCompat {

    public static final boolean HAS_OFFHAND = FishModLoader.hasMod("offhand");

    public static void register() {
        if (!HAS_OFFHAND) return;
        Impl.apply();
    }

    public static boolean isCarrying(ItemStack stack) {

        if (stack == null) return false;

        if (stack.getItem() == CarryOnEvents.TILE_ITEM)   return ItemTile.hasTileData(stack);

        if (stack.getItem() == CarryOnEvents.ENTITY_ITEM) return ItemEntity.hasEntityData(stack);

        return false;
    }

    private static final class Impl {

        static void apply() {
            OffhandCompatRegistry.setActionFilter(
                    new IOffhandActionFilter() {
                        @Override
                        public boolean cancelOffhandRightClick(
                                EntityPlayer player,
                                ItemStack mainhand,
                                ItemStack offhand) {
                            return isCarrying(mainhand);
                        }

                        @Override
                        public boolean cancelOffhandSwapKey(
                                EntityPlayer player,
                                ItemStack mainhand,
                                ItemStack offhand) {
                            return isCarrying(mainhand);
                        }

                        @Override
                        public boolean cancelOffhandSwapPacket(
                                EntityPlayer player,
                                ItemStack mainhand,
                                ItemStack offhand) {
                            return isCarrying(mainhand);
                        }
                    }
            );
        }
    }
}