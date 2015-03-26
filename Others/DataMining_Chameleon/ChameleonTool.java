package DataMining_Chameleon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Chameleon 两阶段聚类算法工具类
 * 
 * @author lyq
 * 
 */
public class ChameleonTool {
	// 测试数据点文件地址
	private String filePath;
	// 第一阶段的k近邻的k大小
	private int k;
	// 簇度量函数阈值
	private double minMetric;
	// 总的坐标点的个数
	private int pointNum;
	// 总的连接矩阵的情况,括号表示的是坐标点的id号
	public static int[][] edges;
	// 点与点之间的边的权重
	public static double[][] weights;
	// 原始坐标点数据
	private ArrayList<Point> totalPoints;
	// 第一阶段产生的所有的连通子图作为最初始的聚类
	private ArrayList<Cluster> initClusters;
	// 结果簇结合
	private ArrayList<Cluster> resultClusters;

	public ChameleonTool(String filePath, int k, double minMetric) {
		this.filePath = filePath;
		this.k = k;
		this.minMetric = minMetric;
		
		readDataFile();
	}

	/**
	 * 从文件中读取数据
	 */
	private void readDataFile() {
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
		pointNum = totalPoints.size();
	}

	/**
	 * 递归的合并小聚簇
	 */
	private void combineSubClusters() {
		Cluster cluster = null;

		resultClusters = new ArrayList<>();

		// 当最后的聚簇只剩下一个的时候，则退出循环
		while (initClusters.size() > 1) {
			cluster = initClusters.get(0);
			combineAndRemove(cluster, initClusters);
		}
	}

	/**
	 * 递归的合并聚簇和移除聚簇
	 * 
	 * @param clusterList
	 */
	private ArrayList<Cluster> combineAndRemove(Cluster cluster,
			ArrayList<Cluster> clusterList) {
		ArrayList<Cluster> remainClusters;
		double metric = 0;
		double maxMetric = -Integer.MAX_VALUE;
		Cluster cluster1 = null;
		Cluster cluster2 = null;

		for (Cluster c2 : clusterList) {
			if(cluster.id == c2.id){
				continue;
			}
			
			metric = calMetricfunction(cluster, c2, 1);

			if (metric > maxMetric) {
				maxMetric = metric;
				cluster1 = cluster;
				cluster2 = c2;
			}
		}

		// 如果度量函数值超过阈值，则进行合并,继续搜寻可以合并的簇
		if (maxMetric > minMetric) {
			clusterList.remove(cluster2);
			//将边进行连接
			connectClusterToCluster(cluster1, cluster2);
			// 将簇1和簇2合并
			cluster1.points.addAll(cluster2.points);
			remainClusters = combineAndRemove(cluster1, clusterList);
		} else {
			clusterList.remove(cluster);
			remainClusters = clusterList;
			resultClusters.add(cluster);
		}

		return remainClusters;
	}
	
	/**
	 * 将2个簇进行边的连接
	 * @param c1
	 * 聚簇1
	 * @param c2
	 * 聚簇2
	 */
	private void connectClusterToCluster(Cluster c1, Cluster c2){
		ArrayList<int[]> connectedEdges;
		
		connectedEdges = c1.calNearestEdge(c2, 2);
		
		for(int[] array: connectedEdges){
			edges[array[0]][array[1]] = 1;
			edges[array[1]][array[0]] = 1;
		}
	}

	/**
	 * 算法第一阶段形成局部的连通图
	 */
	private void connectedGraph() {
		double distance = 0;
		Point p1;
		Point p2;

		// 初始化权重矩阵和连接矩阵
		weights = new double[pointNum][pointNum];
		edges = new int[pointNum][pointNum];
		for (int i = 0; i < pointNum; i++) {
			for (int j = 0; j < pointNum; j++) {
				p1 = totalPoints.get(i);
				p2 = totalPoints.get(j);

				distance = p1.ouDistance(p2);
				if (distance == 0) {
					// 如果点为自身的话，则权重设置为0
					weights[i][j] = 0;
				} else {
					// 边的权重采用的值为距离的倒数,距离越近，权重越大
					weights[i][j] = 1.0 / distance;
				}
			}
		}

		double[] tempWeight;
		int[] ids;
		int id1 = 0;
		int id2 = 0;
		// 对每个id坐标点，取其权重前k个最大的点进行相连
		for (int i = 0; i < pointNum; i++) {
			tempWeight = weights[i];
			// 进行排序
			ids = sortWeightArray(tempWeight);
			
			// 取出前k个权重最大的边进行连接
			for (int j = 0; j < ids.length; j++) {
				if (j < k) {
					id1 = i;
					id2 = ids[j];

					edges[id1][id2] = 1;
					edges[id2][id1] = 1;
				}
			}
		}
	}

	/**
	 * 权重的冒泡算法排序
	 * 
	 * @param array
	 *            待排序数组
	 */
	private int[] sortWeightArray(double[] array) {
		double[] copyArray = array.clone();
		int[] ids = null;
		int k = 0;
		double maxWeight = -1;
		
		ids = new int[pointNum];
		for(int i=0; i<pointNum; i++){
			maxWeight = -1;
			
			for(int j=0; j<copyArray.length; j++){
				if(copyArray[j] > maxWeight){
					 maxWeight = copyArray[j];
					 k = j;
				}
			}
			
			ids[i] = k;
			//将当前找到的最大的值重置为-1代表已经找到过了
			copyArray[k] = -1;
		}
		
		return ids;
	}

