
/**
 * 坐标点类
 * 
 * @author lyq
 * 
 */
public class Point {

	// 坐标点横坐标
	private int x;
	// 坐标点纵坐标
	private int y;
	// 坐标点对于P1的隶属度
	private int memberShip1;
	// 坐标点对于P2的隶属度
	private int memberShip2;

	public Point(int d, int e) {
		this.x = d;
		this.y = e;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMemberShip1() {
		return memberShip1;
	}

	public void setMemberShip1(int memberShip1) {
		this.memberShip1 = memberShip1;
	}

	public int getMemberShip2() {
		return memberShip2;
	}

	public void setMemberShip2(int memberShip2) {
		this.memberShip2 = memberShip2;
	}

}
