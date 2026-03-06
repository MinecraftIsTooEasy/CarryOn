package tschipp.carryon;

import net.minecraft.*;
import tschipp.carryon.item.ItemEntity;
import tschipp.carryon.item.ItemTile;

public class CarryOnItems {

    public static ItemTile TILE_ITEM;
    public static ItemEntity ENTITY_ITEM;

    private static boolean initialized = false;

    public static void register() {
        if (initialized) return;
        initialized = true;

        TILE_ITEM = new ItemTile(12358, Material.mithril);
        ENTITY_ITEM = new ItemEntity(12359, Material.mithril);

//        TILE_ITEM.setUnlocalizedName("elytra");
//        ENTITY_ITEM.setUnlocalizedName("elytra_template");
    }
}