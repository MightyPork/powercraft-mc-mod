package powercraft.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.management.PC_IDataHandler;
import powercraft.management.PC_IMSG;
import powercraft.management.PC_Utils;
import powercraft.management.PC_Utils.SaveHandler;

public class PCnt_RadioManager implements PC_IDataHandler, PC_IMSG {

	public static final String default_radio_channel = "default";

	private static HashMap<String, Integer> channels = new HashMap<String, Integer>();
	private static HashMap<String, Integer> remoteChannels = new HashMap<String, Integer>();
	private static boolean needSave=false;
	
	public static void transmitterOn(String channel) {
		needSave = true;
		int num=1;
		if(channels.containsKey(channel)){
			num += channels.get(channel);
		}
		channels.put(channel, num);
	}

	public static void transmitterOff(String channel) {
		needSave = true;
		if(channels.containsKey(channel)){
			int num = channels.get(channel)-1;
			if(num<=0){
				channels.remove(channel);
			}else{
				channels.put(channel, num);
			}
		}
	}

	public static void remoteOn(String channel) {
		int num=1;
		if(remoteChannels.containsKey(channel)){
			num += remoteChannels.get(channel);
		}
		remoteChannels.put(channel, num);
	}

	public static void remoteOff(String channel) {
		if(remoteChannels.containsKey(channel)){
			int num = remoteChannels.get(channel)-1;
			if(num<=0){
				remoteChannels.remove(channel);
			}else{
				remoteChannels.put(channel, num);
			}
		}
	}
	
	public static boolean getChannelState(String channel) {
		return channels.containsKey(channel) || remoteChannels.containsKey(channel);
	}

	@Override
	public void load(NBTTagCompound nbtTag) {
		channels.clear();
		int num = nbtTag.getInteger("count");
		for(int i=0; i<num; i++){
			String key = nbtTag.getString("key["+i+"]");
			int value = nbtTag.getInteger("value["+i+"]");
			channels.put(key, value);
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound nbtTag) {
		nbtTag.setInteger("count", channels.size());
		int i=0;
		for(Entry<String, Integer> e:channels.entrySet()){
			nbtTag.setString("key["+i+"]", e.getKey());
			nbtTag.setInteger("value["+i+"]", e.getValue());
			i++;
		}
		return nbtTag;
	}

	@Override
	public boolean needSave() {
		boolean ret = needSave;
		needSave = false;
		return ret;
	}

	@Override
	public void reset() {
		channels.clear();
	}

	@Override
	public Object msg(int msg, Object... obj) {
		switch(msg){
			case PC_Utils.MSG_PROVIDES_FUNCTION:{
				List<String> l = (List)obj[1];
				l.add("tx");
				l.add("rx");
				return l;
			}case PC_Utils.MSG_CALL_FUNCTION:{
				String functionName = (String) obj[0];
				if(functionName.equals("tx")){
					if((Boolean)obj[2]){
						transmitterOn((String)obj[1]);
					}else{
						transmitterOff((String)obj[1]);
					}
				}else if(functionName.equals("rx")){
					return getChannelState((String)obj[1]);
				}
				break;
			}default:
				return null;
		}
		return true;
	}

}
