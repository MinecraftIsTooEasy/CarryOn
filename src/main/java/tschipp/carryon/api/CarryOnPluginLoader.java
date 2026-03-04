package tschipp.carryon.api;

import net.minecraft.Block;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;
import net.xiaoyu233.fml.FishModLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central registry for {@link CarryOnPlugin} instances.
 *
 * <p>External mods should call {@link #register(CarryOnPlugin)} from their
 * {@code onInitialize} (or equivalent FML entry-point).</p>
 *
 * <p>CarryOn itself queries this registry inside {@link tschipp.carryon.PickupHandler}
 * after its own built-in checks, so plugins can extend the allow-list without
 * touching core mod code.</p>
 *
 * <h3>FML entrypoint integration</h3>
 * If you want your plugin to be loaded automatically without explicit
 * {@code register()} calls, add an entry to your {@code fabric.mod.json}:
 * <pre>{@code
 * "entrypoints": {
 *   "carryon": [ "com.example.MyCarryOnPlugin" ]
 * }
 * }</pre>
 * CarryOn scans for these entrypoints during {@code onInitialize}.
 */
public final class CarryOnPluginLoader {

    private static final List<CarryOnPlugin> PLUGINS = new ArrayList<>();

    private CarryOnPluginLoader() {}

    // ── Registration ──────────────────────────────────────────────────────────

    /** Register a plugin instance. Duplicate instances are silently ignored. */
    public static void register(CarryOnPlugin plugin) {
        if (plugin != null && !PLUGINS.contains(plugin)) {
            PLUGINS.add(plugin);
        }
    }

    /** Remove all registered plugins (useful for world-reload scenarios). */
    public static void clearAll() {
        PLUGINS.clear();
    }

    /** Returns an unmodifiable view of the currently registered plugins. */
    public static List<CarryOnPlugin> getPlugins() {
        return Collections.unmodifiableList(PLUGINS);
    }

    // ── Query helpers (called by PickupHandler) ───────────────────────────────

    /**
     * Queries all registered plugins for their verdict on {@code block}.
     * <ul>
     *   <li>Any plugin's {@link CarryOnPlugin#denyCarryBlock} returns {@code true} → {@code FALSE} (deny wins)</li>
     *   <li>Any plugin's {@link CarryOnPlugin#canCarryBlock} returns {@code true}  → {@code TRUE}</li>
     *   <li>All plugins abstain → {@code null}</li>
     * </ul>
     */
    public static Boolean queryBlock(EntityPlayer player, Block block, int meta) {
        boolean anyAllow = false;
        for (CarryOnPlugin plugin : PLUGINS) {
            try {
                if (plugin.denyCarryBlock(player, block, meta)) return Boolean.FALSE;
                if (plugin.canCarryBlock(player, block, meta))  anyAllow = true;
            } catch (Exception e) {
                // don't let a broken plugin crash the game
            }
        }
        return anyAllow ? Boolean.TRUE : null;
    }

    /**
     * Queries all registered plugins for their verdict on {@code entity}.
     * Same semantics as {@link #queryBlock}.
     */
    public static Boolean queryEntity(EntityPlayer player, Entity entity) {
        boolean anyAllow = false;
        for (CarryOnPlugin plugin : PLUGINS) {
            try {
                if (plugin.denyCarryEntity(player, entity)) return Boolean.FALSE;
                if (plugin.canCarryEntity(player, entity))  anyAllow = true;
            } catch (Exception e) {
                // don't let a broken plugin crash the game
            }
        }
        return anyAllow ? Boolean.TRUE : null;
    }

    /** @deprecated Use {@link #queryBlock} instead. */
    @Deprecated
    public static boolean anyPluginAllowsBlock(EntityPlayer player, Block block, int meta) {
        return queryBlock(player, block, meta) == Boolean.TRUE;
    }

    /** @deprecated Use {@link #queryEntity} instead. */
    @Deprecated
    public static boolean anyPluginAllowsEntity(EntityPlayer player, Entity entity) {
        return queryEntity(player, entity) == Boolean.TRUE;
    }

    // ── FML entrypoint scan (called once from CarryOn.onInitialize) ───────────

    /**
     * Scans for mods that declared a {@code "carryon"} entrypoint in their
     * {@code fabric.mod.json} and registers each one automatically.
     */
    public static void loadFromEntrypoints() {
        try {
            FishModLoader.getEntrypointContainers("carryon", CarryOnPlugin.class)
                    .forEach(container -> register(container.getEntrypoint()));
        } catch (Exception e) {
            // no mods provided the entrypoint – that is fine
        }
    }
}