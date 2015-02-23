package DataMining_GSpan;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 图编码深度优先搜索类，判断当前编码在给定图中是否为最小编码
 * 
 * @author lyq
 * 
 */
public class DFSCodeTraveler {
	// 当前的编码是否为最下编码标识
	boolean isMin;
	// 当前挖掘的图的边五元组编码组
	ArrayList<Edge> edgeSeqs;
	// 当前的图结构
	Graph graph;
	// 图节点id对应的边五元组中的id标识
	int[] g2s;
	// 代表图中的边是否被用到了
	boolean f[][];

	public DFSCodeTraveler(ArrayList<Edge> edgeSeqs, Graph graph) {
		this.isMin = true;
		this.edgeSeqs = edgeSeqs;
		this.graph = graph;
	}

	public void traveler() {
		int nodeLNums = graph.nodeLabels.size();
		g2s = new int[nodeLNums];
		for (int i = 0; i < nodeLNums; i++) {
			// 设置-1代表此点还未被计入编码
			g2s[i] = -1;
		}

		f = new boolean[nodeLNums][nodeLNums];
		for (int i = 0; i < nodeLNums; i++) {
			for (int j = 0; j < nodeLNums; j++) {
				f[i][j] = false;
			}
		}

		// 从每个点开始寻找最小编码五元组
		for (int i = 0; i < nodeLNums; i++) {
			//对选择的第一个点的标号做判断
			if(graph.getNodeLabels().get(i) > edgeSeqs.get(0).x){
				continue;
			}
			// 五元组id从0开始设置
			g2s[i] = 0;

			Stack<Integer> s = new Stack<>();
			s.push(i);
			dfsSearch(s, 0, 1);
			if (!isMin) {
				return;
			}
			g2s[i] = -1;
		}
	}

	/**
	 * 深度优先搜索最小编码组
	 * 
	 * @param stack
	 *            加入的节点id栈
	 * @param currentPosition
	 *            当前进行的层次，代表找到的第几条边
	 * @param next
	 *            五元组边下一条边的点的临时标识
	 */
	private void dfsSearch(Stack<Integer> stack, int currentPosition, int next) {
		if (currentPosition >= edgeSeqs.size()) {
			stack.pop();
			// 比较到底了则返回
			return;
		}

		while (!stack.isEmpty()) {
			int x = stack.pop();
			for (int i = 0; i < graph.edgeNexts.get(x).size(); i++) {
				// 从此id节点所连接的点中选取1个点作为下一个点
				int y = graph.edgeNexts.get(x).get(i);
				// 如果这2个点所构成的边已经被用过，则继续
				if (f[x][y] || f[y][x]) {
					continue;
				}

				// 如果y这个点未被用过
				if (g2s[y] < 0) {
					// 新建这条边五元组
					Edge e = new Edge(g2s[x], next, graph.nodeLabels.get(x),
							graph.edgeLabels.get(x).get(i),
							graph.nodeLabels.get(y));

					// 与相应位置的边做比较，如果不是最小则失败
					int compareResult = e.compareWith(edgeSeqs
							.get(currentPosition));
					if (compareResult == Edge.EDGE_SMALLER) {
						isMin = false;
						return;
					} else if (compareResult == Edge.EDGE_LARGER) {
						continue;
					}
					// 如果相等则继续比
					g2s[y] = next;
					f[x][y] = true;
					f[y][x] = true;
					stack.push(y);
					dfsSearch(stack, currentPosition + 1, next + 1);
					if (!isMin) {
						return;
					}
					f[x][y] = false;
					f[y][x] = false;
					g2s[y] = -1;
				} else {
					// 这个点已经被用过的时候，不需要再设置五元组id标识
					// 新建这条边五元组
					Edge e = new Edge(g2s[x], g2s[y], graph.nodeLabels.get(x),
							graph.edgeLabels.get(x).get(i),
							graph.nodeLabels.get(y));

					// 与相应位置的边做比较，如果不是最小则失败
					int compareResult = e.compareWith(edgeSeqs
							.get(currentPosition));
					if (compareResult == Edge.EDGE_SMALLER) {
						isMin = false;
						return;
					} else if (compareResult == Edge.EDGE_LARGER) {
						continue;
					}
					// 如果相等则继续比
					g2s[y] = next;
					f[x][y] = true;
					f[y][x] = true;
					stack.push(y);
					dfsSearch(stack, currentPosition + 1, next);
					if (!isMin) {
						return;
					}
					f[x][y] = false;
					f[y][x] = false;
				}
			}
		}
	}
}
