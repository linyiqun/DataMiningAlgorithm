package DataMining_Viterbi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 维特比算法工具类
 * 
 * @author lyq
 * 
 */
public class ViterbiTool {
	// 状态转移概率矩阵文件地址
	private String stmFilePath;
	// 混淆矩阵文件地址
	private String confusionFilePath;
	// 初始状态概率
	private double[] initStatePro;
	// 观察到的状态序列
	public String[] observeStates;
	// 状态转移矩阵值
	private double[][] stMatrix;
	// 混淆矩阵值
	private double[][] confusionMatrix;
	// 各个条件下的潜在特征概率值
	private double[][] potentialValues;
	// 潜在特征
	private ArrayList<String> potentialAttrs;
	// 属性值列坐标映射图
	private HashMap<String, Integer> name2Index;
	// 列坐标属性值映射图
	private HashMap<Integer, String> index2name;

	public ViterbiTool(String stmFilePath, String confusionFilePath,
			double[] initStatePro, String[] observeStates) {
		this.stmFilePath = stmFilePath;
		this.confusionFilePath = confusionFilePath;
		this.initStatePro = initStatePro;
		this.observeStates = observeStates;

		initOperation();
	}

	/**
	 * 初始化数据操作
	 */
	private void initOperation() {
		double[] temp;
		int index;
		ArrayList<String[]> smtDatas;
		ArrayList<String[]> cfDatas;

		smtDatas = readDataFile(stmFilePath);
		cfDatas = readDataFile(confusionFilePath);

		index = 0;
		this.stMatrix = new double[smtDatas.size()][];
		for (String[] array : smtDatas) {
			temp = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				try {
					temp[i] = Double.parseDouble(array[i]);
				} catch (NumberFormatException e) {
					temp[i] = -1;
				}
			}

			// 将转换后的值赋给数组中
			this.stMatrix[index] = temp;
			index++;
		}

		index = 0;
		this.confusionMatrix = new double[cfDatas.size()][];
		for (String[] array : cfDatas) {
			temp = new double[array.length];
			for (int i = 0; i < array.length; i++) {
				try {
					temp[i] = Double.parseDouble(array[i]);
				} catch (NumberFormatException e) {
					temp[i] = -1;
				}
			}

			// 将转换后的值赋给数组中
			this.confusionMatrix[index] = temp;
			index++;
		}

		this.potentialAttrs = new ArrayList<>();
		// 添加潜在特征属性
		for (String s : smtDatas.get(0)) {
			this.potentialAttrs.add(s);
		}
		// 去除首列无效列
		potentialAttrs.remove(0);

		this.name2Index = new HashMap<>();
		this.index2name = new HashMap<>();

		// 添加名称下标映射关系
		for (int i = 1; i < smtDatas.get(0).length; i++) {
			this.name2Index.put(smtDatas.get(0)[i], i);
			// 添加下标到名称的映射
			this.index2name.put(i, smtDatas.get(0)[i]);
		}

		for (int i = 1; i < cfDatas.get(0).length; i++) {
			this.name2Index.put(cfDatas.get(0)[i], i);
		}
	}

	/**
	 * 从文件中读取数据
	 */
	private ArrayList<String[]> readDataFile(String filePath) {
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
	 * 根据观察特征计算隐藏的特征概率矩阵
	 */
	private void calPotencialProMatrix() {
		String curObserveState;
		// 观察特征和潜在特征的下标
		int osIndex;
		int psIndex;
		double temp;
		double maxPro;
		// 混淆矩阵概率值，就是相关影响的因素概率
		double confusionPro;

		this.potentialValues = new double[observeStates.length][potentialAttrs
				.size() + 1];
		for (int i = 0; i < this.observeStates.length; i++) {
			curObserveState = this.observeStates[i];
			osIndex = this.name2Index.get(curObserveState);
			maxPro = -1;

			// 因为是第一个观察特征，没有前面的影响，根据初始状态计算
			if (i == 0) {
				for (String attr : this.potentialAttrs) {
					psIndex = this.name2Index.get(attr);
					confusionPro = this.confusionMatrix[psIndex][osIndex];

					temp = this.initStatePro[psIndex - 1] * confusionPro;
					this.potentialValues[BaseNames.DAY1][psIndex] = temp;
				}
			} else {
				// 后面的潜在特征受前一个特征的影响，以及当前的混淆因素影响
				for (String toDayAttr : this.potentialAttrs) {
					psIndex = this.name2Index.get(toDayAttr);
					confusionPro = this.confusionMatrix[psIndex][osIndex];

					int index;
					maxPro = -1;
					// 通过昨天的概率计算今天此特征的最大概率
					for (String yAttr : this.potentialAttrs) {
						index = this.name2Index.get(yAttr);
						temp = this.potentialValues[i - 1][index]
								* this.stMatrix[index][psIndex];

						// 计算得到今天此潜在特征的最大概率
						if (temp > maxPro) {
							maxPro = temp;
						}
					}

					this.potentialValues[i][psIndex] = maxPro * confusionPro;
				}
			}
		}
	}

	/**
	 * 根据同时期最大概率值输出潜在特征值
	 */
	private void outputResultAttr() {
		double maxPro;
		int maxIndex;
		ArrayList<String> psValues;

		psValues = new ArrayList<>();
		for (int i = 0; i < this.potentialValues.length; i++) {
			maxPro = -1;
			maxIndex = 0;

			for (int j = 0; j < potentialValues[i].length; j++) {
				if (this.potentialValues[i][j] > maxPro) {
					maxPro = potentialValues[i][j];
					maxIndex = j;
				}
			}

			// 取出最大概率下标对应的潜在特征
			psValues.add(this.index2name.get(maxIndex));
		}

		System.out.println("观察特征为：");
		for (String s : this.observeStates) {
			System.out.print(s + ", ");
		}
		System.out.println();

		System.out.println("潜在特征为：");
		for (String s : psValues) {
			System.out.print(s + ", ");
		}
		System.out.println();
	}

	/**
	 * 根据观察属性，得到潜在属性信息
	 */
	public void calHMMObserve() {
		calPotencialProMatrix();
		outputResultAttr();
	}
}
