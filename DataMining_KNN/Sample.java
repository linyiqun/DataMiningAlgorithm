package DataMining_KNN;

/**
 * 样本数据类
 * 
 * @author lyq
 * 
 */
public class Sample implements Comparable<Sample>{
	// 样本数据的分类名称
	private String className;
	// 样本数据的特征向量
	private String[] features;
	//测试样本之间的间距值，以此做排序
	private Integer distance;
	
	public Sample(String[] features){
		this.features = features;
	}
	
	public Sample(String className, String[] features){
		this.className = className;
		this.features = features;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String[] getFeatures() {
		return features;
	}

	public void setFeatures(String[] features) {
		this.features = features;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(Sample o) {
		// TODO Auto-generated method stub
		return this.getDistance().compareTo(o.getDistance());
	}
	
}

