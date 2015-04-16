package DataMining_MSApriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import DataMining_Apriori.FrequentItem;

/**
 * 基于多支持度的Apriori算法工具类
 * 
 * @author lyq
 * 
 */
public class MSAprioriTool {
	// 前件判断的结果值，用于关联规则的推导
	public static final int PREFIX_NOT_SUB = -1;
	public static final int PREFIX_EQUAL = 1;
	public static final int PREFIX_IS_SUB = 2;

	// 是否读取的是事务型数据
	private boolean isTransaction;
	// 最大频繁k项集的k值
	private int initFItemNum;
	// 事务数据文件地址
	private String filePath;
	// 最小支持度阈值
	private double minSup;
	// 最小置信度率
	private double minConf;
	// 最大支持度差别阈值
	private double delta;
	// 多项目的最小支持度数,括号中的下标代表的是商品的ID
	private double[] mis;
	// 每个事务中的商品ID
	private ArrayList<String[]> totalGoodsIDs;
	// 关系表数据所转化的事务数据
	private ArrayList<String[]> transactionDatas;
	// 过程中计算出来的所有频繁项集列表
	private ArrayList<FrequentItem> resultItem;
	// 过程中计算出来频繁项集的ID集合
	private ArrayList<String[]> resultItemID;
	// 属性到数字的映射图
	private HashMap<String, Integer> attr2Num;
	// 数字id对应属性的映射图
	private HashMap<Integer, String> num2Attr;
	// 频繁项集所覆盖的id数值
	private Map<String, int[]> fItem2Id;

	/**
	 * 事务型数据关联挖掘算法
	 * 
	 * @param filePath
	 * @param minConf
	 * @param delta
	 * @param mis
	 * @param isTransaction
	 */
	public MSAprioriTool(String filePath, double minConf, double delta,
			double[] mis, boolean isTransaction) {
		this.filePath = filePath;
		this.minConf = minConf;
		this.delta = delta;
		this.mis = mis;
		this.isTransaction = isTransaction;
		this.fItem2Id = new HashMap<>();

		readDataFile();
	}

	/**
	 * 非事务型关联挖掘
	 * 
	 * @param filePath
	 * @param minConf
	 * @param minSup
	 * @param isTransaction
	 */
	public MSAprioriTool(String filePath, double minConf, double minSup,
			boolean isTransaction) {
		this.filePath = filePath;
		this.minConf = minConf;
		this.minSup = minSup;
		this.isTransaction = isTransaction;
		this.delta = 1.0;
		this.fItem2Id = new HashMap<>();

		readRDBMSData(filePath);
	}

	/**
	 * 从文件中读取数据
	 */
	private void readDataFile() {
		String[] temp = null;
		ArrayList<String[]> dataArray;

		dataArray = readLine(filePath);
		totalGoodsIDs = new ArrayList<>();

		for (String[] array : dataArray) {
			temp = new String[array.length - 1];
			System.arraycopy(array, 1, temp, 0, array.length - 1);

			// 将事务ID加入列表吧中
			totalGoodsIDs.add(temp);
		}
	}

	/**
	 * 从文件中逐行读数据
	 * 
	 * @param filePath
	 *            数据文件地址
	 * @return
	 */
	private ArrayList<String[]> readLine(String filePath) {
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

		return dataArray;
	}

	/**
	 * 计算频繁项集
	 */
	public void calFItems() {
		FrequentItem fItem;

		computeLink();
		printFItems();

		if (isTransaction) {
			fItem = resultItem.get(resultItem.size() - 1);
			// 取出最后一个频繁项集做关联规则的推导
			System.out.println("最后一个频繁项集做关联规则的推导结果：");
			printAttachRuls(fItem.getIdArray());
		}
	}

