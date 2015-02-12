package DataMining_PrefixSpan;

import java.util.ArrayList;

/**
 * 序列类
 * 
 * @author lyq
 * 
 */
public class Sequence {
	// 序列内的项集
	private ArrayList<ItemSet> itemSetList;

	public Sequence() {
		this.itemSetList = new ArrayList<>();
	}

	public ArrayList<ItemSet> getItemSetList() {
		return itemSetList;
	}

	public void setItemSetList(ArrayList<ItemSet> itemSetList) {
		this.itemSetList = itemSetList;
	}

	/**
	 * 判断单一项是否包含于此序列
	 * 
	 * @param c
	 *            待判断项
	 * @return
	 */
	public boolean strIsContained(String c) {
		boolean isContained = false;

		for (ItemSet itemSet : itemSetList) {
			isContained = false;

			for (String s : itemSet.getItems()) {
				if (itemSet.getItems().contains("_")) {
					continue;
				}

				if (s.equals(c)) {
					isContained = true;
					break;
				}
			}

			if (isContained) {
				// 如果已经检测出包含了，直接挑出循环
				break;
			}
		}

		return isContained;
	}

	/**
	 * 判断组合项集是否包含于序列中
	 * 
	 * @param itemSet
	 *            组合的项集，元素超过1个
	 * @return
	 */
	public boolean compoentItemIsContain(ItemSet itemSet) {
		boolean isContained = false;
		ArrayList<String> tempItems;
		String lastItem = itemSet.getLastValue();

		for (int i = 0; i < this.itemSetList.size(); i++) {
			tempItems = this.itemSetList.get(i).getItems();
			// 分2种情况查找，第一种从_X中找出x等于项集最后的元素，因为_前缀已经为原本的元素
			if (tempItems.size() > 1 && tempItems.get(0).equals("_")
					&& tempItems.get(1).equals(lastItem)) {
				isContained = true;
				break;
			} else if (!tempItems.get(0).equals("_")) {
				// 从没有_前缀的项集开始寻找，第二种为从后面的后缀中找出直接找出连续字符为ab为同一项集的项集
				if (strArrayContains(tempItems, itemSet.getItems())) {
					isContained = true;
					break;
				}
			}

			if (isContained) {
				break;
			}
		}

		return isContained;
	}

	/**
	 * 删除单个项
	 * 
	 * @param s
	 *            待删除项
	 */
	public void deleteSingleItem(String s) {
		ArrayList<String> tempItems;
		ArrayList<String> deleteItems = new ArrayList<>();

		for (ItemSet itemSet : this.itemSetList) {
			tempItems = itemSet.getItems();
			deleteItems = new ArrayList<>();

			for (int i = 0; i < tempItems.size(); i++) {
				if (tempItems.get(i).equals(s)) {
					deleteItems.add(tempItems.get(i));
				}
			}

			tempItems.removeAll(deleteItems);
		}
	}

	/**
	 * 提取项s之后所得的序列
	 * 
	 * @param s
	 *            目标提取项s
	 */
	public Sequence extractItem(String s) {
		Sequence extractSeq = this.copySeqence();
		ItemSet itemSet;
		ArrayList<String> items;
		ArrayList<ItemSet> deleteItemSets = new ArrayList<>();
		ArrayList<String> tempItems = new ArrayList<>();

		for (int k = 0; k < extractSeq.itemSetList.size(); k++) {
			itemSet = extractSeq.itemSetList.get(k);
			items = itemSet.getItems();
			if (items.size() == 1 && items.get(0).equals(s)) {
				//如果找到的是单项，则完全移除，跳出循环
				extractSeq.itemSetList.remove(k);
				break;
			} else if (items.size() > 1 && !items.get(0).equals("_")) {
				//在后续的多元素项中判断是否包含此元素
				if (items.contains(s)) {
					//如果包含把s后面的元素加入到临时字符数组中
					int index = items.indexOf(s);
					for (int j = index; j < items.size(); j++) {
						tempItems.add(items.get(j));
					}
					//将第一位的s变成下标符"_"
					tempItems.set(0, "_");
					if (tempItems.size() == 1) {
						// 如果此匹配为在最末端，同样移除
						deleteItemSets.add(itemSet);
					} else {
						//将变化后的项集替换原来的
						extractSeq.itemSetList.set(k, new ItemSet(tempItems));
					}
					break;
				} else {
					deleteItemSets.add(itemSet);
				}
			} else {
				// 不符合以上2项条件的统统移除
				deleteItemSets.add(itemSet);
			}
		}
		extractSeq.itemSetList.removeAll(deleteItemSets);

		return extractSeq;
	}

