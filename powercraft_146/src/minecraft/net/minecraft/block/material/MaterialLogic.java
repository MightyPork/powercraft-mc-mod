package net.minecraft.block.material;

public class MaterialLogic extends Material
{
    public MaterialLogic(MapColor par1MapColor)
    {
        super(par1MapColor);
        this.func_85158_p();
    }

    public boolean isSolid()
    {
        return false;
    }

    /**
     * Will prevent grass from growing on dirt underneath and kill any grass below it if it returns true
     */
    public boolean getCanBlockGrass()
    {
        return false;
    }

    /**
     * Returns if this material is considered solid or not
     */
    public boolean blocksMovement()
    {
        return false;
    }
}
