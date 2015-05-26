package renderGraph;

public class Vec2D {

	public Vec2D(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Vec2D() {
		this(0.0, 0.0);
	}

	private double x;
	private double y;

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public Vec2D scaleBy(double s) {
		this.x *= s;
		this.y *= s;
		return this;
	}

	public Vec2D getScaled(double s) {
		return new Vec2D(this.x * s, this.y * s);
	}

	public Vec2D addVec(Vec2D v) {
		this.x += v.getX();
		this.y += v.getY();
		return this;
	}

	public Vec2D getSum(Vec2D v) {
		return new Vec2D(this.x + v.getX(), this.y + v.getY());
	}

	public Vec2D normalize() {
		double length = this.length();
		if (length == 0.0)
			return this;
		this.x /= length;
		this.y /= length;
		return this;
	}

	public Vec2D getNormal() {
		double length = this.length();
		if (length == 0.0)
			return this.clone();
		return new Vec2D(this.x / length, this.y / length /* length */);
	}

	public double length() {
		return Math.sqrt(x * x + y * y);
	}

	public double angle() {
//		System.out.println(this.x + " "+ this.y+" "+this.y/this.x+" "+Math.atan(this.y/this.x));
		if(x == 0.0){
			if(y < 0.0){
				return 1.5 * Math.PI;
			} else {
				return 0.5 * Math.PI;
			}
		}
		double angle = Math.atan(this.y / this.x)
				+ (this.x <= 0.0 ? Math.PI : 0.0);
		// System.out.println(angle);
		
		return (angle < 0.0 ? (2.0 * Math.PI) + angle : angle);
	}

	public Vec2D setAngle(double angle) {
		double length = length();
		this.x = Math.cos(angle) * length;
		this.y = Math.sin(angle) * length;
		return this;
	}

	public static Vec2D getUnityX() {
		return new Vec2D(1.0, 0.0);
	}

	public static Vec2D getUnityY() {
		return new Vec2D(0.0, 1.0);
	}

	public static Vec2D getZero() {
		return new Vec2D(0.0, 0.0);
	}

	public double dotProduct(Vec2D other) {
		return this.x * other.x + this.y * other.y;
	}

	public double angle(Vec2D other) {
		// return
		// Math.acos(this.dotProduct(other)/(this.length()*other.length()));
		double angle = this.angle() - other.angle();
		return angle < 0.0 ? angle + 2 * Math.PI : angle;
	}

	public static final double rad2deg = 180.0 / Math.PI;

	public static void main(String[] args) {
		// System.out.println(getUnityY().scaleBy(-1.0).setAngle(359.0/rad2deg));

		Vec2D v1 = getUnityX();

		for (int i = 0; i < 72; i++) {

			
			v1.setAngle(5.0*i/rad2deg);
			System.out.println(v1);
			
		}
	}

	public Vec2D clone() {
		return new Vec2D(this.x, this.y);
	}

	@Override
	public String toString() {
		return "Vec2D [x=" + x + ", y=" + y + ", length=" + length()
				+ ", angle=" + angle() * rad2deg + "]";
	}
}
