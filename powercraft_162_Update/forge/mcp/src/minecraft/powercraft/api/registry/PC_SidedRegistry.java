package powercraft.api.registry;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import powercraft.api.block.PC_IBlock;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.item.PC_Item;
import cpw.mods.fml.common.registry.GameRegistry;

class PC_SidedRegistry {
	
	protected PC_SidedRegistry(){}

	protected void registerLanguage(String key, String value){}
	
	protected void registerIcons(PC_IBlock block, IconRegister iconRegistry) {}

	protected void registerIcons(PC_Item item, IconRegister iconRegistry) {}

	protected Icon registerIcon(String icon, String objectName) {
		return null;
	}

	protected void registerTileEntity(Class<? extends PC_TileEntity> tileEntity) {
		GameRegistry.registerTileEntity(tileEntity, tileEntity.getName());
	}

	protected void bindTexture(ResourceLocation texture) {}
	
}
