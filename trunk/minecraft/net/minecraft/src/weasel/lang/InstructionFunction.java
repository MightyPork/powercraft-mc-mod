package net.minecraft.src.weasel.lang;


import java.util.ArrayList;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.weasel.InstructionList;
import net.minecraft.src.weasel.WeaselEngine;
import net.minecraft.src.weasel.exception.WeaselRuntimeException;


/**
 * Function header. Works like label for jumps, all PUSHes and variable initialization were already done in instruction list.
 * 
 * @author MightyPork
 */
public class InstructionFunction extends Instruction {
	
	/**
	 * FUNC
	 * @param functionName function name
	 * @param args array of argument variable names
	 */
	public InstructionFunction(String functionName, String ... args) {
		this.functionName = functionName;
		this.args = args;
	}
	
	/**
	 * FUNC
	 */
	public InstructionFunction() {}

	private String functionName;
	private String[] args;

	@Override
	public void execute(WeaselEngine engine, InstructionList instructionList) throws WeaselRuntimeException {}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setString("Name", functionName);
		
		NBTTagList tags = new NBTTagList();
		for (String argname : args) {
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setString("ArgName", argname);
			tags.appendTag(tag1);
		}
		tag.setTag("Args", tags);
		
		return tag;
	}

	@Override
	public InstructionFunction readFromNBT(NBTTagCompound tag) {
		functionName = tag.getString("Name");
		
		ArrayList<String> list = new ArrayList<String>();
		
		NBTTagList tags = tag.getTagList("Args");
		for (int i = 0; i < tags.tagCount(); i++) {
			NBTTagCompound tag1 = (NBTTagCompound) tags.tagAt(i);
			list.add(tag1.getString("ArgName"));
		}
		
		args = list.toArray(new String[list.size()]);
		
		return this;
	}

	/**
	 * @return name of this label
	 */
	public String getFunctionName() {
		return functionName;
	}

	/**
	 * Set names of the arguments
	 * 
	 * @param argnames argiment names array
	 * @return this
	 */
	public InstructionFunction setArgumentNames(String[] argnames) {
		args = argnames;
		return this;
	}

	/**
	 * Set label name
	 * 
	 * @param functionName function name to set
	 * @return this
	 */
	public InstructionFunction setFunctionName(String functionName) {
		this.functionName = functionName;
		return this;
	}

	/**
	 * Get name of an argiment variable with index i
	 * 
	 * @param i arg index
	 * @return the name
	 */
	public String getArgumentName(int i) {
		if (i >= 0 && i < args.length) return args[i];
		return null;
	}
	
	/**
	 * @return number of arguments.
	 */
	public int getArgumentCount() {
		return args.length;
	}

}
