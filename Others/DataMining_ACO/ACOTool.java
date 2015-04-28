package DataMining_ACO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 蚁群算法工具类
 * 
 * @author lyq
 * 
 */
public class ACOTool {
	// 输入数据类型
	public static final int INPUT_CITY_NAME = 1;
	public static final int INPUT_CITY_DIS = 2;

	// 城市间距离邻接矩阵
	public static double[][] disMatrix;
	// 当前时间
	public static int currentTime;

	// 测试数据地址
	private String filePath;
	// 蚂蚁数量
	private int antNum;
	// 控制参数
	private double alpha;
	private double beita;
	private double p;
	private double Q;
	// 随机数产生器
	private Random random;
	// 城市名称集合,这里为了方便，将城市用数字表示
	private ArrayList<String> totalCitys;
	// 所有的蚂蚁集合
	private ArrayList<Ant> totalAnts;
	// 城市间的信息素浓度矩阵，随着时间的增多而减少
	private double[][] pheromoneMatrix;
	// 目标的最短路径,顺序为从集合的前部往后挪动
	private ArrayList<String> bestPath;
	// 信息素矩阵存储图,key采用的格式(i,j,t)->value
	private Map<String, Double> pheromoneTimeMap;

	public ACOTool(String filePath, int antNum, double alpha, double beita,
			double p, double Q) {
		this.filePath = filePath;
		this.antNum = antNum;
		this.alpha = alpha;
		this.beita = beita;
		this.p = p;
		this.Q = Q;
		this.currentTime = 0;

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

		int flag = -1;
		int src = 0;
		int des = 0;
		int size = 0;
		// 进行城市名称种数的统计
		this.totalCitys = new ArrayList<>();
		for (String[] array : dataArray) {
			if (array[0].equals("#") && totalCitys.size() == 0) {
				flag = INPUT_CITY_NAME;

				continue;
			} else if (array[0].equals("#") && totalCitys.size() > 0) {
				size = totalCitys.size();
				// 初始化距离矩阵
				this.disMatrix = new double[size + 1][size + 1];
				this.pheromoneMatrix = new double[size + 1][size + 1];

				// 初始值-1代表此对应位置无值
				for (int i = 0; i < size; i++) {
					for (int j = 0; j < size; j++) {
						this.disMatrix[i][j] = -1;
						this.pheromoneMatrix[i][j] = -1;
					}
				}

				flag = INPUT_CITY_DIS;
				continue;
			}

			if (flag == INPUT_CITY_NAME) {
				this.totalCitys.add(array[0]);
			} else {
				src = Integer.parseInt(array[0]);
				des = Integer.parseInt(array[1]);

				this.disMatrix[src][des] = Double.parseDouble(array[2]);
				this.disMatrix[des][src] = Double.parseDouble(array[2]);
			}
		}
	}

	/**
	 * 计算从蚂蚁城市i到j的概率
	 * 
	 * @param cityI
	 *            城市I
	 * @param cityJ
	 *            城市J
	 * @param currentTime
	 *            当前时间
	 * @return
	 */
	private double calIToJProbably(String cityI, String cityJ, int currentTime) {
		double pro = 0;
		double n = 0;
		double pheromone;
		int i;
		int j;

		i = Integer.parseInt(cityI);
		j = Integer.parseInt(cityJ);

		pheromone = getPheromone(currentTime, cityI, cityJ);
		n = 1.0 / disMatrix[i][j];

		if (pheromone == 0) {
			pheromone = 1;
		}

		pro = Math.pow(n, alpha) * Math.pow(pheromone, beita);

		return pro;
	}

	/**
	 * 计算综合概率蚂蚁从I城市走到J城市的概率
	 * 
	 * @return
	 */
	public String selectAntNextCity(Ant ant, int currentTime) {
		double randomNum;
		double tempPro;
		// 总概率指数
		double proTotal;
		String nextCity = null;
		ArrayList<String> allowedCitys;
		// 各城市概率集
		double[] proArray;

		// 如果是刚刚开始的时候，没有路过任何城市，则随机返回一个城市
		if (ant.currentPath.size() == 0) {
			nextCity = String.valueOf(random.nextInt(totalCitys.size()) + 1);

			return nextCity;
		} else if (ant.nonVisitedCitys.isEmpty()) {
			// 如果全部遍历完毕，则再次回到起点
			nextCity = ant.currentPath.get(0);

			return nextCity;
		}

		proTotal = 0;
		allowedCitys = ant.nonVisitedCitys;
		proArray = new double[allowedCitys.size()];

		for (int i = 0; i < allowedCitys.size(); i++) {
			nextCity = allowedCitys.get(i);
			proArray[i] = calIToJProbably(ant.currentPos, nextCity, currentTime);
			proTotal += proArray[i];
		}

		for (int i = 0; i < allowedCitys.size(); i++) {
			// 归一化处理
			proArray[i] /= proTotal;
		}

		// 用随机数选择下一个城市
		randomNum = random.nextInt(100) + 1;
		randomNum = randomNum / 100;
		// 因为1.0是无法判断到的，,总和会无限接近1.0取为0.99做判断
		if (randomNum == 1) {
			randomNum = randomNum - 0.01;
		}

		tempPro = 0;
		// 确定区间
		for (int j = 0; j < allowedCitys.size(); j++) {
			if (randomNum > tempPro && randomNum <= tempPro + proArray[j]) {
				// 采用拷贝的方式避免引用重复
				nextCity = allowedCitys.get(j);
				break;
			} else {
				tempPro += proArray[j];
			}
		}

		return nextCity;
	}

