package tschipp.carryon.api;

import net.minecraft.Block;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;

/**
 * Implement this interface and register it via
 * {@link CarryOnPluginLoader#register(CarryOnPlugin)} (called from your mod's
 * {@code onInitialize}) to tell CarryOn which extra blocks / entities your mod
 * wants to make carry-able.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // in fabric.mod.json entrypoints:
 * //   "carryon": [ "com.example.MyCarryOnPlugin" ]
 *
 * public class MyCarryOnPlugin implements CarryOnPlugin {
 *     \@Override
 *     public boolean canCarryBlock(EntityPlayer player, Block block, int meta) {
 *         return block instanceof MyCustomWorkbench;
 *     }
 * }
 * }</pre>
 */
public interface CarryOnPlugin {

    /**
     * Return {@code true} to allow the player to carry {@code block} with the
     * given {@code meta}.  Return {@code false} to deny, or {@code null} /
     * leave the default (which returns {@code false}) to abstain so that other
     * plugins or the built-in rules can decide.
     *
     * @param player the player attempting the carry
     * @param block  the block being targeted
     * @param meta   the block metadata at that position
     * @return {@code true} = allow, {@code false} = abstain (not deny)
     */
    default boolean canCarryBlock(EntityPlayer player, Block block, int meta) {
        return false;
    }

    /**
     * Return {@code true} to allow the player to carry {@code entity}.
     * Return {@code false} to abstain so that other plugins or the built-in
     * rules can decide.
     *
     * @param player the player attempting the carry
     * @param entity the entity being targeted
     * @return {@code true} = allow, {@code false} = abstain
     */
    default boolean canCarryEntity(EntityPlayer player, Entity entity) {
        return false;
    }
}
