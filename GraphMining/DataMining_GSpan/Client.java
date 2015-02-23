package DataMining_GSpan;

/**
 * gSpan频繁子图挖掘算法
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		//测试数据文件地址
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\input.txt";
		//最小支持度率
		double minSupportRate = 0.2;
		
		GSpanTool tool = new GSpanTool(filePath, minSupportRate);
		tool.freqGraphMining();
	}
}
