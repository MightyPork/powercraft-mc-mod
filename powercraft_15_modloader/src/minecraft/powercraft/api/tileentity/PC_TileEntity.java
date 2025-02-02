package powercraft.api.tileentity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import powercraft.api.annotation.PC_ClientServerSync;
import powercraft.api.network.PC_PacketHandler;
import powercraft.api.reflect.PC_FieldWithAnnotation;
import powercraft.api.reflect.PC_IFieldAnnotationIterator;
import powercraft.api.reflect.PC_ReflectHelper;
import powercraft.api.utils.PC_Direction;
import powercraft.api.utils.PC_Entry;
import powercraft.api.utils.PC_Struct2;
import powercraft.api.utils.PC_Utils;
import powercraft.api.utils.PC_VecI;
import powercraft.launcher.PC_Logger;

public class PC_TileEntity extends TileEntity {
	
	protected List<PC_ITileEntityWatcher> watchers = new ArrayList<PC_ITileEntityWatcher>();
	protected List<String> users = new ArrayList<String>();
	protected boolean isWhiteList = false;
	protected List<Long> allowedKeys = new ArrayList<Long>();
	
	public final PC_VecI getCoord() {
		return new PC_VecI(xCoord, yCoord, zCoord);
	}
	
	public final World getWorld() {
		return worldObj;
	}
	
	public void create(ItemStack stack, EntityPlayer player, World world,
			int x, int y, int z, int dir, float hitX, float hitY, float hitZ) {
		
	}
	