	/**
	 * 提取组合项之后的序列
	 * 
	 * @param array
	 *            组合数组
	 * @return
	 */
	public Sequence extractCompoentItem(ArrayList<String> array) {
		// 找到目标项，是否立刻停止
		boolean stopExtract = false;
		Sequence seq = this.copySeqence();
		String lastItem = array.get(array.size() - 1);
		ArrayList<String> tempItems;
		ArrayList<ItemSet> deleteItems = new ArrayList<>();

		for (int i = 0; i < seq.itemSetList.size(); i++) {
			if (stopExtract) {
				break;
			}

			tempItems = seq.itemSetList.get(i).getItems();
			// 分2种情况查找，第一种从_X中找出x等于项集最后的元素，因为_前缀已经为原本的元素
			if (tempItems.size() > 1 && tempItems.get(0).equals("_")
					&& tempItems.get(1).equals(lastItem)) {
				if (tempItems.size() == 2) {
					seq.itemSetList.remove(i);
				} else {
					// 把1号位置变为下标符"_"，往后移1个字符的位置
					tempItems.set(1, "_");
					// 移除第一个的"_"下划符
					tempItems.remove(0);
				}
				stopExtract = true;
				break;
			} else if (!tempItems.get(0).equals("_")) {
				// 从没有_前缀的项集开始寻找，第二种为从后面的后缀中找出直接找出连续字符为ab为同一项集的项集
				if (strArrayContains(tempItems, array)) {
					// 从左往右找出第一个给定字符的位置，把后面的部分截取出来
					int index = tempItems.indexOf(lastItem);
					ArrayList<String> array2 = new ArrayList<String>();

					for (int j = index; j < tempItems.size(); j++) {
						array2.add(tempItems.get(j));
					}
					array2.set(0, "_");

					if (array2.size() == 1) {
						//如果此项在末尾的位置，则移除该项，否则进行替换
						deleteItems.add(seq.itemSetList.get(i));
					} else {
						seq.itemSetList.set(i, new ItemSet(array2));
					}
					stopExtract = true;
					break;
				} else {
					deleteItems.add(seq.itemSetList.get(i));
				}
			} else {
				// 这种情况是处理_X中X不等于最后一个元素的情况
				deleteItems.add(seq.itemSetList.get(i));
			}
		}
		
		seq.itemSetList.removeAll(deleteItems);

		return seq;
	}

	/**
	 * 深拷贝一个序列
	 * 
	 * @return
	 */
	public Sequence copySeqence() {
		Sequence copySeq = new Sequence();
		ItemSet tempItemSet;
		ArrayList<String> items;

		for (ItemSet itemSet : this.itemSetList) {
			items = (ArrayList<String>) itemSet.getItems().clone();
			tempItemSet = new ItemSet(items);
			copySeq.getItemSetList().add(tempItemSet);
		}

		return copySeq;
	}

	/**
	 * 获取序列中最后一个项集的最后1个元素
	 * 
	 * @return
	 */
	public String getLastItemSetValue() {
		int size = this.getItemSetList().size();
		ItemSet itemSet = this.getItemSetList().get(size - 1);
		size = itemSet.getItems().size();

		return itemSet.getItems().get(size - 1);
	}

	/**
	 * 判断strList2是否是strList1的子序列
	 * 
	 * @param strList1
	 * @param strList2
	 * @return
	 */
	public boolean strArrayContains(ArrayList<String> strList1,
			ArrayList<String> strList2) {
		boolean isContained = false;

		for (int i = 0; i < strList1.size() - strList2.size() + 1; i++) {
			isContained = true;

			for (int j = 0, k = i; j < strList2.size(); j++, k++) {
				if (!strList1.get(k).equals(strList2.get(j))) {
					isContained = false;
					break;
				}
			}

			if (isContained) {
				break;
			}
		}

		return isContained;
	}
}
