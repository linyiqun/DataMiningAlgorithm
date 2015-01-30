package DataMining_HITS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * HITS链接分析算法工具类
 * @author lyq
 *
 */
public class HITSTool {
	//输入数据文件地址
	private String filePath;
	//网页个数
	private int pageNum;
	//网页Authority权威值
	private double[] authority;
	//网页hub中心值
	private double[] hub;
	//链接矩阵关系
	private int[][] linkMatrix;
	//网页种类
	private ArrayList<String> pageClass;
	
	public HITSTool(String filePath){
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
		linkMatrix = new int[pageNum][pageNum];
		authority = new double[pageNum];
		hub = new double[pageNum];
		for(int k=0; k<pageNum; k++){
			//初始时默认权威值和中心值都为1
			authority[k] = 1;
			hub[k] = 1;
		}
		
		for (String[] array : dataArray) {

			i = Integer.parseInt(array[0]);
			j = Integer.parseInt(array[1]);

			// 设置linkMatrix[i][j]为1代表i网页包含指向j网页的链接
			linkMatrix[i - 1][j - 1] = 1;
		}
	}
	
	/**
	 * 输出结果页面，也就是authority权威值最高的页面
	 */
	public void printResultPage(){
		//最大Hub和Authority值，用于后面的归一化计算
		double maxHub = 0;
		double maxAuthority = 0;
		int maxAuthorityIndex =0;
		//误差值，用于收敛判断
		double error = Integer.MAX_VALUE;
		double[] newHub = new double[pageNum];
		double[] newAuthority = new double[pageNum];
		
		
		while(error > 0.01 * pageNum){
			for(int k=0; k<pageNum; k++){
				newHub[k] = 0;
				newAuthority[k] = 0;
			}
			
			//hub和authority值的更新计算
			for(int i=0; i<pageNum; i++){
				for(int j=0; j<pageNum; j++){
					if(linkMatrix[i][j] == 1){
						newHub[i] += authority[j];
						newAuthority[j] += hub[i];
					}
				}
			}
			
			maxHub = 0;
			maxAuthority = 0;
			for(int k=0; k<pageNum; k++){
				if(newHub[k] > maxHub){
					maxHub = newHub[k];
				}
				
				if(newAuthority[k] > maxAuthority){
					maxAuthority = newAuthority[k];
					maxAuthorityIndex = k;
				}
			}
			
			error = 0;
			//归一化处理
			for(int k=0; k<pageNum; k++){
				newHub[k] /= maxHub;
				newAuthority[k] /= maxAuthority;
				
				error += Math.abs(newHub[k] - hub[k]);
				System.out.println(newAuthority[k] + ":" + newHub[k]);
				
				hub[k] = newHub[k];
				authority[k] = newAuthority[k];
			}
			System.out.println("---------");
		}
		
		System.out.println("****最终收敛的网页的权威值和中心值****");
		for(int k=0; k<pageNum; k++){
			System.out.println("网页" + pageClass.get(k) + ":"+ authority[k] + ":" + hub[k]);
		}
		System.out.println("权威值最高的网页为：网页" + pageClass.get(maxAuthorityIndex));
	}

}
