package powercraft.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import powercraft.management.PC_Utils.GameInfo;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class PC_3DRecipe {
	
	private PC_VecI size;
	private PC_Struct2<Boolean, List<PC_Struct2<Block, Integer>>>[][][] array;
	private PC_I3DRecipeHandler obj;
	private boolean doMirrow;
	
	public PC_3DRecipe(PC_I3DRecipeHandler obj, Object...o){
		this.obj = obj;
		size = new PC_VecI();
		int i=0;
		List<String[]> layer = new ArrayList<String[]>();
		HashMap<Character, List<PC_Struct2<Block, Integer>>> map = new HashMap<Character, List<PC_Struct2<Block, Integer>>>();
		
		while(o[i] instanceof String[]){
			layer.add((String[])o[i]);
			i++;
		}
		
		while(i<o.length && o[i] instanceof Character){
			char c = (Character)o[i];
			i++;
			List<PC_Struct2<Block, Integer>> list = new ArrayList<PC_Struct2<Block, Integer>>();
			while(i<o.length && o[i] instanceof Block){
				Block b = (Block)o[i];
				i++;
				Integer meta = -1;
				if(i<o.length && o[i] instanceof Integer){
					meta = (Integer)o[i];
					i++;
				}
				list.add(new PC_Struct2<Block, Integer>(b, meta));
			}
			map.put(c, list);
		}
		
		size.y = layer.size();
		for(int y=0; y<size.y; y++){
			String[] strMap = layer.get(y);
			if(size.z < strMap.length){
				size.z = strMap.length;
			}
			for(int z=0; z<strMap.length; z++){
				String line = strMap[z];
				line = line.replaceAll("!", "");
				if(size.x < line.length()){
					size.x = line.length();
				}
			}
		}
		
		array = new PC_Struct2[size.x][size.y][size.z];
		
		for(int y=0; y<size.y; y++){
			String[] strMap = layer.get(y);
			for(int z=0; z<strMap.length; z++){
				String line = strMap[z];
				int xp = 0;
				for(int x=0; x+xp<line.length() && x<size.x; x++){
					char c = line.charAt(x+xp);
					
					PC_Struct2<Boolean, List<PC_Struct2<Block, Integer>>> s = new PC_Struct2<Boolean, List<PC_Struct2<Block, Integer>>>(false, null);
					if(c=='!'){
						s.a = true;
						xp++;
						c = line.charAt(x+xp);
					}
					s.b = map.get(c);
					array[x][y][z] = s;
				}
			}
		}
		
		doMirrow = true;
		
		for(int y=0; y<size.y && doMirrow; y++){
			int maxX = (size.x+1)/2;
			for(int z=0; z<size.z && doMirrow; z++){
				for(int x=0; x<=maxX && doMirrow; x++){
					if(array[x][y][z] != array[size.x - 1 - x][y][z]){
						doMirrow = false;
					}
				}
			}
			int maxZ = (size.z+1)/2;
			for(int x=0; x<size.x && doMirrow; x++){
				for(int z=0; z<=maxZ && doMirrow; z++){
					if(array[x][y][z] != array[x][y][size.z - 1 - z]){
						doMirrow = false;
					}
				}
			}
		}
		
	}
	
	public int getStructRotation(World world, PC_VecI pos){
		for(int r=0; r<4; r++){
			boolean isOk = true;
			for(int x=0; x<size.x && isOk; x++){
				for(int y=0; y<size.y && isOk; y++){
					for(int z=0; z<size.z && isOk; z++){
						int xx = x, zz = z;
						if(r==3||r==2){
							xx=size.x-x-1;
						}
						if(r==2||r==1){
							zz=size.z-z-1;
						}
						if(r==1||r==3){
							int tmp = xx;
							xx = zz;
							zz = tmp;
						}
						PC_Struct2<Boolean, List<PC_Struct2<Block, Integer>>> ok = array[x][y][z];
						if(ok!=null){
							PC_VecI p = pos.offset(xx, y, zz);
							Block block = GameInfo.getBlock(world, p);
							int md = GameInfo.getMD(world, p);
							boolean isOk1 = false;
							if(ok.b!=null){
								isOk1 = true;
								for(PC_Struct2<Block, Integer>s:ok.b){
									if(s.a==block && (s.b==-1 || md==s.b)){
										isOk1 = false;
									}
								}
							}
							if(ok.a != isOk1){
								isOk = false;
							}
						}
					}
				}
			}
			if(isOk){
				return r;
			}
			if(doMirrow)
				return -1;
		}
		return -1;
	}
	
	public PC_Struct2<PC_VecI, Integer> getStructStart(World world, PC_VecI pos){
		for(int x=-size.x; x<=size.x; x++){
			for(int y=-size.y; y<=size.y; y++){
				for(int z=-size.z; z<=size.z; z++){
					PC_VecI p = pos.offset(x, y, z);
					int rot = getStructRotation(world, p);
					if(rot!=-1){
						return new PC_Struct2<PC_VecI, Integer>(p, rot);
					}
				}	
			}
		}
		return null;
	}
	
	public boolean isStruct(World world, PC_VecI pos){
		PC_Struct2<PC_VecI, Integer> structStart = getStructStart(world, pos);
		if(structStart==null)
			return false;
		return obj.foundStructAt(world, structStart);
	}
	
}
