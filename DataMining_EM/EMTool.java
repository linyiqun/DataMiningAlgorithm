package DataMining_EM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * EM最大期望算法工具类
 * 
 * @author lyq
 * 
 */
public class EMTool {
	// 测试数据文件地址
	private String dataFilePath;
	// 测试坐标点数据
	private String[][] data;
	// 测试坐标点数据列表
	private ArrayList<Point> pointArray;
	// 目标C1点
	private Point p1;
	// 目标C2点
	private Point p2;

	public EMTool(String dataFilePath) {
		this.dataFilePath = dataFilePath;
		pointArray = new ArrayList<>();
	}

	/**
	 * 从文件中读取数据
	 */
	public void readDataFile() {
		File file = new File(dataFilePath);
		ArrayList<String[]> dataArray = new ArrayList<String[]>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(" ");
				dataArray.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		data = new String[dataArray.size()][];
		dataArray.toArray(data);

		// 开始时默认取头2个点作为2个簇中心
		p1 = new Point(Integer.parseInt(data[0][0]),
				Integer.parseInt(data[0][1]));
		p2 = new Point(Integer.parseInt(data[1][0]),
				Integer.parseInt(data[1][1]));

		Point p;
		for (String[] array : data) {
			// 将数据转换为对象加入列表方便计算
			p = new Point(Integer.parseInt(array[0]),
					Integer.parseInt(array[1]));
			pointArray.add(p);
		}
	}

	/**
	 * 计算坐标点对于2个簇中心点的隶属度
	 * 
	 * @param p
	 *            待测试坐标点
	 */
	private void computeMemberShip(Point p) {
		// p点距离第一个簇中心点的距离
		double distance1 = 0;
		// p距离第二个中心点的距离
		double distance2 = 0;

		// 用欧式距离计算
		distance1 = Math.pow(p.getX() - p1.getX(), 2)
				+ Math.pow(p.getY() - p1.getY(), 2);
		distance2 = Math.pow(p.getX() - p2.getX(), 2)
				+ Math.pow(p.getY() - p2.getY(), 2);

		// 计算对于p1点的隶属度，与距离成反比关系，距离靠近越小，隶属度越大，所以要用大的distance2另外的距离来表示
		p.setMemberShip1(distance2 / (distance1 + distance2));
		// 计算对于p2点的隶属度
		p.setMemberShip2(distance1 / (distance1 + distance2));
	}

	/**
	 * 执行期望最大化步骤
	 */
	public void exceptMaxStep() {
		// 新的优化过的簇中心点
		double p1X = 0;
		double p1Y = 0;
		double p2X = 0;
		double p2Y = 0;
		double temp1 = 0;
		double temp2 = 0;
		// 误差值
		double errorValue1 = 0;
		double errorValue2 = 0;
		// 上次更新的簇点坐标
		Point lastP1 = null;
		Point lastP2 = null;

		// 当开始计算的时候，或是中心点的误差值超过1的时候都需要再次迭代计算
		while (lastP1 == null || errorValue1 > 1.0 || errorValue2 > 1.0) {
			for (Point p : pointArray) {
				computeMemberShip(p);
				p1X += p.getMemberShip1() * p.getMemberShip1() * p.getX();
				p1Y += p.getMemberShip1() * p.getMemberShip1() * p.getY();
				temp1 += p.getMemberShip1() * p.getMemberShip1();

				p2X += p.getMemberShip2() * p.getMemberShip2() * p.getX();
				p2Y += p.getMemberShip2() * p.getMemberShip2() * p.getY();
				temp2 += p.getMemberShip2() * p.getMemberShip2();
			}

			lastP1 = new Point(p1.getX(), p1.getY());
			lastP2 = new Point(p2.getX(), p2.getY());

			// 套公式计算新的簇中心点坐标,最最大化处理
			p1.setX(p1X / temp1);
			p1.setY(p1Y / temp1);
			p2.setX(p2X / temp2);
			p2.setY(p2Y / temp2);

			errorValue1 = Math.abs(lastP1.getX() - p1.getX())
					+ Math.abs(lastP1.getY() - p1.getY());
			errorValue2 = Math.abs(lastP2.getX() - p2.getX())
					+ Math.abs(lastP2.getY() - p2.getY());
		}

		System.out.println(MessageFormat.format(
				"簇中心节点p1({0}, {1}), p2({2}, {3})", p1.getX(), p1.getY(),
				p2.getX(), p2.getY()));
	}

}
