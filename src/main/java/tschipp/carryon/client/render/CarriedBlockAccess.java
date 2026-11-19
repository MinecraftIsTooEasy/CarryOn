package tschipp.carryon.client.render;

import net.minecraft.BiomeGenBase;
import net.minecraft.Block;
import net.minecraft.EntityLivingBase;
import net.minecraft.IBlockAccess;
import net.minecraft.Material;
import net.minecraft.MathHelper;
import net.minecraft.TileEntity;
import net.minecraft.Vec3Pool;
import net.minecraft.World;

import java.util.function.Function;

public class CarriedBlockAccess implements IBlockAccess {

    private final Block block;
    private final int metadata;
    private final Function<CarriedBlockAccess, TileEntity> tileFactory;
    private final World world;
    private final boolean fullBright;
    final int x;
    final int y;
    final int z;

    public CarriedBlockAccess(Block block, int metadata, Function<CarriedBlockAccess, TileEntity> tileFactory, EntityLivingBase carrier, boolean fullBright) {
        this.block = block;
        this.metadata = metadata;
        this.tileFactory = tileFactory;
        this.world = carrier.worldObj;
        this.fullBright = fullBright;
        this.x = MathHelper.floor_double(carrier.posX);
        this.y = MathHelper.floor_double(carrier.posY + carrier.getEyeHeight());
        this.z = MathHelper.floor_double(carrier.posZ);
    }

    void prepareTile(TileEntity tile) {
        tile.setWorldObj(world);
        tile.setBlock(block);
        tile.blockMetadata = metadata;
        tile.xCoord = x;
        tile.yCoord = y;
        tile.zCoord = z;
    }

    private boolean contains(int x, int y, int z) {
        return x == this.x && y == this.y && z == this.z;
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return contains(x, y, z) ? block.blockID : 0;
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return contains(x, y, z) ? block : null;
    }

    @Override
    public TileEntity getBlockTileEntity(int x, int y, int z) {
        if (!contains(x, y, z) || tileFactory == null) return null;
        TileEntity tile = tileFactory.apply(this);
        if (tile != null) prepareTile(tile);
        return tile;
    }

    @Override
    public int getBlockMetadata(int x, int y, int z) {
        return contains(x, y, z) ? metadata : 0;
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int x, int y, int z, int minimum) {
        return fullBright ? 0xF000F0 : world.getLightBrightnessForSkyBlocks(x, y, z, minimum);
    }

    @Override
    public float getBrightness(int x, int y, int z, int minimum) {
        return fullBright ? 1.0F : world.getBrightness(x, y, z, minimum);
    }

    @Override
    public float getLightBrightness(int x, int y, int z) {
        return fullBright ? 1.0F : world.getLightBrightness(x, y, z);
    }

    @Override
    public Material getBlockMaterial(int x, int y, int z) {
        return contains(x, y, z) ? block.blockMaterial : Material.air;
    }

    @Override
    public Material getBlockMaterial(int id) {
        return world.getBlockMaterial(id);
    }

    @Override
    public boolean isBlockStandardFormOpaqueCube(int x, int y, int z) {
        return contains(x, y, z) && block.isOpaqueStandardFormCube(metadata);
    }

    @Override
    public boolean isBlockNormalCube(int x, int y, int z) {
        return contains(x, y, z) && Block.isNormalCube(block);
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return !contains(x, y, z);
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z) {
        return world.getBiomeGenForCoords(x, z);
    }

    @Override
    public int getHeight() {
        return world.getHeight();
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    @Override
    public boolean isBlockTopFlatAndSolid(int x, int y, int z) {
        return contains(x, y, z) && block.isSolidStandardFormCube(metadata);
    }

    @Override
    public Vec3Pool getWorldVec3Pool() {
        return world.getWorldVec3Pool();
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int side) {
        return 0;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public boolean isBlockSolid(int x, int y, int z) {
        return contains(x, y, z) && block.isSolid(metadata);
    }
}
