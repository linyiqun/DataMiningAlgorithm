package GA;

import java.util.ArrayList;
import java.util.Random;

/**
 * 遗传算法工具类
 * 
 * @author lyq
 * 
 */
public class GATool {
	// 变量最小值
	private int minNum;
	// 变量最大值
	private int maxNum;
	// 单个变量的编码位数
	private int codeNum;
	// 初始种群的数量
	private int initSetsNum;
	// 随机数生成器
	private Random random;
	// 初始群体
	private ArrayList<int[]> initSets;

	public GATool(int minNum, int maxNum, int initSetsNum) {
		this.minNum = minNum;
		this.maxNum = maxNum;
		this.initSetsNum = initSetsNum;

		this.random = new Random();
		produceInitSets();
	}

	/**
	 * 产生初始化群体
	 */
	private void produceInitSets() {
		this.codeNum = 0;
		int num = maxNum;
		int[] array;

		initSets = new ArrayList<>();

		// 确定编码位数
		while (num != 0) {
			codeNum++;
			num /= 2;
		}

		for (int i = 0; i < initSetsNum; i++) {
			array = produceInitCode();
			initSets.add(array);
		}
	}

	/**
	 * 产生初始个体的编码
	 * 
	 * @return
	 */
	private int[] produceInitCode() {
		int num = 0;
		int num2 = 0;
		int[] tempArray;
		int[] array1;
		int[] array2;

		tempArray = new int[2 * codeNum];
		array1 = new int[codeNum];
		array2 = new int[codeNum];

		num = 0;
		while (num < minNum || num > maxNum) {
			num = random.nextInt(maxNum) + 1;
		}
		numToBinaryArray(array1, num);

		while (num2 < minNum || num2 > maxNum) {
			num2 = random.nextInt(maxNum) + 1;
		}
		numToBinaryArray(array2, num2);

		// 组成总的编码
		for (int i = 0, k = 0; i < tempArray.length; i++, k++) {
			if (k < codeNum) {
				tempArray[i] = array1[k];
			} else {
				tempArray[i] = array2[k - codeNum];
			}
		}

		return tempArray;
	}

	/**
	 * 选择操作，把适值较高的个体优先遗传到下一代
	 * 
	 * @param initCodes
	 *            初始个体编码
	 * @return
	 */
	private ArrayList<int[]> selectOperate(ArrayList<int[]> initCodes) {
		double randomNum = 0;
		double sumAdaptiveValue = 0;
		ArrayList<int[]> resultCodes = new ArrayList<>();
		double[] adaptiveValue = new double[initSetsNum];

		for (int i = 0; i < initSetsNum; i++) {
			adaptiveValue[i] = calCodeAdaptiveValue(initCodes.get(i));
			sumAdaptiveValue += adaptiveValue[i];
		}

		// 转成概率的形式，做归一化操作
		for (int i = 0; i < initSetsNum; i++) {
			adaptiveValue[i] = adaptiveValue[i] / sumAdaptiveValue;
		}

		for (int i = 0; i < initSetsNum; i++) {
			randomNum = random.nextInt(100) + 1;
			randomNum = randomNum / 100;
			//因为1.0是无法判断到的，,总和会无限接近1.0取为0.99做判断
			if(randomNum == 1){
				randomNum = randomNum - 0.01;
			}

			sumAdaptiveValue = 0;
			// 确定区间
			for (int j = 0; j < initSetsNum; j++) {
				if (randomNum > sumAdaptiveValue
						&& randomNum <= sumAdaptiveValue + adaptiveValue[j]) {
					//采用拷贝的方式避免引用重复
					resultCodes.add(initCodes.get(j).clone());
					break;
				} else {
					sumAdaptiveValue += adaptiveValue[j];
				}
			}
		}

		return resultCodes;
	}

	/**
	 * 交叉运算
	 * 
	 * @param selectedCodes
	 *            上步骤的选择后的编码
	 * @return
	 */
	private ArrayList<int[]> crossOperate(ArrayList<int[]> selectedCodes) {
		int randomNum = 0;
		// 交叉点
		int crossPoint = 0;
		ArrayList<int[]> resultCodes = new ArrayList<>();
		// 随机编码队列，进行随机交叉配对
		ArrayList<int[]> randomCodeSeqs = new ArrayList<>();

		// 进行随机排序
		while (selectedCodes.size() > 0) {
			randomNum = random.nextInt(selectedCodes.size());

			randomCodeSeqs.add(selectedCodes.get(randomNum));
			selectedCodes.remove(randomNum);
		}

		int temp = 0;
		int[] array1;
		int[] array2;
		// 进行两两交叉运算
		for (int i = 1; i < randomCodeSeqs.size(); i++) {
			if (i % 2 == 1) {
				array1 = randomCodeSeqs.get(i - 1);
				array2 = randomCodeSeqs.get(i);
				crossPoint = random.nextInt(2 * codeNum - 1) + 1;

				// 进行交叉点位置后的编码调换
				for (int j = 0; j < 2 * codeNum; j++) {
					if (j >= crossPoint) {
						temp = array1[j];
						array1[j] = array2[j];
						array2[j] = temp;
					}
				}

				// 加入到交叉运算结果中
				resultCodes.add(array1);
				resultCodes.add(array2);
			}
		}

		return resultCodes;
	}

