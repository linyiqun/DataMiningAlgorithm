package DataMining_SVM;

/**
 * SVM支持向量机场景调用类
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//训练集数据文件路径
		String trainDataPath = "C:\\Users\\lyq\\Desktop\\icon\\trainInput.txt";
		//测试数据文件路径
		String testDataPath = "C:\\Users\\lyq\\Desktop\\icon\\testInput.txt";
		
		SVMTool tool = new SVMTool(trainDataPath);
		//对测试数据进行svm支持向量机分类
		tool.svmPredictData(testDataPath);
	}

}
