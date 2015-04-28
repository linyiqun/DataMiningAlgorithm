package DataMining_ACO;

/**
 * 蚁群算法测试类
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//测试数据
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\input.txt";
		//蚂蚁数量
		int antNum;
		//蚁群算法迭代次数
		int loopCount;
		//控制参数
		double alpha;
		double beita;
		double p;
		double Q;
		
		antNum = 3;
		alpha = 0.5;
		beita = 1;
		p = 0.5;
		Q = 5;
		loopCount = 5;
		
		ACOTool tool = new ACOTool(filePath, antNum, alpha, beita, p, Q);
		tool.antStartSearching(loopCount);
	}
}
