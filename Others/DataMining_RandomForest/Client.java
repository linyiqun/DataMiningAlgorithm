package DataMining_RandomForest;

import java.text.MessageFormat;

/**
 * 随机森林算法测试场景
 * 
 * @author lyq
 * 
 */
public class Client {
	public static void main(String[] args) {
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\input.txt";
		String queryStr = "Age=Youth,Income=Low,Student=No,CreditRating=Fair";
		String resultClassType = "";
		// 决策树的样本占总数的占比率
		double sampleNumRatio = 0.4;
		// 样本数据的采集特征数量占总特征的比例
		double featureNumRatio = 0.5;

		RandomForestTool tool = new RandomForestTool(filePath, sampleNumRatio,
				featureNumRatio);
		tool.constructRandomTree();

		resultClassType = tool.judgeClassType(queryStr);

		System.out.println();
		System.out
				.println(MessageFormat.format(
						"查询属性描述{0},预测的分类结果为BuysCompute:{1}", queryStr,
						resultClassType));
	}
}
