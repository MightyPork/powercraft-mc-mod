package powercraft.api;


public class PC_Vec2I {

	public int x;
	public int y;


	public PC_Vec2I() {

	}


	public PC_Vec2I(int x, int y) {

		this.x = x;
		this.y = y;
	}


	public PC_Vec2I(PC_Vec2I vec) {

		this(vec.x, vec.y);
	}


	public PC_Vec2I(String attribute) {

		String[] attributes = attribute.split(",");
		if (attributes.length != 2) throw new NumberFormatException();
		x = Integer.parseInt(attributes[0].trim());
		y = Integer.parseInt(attributes[1].trim());
	}


	public void setTo(PC_Vec2I vec) {

		x = vec.x;
		y = vec.y;
	}


	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PC_Vec2I) {
			PC_Vec2I vec = (PC_Vec2I) obj;
			return vec.x == x && vec.y == y;
		}
		return false;
	}


	@Override
	public int hashCode() {

		return x ^ 34 + y;
	}


	@Override
	public String toString() {

		return "Vec2I[" + x + ", " + y + "]";
	}


	public PC_Vec2I add(int n) {

		return new PC_Vec2I(x + n, y + n);
	}


	public PC_Vec2I add(int x, int y) {

		return new PC_Vec2I(this.x + x, this.y + y);
	}


	public PC_Vec2I add(PC_Vec2I vec) {

		return new PC_Vec2I(x + vec.x, y + vec.y);
	}


	public PC_Vec2I sub(int n) {

		return new PC_Vec2I(x - n, y - n);
	}


	public PC_Vec2I sub(int x, int y) {

		return new PC_Vec2I(this.x - x, this.y - y);
	}


	public PC_Vec2I sub(PC_Vec2I vec) {

		return new PC_Vec2I(x - vec.x, y - vec.y);
	}


	public PC_Vec2I max(int n) {

		return new PC_Vec2I(x > n ? x : n, y > n ? y : n);
	}


	public PC_Vec2I max(int x, int y) {

		return new PC_Vec2I(this.x > x ? this.x : x, this.y > y ? this.y : y);
	}


	public PC_Vec2I max(PC_Vec2I vec) {

		return new PC_Vec2I(x > vec.x ? x : vec.x, y > vec.y ? y : vec.y);
	}

	public PC_Vec2I min(int n) {

		return new PC_Vec2I(x < n ? x : n, y < n ? y : n);
	}


	public PC_Vec2I min(int x, int y) {

		return new PC_Vec2I(this.x < x ? this.x : x, this.y < y ? this.y : y);
	}


	public PC_Vec2I min(PC_Vec2I vec) {

		return new PC_Vec2I(x < vec.x ? x : vec.x, y < vec.y ? y : vec.y);
	}

	
}
