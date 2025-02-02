package powercraft.api.items;


import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import powercraft.api.PC_CreativeTab;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Module;
import powercraft.api.inventory.PC_InventoryUtils;
import powercraft.api.registries.PC_ModuleRegistry;
import powercraft.api.registries.PC_Registry;
import powercraft.api.registries.PC_TextureRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SuppressWarnings("unused")
public abstract class PC_Item extends Item {

	public final PC_Module module;
	public final PC_ItemInfo itemInfo;


	public PC_Item(int id) {

		super(id);
		itemInfo = getClass().getAnnotation(PC_ItemInfo.class);
		if(itemInfo==null)
			PC_Logger.severe("No ItemInfo for item %s", getClass().getName());
		module = PC_ModuleRegistry.getActiveModule();
		if(module==null)
			PC_Logger.severe("No Module for item %s", getClass().getName());
		
		PC_Registry.registerItemRenderer(this);
		
	}


	public PC_Module getModule() {

		return module;
	}


	public PC_ItemInfo getItemInfo() {

		return itemInfo;
	}


	public abstract void registerRecipes();


	public abstract void loadIcons();


	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegistry) {

		PC_TextureRegistry.registerIcons(this, iconRegistry);
	}


	public int getBurnTime(ItemStack fuel) {

		return 0;
	}

	@Override
	public CreativeTabs[] getCreativeTabs() {
		if(getCreativeTab()==null)
			return new CreativeTabs[]{};
		return new CreativeTabs[]{ getCreativeTab(), PC_CreativeTab.getCrativeTab()};
	}
	
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int i, boolean currentItem) {
		onTick(itemStack, world, PC_InventoryUtils.getInventoryFromEntity(entity), i);
	}


	public void onTick(ItemStack itemStack, World world, IInventory inventory, int i) {
		
	}

	@SideOnly(Side.CLIENT)
	public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean shouldUseRenderHelper(ItemStack item, ItemRenderType type, ItemRendererHelper helper) {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public void renderItem(ItemStack item, ItemRenderType type, Object... data) {
		
	}
	
	
}
