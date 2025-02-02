package powercraft.light;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraftforge.common.Configuration;
import powercraft.core.PC_Block;
import powercraft.core.PC_Item;
import powercraft.core.PC_ItemStack;
import powercraft.core.PC_Module;
import powercraft.core.PC_Utils;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "PowerCraft-Light", name = "PowerCraft-Light", version = "3.5.0AlphaD", dependencies = "required-after:PowerCraft-Core")
@NetworkMod(clientSideRequired = true, serverSideRequired = true)
public class mod_PowerCraftLight extends PC_Module
{
    @SidedProxy(clientSide = "powercraft.light.PCli_ClientProxy", serverSide = "powercraft.light.PCli_CommonProxy")
    public static PCli_CommonProxy proxy;
    public static PC_Block light;
    public static PC_Block lightningConductor;
    public static PC_Block laser;
    public static PC_Block mirrow;
    public static PC_Block prism;
    public static PC_Block laserSensor;
    public static PC_Item laserComposition;

    public static mod_PowerCraftLight getInstance()
    {
        return (mod_PowerCraftLight)PC_Module.getModule("PowerCraft-Light");
    }

    @PreInit
    public void preInit(FMLPreInitializationEvent event)
    {
        preInit(event, proxy);
    }

    @Init
    public void init(FMLInitializationEvent event)
    {
        init();
    }

    @PostInit
    public void postInit(FMLPostInitializationEvent event)
    {
        postInit();
    }

    @Override
    protected void initProperties(Configuration config)
    {
    }

    @Override
    protected List<String> loadTextureFiles(List<String> textures)
    {
        textures.add(getTerrainFile());
        textures.add(getTextureDirectory() + "block_light.png");
        textures.add(getTextureDirectory() + "mirror.png");
        return textures;
    }

    @Override
    protected void initLanguage()
    {
        PC_Utils.registerLanguage(this,
                "pc.gui.light.isHuge", "is Huge",
                "pc.gui.light.isStable", "is Stable",
                "pc.damage.laser", "laser killed %s"
                                 );
    }

    @Override
    protected void initBlocks()
    {
        light = PC_Utils.register(this, 489, PCli_BlockLight.class, PCli_TileEntityLight.class);
        lightningConductor = PC_Utils.register(this, 491, PCli_BlockLightningConductor.class, PCli_ItemBlockLightningConductor.class, PCli_TileEntityLightningConductor.class);
        laser = PC_Utils.register(this, 492, PCli_BlockLaser.class, PCli_TileEntityLaser.class);
        mirrow = PC_Utils.register(this, 493, PCli_BlockMirrow.class, PCli_TileEntityMirrow.class);
        prism = PC_Utils.register(this, 494, PCli_BlockPrism.class, PCli_TileEntityPrism.class);
        laserSensor = PC_Utils.register(this, 495, PCli_BlockLaserSensor.class, PCli_TileEntityLaserSensor.class);
    }

    @Override
    protected void initItems()
    {
    	laserComposition = PC_Utils.register(this, 496, PCli_ItemLaserComposition.class);
    }

    @Override
    protected void initRecipes()
    {
        PC_Utils.addShapelessRecipe(
                new PC_ItemStack(light),
                new Object[]
                {
                    Item.redstone, Block.glowStone
                });
        PC_Utils.addRecipe(
                new PC_ItemStack(lightningConductor),
                new Object[] { " X ", " X ", "XXX",
                        'X', Block.blockSteel
                             });
        
        PC_Utils.addRecipe(new PC_ItemStack(laser, 1),
				new Object[] { " WD", " S ", "SSS",
					'S', Block.stone, 'W', new PC_ItemStack(Block.planks, 1, -1), 'D', Item.diamond });
        
        PC_Utils.addRecipe(new PC_ItemStack(laser, 1),
				new Object[] { " WD", " S ", "SSS",
					'S', Block.cobblestone, 'W', new PC_ItemStack(Block.planks, 1, -1), 'D', Item.diamond });
        
        PC_Utils.addRecipe(new PC_ItemStack(laserSensor, 1),
        		new Object[] { "L", "R", 
        	'L', new PC_ItemStack(laser, 1), 'R', Item.redstone });
        
        PC_Utils.addRecipe(new PC_ItemStack(mirrow, 2, 0),
				new Object[] { "GI", " I",	'G', Block.thinGlass, 'I', Item.ingotIron });
        
        PC_Utils.addRecipe(new PC_ItemStack(prism, 1, 1),
				new Object[] { "GG", "GG", 'G', Block.glass });
        
        PC_Utils.addRecipe(new PC_ItemStack(laserComposition),
                new Object[] { "XXX", "XPX", "XXX",
                        'X', Block.glass, 'P', new PC_ItemStack(PC_Utils.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 0)
                             });
        
        PC_Utils.addRecipe(new PC_ItemStack(laserComposition),
                new Object[] { "XXX", "XPX", "XXX",
                        'X', Block.glass, 'P', new PC_ItemStack(PC_Utils.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 1)
                             });
        
        PC_Utils.addRecipe(new PC_ItemStack(laserComposition),
                new Object[] { "XXX", "XPX", "XXX",
                        'X', Block.glass, 'P', new PC_ItemStack(PC_Utils.getPCBlockByName("PCco_BlockPowerCrystal"), 1, 2)
                             });
        
        for(int i=2; i<10; i++){
        	Object[] o = new Object[i];
        	for(int j=0; j<i; j++){
        		o[j] = new PC_ItemStack(laserComposition);
        	}
        	 PC_Utils.addShapelessRecipe( new PC_ItemStack(laserComposition), o);
        }
        
    }

    @Override
    protected List<String> addSplashes(List<String> list)
    {
        return null;
    }
}
