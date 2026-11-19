package tschipp.carryon.client.render;

import net.minecraft.Block;
import net.minecraft.EntityLivingBase;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import net.minecraft.NBTTagCompound;
import net.minecraft.RenderBlocks;
import net.minecraft.Tessellator;
import net.minecraft.TextureMap;
import net.minecraft.TileEntity;
import net.minecraft.TileEntityRenderer;
import net.xiaoyu233.fml.FishModLoader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import tschipp.carryon.client.compat.RicBlockModelRenderer;
import tschipp.carryon.item.ItemTile;

import java.util.Map;
import java.util.WeakHashMap;

public class CarriedBlockRenderer {

    private static final boolean HAS_RIC = FishModLoader.hasMod("rusted_iron_core");
    private static final Map<ItemStack, CarriedBlockRenderState> STATES = new WeakHashMap<>();

    public static void clearRenderCache() {
        STATES.clear();
    }

    public static boolean render(Block block, int metadata, ItemStack stack, EntityLivingBase carrier, float partialTicks) {
        if (!HAS_RIC) return false;
        CarriedBlockRenderState state = getState(block, metadata, stack);
        CarriedBlockAccess access = new CarriedBlockAccess(block, metadata, state::getTile, carrier, false);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            if (!renderModel(block, metadata, access)) return false;
            GL11.glColor4f(1, 1, 1, 1);
            TileEntity tile = access.getBlockTileEntity(access.x, access.y, access.z);
            if (tile != null) {
                TileEntityRenderer.instance.renderTileEntityAt(tile, 0, 0, 0, partialTicks);
            }
            return true;
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static CarriedBlockRenderState getState(Block block, int metadata, ItemStack stack) {
        NBTTagCompound data = ItemTile.getTileData(stack);
        CarriedBlockRenderState state = STATES.get(stack);
        if (state == null || !state.matches(block, metadata, data)) {
            state = new CarriedBlockRenderState(block, metadata, data);
            STATES.put(stack, state);
        }
        return state;
    }

    private static boolean renderModel(Block block, int metadata, CarriedBlockAccess access) {
        Tessellator mesh = Tessellator.instance;
        double previousX = mesh.xOffset, previousY = mesh.yOffset, previousZ = mesh.zOffset;
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        try {
            GL11.glDisable(GL11.GL_LIGHTING);
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            mesh.setTranslation(-access.x, -access.y, -access.z);
            mesh.startDrawingQuads();
            try {
                return RicBlockModelRenderer.render(new RenderBlocks(access), block, access.x, access.y, access.z, metadata);
            } finally {
                mesh.draw();
            }
        } finally {
            mesh.setTranslation(previousX, previousY, previousZ);
            GL11.glPopAttrib();
        }
    }
}
