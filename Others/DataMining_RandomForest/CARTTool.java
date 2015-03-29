package DataMining_RandomForest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * CART分类回归树算法工具类
 * 
 * @author lyq
 * 
 */
public class CARTTool {
	// 类标号的值类型
	private final String YES = "Yes";
	private final String NO = "No";

	// 所有属性的类型总数,在这里就是data源数据的列数
	private int attrNum;
	private String filePath;
	// 初始源数据，用一个二维字符数组存放模仿表格数据
	private String[][] data;
	// 数据的属性行的名字
	private String[] attrNames;
	// 每个属性的值所有类型
	private HashMap<String, ArrayList<String>> attrValue;

	public CARTTool(ArrayList<String[]> dataArray) {
		attrValue = new HashMap<>();
		readData(dataArray);
	}

	/**
	 * 根据随机选取的样本数据进行初始化
	 * @param dataArray
	 * 已经读入的样本数据
	 */
	public void readData(ArrayList<String[]> dataArray) {
		data = new String[dataArray.size()][];
		dataArray.toArray(data);
		attrNum = data[0].length;
		attrNames = data[0];
	}

	/**
	 * 首先初始化每种属性的值的所有类型，用于后面的子类熵的计算时用
	 */
	public void initAttrValue() {
		ArrayList<String> tempValues;

		// 按照列的方式，从左往右找
		for (int j = 1; j < attrNum; j++) {
			// 从一列中的上往下开始寻找值
			tempValues = new ArrayList<>();
			for (int i = 1; i < data.length; i++) {
				if (!tempValues.contains(data[i][j])) {
					// 如果这个属性的值没有添加过，则添加
					tempValues.add(data[i][j]);
				}
			}

			// 一列属性的值已经遍历完毕，复制到map属性表中
			attrValue.put(data[0][j], tempValues);
		}
	}

	/**
	 * 计算机基尼指数
	 * 
	 * @param remainData
	 *            剩余数据
	 * @param attrName
	 *            属性名称
	 * @param value
	 *            属性值
	 * @param beLongValue
	 *            分类是否属于此属性值
	 * @return
	 */
	public double computeGini(String[][] remainData, String attrName,
			String value, boolean beLongValue) {
		// 实例总数
		int total = 0;
		// 正实例数
		int posNum = 0;
		// 负实例数
		int negNum = 0;
		// 基尼指数
		double gini = 0;

		// 还是按列从左往右遍历属性
		for (int j = 1; j < attrNames.length; j++) {
			// 找到了指定的属性
			if (attrName.equals(attrNames[j])) {
				for (int i = 1; i < remainData.length; i++) {
					// 统计正负实例按照属于和不属于值类型进行划分
					if ((beLongValue && remainData[i][j].equals(value))
							|| (!beLongValue && !remainData[i][j].equals(value))) {
						if (remainData[i][attrNames.length - 1].equals(YES)) {
							// 判断此行数据是否为正实例
							posNum++;
						} else {
							negNum++;
						}
					}
				}
			}
		}

		total = posNum + negNum;
		double posProbobly = (double) posNum / total;
		double negProbobly = (double) negNum / total;
		gini = 1 - posProbobly * posProbobly - negProbobly * negProbobly;

		// 返回计算基尼指数
		return gini;
	}

