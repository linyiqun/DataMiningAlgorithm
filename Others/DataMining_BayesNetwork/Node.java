package DataMining_BayesNetwork;

import java.util.ArrayList;

/**
 * 贝叶斯网络节点类
 * 
 * @author lyq
 * 
 */
public class Node {
	// 节点的属性名称
	String name;
	// 节点的父亲节点，也就是上游节点，可能多个
	ArrayList<Node> parentNodes;
	// 节点的子节点，也就是下游节点，可能多个
	ArrayList<Node> childNodes;

	public Node(String name) {
		this.name = name;

		// 初始化变量
		this.parentNodes = new ArrayList<>();
		this.childNodes = new ArrayList<>();
	}

	/**
	 * 将自身节点连接到目标给定的节点
	 * 
	 * @param node
	 *            下游节点
	 */
	public void connectNode(Node node) {
		// 将下游节点加入自身节点的孩子节点中
		this.childNodes.add(node);
		// 将自身节点加入到下游节点的父节点中
		node.parentNodes.add(this);
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
		if (this.name.equals(node.name)) {
			isEqual = true;
		}

		return isEqual;
	}
}
