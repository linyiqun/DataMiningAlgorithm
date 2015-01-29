package DataMing_ID3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * ID3算法实现类
 * 
 * @author lyq
 * 
 */
public class ID3Tool {
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

	public ID3Tool(String filePath) {
		this.filePath = filePath;
		attrValue = new HashMap<>();
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
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(" ");
				dataArray.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		data = new String[dataArray.size()][];
		dataArray.toArray(data);
		attrNum = data[0].length;
		attrNames = data[0];

		/*
		 * for(int i=0; i<data.length;i++){ for(int j=0; j<data[0].length; j++){
		 * System.out.print(" " + data[i][j]); }
		 * 
		 * System.out.print("\n"); }
		 */
	}

	/**
	 * 首先初始化每种属性的值的所有类型，用于后面的子类熵的计算时用
	 */
	private void initAttrValue() {
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

		/*
		 * for(Map.Entry entry : attrValue.entrySet()){
		 * System.out.println("key:value " + entry.getKey() + ":" +
		 * entry.getValue()); }
		 */
	}

	/**
	 * 计算数据按照不同方式划分的熵
	 * 
	 * @param remainData
	 *            剩余的数据
	 * @param attrName
	 *            待划分的属性，在算信息增益的时候会使用到
	 * @param attrValue
	 *            划分的子属性值
	 * @param isParent
	 *            是否分子属性划分还是原来不变的划分
	 */
	private double computeEntropy(String[][] remainData, String attrName,
			String value, boolean isParent) {
		// 实例总数
		int total = 0;
		// 正实例数
		int posNum = 0;
		// 负实例数
		int negNum = 0;

		// 还是按列从左往右遍历属性
		for (int j = 1; j < attrNames.length; j++) {
			// 找到了指定的属性
			if (attrName.equals(attrNames[j])) {
				for (int i = 1; i < remainData.length; i++) {
					// 如果是父结点直接计算熵或者是通过子属性划分计算熵，这时要进行属性值的过滤
					if (isParent
							|| (!isParent && remainData[i][j].equals(value))) {
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

		if (posProbobly == 1 || posProbobly == 0) {
			// 如果数据全为同种类型，则熵为0，否则带入下面的公式会报错
			return 0;
		}

		double entropyValue = -posProbobly * Math.log(posProbobly)
				/ Math.log(2.0) - negProbobly * Math.log(negProbobly)
				/ Math.log(2.0);

		// 返回计算所得熵
		return entropyValue;
	}

	/**
	 * 为某个属性计算信息增益
	 * 
	 * @param remainData
	 *            剩余的数据
	 * @param value
	 *            待划分的属性名称
	 * @return
	 */
	private double computeGain(String[][] remainData, String value) {
		double gainValue = 0;
		// 源熵的大小将会与属性划分后进行比较
		double entropyOri = 0;
		// 子划分熵和
		double childEntropySum = 0;
		// 属性子类型的个数
		int childValueNum = 0;
		// 属性值的种数
		ArrayList<String> attrTypes = attrValue.get(value);
		// 子属性对应的权重比
		HashMap<String, Integer> ratioValues = new HashMap<>();

		for (int i = 0; i < attrTypes.size(); i++) {
			// 首先都统一计数为0
			ratioValues.put(attrTypes.get(i), 0);
		}

		// 还是按照一列，从左往右遍历
		for (int j = 1; j < attrNames.length; j++) {
			// 判断是否到了划分的属性列
			if (value.equals(attrNames[j])) {
				for (int i = 1; i <= remainData.length - 1; i++) {
					childValueNum = ratioValues.get(remainData[i][j]);
					// 增加个数并且重新存入
					childValueNum++;
					ratioValues.put(remainData[i][j], childValueNum);
				}
			}
		}

		// 计算原熵的大小
		entropyOri = computeEntropy(remainData, value, null, true);
		for (int i = 0; i < attrTypes.size(); i++) {
			double ratio = (double) ratioValues.get(attrTypes.get(i))
					/ (remainData.length - 1);
			childEntropySum += ratio
					* computeEntropy(remainData, value, attrTypes.get(i), false);

			// System.out.println("ratio:value: " + ratio + " " +
			// computeEntropy(remainData, value,
			// attrTypes.get(i), false));
		}

		// 二者熵相减就是信息增益
		gainValue = entropyOri - childEntropySum;
		return gainValue;
	}

	/**
	 * 计算信息增益比
	 * 
	 * @param remainData
	 *            剩余数据
	 * @param value
	 *            待划分属性
	 * @return
	 */
	private double computeGainRatio(String[][] remainData, String value) {
		double gain = 0;
		double spiltInfo = 0;
		int childValueNum = 0;
		// 属性值的种数
		ArrayList<String> attrTypes = attrValue.get(value);
		// 子属性对应的权重比
		HashMap<String, Integer> ratioValues = new HashMap<>();

		for (int i = 0; i < attrTypes.size(); i++) {
			// 首先都统一计数为0
			ratioValues.put(attrTypes.get(i), 0);
		}

		// 还是按照一列，从左往右遍历
		for (int j = 1; j < attrNames.length; j++) {
			// 判断是否到了划分的属性列
			if (value.equals(attrNames[j])) {
				for (int i = 1; i <= remainData.length - 1; i++) {
					childValueNum = ratioValues.get(remainData[i][j]);
					// 增加个数并且重新存入
					childValueNum++;
					ratioValues.put(remainData[i][j], childValueNum);
				}
			}
		}

		// 计算信息增益
		gain = computeGain(remainData, value);
		// 计算分裂信息，分裂信息度量被定义为(分裂信息用来衡量属性分裂数据的广度和均匀)：
		for (int i = 0; i < attrTypes.size(); i++) {
			double ratio = (double) ratioValues.get(attrTypes.get(i))
					/ (remainData.length - 1);
			spiltInfo += -ratio * Math.log(ratio) / Math.log(2.0);
		}

		// 计算机信息增益率
		return gain / spiltInfo;
	}

	/**
	 * 利用源数据构造决策树
	 */
	private void buildDecisionTree(AttrNode node, String parentAttrValue,
			String[][] remainData, ArrayList<String> remainAttr, boolean isID3) {
		node.setParentAttrValue(parentAttrValue);

		String attrName = "";
		double gainValue = 0;
		double tempValue = 0;

		// 如果只有1个属性则直接返回
		if (remainAttr.size() == 1) {
			System.out.println("attr null");
			return;
		}

		// 选择剩余属性中信息增益最大的作为下一个分类的属性
		for (int i = 0; i < remainAttr.size(); i++) {
			// 判断是否用ID3算法还是C4.5算法
			if (isID3) {
				// ID3算法采用的是按照信息增益的值来比
				tempValue = computeGain(remainData, remainAttr.get(i));
			} else {
				// C4.5算法进行了改进，用的是信息增益率来比,克服了用信息增益选择属性时偏向选择取值多的属性的不足
				tempValue = computeGainRatio(remainData, remainAttr.get(i));
			}

			if (tempValue > gainValue) {
				gainValue = tempValue;
				attrName = remainAttr.get(i);
			}
		}

		node.setAttrName(attrName);
		ArrayList<String> valueTypes = attrValue.get(attrName);
		remainAttr.remove(attrName);

		AttrNode[] childNode = new AttrNode[valueTypes.size()];
		String[][] rData;
		for (int i = 0; i < valueTypes.size(); i++) {
			// 移除非此值类型的数据
			rData = removeData(remainData, attrName, valueTypes.get(i));

			childNode[i] = new AttrNode();
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

			if (!sameClass) {
				// 创建新的对象属性，对象的同个引用会出错
				ArrayList<String> rAttr = new ArrayList<>();
				for (String str : remainAttr) {
					rAttr.add(str);
				}

				buildDecisionTree(childNode[i], valueTypes.get(i), rData,
						rAttr, isID3);
			} else {
				// 如果是同种类型，则直接为数据节点
				childNode[i].setParentAttrValue(valueTypes.get(i));
				childNode[i].setChildDataIndex(indexArray);
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
	 */
	private String[][] removeData(String[][] srcData, String attrName,
			String valueType) {
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

		desDataArray = new String[selectData.size()][];
		selectData.toArray(desDataArray);

		return desDataArray;
	}

	/**
	 * 开始构建决策树
	 * 
	 * @param isID3
	 *            是否采用ID3算法构架决策树
	 */
	public void startBuildingTree(boolean isID3) {
		readDataFile();
		initAttrValue();

		ArrayList<String> remainAttr = new ArrayList<>();
		// 添加属性，除了最后一个类标号属性
		for (int i = 1; i < attrNames.length - 1; i++) {
			remainAttr.add(attrNames[i]);
		}

		AttrNode rootNode = new AttrNode();
		buildDecisionTree(rootNode, "", data, remainAttr, isID3);
		showDecisionTree(rootNode, 1);
	}

	/**
	 * 显示决策树
	 * 
	 * @param node
	 *            待显示的节点
	 * @param blankNum
	 *            行空格符，用于显示树型结构
	 */
	private void showDecisionTree(AttrNode node, int blankNum) {
		System.out.println();
		for (int i = 0; i < blankNum; i++) {
			System.out.print("\t");
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

		if (node.getChildDataIndex() != null
				&& node.getChildDataIndex().size() > 0) {
			String i = node.getChildDataIndex().get(0);
			System.out.print("类别:"
					+ data[Integer.parseInt(i)][attrNames.length - 1]);
			System.out.print("[");
			for (String index : node.getChildDataIndex()) {
				System.out.print(index + ", ");
			}
			System.out.print("]");
		} else {
			// 递归显示子节点
			System.out.print("【" + node.getAttrName() + "】");
			for (AttrNode childNode : node.getChildAttrNode()) {
				showDecisionTree(childNode, 2 * blankNum);
			}
		}

	}

}
