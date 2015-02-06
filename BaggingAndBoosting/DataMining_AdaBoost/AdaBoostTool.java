package DataMining_AdaBoost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * AdaBoost提升算法工具类
 * 
 * @author lyq
 * 
 */
public class AdaBoostTool {
	// 分类的类别，程序默认为正类1和负类-1
	public static final int CLASS_POSITIVE = 1;
	public static final int CLASS_NEGTIVE = -1;

	// 事先假设的3个分类器(理论上应该重新对数据集进行训练得到)
	public static final String CLASSIFICATION1 = "X=2.5";
	public static final String CLASSIFICATION2 = "X=7.5";
	public static final String CLASSIFICATION3 = "Y=5.5";

	// 分类器组
	public static final String[] ClASSIFICATION = new String[] {
			CLASSIFICATION1, CLASSIFICATION2, CLASSIFICATION3 };
	// 分类权重组
	private double[] CLASSIFICATION_WEIGHT;

	// 测试数据文件地址
	private String filePath;
	// 误差率阈值
	private double errorValue;
	// 所有的数据点
	private ArrayList<Point> totalPoint;

	public AdaBoostTool(String filePath, double errorValue) {
		this.filePath = filePath;
		this.errorValue = errorValue;
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
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(" ");
				dataArray.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		Point temp;
		totalPoint = new ArrayList<>();
		for (String[] array : dataArray) {
			temp = new Point(array[0], array[1], array[2]);
			temp.setProbably(1.0 / dataArray.size());
			totalPoint.add(temp);
		}
	}

	/**
	 * 根据当前的误差值算出所得的权重
	 * 
	 * @param errorValue
	 *            当前划分的坐标点误差率
	 * @return
	 */
	private double calculateWeight(double errorValue) {
		double alpha = 0;
		double temp = 0;

		temp = (1 - errorValue) / errorValue;
		alpha = 0.5 * Math.log(temp);

		return alpha;
	}

	/**
	 * 计算当前划分的误差率
	 * 
	 * @param pointMap
	 *            划分之后的点集
	 * @param weight
	 *            本次划分得到的分类器权重
	 * @return
	 */
	private double calculateErrorValue(
			HashMap<Integer, ArrayList<Point>> pointMap) {
		double resultValue = 0;
		double temp = 0;
		double weight = 0;
		int tempClassType;
		ArrayList<Point> pList;
		for (Map.Entry entry : pointMap.entrySet()) {
			tempClassType = (int) entry.getKey();

			pList = (ArrayList<Point>) entry.getValue();
			for (Point p : pList) {
				temp = p.getProbably();
				// 如果划分类型不相等，代表划错了
				if (tempClassType != p.getClassType()) {
					resultValue += temp;
				}
			}
		}

		weight = calculateWeight(resultValue);
		for (Map.Entry entry : pointMap.entrySet()) {
			tempClassType = (int) entry.getKey();

			pList = (ArrayList<Point>) entry.getValue();
			for (Point p : pList) {
				temp = p.getProbably();
				// 如果划分类型不相等，代表划错了
				if (tempClassType != p.getClassType()) {
					// 划错的点的权重比例变大
					temp *= Math.exp(weight);
					p.setProbably(temp);
				} else {
					// 划对的点的权重比减小
					temp *= Math.exp(-weight);
					p.setProbably(temp);
				}
			}
		}

		// 如果误差率没有小于阈值，继续处理
		dataNormalized();

		return resultValue;
	}

	/**
	 * 概率做归一化处理
	 */
	private void dataNormalized() {
		double sumProbably = 0;
		double temp = 0;

		for (Point p : totalPoint) {
			sumProbably += p.getProbably();
		}

		// 归一化处理
		for (Point p : totalPoint) {
			temp = p.getProbably();
			p.setProbably(temp / sumProbably);
		}
	}

