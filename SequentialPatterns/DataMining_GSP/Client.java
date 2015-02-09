package DataMining_GSP;

/**
 * GSP序列模式分析算法
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\testInput.txt";
		//最小支持度阈值
		int minSupportCount = 2;
		//时间最小间隔
		int min_gap = 1;
		//施加最大间隔
		int max_gap = 5;
		
		GSPTool tool = new GSPTool(filePath, minSupportCount, min_gap, max_gap);
		tool.gspCalculate();
	}
}
