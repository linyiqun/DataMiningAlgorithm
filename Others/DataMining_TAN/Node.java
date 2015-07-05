package DataMining_TAN;

import java.util.ArrayList;

/**
 * 贝叶斯网络节点类
 * 
 * @author lyq
 * 
 */
public class Node {
	//节点唯一id，方便后面节点连接方向的确定
	int id;
	// 节点的属性名称
	String name;
	// 该节点所连续的节点
	ArrayList<Node> connectedNodes;

	public Node(int id, String name) {
		this.id = id;
		this.name = name;

		// 初始化变量
		this.connectedNodes = new ArrayList<>();
	}

	/**
	 * 将自身节点连接到目标给定的节点
	 * 
	 * @param node
	 *            下游节点
	 */
	public void connectNode(Node node) {
		//避免连接自身
		if(this.id == node.id){
			return;
		}
		
		// 将节点加入自身节点的节点列表中
		this.connectedNodes.add(node);
		// 将自身节点加入到目标节点的列表中
		node.connectedNodes.add(this);
	}

	/**
	 * 判断与目标节点是否相同，主要比较名称是否相同即可
	 * 
	 * @param node
	 *            目标结点
	 * @return
	 */
	public boolean isEqual(Node node) {
		boolean isEqual;

		isEqual = false;
		// 节点名称相同则视为相等
		if (this.id == node.id) {
			isEqual = true;
		}

		return isEqual;
	}
}
