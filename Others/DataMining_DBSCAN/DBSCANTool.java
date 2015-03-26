package DataMining_DBSCAN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * DBSCAN基于密度聚类算法工具类
 * 
 * @author lyq
 * 
 */
public class DBSCANTool {
	// 测试数据文件地址
	private String filePath;
	// 簇扫描半径
	private double eps;
	// 最小包含点数阈值
	private int minPts;
	// 所有的数据坐标点
	private ArrayList<Point> totalPoints;
	// 聚簇结果
	private ArrayList<ArrayList<Point>> resultClusters;
	//噪声数据
	private ArrayList<Point> noisePoint;

	public DBSCANTool(String filePath, double eps, int minPts) {
		this.filePath = filePath;
		this.eps = eps;
		this.minPts = minPts;
		readDataFile();
	}

	/**
	 * 从文件中读取数据
	 */
	public void readDataFile() {
		File file = new File(filePath);
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

		Point p;
		totalPoints = new ArrayList<>();
		for (String[] array : dataArray) {
			p = new Point(array[0], array[1]);
			totalPoints.add(p);
		}
	}

	/**
	 * 递归的寻找聚簇
	 * 
	 * @param pointList
	 *            当前的点列表
	 * @param parentCluster
	 *            父聚簇
	 */
	private void recursiveCluster(Point point, ArrayList<Point> parentCluster) {
		double distance = 0;
		ArrayList<Point> cluster;

		// 如果已经访问过了，则跳过
		if (point.isVisited) {
			return;
		}

		point.isVisited = true;
		cluster = new ArrayList<>();
		for (Point p2 : totalPoints) {
			// 过滤掉自身的坐标点
			if (point.isTheSame(p2)) {
				continue;
			}

			distance = point.ouDistance(p2);
			if (distance <= eps) {
				// 如果聚类小于给定的半径，则加入簇中
				cluster.add(p2);
			}
		}

		if (cluster.size() >= minPts) {
			// 将自己也加入到聚簇中
			cluster.add(point);
			// 如果附近的节点个数超过最下值，则加入到父聚簇中,同时去除重复的点
			addCluster(parentCluster, cluster);

			for (Point p : cluster) {
				recursiveCluster(p, parentCluster);
			}
		}
	}

	/**
	 * 往父聚簇中添加局部簇坐标点
	 * 
	 * @param parentCluster
	 *            原始父聚簇坐标点
	 * @param cluster
	 *            待合并的聚簇
	 */
	private void addCluster(ArrayList<Point> parentCluster,
			ArrayList<Point> cluster) {
		boolean isCotained = false;
		ArrayList<Point> addPoints = new ArrayList<>();

		for (Point p : cluster) {
			isCotained = false;
			for (Point p2 : parentCluster) {
				if (p.isTheSame(p2)) {
					isCotained = true;
					break;
				}
			}

			if (!isCotained) {
				addPoints.add(p);
			}
		}

		parentCluster.addAll(addPoints);
	}

	/**
	 * dbScan算法基于密度的聚类
	 */
	public void dbScanCluster() {
		ArrayList<Point> cluster = null;
		resultClusters = new ArrayList<>();
		noisePoint = new ArrayList<>();
		
		for (Point p : totalPoints) {
			if(p.isVisited){
				continue;
			}
			
			cluster = new ArrayList<>();
			recursiveCluster(p, cluster);

			if (cluster.size() > 0) {
				resultClusters.add(cluster);
			}else{
				noisePoint.add(p);
			}
		}
		removeFalseNoise();
		
		printClusters();
	}
	
	/**
	 * 移除被错误分类的噪声点数据
	 */
	private void removeFalseNoise(){
		ArrayList<Point> totalCluster = new ArrayList<>();
		ArrayList<Point> deletePoints = new ArrayList<>();
		
		//将聚簇合并
		for(ArrayList<Point> list: resultClusters){
			totalCluster.addAll(list);
		} 
		
		for(Point p: noisePoint){
			for(Point p2: totalCluster){
				if(p2.isTheSame(p)){
					deletePoints.add(p);
				}
			}
		}
		
		noisePoint.removeAll(deletePoints);
	}

	/**
	 * 输出聚类结果
	 */
	private void printClusters() {
		int i = 1;
		for (ArrayList<Point> pList : resultClusters) {
			System.out.print("聚簇" + (i++) + ":");
			for (Point p : pList) {
				System.out.print(MessageFormat.format("({0},{1}) ", p.x, p.y));
			}
			System.out.println();
		}
		
		System.out.println();
		System.out.print("噪声数据:");
		for (Point p : noisePoint) {
			System.out.print(MessageFormat.format("({0},{1}) ", p.x, p.y));
		}
		System.out.println();
	}
}