	/**
	 * 获取给定时间点上从城市i到城市j的信息素浓度
	 * 
	 * @param t
	 * @param cityI
	 * @param cityJ
	 * @return
	 */
	private double getPheromone(int t, String cityI, String cityJ) {
		double pheromone = 0;
		String key;

		// 上一周期需将时间倒回一周期
		key = MessageFormat.format("{0},{1},{2}", cityI, cityJ, t);

		if (pheromoneTimeMap.containsKey(key)) {
			pheromone = pheromoneTimeMap.get(key);
		}

		return pheromone;
	}

	/**
	 * 每轮结束，刷新信息素浓度矩阵
	 * 
	 * @param t
	 */
	private void refreshPheromone(int t) {
		double pheromone = 0;
		// 上一轮周期结束后的信息素浓度，丛信息素浓度图中查找
		double lastTimeP = 0;
		// 本轮信息素浓度增加量
		double addPheromone;
		String key;

		for (String i : totalCitys) {
			for (String j : totalCitys) {
				if (!i.equals(j)) {
					// 上一周期需将时间倒回一周期
					key = MessageFormat.format("{0},{1},{2}", i, j, t - 1);

					if (pheromoneTimeMap.containsKey(key)) {
						lastTimeP = pheromoneTimeMap.get(key);
					} else {
						lastTimeP = 0;
					}

					addPheromone = 0;
					for (Ant ant : totalAnts) {
						if(ant.pathContained(i, j)){
							// 每只蚂蚁传播的信息素为控制因子除以距离总成本
							addPheromone += Q / ant.calSumDistance();
						}
					}

					// 将上次的结果值加上递增的量，并存入图中
					pheromone = p * lastTimeP + addPheromone;
					key = MessageFormat.format("{0},{1},{2}", i, j, t);
					pheromoneTimeMap.put(key, pheromone);
				}
			}
		}

	}

	/**
	 * 蚁群算法迭代次数
	 * @param loopCount
	 * 具体遍历次数
	 */
	public void antStartSearching(int loopCount) {
		// 蚁群寻找的总次数
		int count = 0;
		// 选中的下一个城市
		String selectedCity = "";

		pheromoneTimeMap = new HashMap<String, Double>();
		totalAnts = new ArrayList<>();
		random = new Random();

		while (count < loopCount) {
			initAnts();

			while (true) {
				for (Ant ant : totalAnts) {
					selectedCity = selectAntNextCity(ant, currentTime);
					ant.goToNextCity(selectedCity);
				}

				// 如果已经遍历完所有城市，则跳出此轮循环
				if (totalAnts.get(0).isBack()) {
					break;
				}
			}

			// 周期时间叠加
			currentTime++;
			refreshPheromone(currentTime);
			count++;
		}

		// 根据距离成本，选出所花距离最短的一个路径
		Collections.sort(totalAnts);
		bestPath = totalAnts.get(0).currentPath;
		System.out.println(MessageFormat.format("经过{0}次循环遍历，最终得出的最佳路径：", count));
		System.out.print("entrance");
		for (String cityName : bestPath) {
			System.out.print(MessageFormat.format("-->{0}", cityName));
		}
	}

	/**
	 * 初始化蚁群操作
	 */
	private void initAnts() {
		Ant tempAnt;
		ArrayList<String> nonVisitedCitys;
		totalAnts.clear();

		// 初始化蚁群
		for (int i = 0; i < antNum; i++) {
			nonVisitedCitys = (ArrayList<String>) totalCitys.clone();
			tempAnt = new Ant(pheromoneMatrix, nonVisitedCitys);

			totalAnts.add(tempAnt);
		}
	}
}
