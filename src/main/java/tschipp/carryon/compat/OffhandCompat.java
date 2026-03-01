package tschipp.carryon.compat;

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
            com.m.offhand.api.compat.OffhandCompatRegistry.setInteractionPolicy(
                new com.m.offhand.api.compat.IOffhandInteractionPolicy() {
                    @Override
                    public boolean canUseOffhandForBlockInteraction(
                            net.minecraft.EntityPlayer player,
                            net.minecraft.ItemStack mainHand,
                            net.minecraft.ItemStack offHand,
                            net.minecraft.RaycastCollision target) {
                        if (isCarrying(mainHand) || isCarrying(offHand)) return false;
                        return true;
                    }
                }
            );
        }
    }
}