package powercraft.management;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.World;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.PC_Utils.ValueWriting;

/**
 * Laser beam tracing class
 * 
 * @author MightyPork
 * @copy (c) 2012
 */
public class PC_BeamTracer {
	
	private Random rand = new Random();

	private PC_VecI startCoord, startMove;

	/**
	 * The beam color.<br>
	 * This object also contains information about beam visibility (particles
	 * enabled) and power crystal metadata.
	 */
	private PC_Color origColor;

	private PC_IBeamHandler handler;

	/** The current world */
	protected World world;

	private boolean canChangeColor = false;
	private boolean particlesBidi = false;
	private boolean canHitEntity = false;

	private int maxTotalLength = 8000;
	private int start_limit = 40;
	private int maximum_current_limit = 80;
	private float strength = 0.2f;

	private HashMap<String, Object> data = new HashMap<String, Object>();

	/**
	 * Laser beam raytracer
	 * 
	 * @param worldObj the world
	 * @param handler laser handler (interface)
	 */
	public PC_BeamTracer(World worldObj, PC_IBeamHandler handler) {
		this.handler = handler;
		this.world = worldObj;
	}

	public World getWorld() {
		return world;
	}

	/**
	 * Set if the beam can change color on PowerCrystals
	 * 
	 * @param state boolean value
	 * @return self
	 */
	public PC_BeamTracer setCanChangeColor(boolean state) {
		canChangeColor = state;
		return this;
	}


	/**
	 * Set starting strength
	 * 
	 * @param strength 
	 * @return self
	 */
	public PC_BeamTracer setStartStrength(float strength) {
		this.strength = strength;
		return this;
	}

	/**
	 * Set starting range
	 * 
	 * @param length range in blocks
	 * @return self
	 */
	public PC_BeamTracer setStartLength(int length) {
		start_limit = length;
		return this;
	}
	
	/**
	 * Set maximum current limit. When crystal adds some length, it is checked
	 * against this limit and shortened if needed.<br>
	 * If set to small number, crystals won't add more than this number.
	 * 
	 * @param length range in blocks
	 * @return self
	 */
	public PC_BeamTracer setMaxLengthAfterCrystal(int length) {
		maximum_current_limit = length;
		return this;
	}

	/**
	 * Set how many blocks a crystal adds.
	 * 
	 * @param length range in blocks
	 * @return self
	 */
	public PC_BeamTracer setData(String key, Object value) {
		data.put(key, value);
		return this;
	}

	/**
	 * Set the highest possible length (all forked beams together)
	 * 
	 * @param length range in blocks
	 * @return self
	 */
	public PC_BeamTracer setTotalLengthLimit(int length) {
		maxTotalLength = length;
		return this;
	}

	/**
	 * Set starting coordinates of the beam (the device)
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return self
	 */
	public PC_BeamTracer setStartCoord(int x, int y, int z) {
		startCoord = new PC_VecI(x, y, z);
		return this;
	}


	/**
	 * Set starting coordinates of the beam (the device)
	 * 
	 * @param coord
	 * @return self
	 */
	public PC_BeamTracer setStartCoord(PC_VecI coord) {
		startCoord = coord.copy();
		return this;
	}

	public PC_VecI getStartCoord() {
		return startCoord.copy();
	}

	/**
	 * Set starting movement vector of the beam
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return self
	 */
	public PC_BeamTracer setStartMove(int x, int y, int z) {
		startMove = new PC_VecI(x, y, z);
		return this;
	}


	/**
	 * Set starting movement vector of the beam
	 * 
	 * @param coord
	 * @return self
	 */
	public PC_BeamTracer setStartMove(PC_VecI coord) {
		startMove = coord.copy();
		return this;
	}


	/**
	 * Set starting beam color (can be changed by power crystals)
	 * 
	 * @param color color object representing the color;<br>
	 *            Color must also contain information about beam metadata, and
	 *            visibility flag.
	 * @return self
	 */
	public PC_BeamTracer setColor(PC_Color color) {
		origColor = color.copy();
		return this;
	}


	/**
	 * Set initial beam color
	 * 
	 * @param r
	 * @param g
	 * @param b
	 * @param meta metadata of corresponding power crystal -1 <br>
	 *            (crystals meta starts at 1, which equals 0 here)
	 * @return self
	 */
	public PC_BeamTracer setColor(float r, float g, float b, int meta) {
		origColor = new PC_Color(r, g, b);
		return this;
	}


	/**
	 * Set that the particles will "move" both forward and backward to simulate
	 * reflected beam for sensors.
	 * 
	 * @param bidi
	 * @return self
	 */
	public PC_BeamTracer setParticlesBidirectional(boolean bidi) {
		particlesBidi = bidi;
		return this;
	}


	/**
	 * Set whether the beam can detect entities
	 * 
	 * @param state
	 * @return self
	 */
	public PC_BeamTracer setDetectEntities(boolean state) {
		canHitEntity = state;
		return this;
	}


	/**
	 * Total beam length in this flash. <br>
	 * Used to prevent infinite loops and stack overflow.
	 */
	private int totalLength = 0;


