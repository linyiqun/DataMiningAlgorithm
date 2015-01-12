package DataMining_CART;

import java.util.ArrayList;

/**
 * 回归分类树节点
 * 
 * @author lyq
 * 
 */
public class AttrNode {
	// 节点属性名字
	private String attrName;
	// 节点索引标号
	private int nodeIndex;
	//包含的叶子节点数
	private int leafNum;
	// 节点误差率
	private double alpha;
	// 父亲分类属性值
	private String parentAttrValue;
	// 孩子节点
	private AttrNode[] childAttrNode;
	// 数据记录索引
	private ArrayList<String> dataIndex;

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public int getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(int nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public String getParentAttrValue() {
		return parentAttrValue;
	}

	public void setParentAttrValue(String parentAttrValue) {
		this.parentAttrValue = parentAttrValue;
	}

	public AttrNode[] getChildAttrNode() {
		return childAttrNode;
	}

	public void setChildAttrNode(AttrNode[] childAttrNode) {
		this.childAttrNode = childAttrNode;
	}

	public ArrayList<String> getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(ArrayList<String> dataIndex) {
		this.dataIndex = dataIndex;
	}

	public int getLeafNum() {
		return leafNum;
	}

	public void setLeafNum(int leafNum) {
		this.leafNum = leafNum;
	}
	
	
	
}
