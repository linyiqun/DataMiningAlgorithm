package DataMining_PageRank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * PageRank网页排名算法工具类
 * 
 * @author lyq
 * 
 */
public class PageRankTool {
	// 测试输入数据
	private String filePath;
	// 网页总数量
	private int pageNum;
	// 链接关系矩阵
	private double[][] linkMatrix;
	// 每个页面pageRank值初始向量
	private double[] pageRankVecor;

	// 网页数量分类
	ArrayList<String> pageClass;

	public PageRankTool(String filePath) {
		this.filePath = filePath;
		readDataFile();
	}

	/**
	 * 从文件中读取数据
	 */
	private void readDataFile() {
		File file = new File(filePath);
		ArrayList<String[]> dataArray = new ArrayList<String[]>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			String[] tempArray;
			while ((str = in.readLine()) != null) {
				tempArray = str.split(" ");
				dataArray.add(tempArray);
			}
			in.close();
		} catch (IOException e) {
			e.getStackTrace();
		}

		pageClass = new ArrayList<>();
		// 统计网页类型种数
		for (String[] array : dataArray) {
			for (String s : array) {
				if (!pageClass.contains(s)) {
					pageClass.add(s);
				}
			}
		}

		int i = 0;
		int j = 0;
		pageNum = pageClass.size();
		linkMatrix = new double[pageNum][pageNum];
		pageRankVecor = new double[pageNum];
		for (int k = 0; k < pageNum; k++) {
			// 初始每个页面的pageRank值为1
			pageRankVecor[k] = 1.0;
		}
		for (String[] array : dataArray) {

			i = Integer.parseInt(array[0]);
			j = Integer.parseInt(array[1]);

			// 设置linkMatrix[i][j]为1代表i网页包含指向j网页的链接
			linkMatrix[i - 1][j - 1] = 1;
		}
	}

	/**
	 * 将矩阵转置
	 */
	private void transferMatrix() {
		int count = 0;
		for (double[] array : linkMatrix) {
			// 计算页面链接个数
			count = 0;
			for (double d : array) {
				if (d == 1) {
					count++;
				}
			}
			// 按概率均分
			for (int i = 0; i < array.length; i++) {
				if (array[i] == 1) {
					array[i] /= count;
				}
			}
		}

		double t = 0;
		// 将矩阵转置换，作为概率转移矩阵
		for (int i = 0; i < linkMatrix.length; i++) {
			for (int j = i + 1; j < linkMatrix[0].length; j++) {
				t = linkMatrix[i][j];
				linkMatrix[i][j] = linkMatrix[j][i];
				linkMatrix[j][i] = t;
			}
		}
	}

	/**
	 * 利用幂法计算pageRank值
	 */
	public void printPageRankValue() {
		transferMatrix();
		// 阻尼系数
		double damp = 0.5;
		// 链接概率矩阵
		double[][] A = new double[pageNum][pageNum];
		double[][] e = new double[pageNum][pageNum];

		// 调用公式A=d*q+(1-d)*e/m，m为网页总个数,d就是damp
		double temp = (1 - damp) / pageNum;
		for (int i = 0; i < e.length; i++) {
			for (int j = 0; j < e[0].length; j++) {
				e[i][j] = temp;
			}
		}

		for (int i = 0; i < pageNum; i++) {
			for (int j = 0; j < pageNum; j++) {
				temp = damp * linkMatrix[i][j] + e[i][j];
				A[i][j] = temp;

			}
		}

		// 误差值，作为判断收敛标准
		double errorValue = Integer.MAX_VALUE;
		double[] newPRVector = new double[pageNum];
		// 当平均每个PR值误差小于0.001时就算达到收敛
		while (errorValue > 0.001 * pageNum) {
			System.out.println("**********");
			for (int i = 0; i < pageNum; i++) {
				temp = 0;
				// 将A*pageRankVector,利用幂法求解,直到pageRankVector值收敛
				for (int j = 0; j < pageNum; j++) {
					// temp就是每个网页到i页面的pageRank值
					temp += A[i][j] * pageRankVecor[j];
				}

				// 最后的temp就是i网页的总PageRank值
				newPRVector[i] = temp;
				System.out.println(temp);
			}

			errorValue = 0;
			for (int i = 0; i < pageNum; i++) {
				errorValue += Math.abs(pageRankVecor[i] - newPRVector[i]);
				// 新的向量代替旧的向量
				pageRankVecor[i] = newPRVector[i];
			}
		}

		String name = null;
		temp = 0;
		System.out.println("--------------------");
		for (int i = 0; i < pageNum; i++) {
			System.out.println(MessageFormat.format("网页{0}的pageRank值：{1}",
					pageClass.get(i), pageRankVecor[i]));
			if (pageRankVecor[i] > temp) {
				temp = pageRankVecor[i];
				name = pageClass.get(i);
			}
		}
		System.out.println(MessageFormat.format("等级最高的网页为：{0}", name));
	}

}
