package DataMining_EM;

/**
 * EM期望最大化算法场景调用类
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\input.txt";
		
		EMTool tool = new EMTool(filePath);
		tool.readDataFile();
		tool.exceptMaxStep();
	}
}
