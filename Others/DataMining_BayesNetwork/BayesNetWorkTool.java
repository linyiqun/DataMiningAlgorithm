package DataMining_BayesNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 贝叶斯网络算法工具类
 * 
 * @author lyq
 * 
 */
public class BayesNetWorkTool {
	// 联合概率分布数据文件地址
	private String dataFilePath;
	// 事件关联数据文件地址
	private String attachFilePath;
	// 属性列列数
	private int columns;
	// 概率分布数据
	private String[][] totalData;
	// 关联数据对
	private ArrayList<String[]> attachData;
	// 节点存放列表
	private ArrayList<Node> nodes;
	// 属性名与列数之间的对应关系
	private HashMap<String, Integer> attr2Column;

	public BayesNetWorkTool(String dataFilePath, String attachFilePath) {
		this.dataFilePath = dataFilePath;
		this.attachFilePath = attachFilePath;

		initDatas();
	}

	/**
	 * 初始化关联数据和概率分布数据
	 */
	private void initDatas() {
		String[] columnValues;
		String[] array;
		ArrayList<String> datas;
		ArrayList<String> adatas;

		// 从文件中读取数据
		datas = readDataFile(dataFilePath);
		adatas = readDataFile(attachFilePath);

		columnValues = datas.get(0).split(" ");
		// 属性割名称代表事件B(盗窃)，E(地震)，A(警铃响).M(接到M的电话)，J同M的意思,
		// 属性值都是y,n代表yes发生和no不发生
		this.attr2Column = new HashMap<>();
		for (int i = 0; i < columnValues.length; i++) {
			// 从数据中取出属性名称行，列数值存入图中
			this.attr2Column.put(columnValues[i], i);
		}

		this.columns = columnValues.length;
		this.totalData = new String[datas.size()][columns];
		for (int i = 0; i < datas.size(); i++) {
			this.totalData[i] = datas.get(i).split(" ");
		}

		this.attachData = new ArrayList<>();
		// 解析关联数据对
		for (String str : adatas) {
			array = str.split(" ");
			this.attachData.add(array);
		}

		// 构造贝叶斯网络结构图
		constructDAG();
	}

