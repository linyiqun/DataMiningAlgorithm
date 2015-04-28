package DataMining_ACO;

import java.util.ArrayList;

/**
 * 蚂蚁类，进行路径搜索的载体
 * 
 * @author lyq
 * 
 */
public class Ant implements Comparable<Ant> {
	// 蚂蚁当前所在城市
	String currentPos;
	// 蚂蚁遍历完回到原点所用的总距离
	Double sumDistance;
	// 城市间的信息素浓度矩阵，随着时间的增多而减少
	double[][] pheromoneMatrix;
	// 蚂蚁已经走过的城市集合
	ArrayList<String> visitedCitys;
	// 还未走过的城市集合
	ArrayList<String> nonVisitedCitys;
	// 蚂蚁当前走过的路径
	ArrayList<String> currentPath;

	public Ant(double[][] pheromoneMatrix, ArrayList<String> nonVisitedCitys) {
		this.pheromoneMatrix = pheromoneMatrix;
		this.nonVisitedCitys = nonVisitedCitys;

		this.visitedCitys = new ArrayList<>();
		this.currentPath = new ArrayList<>();
	}

	/**
	 * 计算路径的总成本(距离)
	 * 
	 * @return
	 */
	public double calSumDistance() {
		sumDistance = 0.0;
		String lastCity;
		String currentCity;

		for (int i = 0; i < currentPath.size() - 1; i++) {
			lastCity = currentPath.get(i);
			currentCity = currentPath.get(i + 1);

			// 通过距离矩阵进行计算
			sumDistance += ACOTool.disMatrix[Integer.parseInt(lastCity)][Integer
					.parseInt(currentCity)];
		}

		return sumDistance;
	}

	/**
	 * 蚂蚁选择前往下一个城市
	 * 
	 * @param city
	 *            所选的城市
	 */
	public void goToNextCity(String city) {
		this.currentPath.add(city);
		this.currentPos = city;
		this.nonVisitedCitys.remove(city);
		this.visitedCitys.add(city);
	}

	/**
	 * 判断蚂蚁是否已经又重新回到起点
	 * 
	 * @return
	 */
	public boolean isBack() {
		boolean isBack = false;
		String startPos;
		String endPos;

		if (currentPath.size() == 0) {
			return isBack;
		}

		startPos = currentPath.get(0);
		endPos = currentPath.get(currentPath.size() - 1);
		if (currentPath.size() > 1 && startPos.equals(endPos)) {
			isBack = true;
		}

		return isBack;
	}

	/**
	 * 判断蚂蚁在本次的走过的路径中是否包含从城市i到城市j
	 * 
	 * @param cityI
	 *            城市I
	 * @param cityJ
	 *            城市J
	 * @return
	 */
	public boolean pathContained(String cityI, String cityJ) {
		String lastCity;
		String currentCity;
		boolean isContained = false;

		for (int i = 0; i < currentPath.size() - 1; i++) {
			lastCity = currentPath.get(i);
			currentCity = currentPath.get(i + 1);

			// 如果某一段路径的始末位置一致，则认为有经过此城市
			if ((lastCity.equals(cityI) && currentCity.equals(cityJ))
					|| (lastCity.equals(cityJ) && currentCity.equals(cityI))) {
				isContained = true;
				break;
			}
		}

		return isContained;
	}

	@Override
	public int compareTo(Ant o) {
		// TODO Auto-generated method stub
		return this.sumDistance.compareTo(o.sumDistance);
	}
}
