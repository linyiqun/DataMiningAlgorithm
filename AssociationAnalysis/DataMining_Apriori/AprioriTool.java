package DataMining_Apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * apriori算法工具类
 * 
 * @author lyq
 * 
 */
public class AprioriTool {
	// 最小支持度计数
	private int minSupportCount;
	// 测试数据文件地址
	private String filePath;
	// 每个事务中的商品ID
	private ArrayList<String[]> totalGoodsIDs;
	// 过程中计算出来的所有频繁项集列表
	private ArrayList<FrequentItem> resultItem;
	// 过程中计算出来频繁项集的ID集合
	private ArrayList<String[]> resultItemID;

	public AprioriTool(String filePath, int minSupportCount) {
		this.filePath = filePath;
		this.minSupportCount = minSupportCount;
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

		String[] temp = null;
		totalGoodsIDs = new ArrayList<>();
		for (String[] array : dataArray) {
			temp = new String[array.length - 1];
			System.arraycopy(array, 1, temp, 0, array.length - 1);

			// 将事务ID加入列表吧中
			totalGoodsIDs.add(temp);
		}
	}

	/**
	 * 判读字符数组array2是否包含于数组array1中
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	public boolean iSStrContain(String[] array1, String[] array2) {
		if (array1 == null || array2 == null) {
			return false;
		}

		boolean iSContain = false;
		for (String s : array2) {
			// 新的字母比较时，重新初始化变量
			iSContain = false;
			// 判读array2中每个字符，只要包括在array1中 ，就算包含
			for (String s2 : array1) {
				if (s.equals(s2)) {
					iSContain = true;
					break;
				}
			}

			// 如果已经判断出不包含了，则直接中断循环
			if (!iSContain) {
				break;
			}
		}

		return iSContain;
	}

	/**
	 * 项集进行连接运算
	 */
	private void computeLink() {
		// 连接计算的终止数，k项集必须算到k-1子项集为止
		int endNum = 0;
		// 当前已经进行连接运算到几项集,开始时就是1项集
		int currentNum = 1;
		// 商品，1频繁项集映射图
		HashMap<String, FrequentItem> itemMap = new HashMap<>();
		FrequentItem tempItem;
		// 初始列表
		ArrayList<FrequentItem> list = new ArrayList<>();
		// 经过连接运算后产生的结果项集
		resultItem = new ArrayList<>();
		resultItemID = new ArrayList<>();
		// 商品ID的种类
		ArrayList<String> idType = new ArrayList<>();
		for (String[] a : totalGoodsIDs) {
			for (String s : a) {
				if (!idType.contains(s)) {
					tempItem = new FrequentItem(new String[] { s }, 1);
					idType.add(s);
					resultItemID.add(new String[] { s });
				} else {
					// 支持度计数加1
					tempItem = itemMap.get(s);
					tempItem.setCount(tempItem.getCount() + 1);
				}
				itemMap.put(s, tempItem);
			}
		}
		// 将初始频繁项集转入到列表中，以便继续做连接运算
		for (Map.Entry entry : itemMap.entrySet()) {
			list.add((FrequentItem) entry.getValue());
		}
		// 按照商品ID进行排序，否则连接计算结果将会不一致，将会减少
		Collections.sort(list);
		resultItem.addAll(list);

		String[] array1;
		String[] array2;
		String[] resultArray;
		ArrayList<String> tempIds;
		ArrayList<String[]> resultContainer;
		// 总共要算到endNum项集
		endNum = list.size() - 1;

		while (currentNum < endNum) {
			resultContainer = new ArrayList<>();
			for (int i = 0; i < list.size() - 1; i++) {
				tempItem = list.get(i);
				array1 = tempItem.getIdArray();
				for (int j = i + 1; j < list.size(); j++) {
					tempIds = new ArrayList<>();
					array2 = list.get(j).getIdArray();
					for (int k = 0; k < array1.length; k++) {
						// 如果对应位置上的值相等的时候，只取其中一个值，做了一个连接删除操作
						if (array1[k].equals(array2[k])) {
							tempIds.add(array1[k]);
						} else {
							tempIds.add(array1[k]);
							tempIds.add(array2[k]);
						}
					}
					resultArray = new String[tempIds.size()];
					tempIds.toArray(resultArray);

					boolean isContain = false;
					// 过滤不符合条件的的ID数组，包括重复的和长度不符合要求的
					if (resultArray.length == (array1.length + 1)) {
						isContain = isIDArrayContains(resultContainer,
								resultArray);
						if (!isContain) {
							resultContainer.add(resultArray);
						}
					}
				}
			}

			// 做频繁项集的剪枝处理，必须保证新的频繁项集的子项集也必须是频繁项集
			list = cutItem(resultContainer);
			currentNum++;
		}

		// 输出频繁项集
		for (int k = 1; k <= currentNum; k++) {
			System.out.println("频繁" + k + "项集：");
			for (FrequentItem i : resultItem) {
				if (i.getLength() == k) {
					System.out.print("{");
					for (String t : i.getIdArray()) {
						System.out.print(t + ",");
					}
					System.out.print("},");
				}
			}
			System.out.println();
		}
	}

	/**
	 * 判断列表结果中是否已经包含此数组
	 * 
	 * @param container
	 *            ID数组容器
	 * @param array
	 *            待比较数组
	 * @return
	 */
	private boolean isIDArrayContains(ArrayList<String[]> container,
			String[] array) {
		boolean isContain = true;
		if (container.size() == 0) {
			isContain = false;
			return isContain;
		}

		for (String[] s : container) {
			// 比较的视乎必须保证长度一样
			if (s.length != array.length) {
				continue;
			}

			isContain = true;
			for (int i = 0; i < s.length; i++) {
				// 只要有一个id不等，就算不相等
				if (s[i] != array[i]) {
					isContain = false;
					break;
				}
			}

			// 如果已经判断是包含在容器中时，直接退出
			if (isContain) {
				break;
			}
		}

		return isContain;
	}

