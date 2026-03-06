package tschipp.carryon.client.render;

import net.minecraft.*;
import org.lwjgl.opengl.GL11;
import tschipp.carryon.CarryOnItems;
import tschipp.carryon.item.ItemEntity;

public class EntityRendererLayer {

    public static void renderThirdPerson(beu player, float partialTicks)
    {
        ItemStack stack = player.getHeldItemStack();

        if (stack == null || stack.getItem() != CarryOnItems.ENTITY_ITEM) return;

        if (!ItemEntity.hasEntityData(stack)) return;

        Minecraft mc = Minecraft.w();

        if (mc == null) return;

        Entity renderEntity = ItemEntity.getEntity(stack, player.worldObj);

        if (renderEntity == null) return;

        renderEntity.setPosition(
                player.prevPosX + (player.posX - player.prevPosX) * partialTicks,
                player.prevPosY + (player.posY - player.prevPosY) * partialTicks,
                player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks);

        renderEntity.rotationYaw      = 0f; renderEntity.prevRotationYaw   = 0f;
        renderEntity.rotationPitch    = 0f; renderEntity.prevRotationPitch = 0f;

        if (renderEntity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) renderEntity;
            living.rotationYawHead = 0f;
            living.prevRotationYawHead = 0f;
        }

        float height     = renderEntity.height;
        float width      = renderEntity.width;
        float multiplier = height * width;

        GL11.glPushMatrix();
        GL11.glScaled(1, -1, 1);
        GL11.glScaled((10 - multiplier) * 0.08, (10 - multiplier) * 0.08, (10 - multiplier) * 0.08);
        GL11.glRotated(180, 0, 1, 0);
        GL11.glTranslated(0.0, -1.2, (Math.max(width - 0.1, 0.7)) + 0.1);
        GL11.glColor3f(1f, 1f, 1f);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        if (player.isSneaking()) GL11.glTranslated(0, -0.1, 0);

        bgl.a.a(renderEntity, 0, 0, 0, 0, partialTicks);

        GL11.glScaled(1, 1, 1);
        GL11.glPopMatrix();
    }

    public static void renderFirstPerson(EntityLiving player, ItemStack stack, float partialTicks)
    {
        if (stack == null || stack.getItem() != CarryOnItems.ENTITY_ITEM) return;

        if (!ItemEntity.hasEntityData(stack)) return;

        Minecraft mc = Minecraft.w();

        if (mc == null) return;

        Entity renderEntity = ItemEntity.getEntity(stack, player.worldObj);

        if (renderEntity == null) return;

        renderEntity.setPosition(
                player.prevPosX + (player.posX - player.prevPosX) * partialTicks,
                player.prevPosY + (player.posY - player.prevPosY) * partialTicks,
                player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks);

        renderEntity.rotationYaw      = 0f; renderEntity.prevRotationYaw   = 0f;
        renderEntity.rotationPitch    = 0f; renderEntity.prevRotationPitch = 0f;

        if (renderEntity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) renderEntity;
            living.rotationYawHead = 0f;
            living.prevRotationYawHead = 0f;
        }

        float height = renderEntity.height;
        float width  = renderEntity.width;

        att.b();
        GL11.glPushMatrix();
        GL11.glScaled(.55, .55, .55);
        GL11.glRotated(180, 0, 1, 0);
        GL11.glTranslated(0.0, -height - .1, width + 0.6);
        GL11.glEnable(GL11.GL_ALPHA_TEST);

        setLightCoords(player);
        bgl.a.a(renderEntity, 0, 0, 0, 0, partialTicks);

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glScaled(1, 1, 1);
        GL11.glPopMatrix();
        att.a();
    }

    private static void setLightCoords(EntityLiving player)
    {
        Minecraft mc = Minecraft.w();

        if (mc == null || mc.f == null) return;

        int x = (int) player.posX;
        int y = (int) (player.posY + player.getEyeHeight());
        int z = (int) player.posZ;
        int lightValue = mc.f.h(x, y, z, 0);

        bma.a(bma.b,
                (float)(lightValue & 0xFFFF), (float)(lightValue >> 16));
    }
}