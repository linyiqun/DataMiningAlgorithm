package DataMining_GSpan;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 孩子图搜寻类，在当前边的基础上寻找可能的孩子边
 * 
 * @author lyq
 * 
 */
public class SubChildTraveler {
	// 当前的五元组边
	ArrayList<Edge> edgeSeq;
	// 当前的图
	Graph graph;
	// 结果数据，孩子边对所属的图id组
	ArrayList<Edge> childEdge;
	// 图的点id对五元组id标识的映射
	int[] g2s;
	// 五元组id标识对图的点id的映射
	int[] s2g;
	// 图中边是否被用的情况
	boolean f[][];
	// 最右路径，rm[id]表示的是此id节点在最右路径中的下一个节点id
	int[] rm;
	// 下一个五元组的id
	int next;

	public SubChildTraveler(ArrayList<Edge> edgeSeq, Graph graph) {
		this.edgeSeq = edgeSeq;
		this.graph = graph;
		this.childEdge = new ArrayList<>();
	}

	/**
	 * 在图中搜索可能存在的孩子边
	 * 
	 * @param next
	 *            新加入边的节点将设置的id
	 */
	public void traveler() {
		this.next = edgeSeq.size() + 1;
		int size = graph.nodeLabels.size();
		// 做id映射的初始化操作
		g2s = new int[size];
		s2g = new int[size];
		f = new boolean[size][size];

		for (int i = 0; i < size; i++) {
			g2s[i] = -1;
			s2g[i] = -1;

			for (int j = 0; j < size; j++) {
				// 代表点id为i到id为j点此边没有被用过
				f[i][j] = false;
			}
		}

		rm = new int[edgeSeq.size()+1];
		for (int i = 0; i < edgeSeq.size()+1; i++) {
			rm[i] = -1;
		}
		// 寻找最右路径
		for (Edge e : edgeSeq) {
			if (e.ix < e.iy && e.iy > rm[e.ix]) {
				rm[e.ix] = e.iy;
			}
		}

		for (int i = 0; i < size; i++) {
			// 寻找第一个标号相等的点
			if (edgeSeq.get(0).x != graph.nodeLabels.get(i)) {
				continue;
			}

			g2s[i] = 0;
			s2g[0] = i;
			dfsSearchEdge(0);
			g2s[i] = -1;
			s2g[0] = -1;
		}

	}

	/**
	 * 在当前图中深度优先寻找正确的子图
	 * 
	 * @param currentPosition
	 *            当前找到的位置
	 */
	public void dfsSearchEdge(int currentPosition) {
		int rmPosition = 0;
		// 如果找到底了，则在当前的子图的最右路径中寻找可能的边
		if (currentPosition >= edgeSeq.size()) {
			rmPosition = 0;
			while (rmPosition >= 0) {
				int gId = s2g[rmPosition];
				// 在此点附近寻找可能的边
				for (int i = 0; i < graph.edgeNexts.get(gId).size(); i++) {
					int gId2 = graph.edgeNexts.get(gId).get(i);
					// 如果这条边已经被用过
					if (f[gId][gId2] || f[gId][gId2]) {
						continue;
					}

					// 在最右路径中添加边分为2种情况，第一种为在最右节点上添加，第二中为在最右路径上 的点添加
					// 如果找到的点没有被用过，可以进行边的拓展
					if (g2s[gId2] < 0) {
						g2s[gId2] = next;
						Edge e = new Edge(g2s[gId], g2s[gId2],
								graph.nodeLabels.get(gId), graph.edgeLabels
										.get(gId).get(i),
								graph.nodeLabels.get(gId2));
						// 将新建的子边加入集合
						childEdge.add(e);
					} else {
						boolean flag = true;
						// 如果这点已经存在，判断他是不是最右的点
						for (int j = 0; j < graph.edgeNexts.get(gId2).size(); j++) {
							int tempId = graph.edgeNexts.get(gId2).get(j);
							if (g2s[gId2] < g2s[tempId]) {
								flag = false;
								break;
							}
						}

						if (flag) {
							Edge e = new Edge(g2s[gId], g2s[gId2],
									graph.nodeLabels.get(gId), graph.edgeLabels
											.get(gId).get(i),
									graph.nodeLabels.get(gId2));
							// 将新建的子边加入集合
							childEdge.add(e);
						}
					}
				}
				// 一个最右路径上点找完，继续下一个
				rmPosition = rm[rmPosition];
			}
			return;
		}

		Edge e = edgeSeq.get(currentPosition);
		// 所连接的点标号
		int y = e.y;
		// 所连接的边标号
		int a = e.a;
		int gId1 = s2g[e.ix];
		int gId2 = 0;

		for (int i = 0; i < graph.edgeLabels.get(gId1).size(); i++) {
			// 判断所连接的边对应的标号
			if (graph.edgeLabels.get(gId1).get(i) != a) {
				continue;
			}

			// 判断所连接的点的标号
			int tempId = graph.edgeNexts.get(gId1).get(i);
			if (graph.nodeLabels.get(tempId) != y) {
				continue;
			}

			gId2 = tempId;
			// 如果这两点是没有设置过的
			if (g2s[gId2] == -1 && s2g[e.iy] == -1) {
				g2s[gId2] = e.iy;
				s2g[e.iy] = gId2;
				f[gId1][gId2] = true;
				f[gId2][gId1] = true;
				dfsSearchEdge(currentPosition + 1);
				f[gId1][gId2] = false;
				f[gId2][gId1] = false;
				g2s[gId2] = -1;
				s2g[e.iy] = -1;
			} else {
				if (g2s[gId2] != e.iy) {
					continue;
				}
				if (s2g[e.iy] != gId2) {
					continue;
				}
				f[gId1][gId2] = true;
				f[gId2][gId1] = true;
				dfsSearchEdge(currentPosition);
				f[gId1][gId2] = false;
				f[gId2][gId1] = false;
			}
		}

	}

	/**
	 * 获取结果数据对
	 * 
	 * @return
	 */
	public ArrayList<Edge> getResultChildEdge() {
		return this.childEdge;
	}

}
