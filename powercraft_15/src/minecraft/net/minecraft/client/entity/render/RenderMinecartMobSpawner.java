package net.minecraft.client.entity.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderMinecart;
import net.minecraft.client.renderer.tileentity.TileEntityMobSpawnerRenderer;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.item.EntityMinecart;

@SideOnly(Side.CLIENT)
public class RenderMinecartMobSpawner extends RenderMinecart
{
    protected void func_98192_a(EntityMinecartMobSpawner par1EntityMinecartMobSpawner, float par2, Block par3Block, int par4)
    {
        super.renderBlockInMinecart(par1EntityMinecartMobSpawner, par2, par3Block, par4);

        if (par3Block == Block.mobSpawner)
        {
            TileEntityMobSpawnerRenderer.func_98144_a(par1EntityMinecartMobSpawner.func_98039_d(), par1EntityMinecartMobSpawner.posX, par1EntityMinecartMobSpawner.posY, par1EntityMinecartMobSpawner.posZ, par2);
        }
    }

    /**
     * Renders the block that is inside the minecart.
     */
    protected void renderBlockInMinecart(EntityMinecart par1EntityMinecart, float par2, Block par3Block, int par4)
    {
        this.func_98192_a((EntityMinecartMobSpawner)par1EntityMinecart, par2, par3Block, par4);
    }
}