	/**
	 * 输出频繁项集
	 */
	private void printFItems() {
		if (isTransaction) {
			System.out.println("事务型数据频繁项集输出结果:");
		} else {
			System.out.println("非事务(关系)型数据频繁项集输出结果:");
		}

		// 输出频繁项集
		for (int k = 1; k <= initFItemNum; k++) {
			System.out.println("频繁" + k + "项集：");
			for (FrequentItem i : resultItem) {
				if (i.getLength() == k) {
					System.out.print("{");
					for (String t : i.getIdArray()) {
						if (!isTransaction) {
							// 如果原本是非事务型数据，需要重新做替换
							t = num2Attr.get(Integer.parseInt(t));
						}

						System.out.print(t + ",");
					}
					System.out.print("},");
				}
			}
			System.out.println();
		}
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
		for (Map.Entry<String, FrequentItem> entry : itemMap.entrySet()) {
			tempItem = entry.getValue();

			// 判断1频繁项集是否满足支持度阈值的条件
			if (judgeFItem(tempItem.getIdArray())) {
				list.add(tempItem);
			}
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
		initFItemNum = list.size() - 1;

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
		boolean isSatisfied = false;
		for (String[] array : resultIds) {
			isSatisfied = judgeFItem(array);

			// 如果此频繁项集满足多支持度阈值限制条件和支持度差别限制条件，则添加入结果集中
			if (isSatisfied) {
				tempItem = new FrequentItem(array, tempCount);
				newItem.add(tempItem);
				resultItemID.add(array);
				resultItem.add(tempItem);
			}
		}

		return newItem;
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
	 * 判断一个频繁项集是否满足条件
	 * 
	 * @param frequentItem
	 *            待判断频繁项集
	 * @return
	 */
	private boolean judgeFItem(String[] frequentItem) {
		boolean isSatisfied = true;
		int id;
		int count;
		double tempMinSup;
		// 最小的支持度阈值
		double minMis = Integer.MAX_VALUE;
		// 最大的支持度阈值
		double maxMis = -Integer.MAX_VALUE;

		// 如果是事务型数据，用mis数组判断，如果不是统一用同样的最小支持度阈值判断
		if (isTransaction) {
			// 寻找频繁项集中的最小支持度阈值
			for (int i = 0; i < frequentItem.length; i++) {
				id = i + 1;

				if (mis[id] < minMis) {
					minMis = mis[id];
				}

				if (mis[id] > maxMis) {
					maxMis = mis[id];
				}
			}
		} else {
			minMis = minSup;
			maxMis = minSup;
		}

		count = calSupportCount(frequentItem);
		tempMinSup = 1.0 * count / totalGoodsIDs.size();
		// 判断频繁项集的支持度阈值是否超过最小的支持度阈值
		if (tempMinSup < minMis) {
			isSatisfied = false;
		}

		// 如果误差超过了最大支持度差别，也算不满足条件
		if (Math.abs(maxMis - minMis) > delta) {
			isSatisfied = false;
		}

		return isSatisfied;
	}

	/**
	 * 统计候选频繁项集的支持度数，利用他的子集进行技术，无须扫描整个数据集
	 * 
	 * @param frequentItem
	 *            待计算频繁项集
	 * @return
	 */
	private int calSupportCount(String[] frequentItem) {
		int count = 0;
		int[] ids;
		String key;
		String[] array;
		ArrayList<Integer> newIds;

		key = "";
		for (int i = 1; i < frequentItem.length; i++) {
			key += frequentItem[i];
		}

		newIds = new ArrayList<>();
		// 找出所属的事务ID
		ids = fItem2Id.get(key);

		// 如果没有找到子项集的事务id，则全盘扫描数据集
		if (ids == null || ids.length == 0) {
			for (int j = 0; j < totalGoodsIDs.size(); j++) {
				array = totalGoodsIDs.get(j);
				if (isStrArrayContain(array, frequentItem)) {
					count++;
					newIds.add(j);
				}
			}
		} else {
			for (int index : ids) {
				array = totalGoodsIDs.get(index);
				if (isStrArrayContain(array, frequentItem)) {
					count++;
					newIds.add(index);
				}
			}
		}

		ids = new int[count];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = newIds.get(i);
		}

		key = frequentItem[0] + key;
		// 将所求值存入图中，便于下次的计数
		fItem2Id.put(key, ids);

		return count;
	}

	/**
	 * 根据给定的频繁项集输出关联规则
	 * 
	 * @param frequentItems
	 *            频繁项集
	 */
	public void printAttachRuls(String[] frequentItem) {
		// 关联规则前件,后件对
		Map<ArrayList<String>, ArrayList<String>> rules;
		// 前件搜索历史
		Map<ArrayList<String>, ArrayList<String>> searchHistory;
		ArrayList<String> prefix;
		ArrayList<String> suffix;

		rules = new HashMap<ArrayList<String>, ArrayList<String>>();
		searchHistory = new HashMap<>();

		for (int i = 0; i < frequentItem.length; i++) {
			suffix = new ArrayList<>();
			for (int j = 0; j < frequentItem.length; j++) {
				suffix.add(frequentItem[j]);
			}
			prefix = new ArrayList<>();

			recusiveFindRules(rules, searchHistory, prefix, suffix);
		}

		// 依次输出找到的关联规则
		for (Map.Entry<ArrayList<String>, ArrayList<String>> entry : rules
				.entrySet()) {
			prefix = entry.getKey();
			suffix = entry.getValue();

			printRuleDetail(prefix, suffix);
		}
	}

	/**
	 * 根据前件后件，输出关联规则
	 * 
	 * @param prefix
	 * @param suffix
	 */
	private void printRuleDetail(ArrayList<String> prefix,
			ArrayList<String> suffix) {
		// {A}-->{B}的意思为在A的情况下发生B的概率
		System.out.print("{");
		for (String s : prefix) {
			System.out.print(s + ", ");
		}
		System.out.print("}-->");
		System.out.print("{");
		for (String s : suffix) {
			System.out.print(s + ", ");
		}
		System.out.println("}");
	}

	/**
	 * 递归扩展关联规则解
	 * 
	 * @param rules
	 *            关联规则结果集
	 * @param history
	 *            前件搜索历史
	 * @param prefix
	 *            关联规则前件
	 * @param suffix
	 *            关联规则后件
	 */
	private void recusiveFindRules(
			Map<ArrayList<String>, ArrayList<String>> rules,
			Map<ArrayList<String>, ArrayList<String>> history,
			ArrayList<String> prefix, ArrayList<String> suffix) {
		int count1;
		int count2;
		int compareResult;
		// 置信度大小
		double conf;
		String[] temp1;
		String[] temp2;
		ArrayList<String> copyPrefix;
		ArrayList<String> copySuffix;

		// 如果后件只有1个，则函数返回
		if (suffix.size() == 1) {
			return;
		}

		for (String s : suffix) {
			count1 = 0;
			count2 = 0;

			copyPrefix = (ArrayList<String>) prefix.clone();
			copyPrefix.add(s);

			copySuffix = (ArrayList<String>) suffix.clone();
			// 将拷贝的后件移除添加的一项
			copySuffix.remove(s);

			compareResult = isSubSetInRules(history, copyPrefix);
			if (compareResult == PREFIX_EQUAL) {
				// 如果曾经已经被搜索过，则跳过
				continue;
			}

			// 判断是否为子集，如果是子集则无需计算
			compareResult = isSubSetInRules(rules, copyPrefix);
			if (compareResult == PREFIX_IS_SUB) {
				rules.put(copyPrefix, copySuffix);
				// 加入到搜索历史中
				history.put(copyPrefix, copySuffix);
				recusiveFindRules(rules, history, copyPrefix, copySuffix);
				continue;
			}

			// 暂时合并为总的集合
			copySuffix.addAll(copyPrefix);
			temp1 = new String[copyPrefix.size()];
			temp2 = new String[copySuffix.size()];
			copyPrefix.toArray(temp1);
			copySuffix.toArray(temp2);
			// 之后再次移除之前天剑的前件
			copySuffix.removeAll(copyPrefix);

			for (String[] a : totalGoodsIDs) {
				if (isStrArrayContain(a, temp1)) {
					count1++;

					// 在group1的条件下，统计group2的事件发生次数
					if (isStrArrayContain(a, temp2)) {
						count2++;
					}
				}
			}

			conf = 1.0 * count2 / count1;
			if (conf > minConf) {
				// 设置此前件条件下，能导出关联规则
				rules.put(copyPrefix, copySuffix);
			}

			// 加入到搜索历史中
			history.put(copyPrefix, copySuffix);
			recusiveFindRules(rules, history, copyPrefix, copySuffix);
		}
	}

	/**
	 * 判断当前的前件是否会关联规则的子集
	 * 
	 * @param rules
	 *            当前已经判断出的关联规则
	 * @param prefix
	 *            待判断的前件
	 * @return
	 */
	private int isSubSetInRules(
			Map<ArrayList<String>, ArrayList<String>> rules,
			ArrayList<String> prefix) {
		int result = PREFIX_NOT_SUB;
		String[] temp1;
		String[] temp2;
		ArrayList<String> tempPrefix;

		for (Map.Entry<ArrayList<String>, ArrayList<String>> entry : rules
				.entrySet()) {
			tempPrefix = entry.getKey();

			temp1 = new String[tempPrefix.size()];
			temp2 = new String[prefix.size()];

			tempPrefix.toArray(temp1);
			prefix.toArray(temp2);

			// 判断当前构造的前件是否已经是存在前件的子集
			if (isStrArrayContain(temp2, temp1)) {
				if (temp2.length == temp1.length) {
					result = PREFIX_EQUAL;
				} else {
					result = PREFIX_IS_SUB;
				}
			}

			if (result == PREFIX_EQUAL) {
				break;
			}
		}

		return result;
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
	 * 读关系表中的数据，并转化为事务数据
	 * 
	 * @param filePath
	 */
	private void readRDBMSData(String filePath) {
		String str;
		// 属性名称行
		String[] attrNames = null;
		String[] temp;
		String[] newRecord;
		ArrayList<String[]> datas = null;

		datas = readLine(filePath);

		// 获取首行
		attrNames = datas.get(0);
		this.transactionDatas = new ArrayList<>();

		// 去除首行数据
		for (int i = 1; i < datas.size(); i++) {
			temp = datas.get(i);

			// 过滤掉首列id列
			for (int j = 1; j < temp.length; j++) {
				str = "";
				// 采用属性名+属性值的形式避免数据的重复
				str = attrNames[j] + ":" + temp[j];
				temp[j] = str;
			}

			newRecord = new String[attrNames.length - 1];
			System.arraycopy(temp, 1, newRecord, 0, attrNames.length - 1);
			this.transactionDatas.add(newRecord);
		}

		attributeReplace();
		// 将事务数转到totalGoodsID中做统一处理
		this.totalGoodsIDs = transactionDatas;
	}

	/**
	 * 属性值的替换，替换成数字的形式，以便进行频繁项的挖掘
	 */
	private void attributeReplace() {
		int currentValue = 1;
		String s;
		// 属性名到数字的映射图
		attr2Num = new HashMap<>();
		num2Attr = new HashMap<>();

		// 按照1列列的方式来，从左往右边扫描,跳过列名称行和id列
		for (int j = 0; j < transactionDatas.get(0).length; j++) {
			for (int i = 0; i < transactionDatas.size(); i++) {
				s = transactionDatas.get(i)[j];

				if (!attr2Num.containsKey(s)) {
					attr2Num.put(s, currentValue);
					num2Attr.put(currentValue, s);

					transactionDatas.get(i)[j] = currentValue + "";
					currentValue++;
				} else {
					transactionDatas.get(i)[j] = attr2Num.get(s) + "";
				}
			}
		}
	}
}
