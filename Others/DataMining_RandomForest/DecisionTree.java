package DataMining_RandomForest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 决策树
 * 
 * @author lyq
 * 
 */
public class DecisionTree {
	// 树的根节点
	TreeNode rootNode;
	// 数据的属性列名称
	String[] featureNames;
	// 这棵树所包含的数据
	ArrayList<String[]> datas;
	// 决策树构造的的工具类
	CARTTool tool;

	public DecisionTree(ArrayList<String[]> datas) {
		this.datas = datas;
		this.featureNames = datas.get(0);

		tool = new CARTTool(datas);
		// 通过CART工具类进行决策树的构建，并返回树的根节点
		rootNode = tool.startBuildingTree();
	}

	/**
	 * 根据给定的数据特征描述进行类别的判断
	 * 
	 * @param features
	 * @return
	 */
	public String decideClassType(String features) {
		String classType = "";
		// 查询属性组
		String[] queryFeatures;
		// 在本决策树中对应的查询的属性值描述
		ArrayList<String[]> featureStrs;

		featureStrs = new ArrayList<>();
		queryFeatures = features.split(",");

		String[] array;
		for (String name : featureNames) {
			for (String featureValue : queryFeatures) {
				array = featureValue.split("=");
				// 将对应的属性值加入到列表中
				if (array[0].equals(name)) {
					featureStrs.add(array);
				}
			}
		}

		// 开始从根据节点往下递归搜索
		classType = recusiveSearchClassType(rootNode, featureStrs);

		return classType;
	}

	/**
	 * 递归搜索树，查询属性的分类类别
	 * 
	 * @param node
	 *            当前搜索到的节点
	 * @param remainFeatures
	 *            剩余未判断的属性
	 * @return
	 */
	private String recusiveSearchClassType(TreeNode node,
			ArrayList<String[]> remainFeatures) {
		String classType = null;

		// 如果节点包含了数据的id索引，说明已经分类到底了
		if (node.getDataIndex() != null && node.getDataIndex().size() > 0) {
			classType = judgeClassType(node.getDataIndex());

			return classType;
		}

		// 取出剩余属性中的一个匹配属性作为当前的判断属性名称
		String[] currentFeature = null;
		for (String[] featureValue : remainFeatures) {
			if (node.getAttrName().equals(featureValue[0])) {
				currentFeature = featureValue;
				break;
			}
		}

		for (TreeNode childNode : node.getChildAttrNode()) {
			// 寻找子节点中属于此属性值的分支
			if (childNode.getParentAttrValue().equals(currentFeature[1])) {
				remainFeatures.remove(currentFeature);
				classType = recusiveSearchClassType(childNode, remainFeatures);

				// 如果找到了分类结果，则直接挑出循环
				break;
			}else{
				//进行第二种情况的判断加上!符号的情况
				String value = childNode.getParentAttrValue();
				
				if(value.charAt(0) == '!'){
					//去掉第一个！字符
					value = value.substring(1, value.length());
					
					if(!value.equals(currentFeature[1])){
						remainFeatures.remove(currentFeature);
						classType = recusiveSearchClassType(childNode, remainFeatures);

						break;
					}
				}
			}
		}

		return classType;
	}

	/**
	 * 根据得到的数据行分类进行类别的决策
	 * 
	 * @param dataIndex
	 *            根据分类的数据索引号
	 * @return
	 */
	public String judgeClassType(ArrayList<String> dataIndex) {
		// 结果类型值
		String resultClassType = "";
		String classType = "";
		int count = 0;
		int temp = 0;
		Map<String, Integer> type2Num = new HashMap<String, Integer>();

		for (String index : dataIndex) {
			temp = Integer.parseInt(index);
			// 取最后一列的决策类别数据
			classType = datas.get(temp)[featureNames.length - 1];

			if (type2Num.containsKey(classType)) {
				// 如果类别已经存在，则使其计数加1
				count = type2Num.get(classType);
				count++;
			} else {
				count = 1;
			}

			type2Num.put(classType, count);
		}

		// 选出其中类别支持计数最多的一个类别值
		count = -1;
		for (Map.Entry entry : type2Num.entrySet()) {
			if ((int) entry.getValue() > count) {
				count = (int) entry.getValue();
				resultClassType = (String) entry.getKey();
			}
		}

		return resultClassType;
	}
}
