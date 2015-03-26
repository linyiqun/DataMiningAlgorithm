package DataMining_CABDDCC;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 连通图类
 * 
 * @author lyq
 * 
 */
public class Graph {
	// 坐标点之间的连接属性，括号内为坐标id号
	int[][] edges;
	// 连通图内的坐标点数
	ArrayList<Point> points;
	// 此图下分割后的聚类子图
	ArrayList<ArrayList<Point>> clusters;

	public Graph(int[][] edges) {
		this.edges = edges;
		this.points = getPointByEdges(edges);
	}

	public Graph(int[][] edges, ArrayList<Point> points) {
		this.edges = edges;
		this.points = points;
	}

	public int[][] getEdges() {
		return edges;
	}

	public void setEdges(int[][] edges) {
		this.edges = edges;
	}

	public ArrayList<Point> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	/**
	 * 根据距离阈值做连通图的划分,构成连通图集
	 * 
	 * @param length
	 *            距离阈值
	 * @return
	 */
	public ArrayList<Graph> splitGraphByLength(int length) {
		int[][] edges;
		Graph tempGraph;
		ArrayList<Graph> graphs = new ArrayList<>();

		for (Point p : points) {
			if (!p.isVisited) {
				// 括号中的下标为id号
				edges = new int[points.size()][points.size()];
				dfsExpand(p, length, edges);

				tempGraph = new Graph(edges);
				graphs.add(tempGraph);
			} else {
				continue;
			}
		}

		return graphs;
	}

	/**
	 * 深度优先方式扩展连通图
	 * 
	 * @param points
	 *            需要继续深搜的坐标点
	 * @param length
	 *            距离阈值
	 * @param edges
	 *            边数组
	 */
	private void dfsExpand(Point point, int length, int edges[][]) {
		int id1 = 0;
		int id2 = 0;
		double distance = 0;
		ArrayList<Point> tempPoints;

		// 如果处理过了，则跳过
		if (point.isVisited) {
			return;
		}

		id1 = point.id;
		point.isVisited = true;
		tempPoints = new ArrayList<>();
		for (Point p2 : points) {
			id2 = p2.id;

			if (id1 == id2) {
				continue;
			} else {
				distance = point.ouDistance(p2);
				if (distance <= length) {
					edges[id1][id2] = 1;
					edges[id2][id1] = 1;

					tempPoints.add(p2);
				}
			}
		}

		// 继续递归
		for (Point p : tempPoints) {
			dfsExpand(p, length, edges);
		}
	}

	/**
	 * 判断连通图是否还需要再被划分
	 * 
	 * @param pointList1
	 *            坐标点集合1
	 * @param pointList2
	 *            坐标点集合2
	 * @return
	 */
	private boolean needDivided(ArrayList<Point> pointList1,
			ArrayList<Point> pointList2) {
		boolean needDivided = false;
		// 承受系数t=轻的集合的坐标点数/2部分连接的边数
		double t = 0;
		// 分裂阈值，即平均每边所要承受的重量
		double landa = 0;
		int pointNum1 = pointList1.size();
		int pointNum2 = pointList2.size();
		// 总边数
		int totalEdgeNum = 0;
		// 连接2部分的边数量
		int connectedEdgeNum = 0;
		ArrayList<Point> totalPoints = new ArrayList<>();

		totalPoints.addAll(pointList1);
		totalPoints.addAll(pointList2);
		int id1 = 0;
		int id2 = 0;
		for (Point p1 : totalPoints) {
			id1 = p1.id;
			for (Point p2 : totalPoints) {
				id2 = p2.id;

				if (edges[id1][id2] == 1 && id1 < id2) {
					if ((pointList1.contains(p1) && pointList2.contains(p2))
							|| (pointList1.contains(p2) && pointList2
									.contains(p1))) {
						connectedEdgeNum++;
					}
					totalEdgeNum++;
				}
			}
		}

		if (pointNum1 < pointNum2) {
			// 承受系数t=轻的集合的坐标点数/连接2部分的边数
			t = 1.0 * pointNum1 / connectedEdgeNum;
		} else {
			t = 1.0 * pointNum2 / connectedEdgeNum;
		}

		// 计算分裂阈值,括号内为总边数/总点数，就是平均每边所承受的点数量
		landa = 0.5 * Math.exp((1.0 * totalEdgeNum / (pointNum1 + pointNum2)));

		// 如果承受系数不小于分裂阈值，则代表需要分裂
		if (t >= landa) {
			needDivided = true;
		}

		return needDivided;
	}

	/**
	 * 递归的划分连通图
	 * 
	 * @param pointList
	 *            待划分的连通图的所有坐标点
	 */
	public void divideGraph(ArrayList<Point> pointList) {
		// 判断此坐标点集合是否能够被分割
		boolean canDivide = false;
		ArrayList<ArrayList<Point>> pointGroup;
		ArrayList<Point> pointList1 = new ArrayList<>();
		ArrayList<Point> pointList2 = new ArrayList<>();

		for (int m = 2; m <= pointList.size() / 2; m++) {
			// 进行坐标点的分割
			pointGroup = removePoint(pointList, m);
			pointList1 = pointGroup.get(0);
			pointList2 = pointGroup.get(1);

			// 判断是否满足分裂条件
			if (needDivided(pointList1, pointList2)) {
				canDivide = true;
				divideGraph(pointList1);
				divideGraph(pointList2);
			}
		}

		// 如果所有的分割组合都无法分割，则说明此已经是一个聚类
		if (!canDivide) {
			clusters.add(pointList);
		}
	}

	/**
	 * 获取分裂得到的聚类结果
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Point>> getClusterByDivding() {
		clusters = new ArrayList<>();
		
		divideGraph(points);

		return clusters;
	}

	/**
	 * 将当前坐标点集合移除removeNum个点，构成2个子坐标点集合
	 * 
	 * @param pointList
	 *            原集合点
	 * @param removeNum
	 *            移除的数量
	 */
	private ArrayList<ArrayList<Point>> removePoint(ArrayList<Point> pointList,
			int removeNum) {
		//浅拷贝一份原坐标点数据
		ArrayList<Point> copyPointList = (ArrayList<Point>) pointList.clone();
		ArrayList<ArrayList<Point>> pointGroup = new ArrayList<>();
		ArrayList<Point> pointList2 = new ArrayList<>();
		// 进行按照坐标轴大小排序
		Collections.sort(copyPointList);

		for (int i = 0; i < removeNum; i++) {
			pointList2.add(copyPointList.get(i));
		}
		copyPointList.removeAll(pointList2);

		pointGroup.add(copyPointList);
		pointGroup.add(pointList2);

		return pointGroup;
	}

	/**
	 * 根据边的情况获取其中的点
	 * 
	 * @param edges
	 *            当前的已知的边的情况
	 * @return
	 */
	private ArrayList<Point> getPointByEdges(int[][] edges) {
		Point p1;
		Point p2;
		ArrayList<Point> pointList = new ArrayList<>();

		for (int i = 0; i < edges.length; i++) {
			for (int j = 0; j < edges[0].length; j++) {
				if (edges[i][j] == 1) {
					p1 = CABDDCCTool.totalPoints.get(i);
					p2 = CABDDCCTool.totalPoints.get(j);

					if (!pointList.contains(p1)) {
						pointList.add(p1);
					}

					if (!pointList.contains(p2)) {
						pointList.add(p2);
					}
				}
			}
		}

		return pointList;
	}
}
