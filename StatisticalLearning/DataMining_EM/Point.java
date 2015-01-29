package DataMining_EM;

/**
 * 坐标点类
 * 
 * @author lyq
 * 
 */
public class Point {
	// 坐标点横坐标
	private double x;
	// 坐标点纵坐标
	private double y;
	// 坐标点对于P1的隶属度
	private double memberShip1;
	// 坐标点对于P2的隶属度
	private double memberShip2;

	public Point(double d, double e) {
		this.x = d;
		this.y = e;
	}

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

	public double getMemberShip1() {
		return memberShip1;
	}

	public void setMemberShip1(double memberShip1) {
		this.memberShip1 = memberShip1;
	}

	public double getMemberShip2() {
		return memberShip2;
	}

	public void setMemberShip2(double memberShip2) {
		this.memberShip2 = memberShip2;
	}

}
