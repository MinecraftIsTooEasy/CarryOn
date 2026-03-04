package tschipp.carryon.api;

import net.minecraft.Block;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;

/**
 * Implement this interface and register it via
 * {@link CarryOnPluginLoader#register(CarryOnPlugin)} to tell CarryOn which
 * extra blocks / entities your mod wants to allow or explicitly deny.
 *
 * <h3>Three-valued semantics</h3>
 * <ul>
 *   <li>{@code Boolean.TRUE}  — explicitly allow (overrides the whitelist default)</li>
 *   <li>{@code Boolean.FALSE} — explicitly deny  (highest priority, overrides all allows)</li>
 *   <li>{@code null}          — abstain; let other plugins or built-in rules decide</li>
 * </ul>
 *
 * <p>The default implementations return {@code null} (abstain), so existing
 * plugins that only override one method are unaffected.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // in fabric.mod.json entrypoints:
 * //   "carryon": [ "com.example.MyCarryOnPlugin" ]
 *
 * public class MyCarryOnPlugin implements CarryOnPlugin {
 *     @Override
 *     public Boolean canCarryBlock(EntityPlayer player, Block block, int meta) {
 *         if (block instanceof MySpecialBlock) return Boolean.TRUE;   // allow
 *         if (block instanceof MyLockedBlock)  return Boolean.FALSE;  // deny
 *         return null; // abstain
 *     }
 * }
 * }</pre>
 */
public interface CarryOnPlugin {

    /**
     * Called to determine whether the player may carry {@code block}.
     *
     * @param player the player attempting the carry
     * @param block  the block being targeted
     * @param meta   block metadata at that position
     * @return {@code TRUE} to allow, {@code FALSE} to deny, {@code null} to abstain
     */
    default Boolean canCarryBlock(EntityPlayer player, Block block, int meta) {
        return null;
    }

    /**
     * Called to determine whether the player may carry {@code entity}.
     *
     * @param player the player attempting the carry
     * @param entity the entity being targeted
     * @return {@code TRUE} to allow, {@code FALSE} to deny, {@code null} to abstain
     */
    default Boolean canCarryEntity(EntityPlayer player, Entity entity) {
        return null;
    }
}