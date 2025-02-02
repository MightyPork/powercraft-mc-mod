package powercraft.api.registries;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import powercraft.api.blocks.PC_Block;
import powercraft.api.items.PC_Item;
import powercraft.api.security.PC_Security;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PC_TextureRegistry {

	@SideOnly(Side.CLIENT)
	protected static IconRegister iconRegistry;
	private static String objectName;
	
	@SideOnly(Side.CLIENT)
	public static void registerIcons(PC_Block block, IconRegister iconRegistry) {

		if(PC_Security.allowedCaller("PC_TextureRegistry.registerIcons(PC_Block block, IconRegister iconRegistry)", PC_Block.class)){
			PC_ModuleRegistry.setActiveModule(block.getModule());
			PC_TextureRegistry.iconRegistry = iconRegistry;
			objectName = block.getBlockInfo().blockid();
			block.loadIcons();
			PC_TextureRegistry.iconRegistry = null;
			objectName = null;
			PC_ModuleRegistry.releaseActiveModule();
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerIcons(PC_Item item, IconRegister iconRegistry) {

		if(PC_Security.allowedCaller("PC_TextureRegistry.registerIcons(PC_Item item, IconRegister iconRegistry)", PC_Item.class)){
			PC_ModuleRegistry.setActiveModule(item.getModule());
			PC_TextureRegistry.iconRegistry = iconRegistry;
			objectName = item.getItemInfo().itemid();
			item.loadIcons();
			PC_TextureRegistry.iconRegistry = null;
			objectName = null;
			PC_ModuleRegistry.releaseActiveModule();
		}
	}


	public static Icon registerIcon(String icon) {

		return registerIcon(icon, objectName);
	}


	public static Icon registerIcon(String icon, String objectName) {

		return PC_Registry.sidedRegistry.registerIcon(icon, objectName);
	}

	public static void bindTexture(ResourceLocation texture) {
		PC_Registry.sidedRegistry.bindTexture(texture);
	}
	
}