	/**
	 * 变异操作
	 * 
	 * @param crossCodes
	 *            交叉运算后的结果
	 * @return
	 */
	private ArrayList<int[]> variationOperate(ArrayList<int[]> crossCodes) {
		// 变异点
		int variationPoint = 0;
		ArrayList<int[]> resultCodes = new ArrayList<>();

		for (int[] array : crossCodes) {
			variationPoint = random.nextInt(codeNum * 2);

			for (int i = 0; i < array.length; i++) {
				// 变异点进行变异
				if (i == variationPoint) {
					array[i] = (array[i] == 0 ? 1 : 0);
					break;
				}
			}

			resultCodes.add(array);
		}

		return resultCodes;
	}

	/**
	 * 数字转为二进制形式
	 * 
	 * @param binaryArray
	 *            转化后的二进制数组形式
	 * @param num
	 *            待转化数字
	 */
	private void numToBinaryArray(int[] binaryArray, int num) {
		int index = 0;
		int temp = 0;
		while (num != 0) {
			binaryArray[index] = num % 2;
			index++;
			num /= 2;
		}
		
		//进行数组前和尾部的调换
		for(int i=0; i<binaryArray.length/2; i++){
			temp = binaryArray[i];
			binaryArray[i] = binaryArray[binaryArray.length - 1 - i];
			binaryArray[binaryArray.length - 1 - i] = temp;
		}
	}

	/**
	 * 二进制数组转化为数字
	 * 
	 * @param binaryArray
	 *            待转化二进制数组
	 */
	private int binaryArrayToNum(int[] binaryArray) {
		int result = 0;

		for (int i = binaryArray.length-1, k=0; i >=0 ; i--, k++) {
			if (binaryArray[i] == 1) {
				result += Math.pow(2, k);
			}
		}

		return result;
	}

	/**
	 * 计算个体编码的适值
	 * 
	 * @param codeArray
	 */
	private int calCodeAdaptiveValue(int[] codeArray) {
		int result = 0;
		int x1 = 0;
		int x2 = 0;
		int[] array1 = new int[codeNum];
		int[] array2 = new int[codeNum];

		for (int i = 0, k = 0; i < codeArray.length; i++, k++) {
			if (k < codeNum) {
				array1[k] = codeArray[i];
			} else {
				array2[k - codeNum] = codeArray[i];
			}
		}

		// 进行适值的叠加
		x1 = binaryArrayToNum(array1);
		x2 = binaryArrayToNum(array2);
		result = x1 * x1 + x2 * x2;

		return result;
	}

	/**
	 * 进行遗传算法计算
	 */
	public void geneticCal() {
		// 最大适值
		int maxFitness;
		//迭代遗传次数
		int loopCount = 0;
		boolean canExit = false;
		ArrayList<int[]> initCodes;
		ArrayList<int[]> selectedCodes;
		ArrayList<int[]> crossedCodes;
		ArrayList<int[]> variationCodes;
		
		int[] maxCode = new int[2*codeNum];
		//计算最大适值
		for(int i=0; i<2*codeNum; i++){
			maxCode[i] = 1;
		}
		maxFitness = calCodeAdaptiveValue(maxCode);

		initCodes = initSets;
		while (true) {
			for (int[] array : initCodes) {
				// 遗传迭代的终止条件为存在编码达到最大适值
				if (maxFitness == calCodeAdaptiveValue(array)) {
					canExit = true;
					break;
				}
			}

			if (canExit) {
				break;
			}

			selectedCodes = selectOperate(initCodes);
			crossedCodes = crossOperate(selectedCodes);
			variationCodes = variationOperate(crossedCodes);
			initCodes = variationCodes;
			
			loopCount++;
		}

		System.out.println("总共遗传进化了" + loopCount +"次" );
		printFinalCodes(initCodes);
	}

	/**
	 * 输出最后的编码集
	 * 
	 * @param finalCodes
	 *            最后的结果编码
	 */
	private void printFinalCodes(ArrayList<int[]> finalCodes) {
		int j = 0;

		for (int[] array : finalCodes) {
			System.out.print("个体" + (j + 1) + ":");
			for (int i = 0; i < array.length; i++) {
				System.out.print(array[i]);
			}
			System.out.println();
			j++;
		}
	}

}
