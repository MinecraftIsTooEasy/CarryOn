package tschipp.carryon.api;

import net.minecraft.Block;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

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


    /**
     * Scans for CarryOn plugins and registers them automatically.
     *
     * <p>Discovery strategy (in order of preference):
     * <ol>
     *   <li>Automatic discovery via Java's {@link java.util.ServiceLoader} for the
     *       service interface {@code tschipp.carryon.api.CarryOnPlugin}. To enable
     *       this, a plugin JAR must include the resource
     *       {@code META-INF/services/tschipp.carryon.api.CarryOnPlugin} listing the
     *       implementing class(es).</li>
     *   <li>Explicit registration: a plugin may register itself by calling
     *       {@link #register(CarryOnPlugin)} during its initialization. This is the
     *       most reliable approach when mods are loaded in isolated classloaders.</li>
     * </ol>
     * </p>
     *
     * <p>The loader is tolerant: failure to load or instantiate a single plugin will
     * not prevent other plugins from being discovered and registered. Note that
     * ServiceLoader-based discovery depends on classloader visibility; if your
     * modloader isolates mod classloaders automatic discovery may not find plugins,
     * in which case plugins should call {@code register(...)} manually.</p>
     *
     * <p>See the CarryOn documentation for examples of the {@code META-INF/services}
     * file layout and for recommended initialization patterns.</p>
     */
    public static void loadFromEntrypoints() {
        try {
            ServiceLoader<CarryOnPlugin> loader =
                    ServiceLoader.load(CarryOnPlugin.class, CarryOnPluginLoader.class.getClassLoader());
            for (CarryOnPlugin plugin : loader) {
                try {
                    register(plugin);
                } catch (Throwable t) {
                    // optional: log failed plugin registration
                }
            }
        } catch (Throwable t) {
            // no plugins or service loading failed — that's fine
        }
    }
}