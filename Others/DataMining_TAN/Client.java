package DataMining_TAN;

/**
 * TAN树型朴素贝叶斯算法
 * 
 * @author lyq
 * 
 */
public class Client {
	public static void main(String[] args) {
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\input.txt";
		// 条件查询语句
		String queryStr;
		// 分类结果概率1
		double classResult1;
		// 分类结果概率2
		double classResult2;

		TANTool tool = new TANTool(filePath);
		queryStr = "OutLook=Sunny,Temperature=Hot,Humidity=High,Wind=Weak,PlayTennis=No";
		classResult1 = tool.calHappenedPro(queryStr);

		queryStr = "OutLook=Sunny,Temperature=Hot,Humidity=High,Wind=Weak,PlayTennis=Yes";
		classResult2 = tool.calHappenedPro(queryStr);

		System.out.println(String.format("类别为%s所求得的概率为%s", "PlayTennis=No",
				classResult1));
		System.out.println(String.format("类别为%s所求得的概率为%s", "PlayTennis=Yes",
				classResult2));
		if (classResult1 > classResult2) {
			System.out.println("分类类别为PlayTennis=No");
		} else {
			System.out.println("分类类别为PlayTennis=Yes");
		}
	}
}
