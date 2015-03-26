package DataMining_CABDDCC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * 基于连通图的分裂聚类算法
 * 
 * @author lyq
 * 
 */
public class CABDDCCTool {
	// 测试数据点数据
	private String filePath;
	// 连通图距离阈值l
	private int length;
	// 原始坐标点
	public static ArrayList<Point> totalPoints;
	// 聚类结果坐标点集合
	private ArrayList<ArrayList<Point>> resultClusters;
	// 连通图
	private Graph graph;

	public CABDDCCTool(String filePath, int length) {
		this.filePath = filePath;
		this.length = length;

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
			p = new Point(array[0], array[1], array[2]);
			totalPoints.add(p);
		}

		// 用边和点构造图
		graph = new Graph(null, totalPoints);
	}

	/**
	 * 分裂连通图得到聚类
	 */
	public void splitCluster() {
		// 获取形成连通子图
		ArrayList<Graph> subGraphs;
		ArrayList<ArrayList<Point>> pointList;
		resultClusters = new ArrayList<>();

		subGraphs = graph.splitGraphByLength(length);

		for (Graph g : subGraphs) {
			// 获取每个连通子图分裂后的聚类结果
			pointList = g.getClusterByDivding();
			resultClusters.addAll(pointList);
		}
		
		printResultCluster();
	}

	/**
	 * 输出结果聚簇
	 */
	private void printResultCluster() {
		int i = 1;
		for (ArrayList<Point> cluster : resultClusters) {
			System.out.print("聚簇" + i + ":");
			for (Point p : cluster){
				System.out.print(MessageFormat.format("({0}, {1}) ", p.x, p.y));
			}
			System.out.println();
			i++;
		}
		
	}

}
