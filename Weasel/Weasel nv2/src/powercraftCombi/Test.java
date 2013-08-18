package powercraftCombi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class Test {

	public void test(){
		
		WeaselClass.WeaselClassMarker wcm;																
		for(ModContainer mc:Loader.instance().getModList()){
			wcm = mc.getMod().getClass().getAnnotation(WeaselClass.WeaselClassMarker.class);
			if(wcm!=null){
				for(Class c:wcm.classes()){
					WeaselNativeSourceManager.registerNewClass(c);
				}
			}
		}
	}
	public static class WeaselNativeSourceManager{
		public static HashMap<String, Class> classes = new HashMap<String, Class>();
		public static HashMap<String, Object> objects = new HashMap<String, Object>();
		
		public static void init(){
			classes.put("byte", byte.class);
			classes.put("int", int.class);
			classes.put("float", float.class);
			classes.put("double", double.class);
			classes.put("String", String.class);
		}
		
		public static void registerNewClass(Class c){
			WeaselClass wc = (WeaselClass) c.getAnnotation(WeaselClass.class);
			classes.put(wc.weaselName(), wc.getClass());
		}
		
		public static <T> Object callFunc(String obj, String func, Object... params){
			try{
				T o;
				if((o=(T) objects.get(obj))!=null){
					Class types[] = new Class[params.length];
					for(int i=0; i<params.length; i++){
						types[i] = params[i].getClass();
					}
					Method m = o.getClass().getDeclaredMethod(func, types);
					if(m!=null && !m.isAnnotationPresent(WeaselClass.Invisible.class)){
						return m.invoke(o, params);
					}
				}
				
				return null;
			}catch(Exception e){
				
			}
			return null;
		}
	}
}
