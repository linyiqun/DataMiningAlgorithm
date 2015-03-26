package GA_Maze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * 遗传算法在走迷宫游戏的应用-遗传算法工具类
 * 
 * @author lyq
 * 
 */
public class GATool {
	// 迷宫出入口标记
	public static final int MAZE_ENTRANCE_POS = 1;
	public static final int MAZE_EXIT_POS = 2;
	// 方向对应的编码数组
	public static final int[][] MAZE_DIRECTION_CODE = new int[][] { { 0, 0 },
			{ 0, 1 }, { 1, 0 }, { 1, 1 }, };
	// 坐标点方向改变
	public static final int[][] MAZE_DIRECTION_CHANGE = new int[][] {
			{ -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, };
	// 方向的文字描述
	public static final String[] MAZE_DIRECTION_LABEL = new String[] { "上",
			"下", "左", "右" };

	// 地图数据文件地址
	private String filePath;
	// 走迷宫的最短步数
	private int stepNum;
	// 初始个体的数量
	private int initSetsNum;
	// 迷宫入口位置
	private int[] startPos;
	// 迷宫出口位置
	private int[] endPos;
	// 迷宫地图数据
	private int[][] mazeData;
	// 初始个体集
	private ArrayList<int[]> initSets;
	// 随机数产生器
	private Random random;

	public GATool(String filePath, int initSetsNum) {
		this.filePath = filePath;
		this.initSetsNum = initSetsNum;

		readDataFile();
	}

	/**
	 * 从文件中读取数据
	 */
	public void readDataFile() {
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

		int rowNum = dataArray.size();
		mazeData = new int[rowNum][rowNum];
		for (int i = 0; i < rowNum; i++) {
			String[] data = dataArray.get(i);
			for (int j = 0; j < data.length; j++) {
				mazeData[i][j] = Integer.parseInt(data[j]);

				// 赋值入口和出口位置
				if (mazeData[i][j] == MAZE_ENTRANCE_POS) {
					startPos = new int[2];
					startPos[0] = i;
					startPos[1] = j;
				} else if (mazeData[i][j] == MAZE_EXIT_POS) {
					endPos = new int[2];
					endPos[0] = i;
					endPos[1] = j;
				}
			}
		}

		// 计算走出迷宫的最短步数
		stepNum = Math.abs(startPos[0] - endPos[0])
				+ Math.abs(startPos[1] - endPos[1]);
	}

	/**
	 * 产生初始数据集
	 */
	private void produceInitSet() {
		// 方向编码
		int directionCode = 0;
		random = new Random();
		initSets = new ArrayList<>();
		// 每个步骤的操作需要用2位数字表示
		int[] codeNum;

		for (int i = 0; i < initSetsNum; i++) {
			codeNum = new int[stepNum * 2];
			for (int j = 0; j < stepNum; j++) {
				directionCode = random.nextInt(4);
				codeNum[2 * j] = MAZE_DIRECTION_CODE[directionCode][0];
				codeNum[2 * j + 1] = MAZE_DIRECTION_CODE[directionCode][1];
			}

			initSets.add(codeNum);
		}
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
		double sumFitness = 0;
		ArrayList<int[]> resultCodes = new ArrayList<>();
		double[] adaptiveValue = new double[initSetsNum];

		for (int i = 0; i < initSetsNum; i++) {
			adaptiveValue[i] = calFitness(initCodes.get(i));
			sumFitness += adaptiveValue[i];
		}

		// 转成概率的形式，做归一化操作
		for (int i = 0; i < initSetsNum; i++) {
			adaptiveValue[i] = adaptiveValue[i] / sumFitness;
		}

		for (int i = 0; i < initSetsNum; i++) {
			randomNum = random.nextInt(100) + 1;
			randomNum = randomNum / 100;
			//因为1.0是无法判断到的，,总和会无限接近1.0取为0.99做判断
			if(randomNum == 1){
				randomNum = randomNum - 0.01;
			}
			
			sumFitness = 0;
			// 确定区间
			for (int j = 0; j < initSetsNum; j++) {
				if (randomNum > sumFitness
						&& randomNum <= sumFitness + adaptiveValue[j]) {
					// 采用拷贝的方式避免引用重复
					resultCodes.add(initCodes.get(j).clone());
					break;
				} else {
					sumFitness += adaptiveValue[j];
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
				crossPoint = random.nextInt(stepNum - 1) + 1;

				// 进行交叉点位置后的编码调换
				for (int j = 0; j < 2 * stepNum; j++) {
					if (j >= 2 * crossPoint) {
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
			variationPoint = random.nextInt(stepNum);

			for (int i = 0; i < array.length; i += 2) {
				// 变异点进行变异
				if (i % 2 == 0 && i / 2 == variationPoint) {
					array[i] = (array[i] == 0 ? 1 : 0);
					array[i + 1] = (array[i + 1] == 0 ? 1 : 0);
					break;
				}
			}

			resultCodes.add(array);
		}

		return resultCodes;
	}

	/**
	 * 根据编码计算适值
	 * 
	 * @param code
	 *            当前的编码
	 * @return
	 */
	public double calFitness(int[] code) {
		double fintness = 0;
		// 由编码计算所得的终点横坐标
		int endX = 0;
		// 由编码计算所得的终点纵坐标
		int endY = 0;
		// 基于片段所代表的行走方向
		int direction = 0;
		// 临时坐标点横坐标
		int tempX = 0;
		// 临时坐标点纵坐标
		int tempY = 0;

		endX = startPos[0];
		endY = startPos[1];
		for (int i = 0; i < stepNum; i++) {
			direction = binaryArrayToNum(new int[] { code[2 * i],
					code[2 * i + 1] });

			// 根据方向改变数组做坐标点的改变
			tempX = endX + MAZE_DIRECTION_CHANGE[direction][0];
			tempY = endY + MAZE_DIRECTION_CHANGE[direction][1];

			// 判断坐标点是否越界
			if (tempX >= 0 && tempX < mazeData.length && tempY >= 0
					&& tempY < mazeData[0].length) {
				// 判断坐标点是否走到阻碍块
				if (mazeData[tempX][tempY] != -1) {
					endX = tempX;
					endY = tempY;
				}
			}
		}

		// 根据适值函数进行适值的计算
		fintness = 1.0 / (Math.abs(endX - endPos[0])
				+ Math.abs(endY - endPos[1]) + 1);

		return fintness;
	}

	/**
	 * 根据当前编码判断是否已经找到出口位置
	 * 
	 * @param code
	 *            经过若干次遗传的编码
	 * @return
	 */
	private boolean ifArriveEndPos(int[] code) {
		boolean isArrived = false;
		// 由编码计算所得的终点横坐标
		int endX = 0;
		// 由编码计算所得的终点纵坐标
		int endY = 0;
		// 基于片段所代表的行走方向
		int direction = 0;
		// 临时坐标点横坐标
		int tempX = 0;
		// 临时坐标点纵坐标
		int tempY = 0;

		endX = startPos[0];
		endY = startPos[1];
		for (int i = 0; i < stepNum; i++) {
			direction = binaryArrayToNum(new int[] { code[2 * i],
					code[2 * i + 1] });

			// 根据方向改变数组做坐标点的改变
			tempX = endX + MAZE_DIRECTION_CHANGE[direction][0];
			tempY = endY + MAZE_DIRECTION_CHANGE[direction][1];

			// 判断坐标点是否越界
			if (tempX >= 0 && tempX < mazeData.length && tempY >= 0
					&& tempY < mazeData[0].length) {
				// 判断坐标点是否走到阻碍块
				if (mazeData[tempX][tempY] != -1) {
					endX = tempX;
					endY = tempY;
				}
			}
		}

		if (endX == endPos[0] && endY == endPos[1]) {
			isArrived = true;
		}

		return isArrived;
	}

	/**
	 * 二进制数组转化为数字
	 * 
	 * @param binaryArray
	 *            待转化二进制数组
	 */
	private int binaryArrayToNum(int[] binaryArray) {
		int result = 0;

		for (int i = binaryArray.length - 1, k = 0; i >= 0; i--, k++) {
			if (binaryArray[i] == 1) {
				result += Math.pow(2, k);
			}
		}

		return result;
	}

	/**
	 * 进行遗传算法走出迷宫
	 */
	public void goOutMaze() {
		// 迭代遗传次数
		int loopCount = 0;
		boolean canExit = false;
		// 结果路径
		int[] resultCode = null;
		ArrayList<int[]> initCodes;
		ArrayList<int[]> selectedCodes;
		ArrayList<int[]> crossedCodes;
		ArrayList<int[]> variationCodes;

		// 产生初始数据集
		produceInitSet();
		initCodes = initSets;

		while (true) {
			for (int[] array : initCodes) {
				// 遗传迭代的终止条件为是否找到出口位置
				if (ifArriveEndPos(array)) {
					resultCode = array;
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
			
			//如果遗传次数超过100次，则退出
			if(loopCount >= 100){
				break;
			}
		}

		System.out.println("总共遗传进化了" + loopCount + "次");
		printFindedRoute(resultCode);
	}

	/**
	 * 输出找到的路径
	 * 
	 * @param code
	 */
	private void printFindedRoute(int[] code) {
		if(code == null){
			System.out.println("在有限的遗传进化次数内，没有找到最优路径");
			return;
		}
		
		int tempX = startPos[0];
		int tempY = startPos[1];
		int direction = 0;

		System.out.println(MessageFormat.format(
				"起始点位置({0},{1}), 出口点位置({2}, {3})", tempX, tempY, endPos[0],
				endPos[1]));
		
		System.out.print("搜索到的结果编码：");
		for(int value: code){
			System.out.print("" + value);
		}
		System.out.println();
		
		for (int i = 0, k = 1; i < code.length; i += 2, k++) {
			direction = binaryArrayToNum(new int[] { code[i], code[i + 1] });

			tempX += MAZE_DIRECTION_CHANGE[direction][0];
			tempY += MAZE_DIRECTION_CHANGE[direction][1];

			System.out.println(MessageFormat.format(
					"第{0}步,编码为{1}{2},向{3}移动，移动后到达({4},{5})", k, code[i], code[i+1],
					MAZE_DIRECTION_LABEL[direction],  tempX, tempY));
		}
	}

}