	/**
	 * 计算属性划分的最小基尼指数，返回最小的属性值划分和最小的基尼指数，保存在一个数组中
	 * 
	 * @param remainData
	 *            剩余谁
	 * @param attrName
	 *            属性名称
	 * @return
	 */
	public String[] computeAttrGini(String[][] remainData, String attrName) {
		String[] str = new String[2];
		// 最终该属性的划分类型值
		String spiltValue = "";
		// 临时变量
		int tempNum = 0;
		// 保存属性的值划分时的最小的基尼指数
		double minGini = Integer.MAX_VALUE;
		ArrayList<String> valueTypes = attrValue.get(attrName);
		// 属于此属性值的实例数
		HashMap<String, Integer> belongNum = new HashMap<>();

		for (String string : valueTypes) {
			// 重新计数的时候，数字归0
			tempNum = 0;
			// 按列从左往右遍历属性
			for (int j = 1; j < attrNames.length; j++) {
				// 找到了指定的属性
				if (attrName.equals(attrNames[j])) {
					for (int i = 1; i < remainData.length; i++) {
						// 统计正负实例按照属于和不属于值类型进行划分
						if (remainData[i][j].equals(string)) {
							tempNum++;
						}
					}
				}
			}

			belongNum.put(string, tempNum);
		}

		double tempGini = 0;
		double posProbably = 1.0;
		double negProbably = 1.0;
		for (String string : valueTypes) {
			tempGini = 0;

			posProbably = 1.0 * belongNum.get(string) / (remainData.length - 1);
			negProbably = 1 - posProbably;

			tempGini += posProbably
					* computeGini(remainData, attrName, string, true);
			tempGini += negProbably
					* computeGini(remainData, attrName, string, false);

			if (tempGini < minGini) {
				minGini = tempGini;
				spiltValue = string;
			}
		}

		str[0] = spiltValue;
		str[1] = minGini + "";

		return str;
	}

	public void buildDecisionTree(TreeNode node, String parentAttrValue,
			String[][] remainData, ArrayList<String> remainAttr,
			boolean beLongParentValue) {
		// 属性划分值
		String valueType = "";
		// 划分属性名称
		String spiltAttrName = "";
		double minGini = Integer.MAX_VALUE;
		double tempGini = 0;
		// 基尼指数数组，保存了基尼指数和此基尼指数的划分属性值
		String[] giniArray;

		if (beLongParentValue) {
			node.setParentAttrValue(parentAttrValue);
		} else {
			node.setParentAttrValue("!" + parentAttrValue);
		}

		if (remainAttr.size() == 0) {
			if (remainData.length > 1) {
				ArrayList<String> indexArray = new ArrayList<>();
				for (int i = 1; i < remainData.length; i++) {
					indexArray.add(remainData[i][0]);
				}
				node.setDataIndex(indexArray);
			}
		//	System.out.println("attr remain null");
			return;
		}

		for (String str : remainAttr) {
			giniArray = computeAttrGini(remainData, str);
			tempGini = Double.parseDouble(giniArray[1]);

			if (tempGini < minGini) {
				spiltAttrName = str;
				minGini = tempGini;
				valueType = giniArray[0];
			}
		}
		// 移除划分属性
		remainAttr.remove(spiltAttrName);
		node.setAttrName(spiltAttrName);

		// 孩子节点,分类回归树中，每次二元划分，分出2个孩子节点
		TreeNode[] childNode = new TreeNode[2];
		String[][] rData;

		boolean[] bArray = new boolean[] { true, false };
		for (int i = 0; i < bArray.length; i++) {
			// 二元划分属于属性值的划分
			rData = removeData(remainData, spiltAttrName, valueType, bArray[i]);

			boolean sameClass = true;
			ArrayList<String> indexArray = new ArrayList<>();
			for (int k = 1; k < rData.length; k++) {
				indexArray.add(rData[k][0]);
				// 判断是否为同一类的
				if (!rData[k][attrNames.length - 1]
						.equals(rData[1][attrNames.length - 1])) {
					// 只要有1个不相等，就不是同类型的
					sameClass = false;
					break;
				}
			}

			childNode[i] = new TreeNode();
			if (!sameClass) {
				// 创建新的对象属性，对象的同个引用会出错
				ArrayList<String> rAttr = new ArrayList<>();
				for (String str : remainAttr) {
					rAttr.add(str);
				}
				buildDecisionTree(childNode[i], valueType, rData, rAttr,
						bArray[i]);
			} else {
				String pAtr = (bArray[i] ? valueType : "!" + valueType);
				childNode[i].setParentAttrValue(pAtr);
				childNode[i].setDataIndex(indexArray);
			}
		}

		node.setChildAttrNode(childNode);
	}

