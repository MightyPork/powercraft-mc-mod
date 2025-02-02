package powercraftCombi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import weasel.interpreter.WeaselClass;
import weasel.interpreter.WeaselInterpreter;
import weasel.interpreter.WeaselMethodExecutor;
import weasel.interpreter.WeaselNativeException;
import weasel.interpreter.WeaselNativeMethod;
import weasel.interpreter.WeaselObject;
import weasel.interpreter.WeaselThread;

public class WeaselNativeMethodAccessor implements WeaselNativeMethod {

	private final Method method;
	private final String name;
	
	public WeaselNativeMethodAccessor(String nameSpace, String weaselName, Method method){
		this.method = method;
		Class<?>[] paramTypes = method.getParameterTypes();
		String nameBuilder = nameSpace;
		nameBuilder += "." + weaselName + "(";
		boolean gaveThis = false;
		for(int i=0; i<paramTypes.length; i++){
			if(paramTypes[i]==WeaselInterpreter.class){
				Set<String> s = new HashSet<String>(new ArrayList<String>());
			}else if(paramTypes[i]==WeaselThread.class){
				
			}else if(paramTypes[i]==WeaselMethodExecutor.class){
				
			}else if(paramTypes[i]==WeaselObject.class){
				if(gaveThis){
					nameBuilder += "OObject;";
				}else{
					gaveThis = true;
				}
			}else if(paramTypes[i].isPrimitive()){
				nameBuilder += WeaselClass.mapClassNames(paramTypes[i].getName());
			}else{
				throw new WeaselNativeException("Illegal parameter %s in method invokation %s", i, method);
			}
		}
		nameBuilder += ")";
		Class<?> returnType = method.getReturnType();
		if(returnType==WeaselObject.class){
			nameBuilder += "OObject;";
		}else if(returnType.isPrimitive()){
			nameBuilder += WeaselClass.mapClassNames(returnType.getName());
		}else{
			throw new WeaselNativeException("Illegal return parameter in method invokation %s", method);
		}
		name = nameBuilder;
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public Object invoke(WeaselInterpreter interpreter, WeaselThread thread, WeaselMethodExecutor executor, String functionName, WeaselObject _this, Object[] param) {
		Class<?>[] paramTypes = method.getParameterTypes();
		Object[] oParam = new Object[paramTypes.length];
		boolean gaveThis = false;
		int j=0;
		for(int i=0; i<paramTypes.length; i++){
			if(paramTypes[i]==WeaselInterpreter.class){
				oParam[i] = interpreter;
			}else if(paramTypes[i]==WeaselThread.class){
				oParam[i] = thread;
			}else if(paramTypes[i]==WeaselMethodExecutor.class){
				oParam[i] = executor;
			}else if(paramTypes[i]==WeaselObject.class){
				if(gaveThis){
					oParam[i] = param[j++];
				}else{
					oParam[i] = _this;
					gaveThis = true;
				}
			}else if(paramTypes[i].isPrimitive()){
				oParam[i] = param[j++];
			}else{
				throw new WeaselNativeException("Illegal parameter %s in method invokation %s", i, method);
			}
		}
		try {
			return method.invoke(null, oParam);
		} catch (Throwable e) {
			throw new WeaselNativeException(e, "Error on native method invokation %s", method);
		} 
	}
	
	@Override
	public boolean equals(Object paramObject) {
		if(!(paramObject instanceof WeaselNativeMethodAccessor)) return false;
		WeaselNativeMethodAccessor wnma = (WeaselNativeMethodAccessor)paramObject;
		if(name==wnma.name) return true;
		return false;
	}

}
