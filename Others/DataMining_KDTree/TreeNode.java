package DataMining_KDTree;

/**
 * KD树节点
 * @author lyq
 *
 */
public class TreeNode {
	//数据矢量
	Point nodeData;
	//分割平面的分割线
	int spilt;
	//空间矢量，该节点所表示的空间范围
	Range range;
	//父节点
	TreeNode parentNode;
	//位于分割超平面左侧的孩子节点
	TreeNode leftNode;
	//位于分割超平面右侧的孩子节点
	TreeNode rightNode;
	//节点是否被访问过,用于回溯时使用
	boolean isVisited;
	
	public TreeNode(){
		this.isVisited = false;
	}
}
