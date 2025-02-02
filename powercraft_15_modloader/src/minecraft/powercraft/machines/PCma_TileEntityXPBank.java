package powercraft.machines;

import java.util.List;
import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityXPOrb;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.tileentity.PC_TileEntity;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;

public class PCma_TileEntityXPBank extends PC_TileEntity
{
    private Random rand = new Random();
    @PC_ClientServerSync(clientChangeAble=false)
    private int xp = 0;
    
    public int getXP()
    {
        return xp;
    }

    public void setXP(int xp)
    {
    	if(this.xp != xp){
    		this.xp = xp;
    		notifyChanges("xp");
    	}
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public void updateEntity()
    {
        List<EntityXPOrb> hitList = worldObj.getEntitiesWithinAABB(EntityXPOrb.class,
                AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1).expand(0.5D, 0.5D, 0.5D));

        int xp = getXP();
        int xp2 = xp;
        
        if (hitList.size() > 0)
        {
            Loop:

            for (EntityXPOrb orb : hitList)
            {
                if (orb.isDead)
                {
                    continue Loop;
                }

                int oldxp = xp;
                worldObj.playSoundAtEntity(orb, "random.orb", 0.1F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
                xp += orb.getXpValue();
                orb.setDead();

                if (oldxp == 0 && xp > 0)
                {
                    notifyChange();
                }
                else
                {
                    notifyResize();
                }
            }
        }

        if(xp!=xp2){
        	setXP(xp);
        }
        
        return;
    }

    public void withdrawXP(EntityPlayer player)
    {
    	int xp = getXP();
        int xp2 = xp;
    	
        if (xp == 0)
        {
            return;
        }

        worldObj.playSoundAtEntity(player, "random.orb", 0.3F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));

        while (xp > 0)
        {
            int addedXP = Math.min(xp, player.xpBarCap());
            player.addExperience(addedXP);
            xp -= addedXP;
        }

        if (xp < 0)
        {
            xp = 0;
        }

        if(xp!=xp2){
        	setXP(xp);
        }
        
        notifyChange();
    }

    private int addExperience(EntityPlayer player, int num)
    {
        if (num > 0)
        {
            int plus = Integer.MAX_VALUE - player.experienceTotal;

            if (num > plus)
            {
                num = plus;
            }

            int n = num;
            player.experienceTotal += num;

            while (n > 0){

                int needToFinish = (int)(player.xpBarCap() - player.xpBarCap() * player.experience);

                if (n >= needToFinish)
                {
                    player.experience = 0.0f;
                    player.experienceLevel++;
                    n -= needToFinish;
                }
                else
                {
                    player.experience += (float)n / (float)player.xpBarCap();
                    n = 0;
                }
            }
        }
        else
        {
            if (-num > player.experienceTotal)
            {
                num = -player.experienceTotal;
            }

            int n = -num;
            player.experienceTotal += num;

            while (n > 0)
            {
                int needToFinish = (int)(player.xpBarCap() * player.experience);

                if (n >= needToFinish)
                {
                    player.experience = 1.0F;
                    player.experienceLevel--;
                    n -= needToFinish;
                }
                else
                {
                    player.experience -= (float)n / (float)player.xpBarCap();
                    n = 0;
                }
            }

            if (player.experienceLevel < 0)
            {
                player.experienceLevel = 0;
                player.experience = 0;
            }
        }

        return num;
    }

    public void givePlayerXP(EntityPlayer player, int num)
    {
    	int xp = getXP();
        int xp2 = xp;
    	
        if (num > xp)
        {
            num = xp;
        }

        if (worldObj.isRemote)
        {
        	call("givePlayerXP", new PC_Struct2<String, Integer>(player.username, num));
        }

        num = addExperience(player, num);
        xp -= num;

        if (xp < 0)
        {
            xp = 0;
        }

        if(xp!=xp2){
        	setXP(xp);
        }
        
        notifyChange();
    }

    public void givePlayerLevel(EntityPlayer player, int num) {
    	int xp = getXP();
        int xp2 = xp;
    	if (worldObj.isRemote)
        {
    		call("givePlayerLevel", new PC_Struct2<String, Integer>(player.username, num));
        }
    	if (num > 0)
        {
            while (num > 0)
            {
            	num--;

                int needToFinish = (int)(player.xpBarCap() - player.xpBarCap() * player.experience);
                
                if (xp >= needToFinish)
                {
                	player.experience = 0.0f;
                	player.experienceLevel++;
                	player.experienceTotal += needToFinish;
                    xp -= needToFinish;
                }
                else
                {
                    player.experience += (float)xp / (float)player.xpBarCap();
                    player.experienceTotal += xp;
                    xp = 0;
                }
            }
            
        }
        else
        {

            num = -num;
            if(player.experience<=0.0f && player.experienceLevel>0){
            	player.experience = 1.0f;
            	player.experienceLevel--;
            }
            while (num > 0)
            {
            	num--;
                int needToFinish = (int)(player.xpBarCap() * player.experience);

                if (player.experienceLevel>0)
                {
                    player.experience = 1.0f;
                    player.experienceLevel--;
                    player.experienceTotal -= needToFinish;
                    xp += needToFinish;
                }
                else
                {
                    player.experience = 0.0f;
                    player.experienceLevel = 0;
                    player.experienceTotal = 0;
                    xp += needToFinish;
                }
            }
            if(player.experience>=1.0f){
            	player.experience = 0.0f;
            	player.experienceLevel++;
            }
        }
    	
    	 if(xp!=xp2){
         	setXP(xp);
         }
    	
	}
    
    private void notifyChange()
    {
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType().blockID);
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, getBlockType().blockID);
        notifyResize();
    }

    private void notifyResize()
    {
        worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

	@Override
	protected void onCall(String key, Object[] value) {
		if(key.equals("givePlayerXP")){
			PC_Struct2<String, Integer> d = (PC_Struct2<String, Integer>)value[0];
			if (!worldObj.isRemote){
                givePlayerXP(PC_Utils.mcs().getConfigurationManager().getPlayerForUsername(d.a), d.b);
            }
		}else if(key.equals("givePlayerLevel")){
			PC_Struct2<String, Integer> d = (PC_Struct2<String, Integer>)value[0];
			if (!worldObj.isRemote){
				givePlayerLevel(PC_Utils.mcs().getConfigurationManager().getPlayerForUsername(d.a), d.b);
            }
		}
	}
}
