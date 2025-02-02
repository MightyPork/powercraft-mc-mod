package net.minecraft.world.gen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class ComponentStrongholdStairs extends ComponentStronghold
{
    private final boolean field_75024_a;
    private final EnumDoor doorType;

    public ComponentStrongholdStairs(int par1, Random par2Random, int par3, int par4)
    {
        super(par1);
        this.field_75024_a = true;
        this.coordBaseMode = par2Random.nextInt(4);
        this.doorType = EnumDoor.OPENING;

        switch (this.coordBaseMode)
        {
            case 0:
            case 2:
                this.boundingBox = new StructureBoundingBox(par3, 64, par4, par3 + 5 - 1, 74, par4 + 5 - 1);
                break;
            default:
                this.boundingBox = new StructureBoundingBox(par3, 64, par4, par3 + 5 - 1, 74, par4 + 5 - 1);
        }
    }

    public ComponentStrongholdStairs(int par1, Random par2Random, StructureBoundingBox par3StructureBoundingBox, int par4)
    {
        super(par1);
        this.field_75024_a = false;
        this.coordBaseMode = par4;
        this.doorType = this.getRandomDoor(par2Random);
        this.boundingBox = par3StructureBoundingBox;
    }

    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    public void buildComponent(StructureComponent par1StructureComponent, List par2List, Random par3Random)
    {
        if (this.field_75024_a)
        {
            StructureStrongholdPieces.setComponentType(ComponentStrongholdCrossing.class);
        }

        this.getNextComponentNormal((ComponentStrongholdStairs2)par1StructureComponent, par2List, par3Random, 1, 1);
    }

    /**
     * performs some checks, then gives out a fresh Stairs component
     */
    public static ComponentStrongholdStairs getStrongholdStairsComponent(List par0List, Random par1Random, int par2, int par3, int par4, int par5, int par6)
    {
        StructureBoundingBox var7 = StructureBoundingBox.getComponentToAddBoundingBox(par2, par3, par4, -1, -7, 0, 5, 11, 5, par5);
        return canStrongholdGoDeeper(var7) && StructureComponent.findIntersecting(par0List, var7) == null ? new ComponentStrongholdStairs(par6, par1Random, var7, par5) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts(World par1World, Random par2Random, StructureBoundingBox par3StructureBoundingBox)
    {
        if (this.isLiquidInStructureBoundingBox(par1World, par3StructureBoundingBox))
        {
            return false;
        }
        else
        {
            this.fillWithRandomizedBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 4, 10, 4, true, par2Random, StructureStrongholdPieces.getStrongholdStones());
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, this.doorType, 1, 7, 0);
            this.placeDoor(par1World, par2Random, par3StructureBoundingBox, EnumDoor.OPENING, 1, 1, 4);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 2, 6, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 1, 5, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 0, 1, 6, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 1, 5, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 1, 4, 3, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 0, 1, 5, 3, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 2, 4, 3, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3, 3, 3, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 0, 3, 4, 3, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3, 3, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 3, 2, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 0, 3, 3, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 2, 2, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 1, 1, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 0, 1, 2, 1, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneBrick.blockID, 0, 1, 1, 2, par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(par1World, Block.stoneSingleSlab.blockID, 0, 1, 1, 3, par3StructureBoundingBox);
            return true;
        }
    }
}
