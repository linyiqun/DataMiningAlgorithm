package DataMining_TAN;

/**
 * 属性之间的互信息值，表示属性之间的关联性大小
 * @author lyq
 *
 */
public class AttrMutualInfo implements Comparable<AttrMutualInfo>{
	//互信息值
	Double value;
	//关联属性值对
	Node[] nodeArray;
	
	public AttrMutualInfo(double value, Node node1, Node node2){
		this.value = value;
		
		this.nodeArray = new Node[2];
		this.nodeArray[0] = node1;
		this.nodeArray[1] = node2;
	}

	@Override
	public int compareTo(AttrMutualInfo o) {
		// TODO Auto-generated method stub
		return o.value.compareTo(this.value);
	}
	
}
