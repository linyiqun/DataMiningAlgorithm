package DataMining_SVM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import DataMining_SVM.libsvm.svm;
import DataMining_SVM.libsvm.svm_model;
import DataMining_SVM.libsvm.svm_node;
import DataMining_SVM.libsvm.svm_parameter;
import DataMining_SVM.libsvm.svm_problem;

/**
 * SVM支持向量机工具类
 * 
 * @author lyq
 * 
 */
public class SVMTool {
	// 训练集数据文件路径
	private String trainDataPath;
	// svm_problem对象，用于构造svm model模型
	private svm_problem sProblem;
	// svm参数，里面有svm支持向量机的类型和不同 的svm的核函数类型
	private svm_parameter sParam;

	public SVMTool(String trainDataPath) {
		this.trainDataPath = trainDataPath;

		// 初始化svm相关变量
		sProblem = initSvmProblem();
		sParam = initSvmParam();
	}
	
	/**
	 * 初始化操作，根据训练集数据构造分类模型
	 */
	private void initOperation(){
		
	}

	/**
	 * svm_problem对象，训练集数据的相关信息配置
	 * 
	 * @return
	 */
	private svm_problem initSvmProblem() {
		List<Double> label = new ArrayList<Double>();
		List<svm_node[]> nodeSet = new ArrayList<svm_node[]>();
		getData(nodeSet, label, trainDataPath);

		int dataRange = nodeSet.get(0).length;
		svm_node[][] datas = new svm_node[nodeSet.size()][dataRange]; // 训练集的向量表
		for (int i = 0; i < datas.length; i++) {
			for (int j = 0; j < dataRange; j++) {
				datas[i][j] = nodeSet.get(i)[j];
			}
		}
		double[] lables = new double[label.size()]; // a,b 对应的lable
		for (int i = 0; i < lables.length; i++) {
			lables[i] = label.get(i);
		}

		// 定义svm_problem对象
		svm_problem problem = new svm_problem();
		problem.l = nodeSet.size(); // 向量个数
		problem.x = datas; // 训练集向量表
		problem.y = lables; // 对应的lable数组

		return problem;
	}

	/**
	 * 初始化svm支持向量机的参数，包括svm的类型和核函数的类型
	 * 
	 * @return
	 */
	private svm_parameter initSvmParam() {
		// 定义svm_parameter对象
		svm_parameter param = new svm_parameter();
		param.svm_type = svm_parameter.EPSILON_SVR;
		// 设置svm的核函数类型为线型
		param.kernel_type = svm_parameter.LINEAR;
		// 后面的参数配置只针对训练集的数据
		param.cache_size = 100;
		param.eps = 0.00001;
		param.C = 1.9;

		return param;
	}

	/**
	 * 通过svm方式预测数据的类型
	 * 
	 * @param testDataPath
	 */
	public void svmPredictData(String testDataPath) {
		// 获取测试数据
		List<Double> testlabel = new ArrayList<Double>();
		List<svm_node[]> testnodeSet = new ArrayList<svm_node[]>();
		getData(testnodeSet, testlabel, testDataPath);
		int dataRange = testnodeSet.get(0).length;

		svm_node[][] testdatas = new svm_node[testnodeSet.size()][dataRange]; // 训练集的向量表
		for (int i = 0; i < testdatas.length; i++) {
			for (int j = 0; j < dataRange; j++) {
				testdatas[i][j] = testnodeSet.get(i)[j];
			}
		}
		// 测试数据的真实值，在后面将会与svm的预测值做比较
		double[] testlables = new double[testlabel.size()]; // a,b 对应的lable
		for (int i = 0; i < testlables.length; i++) {
			testlables[i] = testlabel.get(i);
		}

		// 如果参数没有问题，则svm.svm_check_parameter()函数返回null,否则返回error描述。
		// 对svm的配置参数叫验证，因为有些参数只针对部分的支持向量机的类型
		System.out.println(svm.svm_check_parameter(sProblem, sParam));
		System.out.println("------------检验参数-----------");
		// 训练SVM分类模型
		svm_model model = svm.svm_train(sProblem, sParam);

		// 预测测试数据的lable
		double err = 0.0;
		for (int i = 0; i < testdatas.length; i++) {
			double truevalue = testlables[i];
			// 测试数据真实值
			System.out.print(truevalue + " ");
			double predictValue = svm.svm_predict(model, testdatas[i]);
			// 测试数据预测值
			System.out.println(predictValue);
		}
	}

	/**
	 * 从文件中获取数据
	 * 
	 * @param nodeSet
	 *            向量节点
	 * @param label
	 *            节点值类型值
	 * @param filename
	 *            数据文件地址
	 */
	private void getData(List<svm_node[]> nodeSet, List<Double> label,
			String filename) {
		try {

			FileReader fr = new FileReader(new File(filename));
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] datas = line.split(",");
				svm_node[] vector = new svm_node[datas.length - 1];
				for (int i = 0; i < datas.length - 1; i++) {
					svm_node node = new svm_node();
					node.index = i + 1;
					node.value = Double.parseDouble(datas[i]);
					vector[i] = node;
				}
				nodeSet.add(vector);
				double lablevalue = Double.parseDouble(datas[datas.length - 1]);
				label.add(lablevalue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
