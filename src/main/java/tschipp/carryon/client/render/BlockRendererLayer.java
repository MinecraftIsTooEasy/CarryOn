package tschipp.carryon.client.render;

import net.minecraft.AbstractClientPlayer;
import net.minecraft.Block;
import net.minecraft.BlockChest;
import net.minecraft.BlockEnderChest;
import net.minecraft.BlockStrongbox;
import net.minecraft.EntityLivingBase;
import net.minecraft.EnumDirection;
import net.minecraft.ItemStack;
import net.minecraft.MathHelper;
import net.minecraft.Minecraft;
import net.minecraft.OpenGlHelper;
import net.minecraft.RenderBlocks;
import net.minecraft.RenderHelper;
import net.minecraft.Tessellator;
import net.minecraft.TextureMap;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import tschipp.carryon.CarryOnEvents;
import tschipp.carryon.item.ItemTile;

public class BlockRendererLayer {

    private static final int RENDER_TYPE_BREWING_STAND = 25;

    public static void renderThirdPerson(AbstractClientPlayer player, float partialTicks) {
        ItemStack stack = player.getHeldItemStack();
        if (stack == null || stack.getItem() != CarryOnEvents.TILE_ITEM || !ItemTile.hasTileData(stack)) return;
        Block block = ItemTile.getBlock(stack);
        if (block == null || block.blockID == 0) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            prepareRender(player);
            GL11.glRotated(180, 1, 0, 0);
            GL11.glRotated(180, 0, 1, 0);
            GL11.glScaled(0.6, 0.6, 0.6);
            GL11.glTranslated(0, -0.75, -0.65);
            if (player.isSneaking()) GL11.glTranslated(0, -0.15, -0.15);
            renderBlock(block, getRenderMetadata(block, stack), stack, player, partialTicks);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    public static void renderFirstPerson(EntityLivingBase entity, ItemStack stack, float partialTicks) {
        if (stack == null || stack.getItem() != CarryOnEvents.TILE_ITEM || !ItemTile.hasTileData(stack)) return;
        Block block = ItemTile.getBlock(stack);
        if (block == null || block.blockID == 0) return;
        int metadata = getRenderMetadata(block, stack);

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            prepareRender(entity);
            GL11.glScaled(1.6, 1.6, 1.6);
            GL11.glTranslated(0, -0.55, -1.4);
            renderBlock(block, metadata, stack, entity, partialTicks);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static int getRenderMetadata(Block block, ItemStack stack) {
        int metadata = ItemTile.getMeta(stack);
        EnumDirection direction = block.getDirectionFacing(metadata);
        if (direction == null) return metadata;
        return switch (direction) {
            case NORTH, SOUTH, EAST, WEST -> block.getMetadataForDirectionFacing(metadata, EnumDirection.SOUTH);
            default -> metadata;
        };
    }

    private static void prepareRender(EntityLivingBase carrier) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableStandardItemLighting();
        setLightCoords(carrier);
    }

    private static void renderBlock(Block block, int metadata, ItemStack stack, EntityLivingBase carrier, float partialTicks) {
        if (CarriedBlockRenderer.render(block, metadata, stack, carrier, partialTicks)) return;
        if (isChest(block)) {
            GL11.glRotated(180, 0, 1, 0);
            renderChestWithMeta(block, metadata, 1.0F);
        } else if (block.getRenderType() == RENDER_TYPE_BREWING_STAND) {
            renderBrewingStand(metadata, carrier);
        } else {
            applyDirectionRotation(block, metadata);
            new RenderBlocks().renderBlockAsItem(block, metadata, 1.0F);
        }
    }

    public static boolean isChest(Block block) {
        return block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockStrongbox;
    }

    private static void renderBrewingStand(int metadata, EntityLivingBase carrier) {
        CarriedBlockAccess access = new CarriedBlockAccess(Block.brewingStand, metadata, null, carrier, true);
        RenderBlocks renderer = new RenderBlocks(access);
        Tessellator mesh = Tessellator.instance;
        double previousX = mesh.xOffset, previousY = mesh.yOffset, previousZ = mesh.zOffset;
        try {
            mesh.setTranslation(-access.x - 0.5, -access.y, -access.z - 0.5);
            mesh.startDrawingQuads();
            mesh.setBrightness(0xF000F0);
            try {
                renderer.renderBlockByRenderType(Block.brewingStand, access.x, access.y, access.z);
            } finally {
                mesh.draw();
            }
        } finally {
            mesh.setTranslation(previousX, previousY, previousZ);
        }
    }

    private static void renderChestWithMeta(Block block, int metadata, float brightness) {
        float rotation = switch (metadata) {
            case 2 -> 180;
            case 4 -> 90;
            case 5 -> -90;
            default -> 0;
        };
        if (rotation != 0) GL11.glRotatef(rotation, 0, 1, 0);
        new RenderBlocks().renderBlockAsItem(block, metadata, brightness);
    }

    private static void applyDirectionRotation(Block block, int metadata) {
        EnumDirection direction = block.getDirectionFacing(metadata);
        if (direction == null) return;
        double rotation = switch (direction) {
            case NORTH -> 180;
            case WEST -> 270;
            case EAST -> 90;
            default -> 0;
        };
        if (rotation != 0) GL11.glRotated(rotation, 0, 1, 0);
    }

    private static void setLightCoords(EntityLivingBase carrier) {
        int x = MathHelper.floor_double(carrier.posX);
        int y = MathHelper.floor_double(carrier.posY + carrier.getEyeHeight());
        int z = MathHelper.floor_double(carrier.posZ);
        int light = carrier.worldObj.getLightBrightnessForSkyBlocks(x, y, z, 0);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light & 0xFFFF, light >> 16);
    }
}
