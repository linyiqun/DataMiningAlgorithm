package DataMining_RandomForest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 随机森林算法工具类
 * 
 * @author lyq
 * 
 */
public class RandomForestTool {
	// 测试数据文件地址
	private String filePath;
	// 决策树的样本占总数的占比率
	private double sampleNumRatio;
	// 样本数据的采集特征数量占总特征的比例
	private double featureNumRatio;
	// 决策树的采样样本数
	private int sampleNum;
	// 样本数据的采集采样特征数
	private int featureNum;
	// 随机森林中的决策树的数目,等于总的数据数/用于构造每棵树的数据的数量
	private int treeNum;
	// 随机数产生器
	private Random random;
	// 样本数据列属性名称行
	private String[] featureNames;
	// 原始的总的数据
	private ArrayList<String[]> totalDatas;
	// 决策树森林
	private ArrayList<DecisionTree> decisionForest;

	public RandomForestTool(String filePath, double sampleNumRatio,
			double featureNumRatio) {
		this.filePath = filePath;
		this.sampleNumRatio = sampleNumRatio;
		this.featureNumRatio = featureNumRatio;

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

		totalDatas = dataArray;
		featureNames = totalDatas.get(0);
		sampleNum = (int) ((totalDatas.size() - 1) * sampleNumRatio);
		//算属性数量的时候需要去掉id属性和决策属性，用条件属性计算
		featureNum = (int) ((featureNames.length -2) * featureNumRatio);
		// 算数量的时候需要去掉首行属性名称行
		treeNum = (totalDatas.size() - 1) / sampleNum;
	}

	/**
	 * 产生决策树
	 */
	private DecisionTree produceDecisionTree() {
		int temp = 0;
		DecisionTree tree;
		String[] tempData;
		//采样数据的随机行号组
		ArrayList<Integer> sampleRandomNum;
		//采样属性特征的随机列号组
		ArrayList<Integer> featureRandomNum;
		ArrayList<String[]> datas;
		
		sampleRandomNum = new ArrayList<>();
		featureRandomNum = new ArrayList<>();
		datas = new ArrayList<>();
		
		for(int i=0; i<sampleNum;){
			temp = random.nextInt(totalDatas.size());
			
			//如果是行首属性名称行，则跳过
			if(temp == 0){
				continue;
			}
			
			if(!sampleRandomNum.contains(temp)){
				sampleRandomNum.add(temp);
				i++;
			}
		}
		
		for(int i=0; i<featureNum;){
			temp = random.nextInt(featureNames.length);
			
			//如果是第一列的数据id号或者是决策属性列，则跳过
			if(temp == 0 || temp == featureNames.length-1){
				continue;
			}
			
			if(!featureRandomNum.contains(temp)){
				featureRandomNum.add(temp);
				i++;
			}
		}

		String[] singleRecord;
		String[] headCulumn = null;
		// 获取随机数据行
		for(int dataIndex: sampleRandomNum){
			singleRecord = totalDatas.get(dataIndex);
			
			//每行的列数=所选的特征数+id号
			tempData = new String[featureNum+2];
			headCulumn = new String[featureNum+2];
			
			for(int i=0,k=1; i<featureRandomNum.size(); i++,k++){
				temp = featureRandomNum.get(i);
				
				headCulumn[k] = featureNames[temp];
				tempData[k] = singleRecord[temp];
			}
			
			//加上id列的信息
			headCulumn[0] = featureNames[0];
			//加上决策分类列的信息
			headCulumn[featureNum+1] = featureNames[featureNames.length-1];
			tempData[featureNum+1] = singleRecord[featureNames.length-1];
			
			//加入此行数据
			datas.add(tempData);
		}
		
		//加入行首列出现名称
		datas.add(0, headCulumn);
		//对筛选出的数据重新做id分配
		temp = 0;
		for(String[] array: datas){
			//从第2行开始赋值
			if(temp > 0){
				array[0] = temp + "";
			}
			
			temp++;
		}
		
		tree = new DecisionTree(datas);
		
		return tree;
	}

	/**
	 * 构造随机森林
	 */
	public void constructRandomTree() {
		DecisionTree tree;
		random = new Random();
		decisionForest = new ArrayList<>();

		System.out.println("下面是随机森林中的决策树：");
		// 构造决策树加入森林中
		for (int i = 0; i < treeNum; i++) {
			System.out.println("\n决策树" + (i+1));
			tree = produceDecisionTree();
			decisionForest.add(tree);
		}
	}

	/**
	 * 根据给定的属性条件进行类别的决策
	 * 
	 * @param features
	 *            给定的已知的属性描述
	 * @return
	 */
	public String judgeClassType(String features) {
		// 结果类型值
		String resultClassType = "";
		String classType = "";
		int count = 0;
		Map<String, Integer> type2Num = new HashMap<String, Integer>();

		for (DecisionTree tree : decisionForest) {
			classType = tree.decideClassType(features);
			if (type2Num.containsKey(classType)) {
				// 如果类别已经存在，则使其计数加1
				count = type2Num.get(classType);
				count++;
			} else {
				count = 1;
			}

			type2Num.put(classType, count);
		}

		// 选出其中类别支持计数最多的一个类别值
		count = -1;
		for (Map.Entry entry : type2Num.entrySet()) {
			if ((int) entry.getValue() > count) {
				count = (int) entry.getValue();
				resultClassType = (String) entry.getKey();
			}
		}

		return resultClassType;
	}
}
