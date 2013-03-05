package powercraft.light;

import java.util.List;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import powercraft.launcher.PC_ClientModule;
import powercraft.launcher.PC_ClientModule.PC_InitLanguage;
import powercraft.launcher.PC_ClientModule.PC_LoadTextureFiles;
import powercraft.launcher.PC_Module.PC_RegisterGuis;
import powercraft.management.PC_IClientModule;
import powercraft.management.PC_Struct2;
import powercraft.management.registry.PC_LangRegistry.LangEntry;
import powercraft.management.registry.PC_TextureRegistry;

@PC_ClientModule
public class PCli_AppClient extends PCli_App {

	@PC_LoadTextureFiles
    public List<String> loadTextureFiles(List<String> textures)
    {
        textures.add("tiles.png");
        textures.add("block_light.png");
        textures.add("mirror.png");
        return textures;
    }
	
	@PC_InitLanguage
	public List<LangEntry> initLanguage(List<LangEntry> lang) {
		lang.add(new LangEntry("pc.gui.light.isHuge", "is Huge"));
		lang.add(new LangEntry("pc.gui.light.isStable", "is Stable"));
		lang.add(new LangEntry("pc.damage.laser", "%s wanted to know his reflexivity"));
		return lang;
	}

	@PC_RegisterGuis
	public List<PC_Struct2<String, Class>> registerGuis(List<PC_Struct2<String, Class>> guis) {
		guis.add(new PC_Struct2<String, Class>("Light", PCli_GuiLight.class));
		return guis;
	}
	
}
