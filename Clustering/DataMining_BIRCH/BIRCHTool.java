package DataMining_BIRCH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * BIRCH聚类算法工具类
 * 
 * @author lyq
 * 
 */
public class BIRCHTool {
	// 节点类型名称
	public static final String NON_LEAFNODE = "【NonLeafNode】";
	public static final String LEAFNODE = "【LeafNode】";
	public static final String CLUSTER = "【Cluster】";

	// 测试数据文件地址
	private String filePath;
	// 内部节点平衡因子B
	public static int B;
	// 叶子节点平衡因子L
	public static int L;
	// 簇直径阈值T
	public static double T;
	// 总的测试数据记录
	private ArrayList<String[]> totalDataRecords;

	public BIRCHTool(String filePath, int B, int L, double T) {
		this.filePath = filePath;
		this.B = B;
		this.L = L;
		this.T = T;
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
				tempArray = str.split("     ");
				dataArray.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		totalDataRecords = new ArrayList<>();
		for (String[] array : dataArray) {
			totalDataRecords.add(array);
		}
	}

	/**
	 * 构建CF聚类特征树
	 * 
	 * @return
	 */
	private ClusteringFeature buildCFTree() {
		NonLeafNode rootNode = null;
		LeafNode leafNode = null;
		Cluster cluster = null;

		for (String[] record : totalDataRecords) {
			cluster = new Cluster(record);

			if (rootNode == null) {
				// CF树只有1个节点的时候的情况
				if (leafNode == null) {
					leafNode = new LeafNode();
				}
				leafNode.addingCluster(cluster);
				if (leafNode.getParentNode() != null) {
					rootNode = leafNode.getParentNode();
				}
			} else {
				if (rootNode.getParentNode() != null) {
					rootNode = rootNode.getParentNode();
				}

				// 从根节点开始，从上往下寻找到最近的添加目标叶子节点
				LeafNode temp = rootNode.findedClosestNode(cluster);
				temp.addingCluster(cluster);
			}
		}

		// 从下往上找出最上面的节点
		LeafNode node = cluster.getParentNode();
		NonLeafNode upNode = node.getParentNode();
		if (upNode == null) {
			return node;
		} else {
			while (upNode.getParentNode() != null) {
				upNode = upNode.getParentNode();
			}

			return upNode;
		}
	}

	/**
	 * 开始构建CF聚类特征树
	 */
	public void startBuilding() {
		// 树深度
		int level = 1;
		ClusteringFeature rootNode = buildCFTree();

		setTreeLevel(rootNode, level);
		showCFTree(rootNode);
	}

	/**
	 * 设置节点深度
	 * 
	 * @param clusteringFeature
	 *            当前节点
	 * @param level
	 *            当前深度值
	 */
	private void setTreeLevel(ClusteringFeature clusteringFeature, int level) {
		LeafNode leafNode = null;
		NonLeafNode nonLeafNode = null;

		if (clusteringFeature instanceof LeafNode) {
			leafNode = (LeafNode) clusteringFeature;
		} else if (clusteringFeature instanceof NonLeafNode) {
			nonLeafNode = (NonLeafNode) clusteringFeature;
		}

		if (nonLeafNode != null) {
			nonLeafNode.setLevel(level);
			level++;
			// 设置子节点
			if (nonLeafNode.getNonLeafChilds() != null) {
				for (NonLeafNode n1 : nonLeafNode.getNonLeafChilds()) {
					setTreeLevel(n1, level);
				}
			} else {
				for (LeafNode n2 : nonLeafNode.getLeafChilds()) {
					setTreeLevel(n2, level);
				}
			}
		} else {
			leafNode.setLevel(level);
			level++;
			// 设置子聚簇
			for (Cluster c : leafNode.getClusterChilds()) {
				c.setLevel(level);
			}
		}
	}

	/**
	 * 显示CF聚类特征树
	 * 
	 * @param rootNode
	 *            CF树根节点
	 */
	private void showCFTree(ClusteringFeature rootNode) {
		// 空格数，用于输出
		int blankNum = 5;
		// 当前树深度
		int currentLevel = 1;
		LinkedList<ClusteringFeature> nodeQueue = new LinkedList<>();
		ClusteringFeature cf;
		LeafNode leafNode;
		NonLeafNode nonLeafNode;
		ArrayList<Cluster> clusterList = new ArrayList<>();
		String typeName;

		nodeQueue.add(rootNode);
		while (nodeQueue.size() > 0) {
			cf = nodeQueue.poll();

			if (cf instanceof LeafNode) {
				leafNode = (LeafNode) cf;
				typeName = LEAFNODE;

				if (leafNode.getClusterChilds() != null) {
					for (Cluster c : leafNode.getClusterChilds()) {
						nodeQueue.add(c);
					}
				}
			} else if (cf instanceof NonLeafNode) {
				nonLeafNode = (NonLeafNode) cf;
				typeName = NON_LEAFNODE;

				if (nonLeafNode.getNonLeafChilds() != null) {
					for (NonLeafNode n1 : nonLeafNode.getNonLeafChilds()) {
						nodeQueue.add(n1);
					}
				} else {
					for (LeafNode n2 : nonLeafNode.getLeafChilds()) {
						nodeQueue.add(n2);
					}
				}
			} else {
				clusterList.add((Cluster)cf);
				typeName = CLUSTER;
			}

			if (currentLevel != cf.getLevel()) {
				currentLevel = cf.getLevel();
				System.out.println();
				System.out.println("|");
				System.out.println("|");
			}else if(currentLevel == cf.getLevel() && currentLevel != 1){
				for (int i = 0; i < blankNum; i++) {
					System.out.print("-");
				}
			}
			
			System.out.print(typeName);
			System.out.print("N:" + cf.getN() + ", LS:");
			System.out.print("[");
			for (double d : cf.getLS()) {
				System.out.print(MessageFormat.format("{0}, ",  d));
			}
			System.out.print("]");
		}
		
		System.out.println();
		System.out.println("*******最终分好的聚簇****");
		//显示已经分好类的聚簇点
		for(int i=0; i<clusterList.size(); i++){
			System.out.println("Cluster" + (i+1) + "：");
			for(double[] point: clusterList.get(i).getData()){
				System.out.print("[");
				for (double d : point) {
					System.out.print(MessageFormat.format("{0}, ",  d));
				}
				System.out.println("]");
			}
		}
	}

}
