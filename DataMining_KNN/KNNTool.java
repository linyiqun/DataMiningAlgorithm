package DataMining_KNN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.filter.ComparisonExpression;

/**
 * k最近邻算法工具类
 * 
 * @author lyq
 * 
 */
public class KNNTool {
	// 为4个类别设置权重，默认权重比一致
	public int[] classWeightArray = new int[] { 1, 1, 1, 1 };
	// 测试数据
	private String testDataPath;
	// 训练集数据地址
	private String trainDataPath;
	// 分类的不同类型
	private ArrayList<String> classTypes;
	// 结果数据
	private ArrayList<Sample> resultSamples;
	// 训练集数据列表容器
	private ArrayList<Sample> trainSamples;
	// 训练集数据
	private String[][] trainData;
	// 测试集数据
	private String[][] testData;

	public KNNTool(String trainDataPath, String testDataPath) {
		this.trainDataPath = trainDataPath;
		this.testDataPath = testDataPath;
		readDataFormFile();
	}

	/**
	 * 从文件中阅读测试数和训练数据集
	 */
	private void readDataFormFile() {
		ArrayList<String[]> tempArray;

		tempArray = fileDataToArray(trainDataPath);
		trainData = new String[tempArray.size()][];
		tempArray.toArray(trainData);

		classTypes = new ArrayList<>();
		for (String[] s : tempArray) {
			if (!classTypes.contains(s[0])) {
				// 添加类型
				classTypes.add(s[0]);
			}
		}

		tempArray = fileDataToArray(testDataPath);
		testData = new String[tempArray.size()][];
		tempArray.toArray(testData);
	}

	/**
	 * 将文件转为列表数据输出
	 * 
	 * @param filePath
	 *            数据文件的内容
	 */
	private ArrayList<String[]> fileDataToArray(String filePath) {
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

		return dataArray;
	}

	/**
	 * 计算样本特征向量的欧几里得距离
	 * 
	 * @param f1
	 *            待比较样本1
	 * @param f2
	 *            待比较样本2
	 * @return
	 */
	private int computeEuclideanDistance(Sample s1, Sample s2) {
		String[] f1 = s1.getFeatures();
		String[] f2 = s2.getFeatures();
		// 欧几里得距离
		int distance = 0;

		for (int i = 0; i < f1.length; i++) {
			int subF1 = Integer.parseInt(f1[i]);
			int subF2 = Integer.parseInt(f2[i]);

			distance += (subF1 - subF2) * (subF1 - subF2);
		}

		return distance;
	}

	/**
	 * 计算K最近邻
	 * @param k
	 * 在多少的k范围内
	 */
	public void knnCompute(int k) {
		String className = "";
		String[] tempF = null;
		Sample temp;
		resultSamples = new ArrayList<>();
		trainSamples = new ArrayList<>();
		// 分类类别计数
		HashMap<String, Integer> classCount;
		// 类别权重比
		HashMap<String, Integer> classWeight = new HashMap<>();
		// 首先讲测试数据转化到结果数据中
		for (String[] s : testData) {
			temp = new Sample(s);
			resultSamples.add(temp);
		}

		for (String[] s : trainData) {
			className = s[0];
			tempF = new String[s.length - 1];
			System.arraycopy(s, 1, tempF, 0, s.length - 1);
			temp = new Sample(className, tempF);
			trainSamples.add(temp);
		}

		// 离样本最近排序的的训练集数据
		ArrayList<Sample> kNNSample = new ArrayList<>();
		// 计算训练数据集中离样本数据最近的K个训练集数据
		for (Sample s : resultSamples) {
			classCount = new HashMap<>();
			int index = 0;
			for (String type : classTypes) {
				// 开始时计数为0
				classCount.put(type, 0);
				classWeight.put(type, classWeightArray[index++]);
			}
			for (Sample tS : trainSamples) {
				int dis = computeEuclideanDistance(s, tS);
				tS.setDistance(dis);
			}

			Collections.sort(trainSamples);
			kNNSample.clear();
			// 挑选出前k个数据作为分类标准
			for (int i = 0; i < trainSamples.size(); i++) {
				if (i < k) {
					kNNSample.add(trainSamples.get(i));
				} else {
					break;
				}
			}
			// 判定K个训练数据的多数的分类标准
			for (Sample s1 : kNNSample) {
				int num = classCount.get(s1.getClassName());
				// 进行分类权重的叠加，默认类别权重平等，可自行改变，近的权重大，远的权重小
				num += classWeight.get(s1.getClassName());
				classCount.put(s1.getClassName(), num);
			}

			int maxCount = 0;
			// 筛选出k个训练集数据中最多的一个分类
			for (Map.Entry entry : classCount.entrySet()) {
				if ((Integer) entry.getValue() > maxCount) {
					maxCount = (Integer) entry.getValue();
					s.setClassName((String) entry.getKey());
				}
			}

			System.out.print("测试数据特征：");
			for (String s1 : s.getFeatures()) {
				System.out.print(s1 + " ");
			}
			System.out.println("分类：" + s.getClassName());
		}
	}
}
