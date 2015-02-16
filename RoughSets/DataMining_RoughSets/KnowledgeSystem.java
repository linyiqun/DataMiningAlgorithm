package DataMining_RoughSets;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 知识系统
 * 
 * @author lyq
 * 
 */
public class KnowledgeSystem {
	// 知识系统内的集合
	ArrayList<RecordCollection> ksCollections;

	public KnowledgeSystem(ArrayList<RecordCollection> ksCollections) {
		this.ksCollections = ksCollections;
	}

	/**
	 * 获取集合的上近似集合
	 * 
	 * @param rc
	 *            原始集合
	 * @return
	 */
	public RecordCollection getUpSimilarRC(RecordCollection rc) {
		RecordCollection resultRc = null;
		ArrayList<String> nameArray;
		ArrayList<String> targetArray;
		ArrayList<RecordCollection> copyRcs = new ArrayList<>();
		ArrayList<RecordCollection> deleteRcs = new ArrayList<>();
		targetArray = rc.getRecordNames();

		// 做一个集合拷贝
		for (RecordCollection recordCollection : ksCollections) {
			copyRcs.add(recordCollection);
		}

		for (RecordCollection recordCollection : copyRcs) {
			nameArray = recordCollection.getRecordNames();

			if (strIsContained(targetArray, nameArray)) {
				removeOverLaped(targetArray, nameArray);
				deleteRcs.add(recordCollection);

				if (resultRc == null) {
					resultRc = recordCollection;
				} else {
					// 进行并运算
					resultRc = resultRc.unionCal(recordCollection);
				}

				if (targetArray.size() == 0) {
					break;
				}
			}
		}
		//去除已经添加过的集合
		copyRcs.removeAll(deleteRcs);

		if (targetArray.size() > 0) {
			// 说明已经完全还未找全上近似的集合
			for (RecordCollection recordCollection : copyRcs) {
				nameArray = recordCollection.getRecordNames();

				if (strHasOverlap(targetArray, nameArray)) {
					removeOverLaped(targetArray, nameArray);

					if (resultRc == null) {
						resultRc = recordCollection;
					} else {
						// 进行并运算
						resultRc = resultRc.unionCal(recordCollection);
					}

					if (targetArray.size() == 0) {
						break;
					}
				}
			}
		}

		return resultRc;
	}

	/**
	 * 获取集合的下近似集合
	 * 
	 * @param rc
	 *            原始集合
	 * @return
	 */
	public RecordCollection getDownSimilarRC(RecordCollection rc) {
		RecordCollection resultRc = null;
		ArrayList<String> nameArray;
		ArrayList<String> targetArray;
		targetArray = rc.getRecordNames();

		for (RecordCollection recordCollection : ksCollections) {
			nameArray = recordCollection.getRecordNames();

			if (strIsContained(targetArray, nameArray)) {
				removeOverLaped(targetArray, nameArray);

				if (resultRc == null) {
					resultRc = recordCollection;
				} else {
					// 进行并运算
					resultRc = resultRc.unionCal(recordCollection);
				}

				if (targetArray.size() == 0) {
					break;
				}
			}
		}

		return resultRc;
	}

	/**
	 * 判断2个字符数组之间是否有交集
	 * 
	 * @param str1
	 *            字符列表1
	 * @param str2
	 *            字符列表2
	 * @return
	 */
	public boolean strHasOverlap(ArrayList<String> str1, ArrayList<String> str2) {
		boolean hasOverlap = false;

		for (String s1 : str1) {
			for (String s2 : str2) {
				if (s1.equals(s2)) {
					hasOverlap = true;
					break;
				}
			}

			if (hasOverlap) {
				break;
			}
		}

		return hasOverlap;
	}

	/**
	 * 判断字符集str2是否完全包含于str1中
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public boolean strIsContained(ArrayList<String> str1, ArrayList<String> str2) {
		boolean isContained = false;
		int count = 0;

		for (String s : str2) {
			if (str1.contains(s)) {
				count++;
			}
		}

		if (count == str2.size()) {
			isContained = true;
		}

		return isContained;
	}

	/**
	 * 字符列表移除公共元素
	 * 
	 * @param str1
	 * @param str2
	 */
	public void removeOverLaped(ArrayList<String> str1, ArrayList<String> str2) {
		ArrayList<String> deleteStrs = new ArrayList<>();

		for (String s1 : str1) {
			for (String s2 : str2) {
				if (s1.equals(s2)) {
					deleteStrs.add(s1);
					break;
				}
			}
		}

		// 进行公共元素的移除
		str1.removeAll(deleteStrs);
	}
}