	/**
	 * 属性划分完毕，进行数据的移除
	 * 
	 * @param srcData
	 *            源数据
	 * @param attrName
	 *            划分的属性名称
	 * @param valueType
	 *            属性的值类型
	 * @parame beLongValue 分类是否属于此值类型
	 */
	private String[][] removeData(String[][] srcData, String attrName,
			String valueType, boolean beLongValue) {
		String[][] desDataArray;
		ArrayList<String[]> desData = new ArrayList<>();
		// 待删除数据
		ArrayList<String[]> selectData = new ArrayList<>();
		selectData.add(attrNames);

		// 数组数据转化到列表中，方便移除
		for (int i = 0; i < srcData.length; i++) {
			desData.add(srcData[i]);
		}

		// 还是从左往右一列列的查找
		for (int j = 1; j < attrNames.length; j++) {
			if (attrNames[j].equals(attrName)) {
				for (int i = 1; i < desData.size(); i++) {
					if (desData.get(i)[j].equals(valueType)) {
						// 如果匹配这个数据，则移除其他的数据
						selectData.add(desData.get(i));
					}
				}
			}
		}

		if (beLongValue) {
			desDataArray = new String[selectData.size()][];
			selectData.toArray(desDataArray);
		} else {
			// 属性名称行不移除
			selectData.remove(attrNames);
			// 如果是划分不属于此类型的数据时，进行移除
			desData.removeAll(selectData);
			desDataArray = new String[desData.size()][];
			desData.toArray(desDataArray);
		}

		return desDataArray;
	}

	/**
	 * 构造分类回归树，并返回根节点
	 * @return
	 */
	public TreeNode startBuildingTree() {
		initAttrValue();

		ArrayList<String> remainAttr = new ArrayList<>();
		// 添加属性，除了最后一个类标号属性
		for (int i = 1; i < attrNames.length - 1; i++) {
			remainAttr.add(attrNames[i]);
		}

		TreeNode rootNode = new TreeNode();
		buildDecisionTree(rootNode, "", data, remainAttr, false);
		setIndexAndAlpah(rootNode, 0, false);
		showDecisionTree(rootNode, 1);
		
		return rootNode;
	}

	/**
	 * 显示决策树
	 * 
	 * @param node
	 *            待显示的节点
	 * @param blankNum
	 *            行空格符，用于显示树型结构
	 */
	private void showDecisionTree(TreeNode node, int blankNum) {
		System.out.println();
		for (int i = 0; i < blankNum; i++) {
			System.out.print("    ");
		}
		System.out.print("--");
		// 显示分类的属性值
		if (node.getParentAttrValue() != null
				&& node.getParentAttrValue().length() > 0) {
			System.out.print(node.getParentAttrValue());
		} else {
			System.out.print("--");
		}
		System.out.print("--");

		if (node.getDataIndex() != null && node.getDataIndex().size() > 0) {
			String i = node.getDataIndex().get(0);
			System.out.print("【" + node.getNodeIndex() + "】类别:"
					+ data[Integer.parseInt(i)][attrNames.length - 1]);
			System.out.print("[");
			for (String index : node.getDataIndex()) {
				System.out.print(index + ", ");
			}
			System.out.print("]");
		} else {
			// 递归显示子节点
			System.out.print("【" + node.getNodeIndex() + ":"
					+ node.getAttrName() + "】");
			if (node.getChildAttrNode() != null) {
				for (TreeNode childNode : node.getChildAttrNode()) {
					showDecisionTree(childNode, 2 * blankNum);
				}
			} else {
				System.out.print("【  Child Null】");
			}
		}
	}

