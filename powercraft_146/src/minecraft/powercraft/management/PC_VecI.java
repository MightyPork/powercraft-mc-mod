package powercraft.management;

import java.io.Serializable;

import net.minecraft.nbt.NBTTagCompound;

public class PC_VecI implements Serializable, PC_Vec<Integer, PC_VecI>{

	public int x;
	public int y;
	public int z;
	
	public PC_VecI(){
		this(0, 0, 0);
	}
	
	public PC_VecI(int x){
		this(x, 0, 0);
	}
	
	public PC_VecI(int x, int y){
		this(x, y, 0);
	}
	
	public PC_VecI(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public PC_VecI(PC_Vec vec){
		x = vec.getX().intValue();
		y = vec.getY().intValue();
		z = vec.getZ().intValue();
	}
	
	@Override
	public Integer getX() {
		return x;
	}

	@Override
	public Integer getY() {
		return y;
	}

	@Override
	public Integer getZ() {
		return z;
	}

	@Override
	public PC_VecI setX(Integer x) {
		this.x = x;
		return this;
	}

	@Override
	public PC_VecI setY(Integer y) {
		this.y = y;
		return this;
	}

	@Override
	public PC_VecI setZ(Integer z) {
		this.z = z;
		return this;
	}

	@Override
	public PC_VecI setTo(PC_Vec vec) {
		return setTo(vec.getX().intValue(), vec.getY().intValue(), vec.getZ().intValue());
	}

	@Override
	public PC_VecI setTo(Integer x, Integer y, Integer z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	@Override
	public PC_VecI add(PC_Vec vec) {
		return add(vec.getX().intValue(), vec.getY().intValue(), vec.getZ().intValue());
	}

	@Override
	public PC_VecI add(Integer n) {
		return add(n, n, n);
	}

	@Override
	public PC_VecI add(Integer x, Integer y, Integer z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	@Override
	public PC_VecI offset(PC_Vec vec) {
		return copy().add(vec);
	}
	
	@Override
	public PC_VecI offset(Integer n) {
		return copy().add(n);
	}
	
	@Override
	public PC_VecI offset(Integer x, Integer y, Integer z) {
		return copy().add(x, y, z);
	}
	
	@Override
	public PC_VecI sub(PC_Vec vec) {
		return sub(vec.getX().intValue(), vec.getY().intValue(), vec.getZ().intValue());
	}

	@Override
	public PC_VecI sub(Integer n) {
		return sub(n, n, n);
	}

	@Override
	public PC_VecI sub(Integer x, Integer y, Integer z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	@Override
	public PC_VecI mul(PC_Vec vec) {
		return mul(vec.getX().intValue(), vec.getY().intValue(), vec.getZ().intValue());
	}

	@Override
	public PC_VecI mul(Integer n) {
		return mul(n, n, n);
	}

	@Override
	public PC_VecI mul(Integer x, Integer y, Integer z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}

	@Override
	public PC_VecI div(PC_Vec vec) {
		return div(vec.getX().intValue(), vec.getY().intValue(), vec.getZ().intValue());
	}

	@Override
	public PC_VecI div(Integer n) {
		return div(n, n, n);
	}

	@Override
	public PC_VecI div(Integer x, Integer y, Integer z) {
		this.x /= x;
		this.y /= y;
		this.z /= z;
		return this;
	}

	@Override
	public double length() {
		return Math.sqrt(x*x + y*y + z*z);
	}

	@Override
	public double distanceTo(PC_Vec vec){
		return copy().sub(vec).length();
	}
	
	@Override
	public double distanceTo(Integer x, Integer y, Integer z){
		return copy().sub(x, y, z).length();
	}
	
	@Override
	public PC_VecI normalize() {
		double length = length();
		x /= length;
		y /= length;
		z /= length;
		return this;
	}

	@Override
	public PC_VecI clamp(PC_Vec min, PC_Vec max) {
		int minVal, maxVal;
		minVal = min.getX().intValue();
		maxVal = max.getX().intValue();
		if(minVal>maxVal){
			if(x<minVal)
				x = minVal;
			else if(x>maxVal)
				x = maxVal;
		}
		minVal = min.getY().intValue();
		maxVal = max.getY().intValue();
		if(minVal>maxVal){
			if(y<minVal)
				y = minVal;
			else if(y>maxVal)
				y = maxVal;
		}
		minVal = min.getZ().intValue();
		maxVal = max.getZ().intValue();
		if(minVal>maxVal){
			if(z<minVal)
				z = minVal;
			else if(z>maxVal)
				z = maxVal;
		}
		return this;
	}

	@Override
	public PC_VecI clamp(PC_Vec min, Integer max) {
		return clamp(min, new PC_VecI(max, max, max));
	}

	@Override
	public PC_VecI clamp(Integer min, PC_Vec max) {
		return clamp(new PC_VecI(min, min, min), max);
	}

	@Override
	public PC_VecI clamp(Integer min, Integer max) {
		return clamp(new PC_VecI(min, min, min), new PC_VecI(max, max, max));
	}

	@Override
	public PC_VecI max(PC_Vec max) {
		int maxVal;
		maxVal = max.getX().intValue();
		if(x>maxVal)
			x = maxVal;
		maxVal = max.getY().intValue();
		if(y>maxVal)
			y = maxVal;
		maxVal = max.getZ().intValue();
		if(z>maxVal)
			z = maxVal;
		return this;
	}

	@Override
	public PC_VecI max(Integer max) {
		if(x>max)
			x = max;
		if(y>max)
			y = max;
		if(z>max)
			z = max;
		return this;
	}

	@Override
	public PC_VecI min(PC_Vec min) {
		int minVal;
		minVal = min.getX().intValue();
		if(x<minVal)
			x = minVal;
		minVal = min.getY().intValue();
		if(y<minVal)
			y = minVal;
		minVal = min.getZ().intValue();
		if(z<minVal)
			z = minVal;
		return this;
	}

	@Override
	public PC_VecI min(Integer min) {
		if(x<min)
			x = min;
		if(y<min)
			y = min;
		if(z<min)
			z = min;
		return this;
	}

	@Override
	public PC_VecI copy() {
		return new PC_VecI(this);
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof PC_Vec)){
			return false;
		}
		PC_Vec vec = (PC_Vec)o;
		if(x != vec.getX().intValue())
			return false;
		if(y != vec.getY().intValue())
			return false;
		if(z != vec.getZ().intValue())
			return false;
		return true;
	}
	
	@Override
	public PC_VecI readFromNBT(NBTTagCompound nbttag) {
		x = nbttag.getInteger("x");
		y = nbttag.getInteger("y");
		z = nbttag.getInteger("z");
		return this;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttag) {
		nbttag.setInteger("x", x);
		nbttag.setInteger("y", y);
		nbttag.setInteger("z", z);
		return nbttag;
	}

	@Override
	public String toString() {
		return "Vec["+x+", "+y+", "+z+"]";
	}
	
}
