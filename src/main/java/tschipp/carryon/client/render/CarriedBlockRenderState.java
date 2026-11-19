package tschipp.carryon.client.render;

import net.minecraft.Block;
import net.minecraft.ITileEntityProvider;
import net.minecraft.NBTTagCompound;
import net.minecraft.TileEntity;

final class CarriedBlockRenderState {

    private final Block block;
    private final int metadata;
    private final NBTTagCompound data;
    private TileEntity tile;
    private boolean loaded;

    CarriedBlockRenderState(Block block, int metadata, NBTTagCompound data) {
        this.block = block;
        this.metadata = metadata;
        this.data = (NBTTagCompound) data.copy();
    }

    boolean matches(Block block, int metadata, NBTTagCompound data) {
        return this.block == block && this.metadata == metadata && this.data.equals(data);
    }

    TileEntity getTile(CarriedBlockAccess access) {
        if (!loaded) {
            loaded = true;
            tile = block instanceof ITileEntityProvider provider ? provider.createNewTileEntity(access.getWorld()) : null;
            if (tile != null && !data.hasNoTags()) {
                access.prepareTile(tile);
                NBTTagCompound renderData = (NBTTagCompound) data.copy();
                renderData.setInteger("x", access.x);
                renderData.setInteger("y", access.y);
                renderData.setInteger("z", access.z);
                tile.readFromNBT(renderData);
            }
        }
        return tile;
    }
}
