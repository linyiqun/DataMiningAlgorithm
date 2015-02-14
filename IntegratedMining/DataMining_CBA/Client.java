package DataMining_CBA;

import java.text.MessageFormat;

/**
 * CBA算法--基于关联规则的分类算法
 * @author lyq
 *
 */
public class Client {
	public static void main(String[] args){
		String filePath = "C:\\Users\\lyq\\Desktop\\icon\\input.txt";
		String attrDesc = "Age=Senior,CreditRating=Fair";
		String classification = null;
		
		//最小支持度阈值率
		double minSupportRate = 0.2;
		//最小置信度阈值
		double minConf = 0.7;
		
		CBATool tool = new CBATool(filePath, minSupportRate, minConf);
		classification = tool.CBAJudge(attrDesc);
		System.out.println(MessageFormat.format("{0}的关联分类结果为{1}", attrDesc, classification));
	}
}
