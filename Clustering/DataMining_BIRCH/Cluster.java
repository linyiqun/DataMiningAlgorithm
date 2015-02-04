package DataMining_BIRCH;

import java.util.ArrayList;

/**
 * 叶子节点中的小集群
 * @author lyq
 *
 */
public class Cluster extends ClusteringFeature{
	//集群中的数据点
	private ArrayList<double[]> data;
	//父亲节点
	private LeafNode parentNode;
	
	public Cluster(String[] record){
		double[] d = new double[record.length];
		data = new ArrayList<>();
		for(int i=0; i<record.length; i++){
			d[i] = Double.parseDouble(record[i]);
		}
		data.add(d);
		//计算CF聚类特征
		this.setLS(data);
		this.setSS(data);
		this.setN(data);
	}

	public ArrayList<double[]> getData() {
		return data;
	}

	public void setData(ArrayList<double[]> data) {
		this.data = data;
	}

	@Override
	protected void directAddCluster(ClusteringFeature node) {
		//如果是聚类包括数据记录，则还需合并数据记录
		Cluster c = (Cluster)node;
		ArrayList<double[]> dataRecords = c.getData();
		this.data.addAll(dataRecords);
		
		super.directAddCluster(node);
	}

	public LeafNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(LeafNode parentNode) {
		this.parentNode = parentNode;
	}

	@Override
	public void addingCluster(ClusteringFeature clusteringFeature) {
		// TODO Auto-generated method stub
		
	}
}
