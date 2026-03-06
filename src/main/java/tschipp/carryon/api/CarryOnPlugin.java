package tschipp.carryon.api;

import net.minecraft.Block;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;

/**
 * Implement this interface and register it via
 * {@link CarryOnPluginLoader#register(CarryOnPlugin)} to tell CarryOn which
 * extra blocks / entities your mod wants to allow or explicitly deny.
 *
 * <h3>Return value semantics</h3>
 * <ul>
 *   <li>{@code true}  — explicitly allow this block/entity</li>
 *   <li>{@code false} — abstain; built-in rules (TileEntity whitelist, extra whitelist) still apply</li>
 * </ul>
 * <p><strong>Important:</strong> returning {@code false} from {@link #canCarryBlock} does
 * <em>not</em> prevent a block from being carried if it passes the built-in whitelist
 * (e.g. it has a TileEntity).  To unconditionally deny a specific block, override
 * {@link #denyCarryBlock} and return {@code true} there instead.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * public class MyPlugin implements CarryOnPlugin {
 *     @Override
 *     public boolean canCarryBlock(EntityPlayer player, Block block, int meta) {
 *         return block instanceof MyWorkbench; // true = allow, false = abstain
 *     }
 *     @Override
 *     public boolean denyCarryBlock(EntityPlayer player, Block block, int meta) {
 *         return block instanceof MyLockedBlock; // true = deny
 *     }
 * }
 * }</pre>
 */
public interface CarryOnPlugin {

    /**
     * Return {@code true} to explicitly allow carrying {@code block}.
     * Return {@code false} to abstain (default).
     */
    default boolean canCarryBlock(EntityPlayer player, Block block, int meta) {
        return false;
    }

    /**
     * Return {@code true} to explicitly <em>deny</em> carrying {@code block}.
     * Deny takes priority over any allow vote. Return {@code false} to abstain (default).
     */
    default boolean denyCarryBlock(EntityPlayer player, Block block, int meta) {
        return false;
    }

    /**
     * Return {@code true} to explicitly allow carrying {@code entity}.
     * Return {@code false} to abstain (default).
     */
    default boolean canCarryEntity(EntityPlayer player, Entity entity) {
        return false;
    }

    /**
     * Return {@code true} to explicitly <em>deny</em> carrying {@code entity}.
     * Deny takes priority over any allow vote. Return {@code false} to abstain (default).
     */
    default boolean denyCarryEntity(EntityPlayer player, Entity entity) {
        return false;
    }
}