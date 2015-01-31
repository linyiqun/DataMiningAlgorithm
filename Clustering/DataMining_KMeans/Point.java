package DataMining_KMeans;

/**
 * 坐标点类
 * 
 * @author lyq
 * 
 */
public class Point implements Comparable<Point>{
	// 坐标点横坐标
	private double x;
	// 坐标点纵坐标
	private double y;
	//以此点作为聚类中心的类的类名称
	private String className;
	// 坐标点之间的欧式距离
	private Double distance;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(String x, String y) {
		this.x = Double.parseDouble(x);
		this.y = Double.parseDouble(y);
	}
	
	public Point(String x, String y, String className) {
		this.x = Double.parseDouble(x);
		this.y = Double.parseDouble(y);
		this.className = className;
	}

	/**
	 * 距离目标点p的欧几里得距离
	 * 
	 * @param p
	 */
	public void computerDistance(Point p) {
		if (p == null) {
			return;
		}

		this.distance = (this.x - p.x) * (this.x - p.x) + (this.y - p.y)
				* (this.y - p.y);
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
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(Point o) {
		// TODO Auto-generated method stub
		return this.distance.compareTo(o.distance);
	}
	
}