	/**
	 * 从文件中读取数据
	 */
	private ArrayList<String> readDataFile(String filePath) {
		File file = new File(filePath);
		ArrayList<String> dataArray = new ArrayList<String>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			while ((str = in.readLine()) != null) {
				dataArray.add(str);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		return dataArray;
	}

	/**
	 * 根据关联数据构造贝叶斯网络无环有向图
	 */
	private void constructDAG() {
		// 节点存在标识
		boolean srcExist;
		boolean desExist;
		String name1;
		String name2;
		Node srcNode;
		Node desNode;

		this.nodes = new ArrayList<>();
		for (String[] array : this.attachData) {
			srcExist = false;
			desExist = false;

			name1 = array[0];
			name2 = array[1];

			// 新建节点
			srcNode = new Node(name1);
			desNode = new Node(name2);

			for (Node temp : this.nodes) {
				// 如果找到相同节点，则取出
				if (srcNode.isEqual(temp)) {
					srcExist = true;
					srcNode = temp;
				} else if (desNode.isEqual(temp)) {
					desExist = true;
					desNode = temp;
				}

				// 如果2个节点都已找到，则跳出循环
				if (srcExist && desExist) {
					break;
				}
			}

			// 将2个节点进行连接
			srcNode.connectNode(desNode);

			// 根据标识判断是否需要加入列表容器中
			if (!srcExist) {
				this.nodes.add(srcNode);
			}

			if (!desExist) {
				this.nodes.add(desNode);
			}
		}
	}

	/**
	 * 查询条件概率
	 * 
	 * @param attrValues
	 *            条件属性值
	 * @return
	 */
	private double queryConditionPro(ArrayList<String[]> attrValues) {
		// 判断是否满足先验属性值条件
		boolean hasPrior;
		// 判断是否满足后验属性值条件
		boolean hasBack;
		int priorIndex;
		int attrIndex;
		double backPro;
		double totalPro;
		double pro;
		double currentPro;
		// 先验属性
		String[] priorValue;
		String[] tempData;

		pro = 0;
		totalPro = 0;
		backPro = 0;
		attrValues.get(0);
		priorValue = attrValues.get(0);
		// 得到后验概率
		attrValues.remove(0);

		// 取出先验属性的列数
		priorIndex = this.attr2Column.get(priorValue[0]);
		// 跳过第一行的属性名称行
		for (int i = 1; i < this.totalData.length; i++) {
			tempData = this.totalData[i];

			hasPrior = false;
			hasBack = true;

			// 当前行的概率
			currentPro = Double.parseDouble(tempData[this.columns - 1]);
			// 判断是否满足先验条件
			if (tempData[priorIndex].equals(priorValue[1])) {
				hasPrior = true;
			}

			for (String[] array : attrValues) {
				attrIndex = this.attr2Column.get(array[0]);

				// 判断值是否满足条件
				if (!tempData[attrIndex].equals(array[1])) {
					hasBack = false;
					break;
				}
			}

			// 进行计数统计，分别计算满足后验属性的值和同时满足条件的个数
			if (hasBack) {
				backPro += currentPro;
				if (hasPrior) {
					totalPro += currentPro;
				}
			} else if (hasPrior && attrValues.size() == 0) {
				// 如果只有先验概率则为纯概率的计算
				totalPro += currentPro;
				backPro = 1.0;
			}
		}

		// 计算总的概率=都发生概率/只发生后验条件的时间概率
		pro = totalPro / backPro;

		return pro;
	}

	/**
	 * 根据贝叶斯网络计算概率
	 * 
	 * @param queryStr
	 *            查询条件串
	 * @return
	 */
	public double calProByNetWork(String queryStr) {
		double temp;
		double pro;
		String[] array;
		// 先验条件值
		String[] preValue;
		// 后验条件值
		String[] backValue;
		// 所有先验条件和后验条件值的属性值的汇总
		ArrayList<String[]> attrValues;

		// 判断是否满足网络结构
		if (!satisfiedNewWork(queryStr)) {
			return -1;
		}

		pro = 1;
		// 首先做查询条件的分解
		array = queryStr.split(",");

		// 概率的初值等于第一个事件发生的随机概率
		attrValues = new ArrayList<>();
		attrValues.add(array[0].split("="));
		pro = queryConditionPro(attrValues);

		for (int i = 0; i < array.length - 1; i++) {
			attrValues.clear();

			// 下标小的在前面的属于后验属性
			backValue = array[i].split("=");
			preValue = array[i + 1].split("=");
			attrValues.add(preValue);
			attrValues.add(backValue);

			// 算出此种情况的概率值
			temp = queryConditionPro(attrValues);
			// 进行积的相乘
			pro *= temp;
		}

		return pro;
	}

	/**
	 * 验证事件的查询因果关系是否满足贝叶斯网络
	 * 
	 * @param queryStr
	 *            查询字符串
	 * @return
	 */
	private boolean satisfiedNewWork(String queryStr) {
		String attrName;
		String[] array;
		boolean isExist;
		boolean isSatisfied;
		// 当前节点
		Node currentNode;
		// 候选节点列表
		ArrayList<Node> nodeList;

		isSatisfied = true;
		currentNode = null;
		// 做查询字符串的分解
		array = queryStr.split(",");
		nodeList = this.nodes;

		for (String s : array) {
			// 开始时默认属性对应的节点不存在
			isExist = false;
			// 得到属性事件名
			attrName = s.split("=")[0];

			for (Node n : nodeList) {
				if (n.name.equals(attrName)) {
					isExist = true;

					currentNode = n;
					// 下一轮的候选节点为当前节点的孩子节点
					nodeList = currentNode.childNodes;

					break;
				}
			}

			// 如果存在未找到的节点，则说明不满足依赖结构跳出循环
			if (!isExist) {
				isSatisfied = false;
				break;
			}
		}

		return isSatisfied;
	}
}
