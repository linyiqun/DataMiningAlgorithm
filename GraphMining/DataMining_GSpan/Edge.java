package DataMining_GSpan;

/**
 * 边，用五元组表示
 * 
 * @author lyq
 * 
 */
public class Edge {
	// 五元组的大小比较结果
	public static final int EDGE_EQUAL = 0;
	public static final int EDGE_SMALLER = 1;
	public static final int EDGE_LARGER = 2;

	// 边的一端的id号标识
	int ix;
	// 边的另一端的id号标识
	int iy;
	// 边的一端的点标号
	int x;
	// 边的标号
	int a;
	// 边的另一端的点标号
	int y;

	public Edge(int ix, int iy, int x, int a, int y) {
		this.ix = ix;
		this.iy = iy;
		this.x = x;
		this.a = a;
		this.y = y;
	}

	/**
	 * 当前边是与给定的边的大小比较关系
	 * 
	 * @param e
	 * @return
	 */
	public int compareWith(Edge e) {
		int result = EDGE_EQUAL;
		int[] array1 = new int[] { ix, iy, x, y, a };
		int[] array2 = new int[] { e.ix, e.iy, e.x, e.y, e.a };

		// 按照ix, iy,x,y,a的次序依次比较
		for (int i = 0; i < array1.length; i++) {
			if (array1[i] < array2[i]) {
				result = EDGE_SMALLER;
				break;
			} else if (array1[i] > array2[i]) {
				result = EDGE_LARGER;
				break;
			} else {
				// 如果相等，继续比较下一个
				continue;
			}
		}

		return result;
	}

}
