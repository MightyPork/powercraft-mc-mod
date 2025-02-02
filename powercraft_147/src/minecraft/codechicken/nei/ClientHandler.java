package codechicken.nei;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import codechicken.core.ClientUtils;
import codechicken.core.NetworkClosedException;
import codechicken.core.PacketCustom;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.world.World;
import net.minecraft.client.multiplayer.WorldClient;

public class ClientHandler implements ITickHandler
{	
	private static ClientHandler instance;
	
	private ArrayList<EntityItem> SMPmagneticItems = new ArrayList<EntityItem>();
    private World lastworld;
    private boolean firstTick = true;
	
	public void addSMPMagneticItem(int i, World world)
	{
		WorldClient cworld = (WorldClient)world;
		Entity e = cworld.getEntityByID(i);
		if(e == null || !(e instanceof EntityItem))
		{
			return;
		}
		SMPmagneticItems.add((EntityItem)e);
	}
    
	@SuppressWarnings("unchecked")
	private void updateMagnetMode(World world, EntityPlayerSP player)
	{
		if(!NEIClientConfig.getMagnetMode())return;
		
		float distancexz = 16;
		float distancey = 8;
		double maxspeedxz = 0.5;
		double maxspeedy = 0.5;
		double speedxz = 0.05;
		double speedy = 0.07;
		
		List<EntityItem> items;
		if(world.isRemote)
		{
			items = SMPmagneticItems;
		}
		else
		{
			items = world.getEntitiesWithinAABB(EntityItem.class, player.boundingBox.expand(distancexz, distancey, distancexz));
		}
		for(Iterator<EntityItem> iterator = items.iterator(); iterator.hasNext();)
		{
			EntityItem item = iterator.next();
			
			if(item.delayBeforeCanPickup > 0)continue;
			if(item.isDead && world.isRemote)iterator.remove();
			
			if(!NEIClientUtils.canItemFitInInventory(player, item.func_92014_d()))continue;
			
			double dx = player.posX - item.posX;
			double dy = player.posY + player.getEyeHeight() - item.posY;
			double dz = player.posZ - item.posZ;
			double absxz = Math.sqrt(dx*dx+dz*dz);
			double absy = Math.abs(dy);
			if(absxz > distancexz)
			{
				continue;
			}
			
			if(absxz > 1)
			{
				dx /= absxz;
				dz /= absxz;
			}
			
			if(absy > 1)
			{
				dy /= absy;
			}

			double vx = item.motionX + speedxz*dx;
			double vy = item.motionY + speedy*dy;
			double vz = item.motionZ + speedxz*dz;
			
			double absvxz = Math.sqrt(vx*vx+vz*vz);
			double absvy = Math.abs(vy);
			
			double rationspeedxz = absvxz / maxspeedxz;
			if(rationspeedxz > 1)
			{
				vx/=rationspeedxz;
				vz/=rationspeedxz;
			}
			
			double rationspeedy = absvy / maxspeedy;
			if(rationspeedy > 1)
			{
				vy/=rationspeedy;
			}
			
			if(absvxz < 0.2 && absxz < 0.2 && world.isRemote)
			{
				item.setDead();
			}
			
			item.setVelocity(vx, vy, vz);
		}
	}

	public static void load() 
	{
        try
        {
	        TMIUninstaller.deleteTMIUninstaller();
	        
	        if(TMIUninstaller.TMIInstalled())
	        {
	        	TMIUninstaller.runTMIUninstaller();
	        	
	        	NEIClientUtils.mc().shutdownMinecraftApplet();
	        }
        }
        catch(Exception e)
        {
        	throw new RuntimeException(e);
        }
        
        instance = new ClientHandler();        

		PacketCustom.assignHandler(ClientPacketHandler.channel, 0, 255, new ClientPacketHandler());	
        TickRegistry.registerTickHandler(instance, Side.CLIENT);
        LanguageRegistry.instance().addStringLocalization("entity.SnowMan.name", "Snow Golem");
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
		Minecraft mc = ClientUtils.mc();
		
		if(type.contains(TickType.CLIENT) && mc.theWorld != null)
		{			
			if(mc.theWorld != lastworld)
				onWorldChange(mc.theWorld);
			
			NEIController.updateUnlimitedItems(mc.thePlayer.inventory);
			NEIController.processCreativeCycling(mc.thePlayer.inventory);
			
	        updateMagnetMode(mc.theWorld, mc.thePlayer);
		}
		
		if(type.contains(TickType.CLIENTGUI))
		{
			GuiScreen gui = (GuiScreen) tickData[0];
			if(gui instanceof GuiMainMenu)
	    	{
	    		if(firstTick)
	    		{
	    			firstTick = false;
	    			onMainMenuInit();
	    		}
	    		
	    		if(lastworld != null)
	    			lastworld = null;
	    	}
		}
	}

    private void onWorldChange(World world) 
    {
		NEIClientConfig.setHasSMPCounterPart(false);
		SMPmagneticItems.clear();
    	
		NEIClientConfig.setInternalEnabled(false);		
		
		if(ClientUtils.isLocal())//wait for server to initiate
			return;
		
		try
		{
			NEIClientConfig.loadWorld(ClientUtils.getServerIP().replace(':', '~'));
		}
		catch(NetworkClosedException e)
		{
			return;
		}
	}

	private void onMainMenuInit()
	{
    	if(NEIClientConfig.getBooleanSetting("ID dump.dump on load"))
    		NEIClientUtils.dumpIDs();
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
		if(type.contains(TickType.RENDER))
		{
			HUDAugmenter.renderOverlay();
		}
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.CLIENT, TickType.CLIENTGUI, TickType.RENDER);
	}

	@Override
	public String getLabel() 
	{
		return "NEI Client";
	}

	public static ClientHandler instance() 
	{
		return instance;
	}

	public void setWorld(World world)
	{
		lastworld = world;
	}
}
