package DataMining_GSpan;

/**
 * 边的频繁统计
 * @author lyq
 *
 */
public class EdgeFrequency {
	//节点标号数量
	private int nodeLabelNum;
	//边的标号数量
	private int edgeLabelNum;
	//用于存放边计数的3维数组
	public int[][][] edgeFreqCount;
	
	public EdgeFrequency(int nodeLabelNum, int edgeLabelNum){
		this.nodeLabelNum = nodeLabelNum;
		this.edgeLabelNum = edgeLabelNum;
		
		edgeFreqCount = new int[nodeLabelNum][edgeLabelNum][nodeLabelNum];
		//最初始化操作
		for(int i=0; i<nodeLabelNum; i++){
			for(int j=0; j<edgeLabelNum; j++){
				for(int k=0; k<nodeLabelNum; k++){
					edgeFreqCount[i][j][k] = 0;
				}
			}
		}
	}
	
}
