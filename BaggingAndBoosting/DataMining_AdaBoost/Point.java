package DataMining_AdaBoost;

/**
 * 坐标点类
 * 
 * @author lyq
 * 
 */
public class Point {
	// 坐标点x坐标
	private int x;
	// 坐标点y坐标
	private int y;
	// 坐标点的分类类别
	private int classType;
	//如果此节点被划错，他的误差率，不能用个数除以总数，因为不同坐标点的权重不一定相等
	private double probably;
	
	public Point(int x, int y, int classType){
		this.x = x;
		this.y = y;
		this.classType = classType;
	}
	
	public Point(String x, String y, String classType){
		this.x = Integer.parseInt(x);
		this.y = Integer.parseInt(y);
		this.classType = Integer.parseInt(classType);
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

	public int getClassType() {
		return classType;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	public double getProbably() {
		return probably;
	}

	public void setProbably(double probably) {
		this.probably = probably;
	}
}