	/**
	 * 为节点设置序列号，并计算每个节点的误差率，用于后面剪枝
	 * 
	 * @param node
	 *            开始的时候传入的是根节点
	 * @param index
	 *            开始的索引号，从1开始
	 * @param ifCutNode
	 *            是否需要剪枝
	 */
	private void setIndexAndAlpah(TreeNode node, int index, boolean ifCutNode) {
		TreeNode tempNode;
		// 最小误差代价节点，即将被剪枝的节点
		TreeNode minAlphaNode = null;
		double minAlpah = Integer.MAX_VALUE;
		Queue<TreeNode> nodeQueue = new LinkedList<TreeNode>();

		nodeQueue.add(node);
		while (nodeQueue.size() > 0) {
			index++;
			// 从队列头部获取首个节点
			tempNode = nodeQueue.poll();
			tempNode.setNodeIndex(index);
			if (tempNode.getChildAttrNode() != null) {
				for (TreeNode childNode : tempNode.getChildAttrNode()) {
					nodeQueue.add(childNode);
				}
				computeAlpha(tempNode);
				if (tempNode.getAlpha() < minAlpah) {
					minAlphaNode = tempNode;
					minAlpah = tempNode.getAlpha();
				} else if (tempNode.getAlpha() == minAlpah) {
					// 如果误差代价值一样，比较包含的叶子节点个数，剪枝有多叶子节点数的节点
					if (tempNode.getLeafNum() > minAlphaNode.getLeafNum()) {
						minAlphaNode = tempNode;
					}
				}
			}
		}

		if (ifCutNode) {
			// 进行树的剪枝，让其左右孩子节点为null
			minAlphaNode.setChildAttrNode(null);
		}
	}

	/**
	 * 为非叶子节点计算误差代价，这里的后剪枝法用的是CCP代价复杂度剪枝
	 * 
	 * @param node
	 *            待计算的非叶子节点
	 */
	private void computeAlpha(TreeNode node) {
		double rt = 0;
		double Rt = 0;
		double alpha = 0;
		// 当前节点的数据总数
		int sumNum = 0;
		// 最少的偏差数
		int minNum = 0;

		ArrayList<String> dataIndex;
		ArrayList<TreeNode> leafNodes = new ArrayList<>();

		addLeafNode(node, leafNodes);
		node.setLeafNum(leafNodes.size());
		for (TreeNode attrNode : leafNodes) {
			dataIndex = attrNode.getDataIndex();

			int num = 0;
			sumNum += dataIndex.size();
			for (String s : dataIndex) {
				// 统计分类数据中的正负实例数
				if (data[Integer.parseInt(s)][attrNames.length - 1].equals(YES)) {
					num++;
				}
			}
			minNum += num;

			// 取小数量的值部分
			if (1.0 * num / dataIndex.size() > 0.5) {
				num = dataIndex.size() - num;
			}

			rt += (1.0 * num / (data.length - 1));
		}
		
		//同样取出少偏差的那部分
		if (1.0 * minNum / sumNum > 0.5) {
			minNum = sumNum - minNum;
		}

		Rt = 1.0 * minNum / (data.length - 1);
		alpha = 1.0 * (Rt - rt) / (leafNodes.size() - 1);
		node.setAlpha(alpha);
	}

	/**
	 * 筛选出节点所包含的叶子节点数
	 * 
	 * @param node
	 *            待筛选节点
	 * @param leafNode
	 *            叶子节点列表容器
	 */
	private void addLeafNode(TreeNode node, ArrayList<TreeNode> leafNode) {
		ArrayList<String> dataIndex;

		if (node.getChildAttrNode() != null) {
			for (TreeNode childNode : node.getChildAttrNode()) {
				dataIndex = childNode.getDataIndex();
				if (dataIndex != null && dataIndex.size() > 0) {
					// 说明此节点为叶子节点
					leafNode.add(childNode);
				} else {
					// 如果还是非叶子节点则继续递归调用
					addLeafNode(childNode, leafNode);
				}
			}
		}
	}

}
