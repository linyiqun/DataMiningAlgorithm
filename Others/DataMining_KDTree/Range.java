package DataMining_KDTree;

/**
 * 空间矢量，表示所代表的空间范围
 * 
 * @author lyq
 * 
 */
public class Range {
	// 边界左边界
	double left;
	// 边界右边界
	double right;
	// 边界上边界
	double top;
	// 边界下边界
	double bottom;

	public Range() {
		this.left = -Integer.MAX_VALUE;
		this.right = Integer.MAX_VALUE;
		this.top = Integer.MAX_VALUE;
		this.bottom = -Integer.MAX_VALUE;
	}

	public Range(int left, int right, int top, int bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	/**
	 * 空间矢量进行并操作
	 * 
	 * @param range
	 * @return
	 */
	public Range crossOperation(Range r) {
		Range range = new Range();

		// 取靠近右侧的左边界
		if (r.left > this.left) {
			range.left = r.left;
		} else {
			range.left = this.left;
		}

		// 取靠近左侧的右边界
		if (r.right < this.right) {
			range.right = r.right;
		} else {
			range.right = this.right;
		}

		// 取靠近下侧的上边界
		if (r.top < this.top) {
			range.top = r.top;
		} else {
			range.top = this.top;
		}

		// 取靠近上侧的下边界
		if (r.bottom > this.bottom) {
			range.bottom = r.bottom;
		} else {
			range.bottom = this.bottom;
		}

		return range;
	}

	/**
	 * 根据坐标点分割方向确定左侧空间矢量
	 * 
	 * @param p
	 *            数据矢量
	 * @param dir
	 *            分割方向
	 * @return
	 */
	public static Range initLeftRange(Point p, int dir) {
		Range range = new Range();

		if (dir == KDTreeTool.DIRECTION_X) {
			range.right = p.x;
		} else {
			range.bottom = p.y;
		}

		return range;
	}

	/**
	 * 根据坐标点分割方向确定右侧空间矢量
	 * 
	 * @param p
	 *            数据矢量
	 * @param dir
	 *            分割方向
	 * @return
	 */
	public static Range initRightRange(Point p, int dir) {
		Range range = new Range();

		if (dir == KDTreeTool.DIRECTION_X) {
			range.left = p.x;
		} else {
			range.top = p.y;
		}

		return range;
	}
}
