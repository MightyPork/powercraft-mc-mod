package powercraft.launcher.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import powercraft.launcher.PC_Version;
import powercraft.launcher.loader.PC_ModLoader;

public class PC_AnnotationVisitor extends AnnotationVisitor {
	
	private String name;
	private PC_Version version;
	private String dependencies = "";
	private PC_ModLoader modLoader = PC_ModLoader.ALL;
	
	public PC_AnnotationVisitor() {
		super(Opcodes.ASM4);
	}
	
	@Override
	public void visit(String name, Object value) {
		if (name.equals("name")) {
			this.name = (String) value;
		} else if (name.equals("version")) {
			version = new PC_Version((String) value);
		} else if (name.equals("dependencies")) {
			dependencies = (String) value;
		}
	}
	
	@Override
	public void visitEnum(String name, String desc, String value) {
		if (name.equals("modLoader")) {
			if (desc.equals("Lpowercraft/launcher/loader/PC_ModLoader;")) {
				modLoader = PC_ModLoader.valueOf(value);
			}
		}
	}
	
	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		return null;
	}
	
	@Override
	public AnnotationVisitor visitArray(String name) {
		
		return null;
	}
	
	@Override
	public void visitEnd() {
		
	}
	
	public String getModuleName() {
		return name;
	}
	
	public String getDependencies() {
		return dependencies;
	}
	
	public PC_Version getVersion() {
		return version;
	}
	
	public PC_ModLoader getModLoader() {
		return modLoader;
	}
	
}
