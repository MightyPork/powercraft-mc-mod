package powercraft.core;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import powercraft.api.block.PC_ItemBlock;
import powercraft.api.registry.PC_LangRegistry.LangEntry;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PCco_ItemBlockBlockSaver extends PC_ItemBlock {

	public PCco_ItemBlockBlockSaver(int id) {
		super(id);
	}
	
	@Override
	public boolean showInCraftingTool() {
		return false;
	}

	@Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int face, float par8, float par9, float par10)
    {
		int i = world.getBlockId(x, y, z);

		if (i == Block.snow.blockID) {
			face = 1;
		} else if (i != Block.vine.blockID && i != Block.tallGrass.blockID && i != Block.deadBush.blockID) {
			if (face == 0) {
				y--;
			}

			if (face == 1) {
				y++;
			}

			if (face == 2) {
				z--;
			}

			if (face == 3) {
				z++;
			}

			if (face == 4) {
				x--;
			}

			if (face == 5) {
				x++;
			}
		}

		if (itemstack.stackSize == 0) {
			return false;
		}

		if (!entityplayer.canPlayerEdit(x, y, z, face, itemstack)) {
			return false;
		}

		int placedID = getBlockID(itemstack);
		int placedMeta = getBlockMetadata(itemstack);
		if (Block.blocksList[placedID] == null) {
			entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			return true;
		}

		ItemStack setItem = new ItemStack(placedID, 0, placedMeta);
		
		if (y == 255 && Block.blocksList[placedID].blockMaterial.isSolid()) {
			return false;
		}

		PC_VecI pos = new PC_VecI(x, y, z);

		if (world.canPlaceEntityOnSide(placedID, x, y, z, false, face, entityplayer, setItem)) {
			Block block = Block.blocksList[placedID];

			if (PC_Utils.setBID(world, pos, placedID, placedMeta)) {
				if (PC_Utils.getBID(world, pos) == placedID) {
					/** @todo onBlockPlacedBy*/
					Block.blocksList[placedID].onBlockPlacedBy(world, x, y, z, entityplayer, setItem);

					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, block.stepSound.getStepSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F,
							block.stepSound.getPitch() * 0.8F);

					if (itemstack.hasTagCompound()) {
						NBTTagCompound tag = itemstack.getTagCompound();

						TileEntity tec = PC_Utils.getTE(world, pos);
						if (tec != null) {
							int cx = tec.xCoord;
							int cy = tec.yCoord;
							int cz = tec.zCoord;

							tec.readFromNBT(tag);
							tec.xCoord = cx;
							tec.yCoord = cy;
							tec.zCoord = cz;
							itemstack.setTagCompound(null);
							world.setBlockTileEntity(cx, cy, cz, tec);
						}
					}

					Block.blocksList[placedID].onBlockPlacedBy(world, x, y, z, entityplayer, setItem);

				}


				entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, null);
			}

			return true;
		} else {
			return false;
		}
    }

	private int getBlockMetadata(ItemStack itemstack) {
		if (!itemstack.hasTagCompound()) {
			return 0;
		} else {
			return itemstack.getTagCompound().getInteger("BlockMeta");
		}
	}


	private int getBlockID(ItemStack itemstack) {
		if (!itemstack.hasTagCompound()) {
			return 0;
		} else {
			return itemstack.getItemDamage();
		}
	}

	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		return par1ItemStack.hasTagCompound();
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		Item i = Item.itemsList[itemStack.getItemDamage()];
		ItemStack is = new ItemStack(i);
		list.add(i.getItemDisplayName(is));
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int slot, boolean isCurrentItem) {
		if(entity instanceof EntityLiving){
			EntityLiving living = (EntityLiving)entity;
			living.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1));
			living.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 100, 1));
			living.addPotionEffect(new PotionEffect(Potion.hunger.id, 100, 1));
			living.addPotionEffect(new PotionEffect(Potion.weakness.id, 100, 1));
			living.addPotionEffect(new PotionEffect(Potion.jump.id, 100, -2));
		}
	}
	
	
	
}
