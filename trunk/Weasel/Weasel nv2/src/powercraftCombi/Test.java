package powercraftCombi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import weasel.interpreter.WeaselInterpreter;
import weasel.interpreter.WeaselNativeException;

public class Test {
	
	public void loader() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		ClassLoader cl = Test.class.getClassLoader();
		Field f = ClassLoader.class.getDeclaredField("classes");
		f.setAccessible(true);
		@SuppressWarnings("unchecked")
		Vector<Class<?>> classes = (Vector<Class<?>>)f.get(cl);
		for(Class<?> c:classes){
			WeaselClassList wc = c.getAnnotation(WeaselClassList.class);
			if(wc!=null){
				WeaselNativeSourceManager.registerMethodsInClass(c);
			}
		}
		WeaselNativeSourceManager.finished=true;
	}
	
	public static class WeaselNativeSourceManager{
		private static List<WeaselNativeMethodAccessor> methods = new ArrayList<WeaselNativeMethodAccessor>();
		public static boolean finished=false;
		
		public static void registerMethodsInClass(Class<?> c){
			Named named;
			for(Method m:c.getMethods()){
				if((named=m.getAnnotation(Named.class))!=null){
					if(((m.getModifiers()&Modifier.STATIC)==Modifier.STATIC)){
						for(String namespace:named.nameSpaces()){
							for(String weaselName:named.weaselNames()){
								methods.add(new WeaselNativeMethodAccessor(namespace, weaselName, m));
							}
						}
					}else{
						throw new WeaselNativeException("Only static Methods can be loaded");
					}
				}
			}
		}
		
		public static boolean registerNativeMethodsInWeasel(WeaselInterpreter wi){
			WeaselNativeMethodAccessor tmp;
			while(methods.size()>0){
				tmp = methods.remove(0);
				wi.registerNativeMethod(tmp.getName(), tmp);
			}
			return finished;
		}
	}
}
