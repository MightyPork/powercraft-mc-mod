package powercraft.api.registry;

import net.minecraft.util.StringTranslate;
import powercraft.api.utils.PC_Struct3;
import powercraft.launcher.loader.PC_ModuleObject;

public final class PC_LangRegistry {
	
	public static void registerLanguageForLang(PC_ModuleObject module, String lang, LangEntry... translations) {
		PC_RegistryServer.getInstance().registerLanguage(module, lang, translations);
	}
	
	public static void registerLanguage(PC_ModuleObject module, LangEntry... translations) {
		registerLanguageForLang(module, "en_US", translations);
	}
	
	public static void loadLanguage(PC_ModuleObject module) {
		PC_RegistryServer.getInstance().loadLanguage(module);
	}
	
	public static void saveLanguage(PC_ModuleObject module) {
		PC_RegistryServer.getInstance().saveLanguage(module);
	}
	
	public static String tr(String identifier) {
		if (identifier == null)
			return "";
		return StringTranslate.getInstance().translateKey(identifier).trim();
	}
	
	public static String tr(String identifier, String... replacements) {
		if (identifier == null)
			return "";
		return StringTranslate.getInstance().translateKeyFormat(identifier, (Object[]) replacements);
	}
	
	public static class LangEntry extends PC_Struct3<String, String, String[]> {
		public LangEntry(String key, String trans, String... desc) {
			super(key, trans, desc);
		}
	}

	public static void reloadLanguage() {
		for(PC_ModuleObject module:PC_ModuleRegistry.getModuleList()){
			loadLanguage(module);
		}
	}

	public static String getUsedLang() {
		return PC_RegistryServer.getInstance().getUsedLang();
	}
	
}