	/**
	 * Send one light quantum and spawn particles on the way.
	 */
	public void flash() {
		totalLength = 0;

		forkBeam(new BeamSettings(this, startCoord, startMove, origColor, strength, start_limit));
	}


	/**
	 * Fork current beam. To be called only by subclasses.
	 * 
	 * @param par_cnt starting coordinate
	 * @param par_move starting movement
	 * @param par_color starting color object
	 * @param limit length limit for this fork
	 */
	public void forkBeam(BeamSettings settings) {
		// copy parameters to prevent interference

		for (;settings.length>0; settings.length--) {

			if (++totalLength > maxTotalLength) {
				return;
			}

			if (world.isRemote){
				
				addLaser(world, settings.pos, settings.move, strength, settings.color);
				
			}
			
			settings.pos.add(settings.move);

			int id = GameInfo.getBID(world, settings.pos);

			Block b = Block.blocksList[id];
			result res = result.CONTINUE;
			if (b!=null){
				res = result.FALLBACK;
				if(b instanceof PC_IMSG){
					Object o = ((PC_IMSG) b).msg(PC_Utils.MSG_ON_HIT_BY_BEAM_TRACER, getWorld(), settings);
					if(o instanceof result)
						res = (result)o;
				}
			}

			if (res == result.FALLBACK) {

				boolean stop = handler.onBlockHit(this, b, settings.pos);

				if (stop) {
					return;
				}

			} else if (res == result.CONTINUE) {

				// just continue

			} else if (res == result.STOP) {

				// break loop
				return;

			}
			
			if (canHitEntity) {

				// check for entities in this block.
				/**
				 * @todo getBoundingBox??
				 */
				List<Entity> hitList = world.getEntitiesWithinAABB(net.minecraft.src.Entity.class,
						AxisAlignedBB.getBoundingBox(settings.pos.x, settings.pos.y, settings.pos.z, settings.pos.x + 1, settings.pos.y + 1, settings.pos.z + 1));

				boolean stop = false;
				for(Entity entity:hitList){
					res = result.FALLBACK;
					if(entity instanceof PC_IMSG){
						Object o = ((PC_IMSG) entity).msg(PC_Utils.MSG_ON_HIT_BY_BEAM_TRACER, getWorld(), settings);
						if(o instanceof result)
							res = (result)o;
					}
					if (res == result.FALLBACK) {

						if(handler.onEntityHit(this, entity, settings.pos)){
							stop = true;
						}

					} else if (res == result.CONTINUE) {

						// just continue

					} else if (res == result.STOP) {

						stop = true;

					}
				}
				
				if(stop){
					return;
				}
				
			}
		}

	}


	public static void addLaser(World world, PC_VecI cnt, PC_VecI move, float strength, PC_Color color){
		cnt = cnt.copy();
		PC_VecI oldMove = move;
		move = move.copy();
		color = color.copy();
		boolean dirChage = false;
		if(move.x<0){
			move.x = -move.x;
			move.y = -move.y;
			move.z = -move.z;
			dirChage = true;
		}else if(move.x==0 && move.y<0){
			move.y = -move.y;
			move.z = -move.z;
			dirChage = true;
		}else if(move.x==0 && move.y==0 && move.z<0){
			move.z = -move.z;
			dirChage = true;
		}
		if(dirChage)
			cnt = cnt.offset(oldMove);
		
		ValueWriting.spawnParticle("PC_EntityLaserFX", world, cnt, move, strength, color);
	}
	
	/**
	 * Result state enum for extending class's block hit method.
	 */
	public static enum result {
		/** Fall back to handler */
		FALLBACK,
		/** Continue to next block */
		CONTINUE,
		/** Stop the beam propagation */
		STOP;
	}

	public static class BeamSettings{
		private PC_BeamTracer beamTracer;
		private PC_VecI pos;
		private PC_VecI move;
		private PC_Color color;
		private float strength;
		private int length;
		
		public BeamSettings(PC_BeamTracer beamTracer, PC_VecI startCoord, PC_VecI startMove, PC_Color origColor, float strength2, int start_limit) {
			this.beamTracer = beamTracer;
			pos = startCoord.copy();
			move = startMove.copy();
			color = origColor.copy();
			strength = strength2;
			length = start_limit;
		}
		
		public PC_BeamTracer getBeamTracer(){
			return beamTracer;
		}
		
		public PC_VecI getPos(){
			return pos.copy();
		}
		
		public PC_VecI getMove(){
			return move.copy();
		}
		
		public PC_Color getColor(){
			return color.copy();
		}
		
		public float getStrength(){
			return strength;
		}
		
		public int getLength(){
			return length;
		}
		
		public Object getData(String key){
			return beamTracer.data.get(key);
		}
		
		public void setPos(PC_VecI newPos){
			pos.setTo(newPos);
		}
		
		public void setMove(PC_VecI newMove){
			move.setTo(newMove);
		}
		
		public void setColor(PC_Color newColor){
			if(beamTracer.canChangeColor)
				color.setTo(newColor);
		}
		
		public void setStrength(float newStrength){
			strength = newStrength;
		}
		
		public void setLength(int newLength){
			length = newLength; 
		}
		
	}
	
}