	/**
	 * 根据边的连通性去深度优先搜索所有的小聚簇
	 */
	private void searchSmallCluster() {
		int currentId = 0;
		Point p;
		Cluster cluster;
		initClusters = new ArrayList<>();
		ArrayList<Point> pointList = null;

		// 以id的方式逐个去dfs搜索
		for (int i = 0; i < pointNum; i++) {
			p = totalPoints.get(i);

			if (p.isVisited) {
				continue;
			}

			pointList = new ArrayList<>();
			pointList.add(p);
			recusiveDfsSearch(p, -1, pointList);
			
			cluster = new Cluster(currentId, pointList);
			initClusters.add(cluster);
			
			currentId++;
		}
	}

	/**
	 * 深度优先的方式找到边所连接着的所有坐标点
	 * 
	 * @param p
	 *            当前搜索的起点
	 * @param lastId
	 *            此点的父坐标点
	 * @param pList
	 *            坐标点列表
	 */
	private void recusiveDfsSearch(Point p, int parentId, ArrayList<Point> pList) {
		int id1 = 0;
		int id2 = 0;
		Point newPoint;

		if (p.isVisited) {
			return;
		}

		p.isVisited = true;
		for (int j = 0; j < pointNum; j++) {
			id1 = p.id;
			id2 = j;

			if (edges[id1][id2] == 1 && id2 != parentId) {
				newPoint = totalPoints.get(j);
				pList.add(newPoint);
				// 以此点为起点，继续递归搜索
				recusiveDfsSearch(newPoint, id1, pList);
			}
		}
	}

	/**
	 * 计算连接2个簇的边的权重
	 * 
	 * @param c1
	 *            聚簇1
	 * @param c2
	 *            聚簇2
	 * @return
	 */
	private double calEC(Cluster c1, Cluster c2) {
		double resultEC = 0;
		ArrayList<int[]> connectedEdges = null;

		connectedEdges = c1.calNearestEdge(c2, 2);

		// 计算连接2部分的边的权重和
		for (int[] array : connectedEdges) {
			resultEC += weights[array[0]][array[1]];
		}

		return resultEC;
	}

	/**
	 * 计算2个簇的相对互连性
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	private double calRI(Cluster c1, Cluster c2) {
		double RI = 0;
		double EC1 = 0;
		double EC2 = 0;
		double EC1To2 = 0;

		EC1 = c1.calEC();
		EC2 = c2.calEC();
		EC1To2 = calEC(c1, c2);

		RI = 2 * EC1To2 / (EC1 + EC2);

		return RI;
	}

	/**
	 * 计算簇的相对近似度
	 * 
	 * @param c1
	 *            簇1
	 * @param c2
	 *            簇2
	 * @return
	 */
	private double calRC(Cluster c1, Cluster c2) {
		double RC = 0;
		double EC1 = 0;
		double EC2 = 0;
		double EC1To2 = 0;
		int pNum1 = c1.points.size();
		int pNum2 = c2.points.size();

		EC1 = c1.calEC();
		EC2 = c2.calEC();
		EC1To2 = calEC(c1, c2);

		RC = EC1To2 * (pNum1 + pNum2) / (pNum2 * EC1 + pNum1 * EC2);

		return RC;
	}

	/**
	 * 计算度量函数的值
	 * 
	 * @param c1
	 *            簇1
	 * @param c2
	 *            簇2
	 * @param alpha
	 *            幂的参数值
	 * @return
	 */
	private double calMetricfunction(Cluster c1, Cluster c2, int alpha) {
		// 度量函数值
		double metricValue = 0;
		double RI = 0;
		double RC = 0;

		RI = calRI(c1, c2);
		RC = calRC(c1, c2);
		// 如果alpha大于1，则更重视相对近似性，如果alpha逍遥于1，注重相对互连性
		metricValue = RI * Math.pow(RC, alpha);

		return metricValue;
	}

	/**
	 * 输出聚簇列
	 * @param clusterList
	 * 输出聚簇列
	 */
	private void printClusters(ArrayList<Cluster> clusterList) {
		int i = 1;

		for (Cluster cluster : clusterList) {
			System.out.print("聚簇" + i + ":");
			for (Point p : cluster.points) {
				System.out.print(MessageFormat.format("({0}, {1}) ", p.x, p.y));
			}
			System.out.println();
			i++;
		}

	}

	/**
	 * 创建聚簇
	 */
	public void buildCluster() {
		// 第一阶段形成小聚簇
		connectedGraph();
		searchSmallCluster();
		System.out.println("第一阶段形成的小簇集合：");
		printClusters(initClusters);
		
		// 第二阶段根据RI和RC的值合并小聚簇形成最终结果聚簇
		combineSubClusters();
		System.out.println("最终的聚簇集合：");
		printClusters(resultClusters);
	}
}