	/**
	 * 对频繁项集做剪枝步骤，必须保证新的频繁项集的子项集也必须是频繁项集
	 */
	private ArrayList<FrequentItem> cutItem(ArrayList<String[]> resultIds) {
		String[] temp;
		// 忽略的索引位置，以此构建子集
		int igNoreIndex = 0;
		FrequentItem tempItem;
		// 剪枝生成新的频繁项集
		ArrayList<FrequentItem> newItem = new ArrayList<>();
		// 不符合要求的id
		ArrayList<String[]> deleteIdArray = new ArrayList<>();
		// 子项集是否也为频繁子项集
		boolean isContain = true;

		for (String[] array : resultIds) {
			// 列举出其中的一个个的子项集，判断存在于频繁项集列表中
			temp = new String[array.length - 1];
			for (igNoreIndex = 0; igNoreIndex < array.length; igNoreIndex++) {
				isContain = true;
				for (int j = 0, k = 0; j < array.length; j++) {
					if (j != igNoreIndex) {
						temp[k] = array[j];
						k++;
					}
				}

				if (!isIDArrayContains(resultItemID, temp)) {
					isContain = false;
					break;
				}
			}

			if (!isContain) {
				deleteIdArray.add(array);
			}
		}

		// 移除不符合条件的ID组合
		resultIds.removeAll(deleteIdArray);

		// 移除支持度计数不够的id集合
		int tempCount = 0;
		for (String[] array : resultIds) {
			tempCount = 0;
			for (String[] array2 : totalGoodsIDs) {
				if (isStrArrayContain(array2, array)) {
					tempCount++;
				}
			}

			// 如果支持度计数大于等于最小最小支持度计数则生成新的频繁项集，并加入结果集中
			if (tempCount >= minSupportCount) {
				tempItem = new FrequentItem(array, tempCount);
				newItem.add(tempItem);
				resultItemID.add(array);
				resultItem.add(tempItem);
			}
		}

		return newItem;
	}

	/**
	 * 数组array2是否包含于array1中，不需要完全一样
	 * 
	 * @param array1
	 * @param array2
	 * @return
	 */
	private boolean isStrArrayContain(String[] array1, String[] array2) {
		boolean isContain = true;
		for (String s2 : array2) {
			isContain = false;
			for (String s1 : array1) {
				// 只要s2字符存在于array1中，这个字符就算包含在array1中
				if (s2.equals(s1)) {
					isContain = true;
					break;
				}
			}

			// 一旦发现不包含的字符，则array2数组不包含于array1中
			if (!isContain) {
				break;
			}
		}

		return isContain;
	}

	/**
	 * 根据产生的频繁项集输出关联规则
	 * 
	 * @param minConf
	 *            最小置信度阈值
	 */
	public void printAttachRule(double minConf) {
		// 进行连接和剪枝操作
		computeLink();

		int count1 = 0;
		int count2 = 0;
		ArrayList<String> childGroup1;
		ArrayList<String> childGroup2;
		String[] group1;
		String[] group2;
		// 以最后一个频繁项集做关联规则的输出
		String[] array = resultItem.get(resultItem.size() - 1).getIdArray();
		// 子集总数，计算的时候除去自身和空集
		int totalNum = (int) Math.pow(2, array.length);
		String[] temp;
		// 二进制数组，用来代表各个子集
		int[] binaryArray;
		// 除去头和尾部
		for (int i = 1; i < totalNum - 1; i++) {
			binaryArray = new int[array.length];
			numToBinaryArray(binaryArray, i);

			childGroup1 = new ArrayList<>();
			childGroup2 = new ArrayList<>();
			count1 = 0;
			count2 = 0;
			// 按照二进制位关系取出子集
			for (int j = 0; j < binaryArray.length; j++) {
				if (binaryArray[j] == 1) {
					childGroup1.add(array[j]);
				} else {
					childGroup2.add(array[j]);
				}
			}

			group1 = new String[childGroup1.size()];
			group2 = new String[childGroup2.size()];

			childGroup1.toArray(group1);
			childGroup2.toArray(group2);

			for (String[] a : totalGoodsIDs) {
				if (isStrArrayContain(a, group1)) {
					count1++;

					// 在group1的条件下，统计group2的事件发生次数
					if (isStrArrayContain(a, group2)) {
						count2++;
					}
				}
			}

			// {A}-->{B}的意思为在A的情况下发生B的概率
			System.out.print("{");
			for (String s : group1) {
				System.out.print(s + ", ");
			}
			System.out.print("}-->");
			System.out.print("{");
			for (String s : group2) {
				System.out.print(s + ", ");
			}
			System.out.print(MessageFormat.format(
					"},confidence(置信度)：{0}/{1}={2}", count2, count1, count2
							* 1.0 / count1));
			if (count2 * 1.0 / count1 < minConf) {
				// 不符合要求，不是强规则
				System.out.println("由于此规则置信度未达到最小置信度的要求，不是强规则");
			} else {
				System.out.println("为强规则");
			}
		}

	}

	/**
	 * 数字转为二进制形式
	 * 
	 * @param binaryArray
	 *            转化后的二进制数组形式
	 * @param num
	 *            待转化数字
	 */
	private void numToBinaryArray(int[] binaryArray, int num) {
		int index = 0;
		while (num != 0) {
			binaryArray[index] = num % 2;
			index++;
			num /= 2;
		}
	}

}
