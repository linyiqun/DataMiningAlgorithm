package DataMining_GSP;

import java.util.ArrayList;

/**
 * 序列，每个序列内部包含多组ItemSet项集
 * 
 * @author lyq
 * 
 */
public class Sequence implements Comparable<Sequence>, Cloneable {
	// 序列所属事务ID
	private int trsanctionID;
	// 项集列表
	private ArrayList<ItemSet> itemSetList;

	public Sequence(int trsanctionID) {
		this.trsanctionID = trsanctionID;
		this.itemSetList = new ArrayList<>();
	}

	public Sequence() {
		this.itemSetList = new ArrayList<>();
	}

	public int getTrsanctionID() {
		return trsanctionID;
	}

	public void setTrsanctionID(int trsanctionID) {
		this.trsanctionID = trsanctionID;
	}

	public ArrayList<ItemSet> getItemSetList() {
		return itemSetList;
	}

	public void setItemSetList(ArrayList<ItemSet> itemSetList) {
		this.itemSetList = itemSetList;
	}

	/**
	 * 取出序列中第一个项集的第一个元素
	 * 
	 * @return
	 */
	public Integer getFirstItemSetNum() {
		return this.getItemSetList().get(0).getItems().get(0);
	}

	/**
	 * 获取序列中最后一个项集
	 * 
	 * @return
	 */
	public ItemSet getLastItemSet() {
		return getItemSetList().get(getItemSetList().size() - 1);
	}

	/**
	 * 获取序列中最后一个项集的最后一个一个元素
	 * 
	 * @return
	 */
	public Integer getLastItemSetNum() {
		ItemSet lastItemSet = getItemSetList().get(getItemSetList().size() - 1);
		int lastItemNum = lastItemSet.getItems().get(
				lastItemSet.getItems().size() - 1);

		return lastItemNum;
	}

	/**
	 * 判断序列中最后一个项集是否为单一的值
	 * 
	 * @return
	 */
	public boolean isLastItemSetSingleNum() {
		ItemSet lastItemSet = getItemSetList().get(getItemSetList().size() - 1);
		int size = lastItemSet.getItems().size();

		return size == 1 ? true : false;
	}

	@Override
	public int compareTo(Sequence o) {
		// TODO Auto-generated method stub
		return this.getFirstItemSetNum().compareTo(o.getFirstItemSetNum());
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
	/**
	 * 拷贝一份一模一样的序列
	 */
	public Sequence copySeqence(){
		Sequence copySeq = new Sequence();
		for(ItemSet itemSet: this.itemSetList){
			copySeq.getItemSetList().add(new ItemSet(itemSet.copyItems()));
		}
		
		return copySeq;
	}

	/**
	 * 比较2个序列是否相等，需要判断内部的每个项集是否完全一致
	 * 
	 * @param seq
	 *            比较的序列对象
	 * @return
	 */
	public boolean compareIsSame(Sequence seq) {
		boolean result = true;
		ArrayList<ItemSet> itemSetList2 = seq.getItemSetList();
		ItemSet tempItemSet1;
		ItemSet tempItemSet2;

		if (itemSetList2.size() != this.itemSetList.size()) {
			return false;
		}
		for (int i = 0; i < itemSetList2.size(); i++) {
			tempItemSet1 = this.itemSetList.get(i);
			tempItemSet2 = itemSetList2.get(i);

			if (!tempItemSet1.compareIsSame(tempItemSet2)) {
				// 只要不相等，直接退出函数
				result = false;
				break;
			}
		}

		return result;
	}

	/**
	 * 生成此序列的所有子序列
	 * 
	 * @return
	 */
	public ArrayList<Sequence> createChildSeqs() {
		ArrayList<Sequence> childSeqs = new ArrayList<>();
		ArrayList<Integer> tempItems;
		Sequence tempSeq = null;
		ItemSet tempItemSet;

		for (int i = 0; i < this.itemSetList.size(); i++) {
			tempItemSet = itemSetList.get(i);
			if (tempItemSet.getItems().size() == 1) {
				tempSeq = this.copySeqence();
				
				// 如果只有项集中只有1个元素，则直接移除
				tempSeq.itemSetList.remove(i);
				childSeqs.add(tempSeq);
			} else {
				tempItems = tempItemSet.getItems();
				for (int j = 0; j < tempItems.size(); j++) {
					tempSeq = this.copySeqence();

					// 在拷贝的序列中移除一个数字
					tempSeq.getItemSetList().get(i).getItems().remove(j);
					childSeqs.add(tempSeq);
				}
			}
		}

		return childSeqs;
	}

}
