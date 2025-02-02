package powercraft.api;


import net.minecraft.entity.Entity;
import net.minecraftforge.common.ForgeDirection;

/**
 * 
 * PowerCrafts direction class, like forges
 * 
 * @author XOR
 *
 */
public enum PC_Direction{
	/** -Y */
	DOWN(0, -1, 0),

	/** +Y */
	UP(0, 1, 0),

	/** -Z */
	NORTH(0, 0, -1),

	/** +Z */
	SOUTH(0, 0, 1),
	
	/** -X */
	WEST(-1, 0, 0),

	/** +X */
	EAST(1, 0, 0),
	
	
	/**
	 * Used only by getOrientation, for invalid inputs
	 */
	UNKNOWN(0, 0, 0);

	public final int offsetX;
	public final int offsetY;
	public final int offsetZ;
	public final int flag;
	public static final PC_Direction FRONT = NORTH;
	public static final PC_Direction BACK = SOUTH;
	public static final PC_Direction RIGHT = EAST;
	public static final PC_Direction LEFT = WEST;
	public static final PC_Direction TOP = UP;
	public static final PC_Direction BOTTOM = DOWN;
	public static final PC_Direction[] VALID_DIRECTIONS = { DOWN, UP, NORTH, SOUTH, WEST, EAST };
	public static final int[] OPPOSITES = { 1, 0, 3, 2, 5, 4, 6 };
	
	// After testing the formula:
	// int l = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
	// I've discovered that the resulting l values are thus:
	// { North, East, South, West } = { 2, 3, 0, 1 }
	public static final int[] PLAYER2MD = { 0, 3, 1, 2 };
	
	
	// Left hand rule rotation matrix for all possible axes of rotation
	public static final int[][] ROTATION_MATRIX = {
		{ 0, 1, 4, 5, 3, 2, 6 },
		{ 0, 1, 5, 4, 2, 3, 6 },
		{ 5, 4, 2, 3, 0, 1, 6 },
		{ 4, 5, 2, 3, 1, 0, 6 },
		{ 2, 3, 1, 0, 4, 5, 6 },
		{ 3, 2, 0, 1, 4, 5, 6 },
		{ 0, 1, 2, 3, 4, 5, 6 }};

	public static final int[][] ROTATIONMD_MATRIX = {
		{ 0, 1, 2, 3, 4, 5, 6 },
		{ 0, 1, 2, 3, 4, 5, 6 },
		{ 0, 1, 2, 3, 4, 5, 6 },
		{ 0, 1, 5, 2, 3, 4, 6 },
		{ 0, 1, 4, 5, 2, 3, 6 },
		{ 0, 1, 3, 4, 5, 2, 6 }};
	
	PC_Direction(int x, int y, int z) {
		offsetX = x;
		offsetY = y;
		offsetZ = z;
		flag = 1 << ordinal();
	}

	/**
	 * get the orintation for a int side
	 * @param id the int side
	 * @return the direction or UNKNOWN when there is no valid direction
	 */
	public static PC_Direction getOrientation(int id) {

		if (id >= 0 && id < VALID_DIRECTIONS.length) {
			return VALID_DIRECTIONS[id];
		}
		return UNKNOWN;
	}

	/**
	 * get the opposite direction of this direction
	 * @return the opposite direction
	 */
	public PC_Direction getOpposite() {

		return getOrientation(OPPOSITES[ordinal()]);
	}

	/**
	 * rotate the direction around another axis
	 * @param axis the axis to rotate around
	 * @return the rotated direction
	 */
	public PC_Direction getRotation(PC_Direction axis) {

		return getOrientation(ROTATION_MATRIX[axis.ordinal()][ordinal()]);
	}

	/**
	 * used for rotate a direction for a block with the block metadata
	 * @param md the block metadata
	 * @return the direction
	 */
	public PC_Direction rotateMD(int md){
		return getOrientation(ROTATIONMD_MATRIX[md][ordinal()]);
	}
	
	/**
	 * gets the forge direction for this direction
	 * @return the forge direction
	 */
	public ForgeDirection getForgeDirection(){
		return ForgeDirection.getOrientation(ordinal());
	}
	
	/**
	 * gets the direction for the forge direction
	 * @param forgeDirection the forge direction
	 * @return the same PC direction
	 */
	public static PC_Direction getDirection(ForgeDirection forgeDirection){
		return getOrientation(forgeDirection.ordinal());
	}

	/**
	 * the direction in which a entity is walking mostly, or UNKNOWN if its not walking
	 * @param entity the entity form witch to know the walk direction
	 * @param considerY should also y-axis be included
	 * @return the direction in which the entity is wallking
	 */
	public static PC_Direction getDirectionFromEntity(Entity entity, boolean considerY) {
		double x = PC_MathHelper.abs_double(entity.motionX);
		double y = PC_MathHelper.abs_double(entity.motionY);
		double z = PC_MathHelper.abs_double(entity.motionZ);
		PC_Direction tmp;
		boolean isNeg;
		if(x<0.1&&y<0.1&&z<0.1) return PC_Direction.UNKNOWN;
		
		if((x>y||!considerY) && x>z){
			tmp = PC_Direction.EAST;
			isNeg = entity.motionX<0;
		}
		else if(considerY && y>x && y>z){
			tmp = PC_Direction.UP;
			isNeg = entity.motionY<0;
		}
		else{
			tmp = PC_Direction.SOUTH;
			isNeg = entity.motionZ<0;
		}
		if(isNeg) tmp = tmp.getOpposite();
		return tmp;
	}
	
	/**
	 * returns the names of the directions
	 * @return the name of the direction
	 */
	@Override
	public String toString(){
		switch (this){
			case NORTH:
				return "North";
			case EAST: 
				return "East";
			case SOUTH:
				return "South";
			case WEST:
				return "West";
			case UP:
				return "Up";
			case DOWN:
				return "Down";
			default:
				return "Unknown";					
		}
	}
}
