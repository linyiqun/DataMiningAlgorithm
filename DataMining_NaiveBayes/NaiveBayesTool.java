package DataMining_NaiveBayes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 朴素贝叶斯算法工具类
 * 
 * @author lyq
 * 
 */
public class NaiveBayesTool {
	// 类标记符，这里分为2类，YES和NO
	private String YES = "Yes";
	private String NO = "No";

	// 已分类训练数据集文件路径
	private String filePath;
	// 属性名称数组
	private String[] attrNames;
	// 训练数据集
	private String[][] data;

	// 每个属性的值所有类型
	private HashMap<String, ArrayList<String>> attrValue;

	public NaiveBayesTool(String filePath) {
		this.filePath = filePath;

		readDataFile();
		initAttrValue();
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
		attrValue = new HashMap<>();
		ArrayList<String> tempValues;

		// 按照列的方式，从左往右找
		for (int j = 1; j < attrNames.length; j++) {
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
	 * 在classType的情况下，发生condition条件的概率
	 * 
	 * @param condition
	 *            属性条件
	 * @param classType
	 *            分类的类型
	 * @return
	 */
	private double computeConditionProbably(String condition, String classType) {
		// 条件计数器
		int count = 0;
		// 条件属性的索引列
		int attrIndex = 1;
		// yes类标记符数据
		ArrayList<String[]> yClassData = new ArrayList<>();
		// no类标记符数据
		ArrayList<String[]> nClassData = new ArrayList<>();
		ArrayList<String[]> classData;

		for (int i = 1; i < data.length; i++) {
			// data数据按照yes和no分类
			if (data[i][attrNames.length - 1].equals(YES)) {
				yClassData.add(data[i]);
			} else {
				nClassData.add(data[i]);
			}
		}

		if (classType.equals(YES)) {
			classData = yClassData;
		} else {
			classData = nClassData;
		}

		// 如果没有设置条件则，计算的是纯粹的类事件概率
		if (condition == null) {
			return 1.0 * classData.size() / (data.length - 1);
		}

		// 寻找此条件的属性列
		attrIndex = getConditionAttrName(condition);

		for (String[] s : classData) {
			if (s[attrIndex].equals(condition)) {
				count++;
			}
		}

		return 1.0 * count / classData.size();
	}

	/**
	 * 根据条件值返回条件所属属性的列值
	 * 
	 * @param condition
	 *            条件
	 * @return
	 */
	private int getConditionAttrName(String condition) {
		// 条件所属属性名
		String attrName = "";
		// 条件所在属性列索引
		int attrIndex = 1;
		// 临时属性值类型
		ArrayList<String[]> valueTypes;
		for (Map.Entry entry : attrValue.entrySet()) {
			valueTypes = (ArrayList<String[]>) entry.getValue();
			if (valueTypes.contains(condition)
					&& !((String) entry.getKey()).equals("BuysComputer")) {
				attrName = (String) entry.getKey();
			}
		}

		for (int i = 0; i < attrNames.length - 1; i++) {
			if (attrNames[i].equals(attrName)) {
				attrIndex = i;
				break;
			}
		}

		return attrIndex;
	}

	/**
	 * 进行朴素贝叶斯分类
	 * 
	 * @param data
	 *            待分类数据
	 */
	public String naiveBayesClassificate(String data) {
		// 测试数据的属性值特征
		String[] dataFeatures;
		// 在yes的条件下，x事件发生的概率
		double xWhenYes = 1.0;
		// 在no的条件下，x事件发生的概率
		double xWhenNo = 1.0;
		// 最后也是yes和no分类的总概率，用P(X|Ci)*P(Ci)的公式计算
		double pYes = 1;
		double pNo = 1;

		dataFeatures = data.split(" ");
		for (int i = 0; i < dataFeatures.length; i++) {
			// 因为朴素贝叶斯算法是类条件独立的，所以可以进行累积的计算
			xWhenYes *= computeConditionProbably(dataFeatures[i], YES);
			xWhenNo *= computeConditionProbably(dataFeatures[i], NO);
		}

		pYes = xWhenYes * computeConditionProbably(null, YES);
		pNo = xWhenNo * computeConditionProbably(null, NO);
		
		return (pYes > pNo ? YES : NO);
	}

}
