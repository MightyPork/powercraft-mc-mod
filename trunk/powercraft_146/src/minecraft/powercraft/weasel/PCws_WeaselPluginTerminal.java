package powercraft.weasel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.management.PC_PacketHandler;
import powercraft.management.PC_Utils.Gres;
import weasel.Calc;
import weasel.WeaselEngine;
import weasel.WeaselFunctionManager;
import weasel.exception.WeaselRuntimeException;
import weasel.obj.WeaselBoolean;
import weasel.obj.WeaselNull;
import weasel.obj.WeaselObject;
import weasel.obj.WeaselString;

public class PCws_WeaselPluginTerminal extends PCws_WeaselPlugin {

	/** Displayed text. "\n" is a newline. */
	public String text = "";
	/**  */
	private List<String> userInput = new ArrayList<String>();
	
	private long lastTime = -1;
	
	@Override
	public WeaselFunctionManager makePluginProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected PCws_WeaselPlugin readPluginFromNBT(NBTTagCompound tag) {
		userInput.clear();
		text = tag.getString("text");
		int num = tag.getInteger("count");
		for(int i=0; i<num; i++){
			userInput.add(tag.getString("value["+i+"]"));
		}
		return this;
	}

	@Override
	protected NBTTagCompound writePluginToNBT(NBTTagCompound tag) {
		tag.setString("text", text);
		int num = userInput.size();
		tag.setInteger("count", num);
		for(int i=0; i<num; i++){
			tag.setString("value["+i+"]", userInput.get(i));
		}
		return tag;
	}
	
	private int countIn(String str, char c) {
		int counter = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == c) {
				counter++;
			}
		}
		return counter;
	}

	/**
	 * Add text to the terminal, if too long remove oldest.
	 * @param text
	 */
	public void addText(String text) {
		if(System.currentTimeMillis() - lastTime > 100){
			if(getTE()!=null){
				PC_PacketHandler.setTileEntity(getTE(), "msg", "play", null);
				lastTime = System.currentTimeMillis();
			}
		}
		
		
		this.text += text.replace("\\n", "\n");
		if (countIn(this.text, '\n') > 60) {
			while (countIn(this.text, '\n') > 60) {
				this.text = this.text.substring(this.text.indexOf('\n') + 1);
			}
		}
		setData("text", this.text);
		needsSave();
	}

	@Override
	public void update() {}

	@Override
	public void getClientMsg(String msg, Object obj) {
		if(msg.equalsIgnoreCase("input")){
			String input = (String)obj;
			input = input.trim();
			if (input.length() > 0) {
				userInput.add(input);
				addText("> " + input + "\n");
				if(getNetwork()!=null){
					if(!getNetwork().callFunctionOnEngine("termIn."+getName(), new WeaselString(input))){
						getNetwork().callFunctionOnEngine("termIn", new WeaselString(getName()), new WeaselString(input));
					}
				}
			}
			if (userInput.size() > 16) {
				userInput.remove(0);
			}
			needsSave();
		}else{
			super.getClientMsg(msg, obj);
		}
	}
	
	@Override
	public void syncWithClient(PCws_TileEntityWeasel tileEntityWeasel) {
		tileEntityWeasel.setData("text", text);
	}

	@Override
	public void refreshInport(){}
	
	@Override
	protected void openPluginGui(EntityPlayer player) {
		Gres.openGres("WeaselTerminal", player, getPos().x, getPos().y, getPos().z);
	}

	@Override
	public void restart() {
		text = "";
		userInput.clear();
	}

}
