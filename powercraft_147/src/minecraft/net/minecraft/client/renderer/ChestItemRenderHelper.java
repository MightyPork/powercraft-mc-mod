package net.minecraft.client.renderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;

@SideOnly(Side.CLIENT)
public class ChestItemRenderHelper
{
    /** The static instance of ChestItemRenderHelper. */
    public static ChestItemRenderHelper instance = new ChestItemRenderHelper();

    /** Instance of Chest's Tile Entity. */
    private TileEntityChest theChest = new TileEntityChest();

    /** Instance of Ender Chest's Tile Entity. */
    private TileEntityEnderChest theEnderChest = new TileEntityEnderChest();

    /**
     * Renders a chest at 0,0,0 - used for item rendering
     */
    public void renderChest(Block par1Block, int par2, float par3)
    {
        if (par1Block.blockID == Block.enderChest.blockID)
        {
            TileEntityRenderer.instance.renderTileEntityAt(this.theEnderChest, 0.0D, 0.0D, 0.0D, 0.0F);
        }
        else
        {
            TileEntityRenderer.instance.renderTileEntityAt(this.theChest, 0.0D, 0.0D, 0.0D, 0.0F);
        }
    }
}
