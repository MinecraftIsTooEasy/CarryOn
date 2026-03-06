//package tschipp.carryon;
//
//import com.google.common.eventbus.Subscribe;
//import net.minecraft.Item;
//import net.minecraft.Material;
//import net.xiaoyu233.fml.reload.event.ItemRegistryEvent;
//import tschipp.carryon.item.ItemEntity;
//import tschipp.carryon.item.ItemTile;
//
//public class CarryOnEvents {
//
//    public static Item TILE_ITEM;
//    public static Item ENTITY_ITEM;
//
//    private static final String NAMESPACE = "carryon";
//
//    @Subscribe
//    public void onItemRegister(ItemRegistryEvent event) {
//        TILE_ITEM = new ItemTile(3488, Material.mithril);
//        event.register(NAMESPACE, NAMESPACE + ":carryon_tile", "carryon.tile_item", TILE_ITEM);
//
//        ENTITY_ITEM = new ItemEntity(3489, Material.mithril);
//        event.register(NAMESPACE, NAMESPACE + ":carryon_entity", "carryon.entity_item", ENTITY_ITEM);
//    }
//
//}