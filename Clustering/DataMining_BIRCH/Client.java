package DataMining_BIRCH;

/**
 * BIRCH聚类算法调用类
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\testInput.txt";
		//内部节点平衡因子B
		int B = 2;
		//叶子节点平衡因子L
		int L = 2;
		//簇直径阈值T
		double T = 0.6;
		
		BIRCHTool tool = new BIRCHTool(filePath, B, L, T);
		tool.startBuilding();
	}
}
