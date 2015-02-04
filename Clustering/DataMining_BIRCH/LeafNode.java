package DataMining_BIRCH;

import java.util.ArrayList;

/**
 * CF树叶子节点
 * 
 * @author lyq
 * 
 */
public class LeafNode extends ClusteringFeature {
	// 孩子集群
	private ArrayList<Cluster> clusterChilds;
	// 父亲节点
	private NonLeafNode parentNode;

	public ArrayList<Cluster> getClusterChilds() {
		return clusterChilds;
	}

	public void setClusterChilds(ArrayList<Cluster> clusterChilds) {
		this.clusterChilds = clusterChilds;
	}

	/**
	 * 将叶子节点划分出2个
	 * 
	 * @return
	 */
	public LeafNode[] divideLeafNode() {
		LeafNode[] leafNodeArray = new LeafNode[2];
		// 簇间距离差距最大的2个簇，后面的簇按照就近原则划分即可
		Cluster cluster1 = null;
		Cluster cluster2 = null;
		Cluster tempCluster = null;
		double maxValue = 0;
		double temp = 0;

		// 找出簇心距离差距最大的2个簇
		for (int i = 0; i < clusterChilds.size() - 1; i++) {
			tempCluster = clusterChilds.get(i);
			for (int j = i + 1; j < clusterChilds.size(); j++) {
				temp = tempCluster
						.computerClusterDistance(clusterChilds.get(j));

				if (temp > maxValue) {
					maxValue = temp;
					cluster1 = tempCluster;
					cluster2 = clusterChilds.get(j);
				}
			}
		}

		leafNodeArray[0] = new LeafNode();
		leafNodeArray[0].addingCluster(cluster1);
		cluster1.setParentNode(leafNodeArray[0]);
		leafNodeArray[1] = new LeafNode();
		leafNodeArray[1].addingCluster(cluster2);
		cluster2.setParentNode(leafNodeArray[1]);
		clusterChilds.remove(cluster1);
		clusterChilds.remove(cluster2);
		// 就近分配簇
		for (Cluster c : clusterChilds) {
			if (cluster1.computerClusterDistance(c) < cluster2
					.computerClusterDistance(c)) {
				// 簇间距离如果接近最小簇，就加入最小簇所属叶子节点
				leafNodeArray[0].addingCluster(c);
				c.setParentNode(leafNodeArray[0]);
			} else {
				leafNodeArray[1].addingCluster(c);
				c.setParentNode(leafNodeArray[1]);
			}
		}

		return leafNodeArray;
	}

	public NonLeafNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(NonLeafNode parentNode) {
		this.parentNode = parentNode;
	}

	@Override
	public void addingCluster(ClusteringFeature clusteringFeature) {
		//更新聚类特征值
		directAddCluster(clusteringFeature);
		
		// 寻找到的目标集群
		Cluster findedCluster = null;
		Cluster cluster = (Cluster) clusteringFeature;
		// 簇内对象平均距离
		double disance = Integer.MAX_VALUE;
		// 簇间距离差值
		double errorDistance = 0;
		boolean needDivided = false;
		if (clusterChilds == null) {
			clusterChilds = new ArrayList<>();
			clusterChilds.add(cluster);
			cluster.setParentNode(this);
		} else {
			for (Cluster c : clusterChilds) {
				errorDistance = c.computerClusterDistance(cluster);
				if (disance > errorDistance) {
					// 选出簇间距离最近的
					disance = errorDistance;
					findedCluster = c;
				}
			}

			ArrayList<double[]> data1 = (ArrayList<double[]>) findedCluster
					.getData().clone();
			ArrayList<double[]> data2 = cluster.getData();
			data1.addAll(data2);
			// 如果添加后的聚类的簇间距离超过给定阈值，需要额外新建簇
			if (findedCluster.computerInClusterDistance(data1) > BIRCHTool.T) {
				// 叶子节点的孩子数不能超过平衡因子L
				if (clusterChilds.size() + 1 > BIRCHTool.L) {
					needDivided = true;
				}
				clusterChilds.add(cluster);
				cluster.setParentNode(this);
			} else {
				findedCluster.directAddCluster(cluster);
				cluster.setParentNode(this);
			}
		}
		
		if(needDivided){
			if(parentNode == null){
				parentNode = new NonLeafNode();
			}else{
				parentNode.getLeafChilds().remove(this);
			}
			
			LeafNode[] nodeArray = divideLeafNode();
			for(LeafNode n: nodeArray){
				parentNode.addingCluster(n);
			}
		}
	}

	@Override
	protected void directAddCluster(ClusteringFeature node) {
		// TODO Auto-generated method stub
		if(parentNode != null){
			parentNode.directAddCluster(node);
		}
		
		super.directAddCluster(node);
	}
	
}