	/**
	 * 用AdaBoost算法得到的组合分类器对数据进行分类
	 * 
	 */
	public void adaBoostClassify() {
		double value = 0;
		Point p;

		calculateWeightArray();
		for (int i = 0; i < ClASSIFICATION.length; i++) {
			System.out.println(MessageFormat.format("分类器{0}权重为：{1}", (i+1), CLASSIFICATION_WEIGHT[i]));
		}
		
		for (int j = 0; j < totalPoint.size(); j++) {
			p = totalPoint.get(j);
			value = 0;

			for (int i = 0; i < ClASSIFICATION.length; i++) {
				value += 1.0 * classifyData(ClASSIFICATION[i], p)
						* CLASSIFICATION_WEIGHT[i];
			}
			
			//进行符号判断
			if (value > 0) {
				System.out
						.println(MessageFormat.format(
								"点({0}, {1})的组合分类结果为：1，该点的实际分类为{2}", p.getX(), p.getY(),
								p.getClassType()));
			} else {
				System.out.println(MessageFormat.format(
						"点({0}, {1})的组合分类结果为：-1，该点的实际分类为{2}", p.getX(), p.getY(),
						p.getClassType()));
			}
		}
	}

	/**
	 * 计算分类器权重数组
	 */
	private void calculateWeightArray() {
		int tempClassType = 0;
		double errorValue = 0;
		ArrayList<Point> posPointList;
		ArrayList<Point> negPointList;
		HashMap<Integer, ArrayList<Point>> mapList;
		CLASSIFICATION_WEIGHT = new double[ClASSIFICATION.length];

		for (int i = 0; i < CLASSIFICATION_WEIGHT.length; i++) {
			mapList = new HashMap<>();
			posPointList = new ArrayList<>();
			negPointList = new ArrayList<>();

			for (Point p : totalPoint) {
				tempClassType = classifyData(ClASSIFICATION[i], p);

				if (tempClassType == CLASS_POSITIVE) {
					posPointList.add(p);
				} else {
					negPointList.add(p);
				}
			}

			mapList.put(CLASS_POSITIVE, posPointList);
			mapList.put(CLASS_NEGTIVE, negPointList);

			if (i == 0) {
				// 最开始的各个点的权重一样，所以传入0，使得e的0次方等于1
				errorValue = calculateErrorValue(mapList);
			} else {
				// 每次把上次计算所得的权重代入，进行概率的扩大或缩小
				errorValue = calculateErrorValue(mapList);
			}

			// 计算当前分类器的所得权重
			CLASSIFICATION_WEIGHT[i] = calculateWeight(errorValue);
		}
	}

	/**
	 * 用各个子分类器进行分类
	 * 
	 * @param classification
	 *            分类器名称
	 * @param p
	 *            待划分坐标点
	 * @return
	 */
	private int classifyData(String classification, Point p) {
		// 分割线所属坐标轴
		String position;
		// 分割线的值
		double value = 0;
		double posProbably = 0;
		double negProbably = 0;
		// 划分是否是大于一边的划分
		boolean isLarger = false;
		String[] array;
		ArrayList<Point> pList = new ArrayList<>();

		array = classification.split("=");
		position = array[0];
		value = Double.parseDouble(array[1]);

		if (position.equals("X")) {
			if (p.getX() > value) {
				isLarger = true;
			}

			// 将训练数据中所有属于这边的点加入
			for (Point point : totalPoint) {
				if (isLarger && point.getX() > value) {
					pList.add(point);
				} else if (!isLarger && point.getX() < value) {
					pList.add(point);
				}
			}
		} else if (position.equals("Y")) {
			if (p.getY() > value) {
				isLarger = true;
			}

			// 将训练数据中所有属于这边的点加入
			for (Point point : totalPoint) {
				if (isLarger && point.getY() > value) {
					pList.add(point);
				} else if (!isLarger && point.getY() < value) {
					pList.add(point);
				}
			}
		}

		for (Point p2 : pList) {
			if (p2.getClassType() == CLASS_POSITIVE) {
				posProbably++;
			} else {
				negProbably++;
			}
		}
		
		//分类按正负类数量进行划分
		if (posProbably > negProbably) {
			return CLASS_POSITIVE;
		} else {
			return CLASS_NEGTIVE;
		}
	}

}
