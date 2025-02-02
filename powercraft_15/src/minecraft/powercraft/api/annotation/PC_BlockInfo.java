package powercraft.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import powercraft.api.block.PC_ItemBlock;
import powercraft.api.tileentity.PC_TileEntity;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PC_BlockInfo {
	
	public final static class PC_FakeItemBlock extends PC_ItemBlock {
		
		private PC_FakeItemBlock() {
			super(-1);
		}
		
	}
	
	public final static class PC_FakeTileEntity extends PC_TileEntity {
	}
	
	public String name();
	
	public Class<? extends PC_ItemBlock> itemBlock() default PC_FakeItemBlock.class;
	
	public Class<? extends PC_TileEntity> tileEntity() default PC_FakeTileEntity.class;
	
	public boolean canPlacedRotated() default false;
	
}
