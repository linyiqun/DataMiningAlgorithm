package DataMining_Chameleon;



/**
 * 坐标点类
 * @author lyq
 *
 */
public class Point{
	//坐标点id号,id号唯一
	int id;
	//坐标横坐标
	Integer x;
	//坐标纵坐标
	Integer y;
	//是否已经被访问过
	boolean isVisited;
	
	public Point(String id, String x, String y){
		this.id = Integer.parseInt(id);
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
	}
	
	/**
	 * 计算当前点与制定点之间的欧式距离
	 * 
	 * @param p
	 *            待计算聚类的p点
	 * @return
	 */
	public double ouDistance(Point p) {
		double distance = 0;

		distance = (this.x - p.x) * (this.x - p.x) + (this.y - p.y)
				* (this.y - p.y);
		distance = Math.sqrt(distance);

		return distance;
	}
	
	/**
	 * 判断2个坐标点是否为用个坐标点
	 * 
	 * @param p
	 *            待比较坐标点
	 * @return
	 */
	public boolean isTheSame(Point p) {
		boolean isSamed = false;

		if (this.x == p.x && this.y == p.y) {
			isSamed = true;
		}

		return isSamed;
	}
}
