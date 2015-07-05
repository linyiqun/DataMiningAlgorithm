package DataMining_TAN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * TAN树型朴素贝叶斯算法工具类
 * 
 * @author lyq
 * 
 */
public class TANTool {
	// 测试数据集地址
	private String filePath;
	// 数据集属性总数,其中一个个分类属性
	private int attrNum;
	// 分类属性名
	private String classAttrName;
	// 属性列名称行
	private String[] attrNames;
	// 贝叶斯网络边的方向，数组内的数值为节点id,从i->j
	private int[][] edges;
	// 属性名到列下标的映射
	private HashMap<String, Integer> attr2Column;
	// 属性，属性对取值集合映射对
	private HashMap<String, ArrayList<String>> attr2Values;
	// 贝叶斯网络总节点列表
	private ArrayList<Node> totalNodes;
	// 总的测试数据
	private ArrayList<String[]> totalDatas;

	public TANTool(String filePath) {
		this.filePath = filePath;

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
			String[] array;

			while ((str = in.readLine()) != null) {
				array = str.split(" ");
				dataArray.add(array);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		this.totalDatas = dataArray;
		this.attrNames = this.totalDatas.get(0);
		this.attrNum = this.attrNames.length;
		this.classAttrName = this.attrNames[attrNum - 1];

		Node node;
		this.edges = new int[attrNum][attrNum];
		this.totalNodes = new ArrayList<>();
		this.attr2Column = new HashMap<>();
		this.attr2Values = new HashMap<>();

		// 分类属性节点id最小设为0
		node = new Node(0, attrNames[attrNum - 1]);
		this.totalNodes.add(node);
		for (int i = 0; i < attrNames.length; i++) {
			if (i < attrNum - 1) {
				// 创建贝叶斯网络节点，每个属性一个节点
				node = new Node(i + 1, attrNames[i]);
				this.totalNodes.add(node);
			}

			// 添加属性到列下标的映射
			this.attr2Column.put(attrNames[i], i);
		}

		String[] temp;
		ArrayList<String> values;
		// 进行属性名，属性值对的映射匹配
		for (int i = 1; i < this.totalDatas.size(); i++) {
			temp = this.totalDatas.get(i);

			for (int j = 0; j < temp.length; j++) {
				// 判断map中是否包含此属性名
				if (this.attr2Values.containsKey(attrNames[j])) {
					values = this.attr2Values.get(attrNames[j]);
				} else {
					values = new ArrayList<>();
				}

				if (!values.contains(temp[j])) {
					// 加入新的属性值
					values.add(temp[j]);
				}

				this.attr2Values.put(attrNames[j], values);
			}
		}
	}

	/**
	 * 根据条件互信息度对构建最大权重跨度树,返回第一个节点为根节点
	 * 
	 * @param iArray
	 */
	private Node constructWeightTree(ArrayList<Node[]> iArray) {
		Node node1;
		Node node2;
		Node root;
		ArrayList<Node> existNodes;

		existNodes = new ArrayList<>();

		for (Node[] i : iArray) {
			node1 = i[0];
			node2 = i[1];

			// 将2个节点进行连接
			node1.connectNode(node2);
			// 避免出现环路现象
			addIfNotExist(node1, existNodes);
			addIfNotExist(node2, existNodes);

			if (existNodes.size() == attrNum - 1) {
				break;
			}
		}

		// 返回第一个作为根节点
		root = existNodes.get(0);
		return root;
	}

	/**
	 * 为树型结构确定边的方向，方向为属性根节点方向指向其他属性节点方向
	 * 
	 * @param root
	 *            当前遍历到的节点
	 */
	private void confirmGraphDirection(Node currentNode) {
		int i;
		int j;
		ArrayList<Node> connectedNodes;

		connectedNodes = currentNode.connectedNodes;

		i = currentNode.id;
		for (Node n : connectedNodes) {
			j = n.id;

			// 判断连接此2节点的方向是否被确定
			if (edges[i][j] == 0 && edges[j][i] == 0) {
				// 如果没有确定，则制定方向为i->j
				edges[i][j] = 1;

				// 递归继续搜索
				confirmGraphDirection(n);
			}
		}
	}

	/**
	 * 为属性节点添加分类属性节点为父节点
	 * 
	 * @param parentNode
	 *            父节点
	 * @param nodeList
	 *            子节点列表
	 */
	private void addParentNode() {
		// 分类属性节点
		Node parentNode;

		parentNode = null;
		for (Node n : this.totalNodes) {
			if (n.id == 0) {
				parentNode = n;
				break;
			}
		}

		for (Node child : this.totalNodes) {
			parentNode.connectNode(child);

			if (child.id != 0) {
				// 确定连接方向
				this.edges[0][child.id] = 1;
			}
		}
	}

	/**
	 * 在节点集合中添加节点
	 * 
	 * @param node
	 *            待添加节点
	 * @param existNodes
	 *            已存在的节点列表
	 * @return
	 */
	public boolean addIfNotExist(Node node, ArrayList<Node> existNodes) {
		boolean canAdd;

		canAdd = true;
		for (Node n : existNodes) {
			// 如果节点列表中已经含有节点，则算添加失败
			if (n.isEqual(node)) {
				canAdd = false;
				break;
			}
		}

		if (canAdd) {
			existNodes.add(node);
		}

		return canAdd;
	}

	/**
	 * 计算节点条件概率
	 * 
	 * @param node
	 *            关于node的后验概率
	 * @param queryParam
	 *            查询的属性参数
	 * @return
	 */
	private double calConditionPro(Node node, HashMap<String, String> queryParam) {
		int id;
		double pro;
		String value;
		String[] attrValue;

		ArrayList<String[]> priorAttrInfos;
		ArrayList<String[]> backAttrInfos;
		ArrayList<Node> parentNodes;

		pro = 1;
		id = node.id;
		parentNodes = new ArrayList<>();
		priorAttrInfos = new ArrayList<>();
		backAttrInfos = new ArrayList<>();

		for (int i = 0; i < this.edges.length; i++) {
			// 寻找父节点id
			if (this.edges[i][id] == 1) {
				for (Node temp : this.totalNodes) {
					// 寻找目标节点id
					if (temp.id == i) {
						parentNodes.add(temp);
						break;
					}
				}
			}
		}

		// 获取先验属性的属性值,首先添加先验属性
		value = queryParam.get(node.name);
		attrValue = new String[2];
		attrValue[0] = node.name;
		attrValue[1] = value;
		priorAttrInfos.add(attrValue);

		// 逐一添加后验属性
		for (Node p : parentNodes) {
			value = queryParam.get(p.name);
			attrValue = new String[2];
			attrValue[0] = p.name;
			attrValue[1] = value;

			backAttrInfos.add(attrValue);
		}

		pro = queryConditionPro(priorAttrInfos, backAttrInfos);

		return pro;
	}

	/**
	 * 查询条件概率
	 * 
	 * @param attrValues
	 *            条件属性值
	 * @return
	 */
	private double queryConditionPro(ArrayList<String[]> priorValues,
			ArrayList<String[]> backValues) {
		// 判断是否满足先验属性值条件
		boolean hasPrior;
		// 判断是否满足后验属性值条件
		boolean hasBack;
		int attrIndex;
		double backPro;
		double totalPro;
		double pro;
		String[] tempData;

		pro = 0;
		totalPro = 0;
		backPro = 0;

		// 跳过第一行的属性名称行
		for (int i = 1; i < this.totalDatas.size(); i++) {
			tempData = this.totalDatas.get(i);

			hasPrior = true;
			hasBack = true;

			// 判断是否满足先验条件
			for (String[] array : priorValues) {
				attrIndex = this.attr2Column.get(array[0]);

				// 判断值是否满足条件
				if (!tempData[attrIndex].equals(array[1])) {
					hasPrior = false;
					break;
				}
			}

			// 判断是否满足后验条件
			for (String[] array : backValues) {
				attrIndex = this.attr2Column.get(array[0]);

				// 判断值是否满足条件
				if (!tempData[attrIndex].equals(array[1])) {
					hasBack = false;
					break;
				}
			}

			// 进行计数统计，分别计算满足后验属性的值和同时满足条件的个数
			if (hasBack) {
				backPro++;
				if (hasPrior) {
					totalPro++;
				}
			} else if (hasPrior && backValues.size() == 0) {
				// 如果只有先验概率则为纯概率的计算
				totalPro++;
				backPro = 1.0;
			}
		}

		if (backPro == 0) {
			pro = 0;
		} else {
			// 计算总的概率=都发生概率/只发生后验条件的时间概率
			pro = totalPro / backPro;
		}

		return pro;
	}

	/**
	 * 输入查询条件参数，计算发生概率
	 * 
	 * @param queryParam
	 *            条件参数
	 * @return
	 */
	public double calHappenedPro(String queryParam) {
		double result;
		double temp;
		// 分类属性值
		String classAttrValue;
		String[] array;
		String[] array2;
		HashMap<String, String> params;

		result = 1;
		params = new HashMap<>();

		// 进行查询字符的参数分解
		array = queryParam.split(",");
		for (String s : array) {
			array2 = s.split("=");
			params.put(array2[0], array2[1]);
		}

		classAttrValue = params.get(classAttrName);
		// 构建贝叶斯网络结构
		constructBayesNetWork(classAttrValue);

		for (Node n : this.totalNodes) {
			temp = calConditionPro(n, params);

			// 为了避免出现条件概率为0的现象，进行轻微矫正
			if (temp == 0) {
				temp = 0.001;
			}

			// 按照联合概率公式，进行乘积运算
			result *= temp;
		}

		return result;
	}

	/**
	 * 构建树型贝叶斯网络结构
	 * 
	 * @param value
	 *            类别量值
	 */
	private void constructBayesNetWork(String value) {
		Node rootNode;
		ArrayList<AttrMutualInfo> mInfoArray;
		// 互信息度对
		ArrayList<Node[]> iArray;

		iArray = null;
		rootNode = null;

		// 在每次重新构建贝叶斯网络结构的时候，清空原有的连接结构
		for (Node n : this.totalNodes) {
			n.connectedNodes.clear();
		}
		this.edges = new int[attrNum][attrNum];

		// 从互信息对象中取出属性值对
		iArray = new ArrayList<>();
		mInfoArray = calAttrMutualInfoArray(value);
		for (AttrMutualInfo v : mInfoArray) {
			iArray.add(v.nodeArray);
		}

		// 构建最大权重跨度树
		rootNode = constructWeightTree(iArray);
		// 为无向图确定边的方向
		confirmGraphDirection(rootNode);
		// 为每个属性节点添加分类属性父节点
		addParentNode();
	}

	/**
	 * 给定分类变量值，计算属性之间的互信息值
	 * 
	 * @param value
	 *            分类变量值
	 * @return
	 */
	private ArrayList<AttrMutualInfo> calAttrMutualInfoArray(String value) {
		double iValue;
		Node node1;
		Node node2;
		AttrMutualInfo mInfo;
		ArrayList<AttrMutualInfo> mInfoArray;

		mInfoArray = new ArrayList<>();

		for (int i = 0; i < this.totalNodes.size() - 1; i++) {
			node1 = this.totalNodes.get(i);
			// 跳过分类属性节点
			if (node1.id == 0) {
				continue;
			}

			for (int j = i + 1; j < this.totalNodes.size(); j++) {
				node2 = this.totalNodes.get(j);
				// 跳过分类属性节点
				if (node2.id == 0) {
					continue;
				}

				// 计算2个属性节点之间的互信息值
				iValue = calMutualInfoValue(node1, node2, value);
				mInfo = new AttrMutualInfo(iValue, node1, node2);
				mInfoArray.add(mInfo);
			}
		}

		// 将结果进行降序排列，让互信息值高的优先用于构建树
		Collections.sort(mInfoArray);

		return mInfoArray;
	}

	/**
	 * 计算2个属性节点的互信息值
	 * 
	 * @param node1
	 *            节点1
	 * @param node2
	 *            节点2
	 * @param vlaue
	 *            分类变量值
	 */
	private double calMutualInfoValue(Node node1, Node node2, String value) {
		double iValue;
		double temp;
		// 三种不同条件的后验概率
		double pXiXj;
		double pXi;
		double pXj;
		String[] array1;
		String[] array2;
		ArrayList<String> attrValues1;
		ArrayList<String> attrValues2;
		ArrayList<String[]> priorValues;
		// 后验概率，在这里就是类变量值
		ArrayList<String[]> backValues;

		array1 = new String[2];
		array2 = new String[2];
		priorValues = new ArrayList<>();
		backValues = new ArrayList<>();

		iValue = 0;
		array1[0] = classAttrName;
		array1[1] = value;
		// 后验属性都是类属性
		backValues.add(array1);

		// 获取节点属性的属性值集合
		attrValues1 = this.attr2Values.get(node1.name);
		attrValues2 = this.attr2Values.get(node2.name);

		for (String v1 : attrValues1) {
			for (String v2 : attrValues2) {
				priorValues.clear();

				array1 = new String[2];
				array1[0] = node1.name;
				array1[1] = v1;
				priorValues.add(array1);

				array2 = new String[2];
				array2[0] = node2.name;
				array2[1] = v2;
				priorValues.add(array2);

				// 计算3种条件下的概率
				pXiXj = queryConditionPro(priorValues, backValues);

				priorValues.clear();
				priorValues.add(array1);
				pXi = queryConditionPro(priorValues, backValues);

				priorValues.clear();
				priorValues.add(array2);
				pXj = queryConditionPro(priorValues, backValues);

				// 如果出现其中一个计数概率为0，则直接赋值为0处理
				if (pXiXj == 0 || pXi == 0 || pXj == 0) {
					temp = 0;
				} else {
					// 利用公式计算针对此属性值对组合的概率
					temp = pXiXj * Math.log(pXiXj / (pXi * pXj)) / Math.log(2);
				}

				// 进行和属性值对组合的累加即为整个属性的互信息值
				iValue += temp;
			}
		}

		return iValue;
	}
}
