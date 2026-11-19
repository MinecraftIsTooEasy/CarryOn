package tschipp.carryon.client.compat;

import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import net.minecraft.Block;
import net.minecraft.RenderBlocks;

public class RicBlockModelRenderer {

    public static boolean render(RenderBlocks renderer, Block block, int x, int y, int z, int metadata) {
        return JsonBlockModelManager.INSTANCE.renderBlock(renderer, block, x, y, z, metadata);
    }
}