	@Override
	public Packet getDescriptionPacket() {
		PC_Struct2<String, Object>[] o = getData();
		
		if (o == null) {
			return null;
		}
		
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		ObjectOutputStream sendData;
		
		try {
			sendData = new ObjectOutputStream(data);
			sendData.writeInt(PC_PacketHandler.PACKETTILEENTITY);
			sendData.writeInt(xCoord);
			sendData.writeInt(yCoord);
			sendData.writeInt(zCoord);
			sendData.writeObject(o);
			sendData.writeInt(PC_PacketHandler.PACKETTILEENTITY);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new Packet250CustomPayload("PowerCraft", data.toByteArray());
	}
	
	@Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
            super.readFromNBT(nbtTagCompound);
            final NBTTagCompound nbtTag = nbtTagCompound.getCompoundTag("map");
            PC_ReflectHelper.getAllFieldsWithAnnotation(getClass(), this, PC_ClientServerSync.class, new PC_IFieldAnnotationIterator<PC_ClientServerSync>() {

                    @Override
                    public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<PC_ClientServerSync> fieldWithAnnotation) {
                            if(fieldWithAnnotation.getAnnotation().save()){
                                    String fieldName = fieldWithAnnotation.getAnnotation().name();
                                    if(fieldName.equals("")){
                                            fieldName = fieldWithAnnotation.getFieldName();
                                    }
                                    Object o = PC_Utils.loadFromNBT(nbtTag, fieldName);
                                    fieldWithAnnotation.setValue(o);
                            }
                            return false;
                    }
            });
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
            super.writeToNBT(nbtTagCompound);
            final NBTTagCompound nbtTag = new NBTTagCompound();
            PC_ReflectHelper.getAllFieldsWithAnnotation(getClass(), this, PC_ClientServerSync.class, new PC_IFieldAnnotationIterator<PC_ClientServerSync>() {

                    @Override
                    public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<PC_ClientServerSync> fieldWithAnnotation) {
                            if(fieldWithAnnotation.getAnnotation().save()){
                                    String fieldName = fieldWithAnnotation.getAnnotation().name();
                                    if(fieldName.equals("")){
                                            fieldName = fieldWithAnnotation.getFieldName();
                                    }
                                    PC_Utils.saveToNBT(nbtTag, fieldName, fieldWithAnnotation.getValue());
                            }
                            return false;
                    }
            });
            nbtTagCompound.setCompoundTag("map", nbtTag);
    }

	
	public boolean canPlayerSetField(String fieldName, PC_FieldWithAnnotation<PC_ClientServerSync> fieldWithAnnotation, EntityPlayer player) {
		if (!worldObj.isRemote) {
			if (isWhiteList) {
				if (users.contains(player.username))
					return true;
			} else {
				if (!users.contains(player.username))
					return true;
			}
			if (PC_Utils.isPlayerOPOrOwner(player))
				return true;
			return false;
		}
		return true;
	}
	
	public Field getSyncFieldWithName(final String name, final EntityPlayer player) {
		List<Field> l = PC_ReflectHelper.getAllFieldsWithAnnotation(getClass(), this, PC_ClientServerSync.class,
				new PC_IFieldAnnotationIterator<PC_ClientServerSync>() {
					
					@Override
					public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<PC_ClientServerSync> fieldWithAnnotation) {
						if (!worldObj.isRemote && !fieldWithAnnotation.getAnnotation().clientChangeAble())
							return false;
						String fieldName = fieldWithAnnotation.getAnnotation().name();
						if (fieldName.equals("")) {
							fieldName = fieldWithAnnotation.getFieldName();
						}
						if (canPlayerSetField(fieldName, fieldWithAnnotation, player))
							if (fieldName.equals(name))
								return true;
						return false;
					}
				});
		if (l.size() > 0) {
			return l.get(0);
		}
		return null;
	}
	
	public void setData(EntityPlayer player, PC_Struct2<String, Object>[] o) {
		for (int i = 0; i < o.length; i++) {
			if (o[i].a.equals("call")) {
				PC_Struct2<String, Object[]> s = (PC_Struct2<String, Object[]>) o[i].b;
				onCall(s.a, s.b);
			} else {
				if (tryDataChange(o[i].a, o[i].b)) {
					Field f = getSyncFieldWithName(o[i].a, player);
					if (f != null) {
						f.setAccessible(true);
						try {
							f.set(this, o[i].b);
							dataChanged(o[i].a, o[i].b);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						PC_Logger.warning(this + ":Try to write non existing Field " + o[i].a);
					}
				}
			}
		}
		dataRecieved();
	}
	
	protected void dataRecieved() {
	}
	
	protected boolean tryDataChange(String key, Object value) {
		return true;
	}
	
	protected void dataChanged(String key, Object value) {
	}
	
	protected void onCall(String key, Object[] value) {
	}
	
	public void call(String key, Object... value) {
		onCall(key, value);
		PC_PacketHandler.setTileEntity(this, new PC_Entry("call", new PC_Struct2<String, Object[]>(key, value)));
		dataRecieved();
	}
	
	protected void notifyChanges(final String... fields) {
		final List<PC_Struct2<String, Object>> data = new ArrayList<PC_Struct2<String, Object>>();
		PC_ReflectHelper.getAllFieldsWithAnnotation(getClass(), this, PC_ClientServerSync.class, new PC_IFieldAnnotationIterator<PC_ClientServerSync>() {
			
			@Override
			public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<PC_ClientServerSync> fieldWithAnnotation) {
				if (worldObj.isRemote && !fieldWithAnnotation.getAnnotation().clientChangeAble())
					return false;
				String fieldName = fieldWithAnnotation.getAnnotation().name();
				if (fieldName.equals("")) {
					fieldName = fieldWithAnnotation.getFieldName();
				}
				for (String name : fields) {
					if (fieldName.equals(name)) {
						data.add(new PC_Struct2<String, Object>(name, fieldWithAnnotation.getValue()));
						break;
					}
				}
				return false;
			}
		});
		PC_PacketHandler.setTileEntity(this, data.toArray(new PC_Struct2[0]));
	}
	
	public PC_Struct2<String, Object>[] getData() {
		final List<PC_Struct2<String, Object>> data = new ArrayList<PC_Struct2<String, Object>>();
		PC_ReflectHelper.getAllFieldsWithAnnotation(getClass(), this, PC_ClientServerSync.class, new PC_IFieldAnnotationIterator<PC_ClientServerSync>() {
			
			@Override
			public boolean onFieldWithAnnotation(PC_FieldWithAnnotation<PC_ClientServerSync> fieldWithAnnotation) {
				String fieldName = fieldWithAnnotation.getAnnotation().name();
				if (fieldName.equals("")) {
					fieldName = fieldWithAnnotation.getFieldName();
				}
				data.add(new PC_Struct2<String, Object>(fieldName, fieldWithAnnotation.getValue()));
				return false;
			}
		});
		return data.toArray(new PC_Struct2[0]);
	}
	
	public void addTileEntityWatcher(PC_ITileEntityWatcher tileEntityWatcher) {
		if (!watchers.contains(tileEntityWatcher))
			watchers.add(tileEntityWatcher);
	}
	
	public void removeTileEntityWatcher(PC_ITileEntityWatcher tileEntityWatcher) {
		if (watchers.contains(tileEntityWatcher))
			watchers.remove(tileEntityWatcher);
	}
	
	public void onNeighborBlockChange(int id) {
		
	}
	
	public boolean openGui(EntityPlayer entityPlayer) {
		return false;
	}
	
	public int getProvidingStrongRedstonePowerValue(PC_Direction dir) {
		return 0;
	}
	
	public int getProvidingWeakRedstonePowerValue(PC_Direction dir) {
		return getProvidingStrongRedstonePowerValue(dir);
	}
	
	public boolean canUpdate(){
		return true;
	}
	
	public int getPickMetadata(){
		return 0;
	}
	
}
