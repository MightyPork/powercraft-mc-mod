package powercraft.api.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import powercraft.api.interfaces.PC_IDataHandler;
import powercraft.api.tick.PC_ITickHandler;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;

public class PC_ChunkForcerRegistry implements PC_IDataHandler, PC_ITickHandler {
	
	private static PC_ChunkForcerRegistry instance;
	private static HashMap<Integer, HashMap<PC_VecI, Integer>> chunks = new HashMap<Integer, HashMap<PC_VecI, Integer>>();
	private static boolean needSave = false;
	
	public static PC_ChunkForcerRegistry getInstance() {
		if (instance == null)
			instance = new PC_ChunkForcerRegistry();
		return instance;
	}
	
	private HashMap<PC_VecI, Integer> loadList(NBTTagCompound nbtTag, HashMap<PC_VecI, Integer> distance) {
		int count = nbtTag.getInteger("count");
		for (int i = 0; i < count; i++) {
			PC_VecI pos = new PC_VecI();
			PC_Utils.loadFromNBT(nbtTag, "key" + i + "]", pos);
			int radius = nbtTag.getInteger("value[" + i + "]");
			distance.put(pos, radius);
		}
		return distance;
	}
	
	@Override
	public void load(NBTTagCompound nbtTag) {
		reset();
		int count = nbtTag.getInteger("count");
		for (int i = 0; i < count; i++) {
			int dim = nbtTag.getInteger("key[" + i + "]");
			HashMap<PC_VecI, Integer> distance = loadList(nbtTag.getCompoundTag("value[" + i + "]"), new HashMap<PC_VecI, Integer>());
			chunks.put(dim, distance);
		}
	}
	
	private NBTTagCompound saveList(NBTTagCompound nbtTag, HashMap<PC_VecI, Integer> distance) {
		nbtTag.setInteger("count", distance.size());
		int i = 0;
		for (Entry<PC_VecI, Integer> e : distance.entrySet()) {
			PC_Utils.saveToNBT(nbtTag, "key[" + i + "]", e.getKey());
			nbtTag.setInteger("value[" + i + "]", e.getValue());
			i++;
		}
		return nbtTag;
	}
	
	@Override
	public NBTTagCompound save(NBTTagCompound nbtTag) {
		needSave = false;
		nbtTag.setInteger("count", chunks.size());
		int i = 0;
		for (Entry<Integer, HashMap<PC_VecI, Integer>> e : chunks.entrySet()) {
			nbtTag.setInteger("key[" + i + "]", e.getKey());
			nbtTag.setCompoundTag("value[" + i + "]", saveList(new NBTTagCompound(), e.getValue()));
			i++;
		}
		return nbtTag;
	}
	
	@Override
	public boolean needSave() {
		return needSave;
	}
	
	@Override
	public void reset() {
		needSave = false;
		chunks.clear();
	}
	
	@Override
	public void tickEvent() {
		for (Entry<Integer, HashMap<PC_VecI, Integer>> e : chunks.entrySet()) {
			World world = PC_Utils.getWorldForDimension(e.getKey());
			if (world != null) {
				IChunkProvider chunckProvider = world.getChunkProvider();
				if (chunckProvider != null) {
					List<PC_VecI> chunckList = new ArrayList<PC_VecI>();
					for (Entry<PC_VecI, Integer> e2 : e.getValue().entrySet()) {
						PC_VecI p = e2.getKey().copy();
						p.x >>= 4;
						p.y = 0;
						p.z >>= 4;
						int r = e2.getValue();
						for (int i = -r; i <= r; i++) {
							for (int j = -r; j <= r; j++) {
								PC_VecI p2 = new PC_VecI(p.x + i, 0, p.z + j);
								if (!chunckList.contains(p2)) {
									chunckList.add(p2);
								}
							}
						}
					}
					for (PC_VecI pos : chunckList) {
						chunckProvider.loadChunk(pos.x, pos.z);
					}
				}
			}
		}
	}
	
	public static void forceChunkUpdate(World world, PC_VecI pos, int radius) {
		if (world.isRemote)
			return;
		int dimension = world.getWorldInfo().getDimension();
		HashMap<PC_VecI, Integer> distance;
		if (chunks.containsKey(dimension))
			distance = chunks.get(dimension);
		else
			chunks.put(dimension, distance = new HashMap<PC_VecI, Integer>());
		pos = pos.copy();
		distance.put(pos, radius);
		needSave = true;
	}
	
	public static void stopForceChunkUpdate(World world, PC_VecI pos) {
		if (world.isRemote)
			return;
		int dimension = world.getWorldInfo().getDimension();
		if (chunks.containsKey(dimension)) {
			HashMap<PC_VecI, Integer> distance = chunks.get(dimension);
			pos = pos.copy();
			if (distance.containsKey(pos)) {
				distance.remove(pos);
				needSave = true;
			}
			if (distance.size() == 0) {
				chunks.remove(dimension);
				needSave = true;
			}
		}
	}
	
	@Override
	public String getName() {
		return "PC_ChunkForcerRegistry";
	}
	
}
