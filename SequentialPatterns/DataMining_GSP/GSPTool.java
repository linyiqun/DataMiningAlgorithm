package DataMining_GSP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * GSP序列模式分析算法
 * 
 * @author lyq
 * 
 */
public class GSPTool {
	// 测试数据文件地址
	private String filePath;
	// 最小支持度阈值
	private int minSupportCount;
	// 时间最小间隔
	private int min_gap;
	// 时间最大间隔
	private int max_gap;
	// 原始数据序列
	private ArrayList<Sequence> totalSequences;
	// GSP算法中产生的所有的频繁项集序列
	private ArrayList<Sequence> totalFrequencySeqs;
	// 序列项数字对时间的映射图容器
	private ArrayList<ArrayList<HashMap<Integer, Integer>>> itemNum2Time;

	public GSPTool(String filePath, int minSupportCount, int min_gap,
			int max_gap) {
		this.filePath = filePath;
		this.minSupportCount = minSupportCount;
		this.min_gap = min_gap;
		this.max_gap = max_gap;
		totalFrequencySeqs = new ArrayList<>();
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

		HashMap<Integer, Sequence> mapSeq = new HashMap<>();
		Sequence seq;
		ItemSet itemSet;
		int tID;
		String[] itemStr;
		for (String[] str : dataArray) {
			tID = Integer.parseInt(str[0]);
			itemStr = new String[Integer.parseInt(str[1])];
			System.arraycopy(str, 2, itemStr, 0, itemStr.length);
			itemSet = new ItemSet(itemStr);

			if (mapSeq.containsKey(tID)) {
				seq = mapSeq.get(tID);
			} else {
				seq = new Sequence(tID);
			}
			seq.getItemSetList().add(itemSet);
			mapSeq.put(tID, seq);
		}

		// 将序列图加入到序列List中
		totalSequences = new ArrayList<>();
		for (Map.Entry entry : mapSeq.entrySet()) {
			totalSequences.add((Sequence) entry.getValue());
		}
	}

	/**
	 * 生成1频繁项集
	 * 
	 * @return
	 */
	private ArrayList<Sequence> generateOneFrequencyItem() {
		int count = 0;
		int currentTransanctionID = 0;
		Sequence tempSeq;
		ItemSet tempItemSet;
		HashMap<Integer, Integer> itemNumMap = new HashMap<>();
		ArrayList<Sequence> seqList = new ArrayList<>();

		for (Sequence seq : totalSequences) {
			for (ItemSet itemSet : seq.getItemSetList()) {
				for (int num : itemSet.getItems()) {
					// 如果没有此种类型项，则进行添加操作
					if (!itemNumMap.containsKey(num)) {
						itemNumMap.put(num, 1);
					}
				}
			}
		}
		
		boolean isContain = false;
		int number = 0;
		for (Map.Entry entry : itemNumMap.entrySet()) {
			count = 0;
			number = (int) entry.getKey();
			for (Sequence seq : totalSequences) {
				isContain = false;
				
				for (ItemSet itemSet : seq.getItemSetList()) {
					for (int num : itemSet.getItems()) {
						// 如果没有此种类型项，则进行添加操作
						if (num == number) {
							isContain = true;
							break;
						}
					}
					
					if(isContain){
						break;
					}
				}
				
				if(isContain){
					count++;
				}
			}
			
			itemNumMap.put(number, count);
		}
		

		for (Map.Entry entry : itemNumMap.entrySet()) {
			count = (int) entry.getValue();
			if (count >= minSupportCount) {
				tempSeq = new Sequence();
				tempItemSet = new ItemSet(new int[] { (int) entry.getKey() });

				tempSeq.getItemSetList().add(tempItemSet);
				seqList.add(tempSeq);
			}

		}
		// 将序列升序排列
		Collections.sort(seqList);
		// 将频繁1项集加入总频繁项集列表中
		totalFrequencySeqs.addAll(seqList);

		return seqList;
	}

	/**
	 * 通过1频繁项集连接产生2频繁项集
	 * 
	 * @param oneSeq
	 *            1频繁项集序列
	 * @return
	 */
	private ArrayList<Sequence> generateTwoFrequencyItem(
			ArrayList<Sequence> oneSeq) {
		Sequence tempSeq;
		ArrayList<Sequence> resultSeq = new ArrayList<>();
		ItemSet tempItemSet;
		int num1;
		int num2;

		// 假如将<a>,<b>2个1频繁项集做连接组合，可以分为<a a>，<a b>，<b a>,<b b>4个序列模式
		// 注意此时的每个序列中包含2个独立项集
		for (int i = 0; i < oneSeq.size(); i++) {
			num1 = oneSeq.get(i).getFirstItemSetNum();
			for (int j = 0; j < oneSeq.size(); j++) {
				num2 = oneSeq.get(j).getFirstItemSetNum();

				tempSeq = new Sequence();
				tempItemSet = new ItemSet(new int[] { num1 });
				tempSeq.getItemSetList().add(tempItemSet);
				tempItemSet = new ItemSet(new int[] { num2 });
				tempSeq.getItemSetList().add(tempItemSet);

				if (countSupport(tempSeq) >= minSupportCount) {
					resultSeq.add(tempSeq);
				}
			}
		}

		// 上面连接还有1种情况是每个序列中只包含有一个项集的情况，此时a,b的划分则是<(a,a)> <(a,b)> <(b,b)>
		for (int i = 0; i < oneSeq.size(); i++) {
			num1 = oneSeq.get(i).getFirstItemSetNum();
			for (int j = i; j < oneSeq.size(); j++) {
				num2 = oneSeq.get(j).getFirstItemSetNum();

				tempSeq = new Sequence();
				tempItemSet = new ItemSet(new int[] { num1, num2 });
				tempSeq.getItemSetList().add(tempItemSet);

				if (countSupport(tempSeq) >= minSupportCount) {
					resultSeq.add(tempSeq);
				}
			}
		}
		// 同样将2频繁项集加入到总频繁项集中
		totalFrequencySeqs.addAll(resultSeq);

		return resultSeq;
	}

	/**
	 * 根据上次的频繁集连接产生新的侯选集
	 * 
	 * @param seqList
	 *            上次产生的候选集
	 * @return
	 */
	private ArrayList<Sequence> generateCandidateItem(
			ArrayList<Sequence> seqList) {
		Sequence tempSeq;
		ArrayList<Integer> tempNumArray;
		ArrayList<Sequence> resultSeq = new ArrayList<>();
		// 序列数字项列表
		ArrayList<ArrayList<Integer>> seqNums = new ArrayList<>();

		for (int i = 0; i < seqList.size(); i++) {
			tempNumArray = new ArrayList<>();
			tempSeq = seqList.get(i);
			for (ItemSet itemSet : tempSeq.getItemSetList()) {
				tempNumArray.addAll(itemSet.copyItems());
			}
			seqNums.add(tempNumArray);
		}

		ArrayList<Integer> array1;
		ArrayList<Integer> array2;
		// 序列i,j的拷贝
		Sequence seqi = null;
		Sequence seqj = null;
		// 判断是否能够连接，默认能连接
		boolean canConnect = true;
		// 进行连接运算，包括自己与自己连接
		for (int i = 0; i < seqNums.size(); i++) {
			for (int j = 0; j < seqNums.size(); j++) {
				array1 = (ArrayList<Integer>) seqNums.get(i).clone();
				array2 = (ArrayList<Integer>) seqNums.get(j).clone();

				// 将第一个数字组去掉第一个，第二个数字组去掉最后一个，如果剩下的部分相等，则可以连接
				array1.remove(0);
				array2.remove(array2.size() - 1);

				canConnect = true;
				for (int k = 0; k < array1.size(); k++) {
					if (array1.get(k) != array2.get(k)) {
						canConnect = false;
						break;
					}
				}

				if (canConnect) {
					seqi = seqList.get(i).copySeqence();
					seqj = seqList.get(j).copySeqence();

					int lastItemNum = seqj.getLastItemSetNum();
					if (seqj.isLastItemSetSingleNum()) {
						// 如果j序列的最后项集为单一值，则最后一个数字以独立项集加入i序列
						ItemSet itemSet = new ItemSet(new int[] { lastItemNum });
						seqi.getItemSetList().add(itemSet);
					} else {
						// 如果j序列的最后项集为非单一值，则最后一个数字加入i序列最后一个项集中
						ItemSet itemSet = seqi.getLastItemSet();
						itemSet.getItems().add(lastItemNum);
					}

					// 判断是否超过最小支持度阈值
					if (isChildSeqContained(seqi)
							&& countSupport(seqi) >= minSupportCount) {
						resultSeq.add(seqi);
					}
				}
			}
		}

		totalFrequencySeqs.addAll(resultSeq);
		return resultSeq;
	}

	/**
	 * 判断此序列的所有子序列是否也是频繁序列
	 * 
	 * @param seq
	 *            待比较序列
	 * @return
	 */
	private boolean isChildSeqContained(Sequence seq) {
		boolean isContained = false;
		ArrayList<Sequence> childSeqs;

		childSeqs = seq.createChildSeqs();
		for (Sequence tempSeq : childSeqs) {
			isContained = false;

			for (Sequence frequencySeq : totalFrequencySeqs) {
				if (tempSeq.compareIsSame(frequencySeq)) {
					isContained = true;
					break;
				}
			}

			if (!isContained) {
				break;
			}
		}

		return isContained;
	}

	/**
	 * 候选集判断支持度的值
	 * 
	 * @param seq
	 *            待判断序列
	 * @return
	 */
	private int countSupport(Sequence seq) {
		int count = 0;
		int matchNum = 0;
		Sequence tempSeq;
		ItemSet tempItemSet;
		HashMap<Integer, Integer> timeMap;
		ArrayList<ItemSet> itemSetList;
		ArrayList<ArrayList<Integer>> numArray = new ArrayList<>();
		// 每项集对应的时间链表
		ArrayList<ArrayList<Integer>> timeArray = new ArrayList<>();

		for (ItemSet itemSet : seq.getItemSetList()) {
			numArray.add(itemSet.getItems());
		}

		for (int i = 0; i < totalSequences.size(); i++) {
			timeArray = new ArrayList<>();

			for (int s = 0; s < numArray.size(); s++) {
				ArrayList<Integer> childNum = numArray.get(s);
				ArrayList<Integer> localTime = new ArrayList<>();
				tempSeq = totalSequences.get(i);
				itemSetList = tempSeq.getItemSetList();

				for (int j = 0; j < itemSetList.size(); j++) {
					tempItemSet = itemSetList.get(j);
					matchNum = 0;
					int t = 0;

					if (tempItemSet.getItems().size() == childNum.size()) {
						timeMap = itemNum2Time.get(i).get(j);
						// 只有当项集长度匹配时才匹配
						for (int k = 0; k < childNum.size(); k++) {
							if (timeMap.containsKey(childNum.get(k))) {
								matchNum++;
								t = timeMap.get(childNum.get(k));
							}
						}

						// 如果完全匹配，则记录时间
						if (matchNum == childNum.size()) {
							localTime.add(t);
						}
					}

				}

				if (localTime.size() > 0) {
					timeArray.add(localTime);
				}
			}

			// 判断时间是否满足时间最大最小约束，如果满足，则此条事务包含候选事务
			if (timeArray.size() == numArray.size()
					&& judgeTimeInGap(timeArray)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * 判断事务是否满足时间约束
	 * 
	 * @param timeArray
	 *            时间数组，每行代表各项集的在事务中的发生时间链表
	 * @return
	 */
	private boolean judgeTimeInGap(ArrayList<ArrayList<Integer>> timeArray) {
		boolean result = false;
		int preTime = 0;
		ArrayList<Integer> firstTimes = timeArray.get(0);
		timeArray.remove(0);

		if (timeArray.size() == 0) {
			return false;
		}

		for (int i = 0; i < firstTimes.size(); i++) {
			preTime = firstTimes.get(i);

			if (dfsJudgeTime(preTime, timeArray)) {
				result = true;
				break;
			}
		}

		return result;
	}

	/**
	 * 深度优先遍历时间，判断是否有符合条件的时间间隔
	 * 
	 * @param preTime
	 * @param timeArray
	 * @return
	 */
	private boolean dfsJudgeTime(int preTime,
			ArrayList<ArrayList<Integer>> timeArray) {
		boolean result = false;
		ArrayList<ArrayList<Integer>> timeArrayClone = (ArrayList<ArrayList<Integer>>) timeArray
				.clone();
		ArrayList<Integer> firstItemItem = timeArrayClone.get(0);

		for (int i = 0; i < firstItemItem.size(); i++) {
			if (firstItemItem.get(i) - preTime >= min_gap
					&& firstItemItem.get(i) - preTime <= max_gap) {
				// 如果此2项间隔时间满足时间约束，则继续往下递归
				preTime = firstItemItem.get(i);
				timeArrayClone.remove(0);

				if (timeArrayClone.size() == 0) {
					return true;
				} else {
					result = dfsJudgeTime(preTime, timeArrayClone);
					if (result) {
						return true;
					}
				}
			}
		}

		return result;
	}

	/**
	 * 初始化序列项到时间的序列图，为了后面的时间约束计算
	 */
	private void initItemNumToTimeMap() {
		Sequence seq;
		itemNum2Time = new ArrayList<>();
		HashMap<Integer, Integer> tempMap;
		ArrayList<HashMap<Integer, Integer>> tempMapList;

		for (int i = 0; i < totalSequences.size(); i++) {
			seq = totalSequences.get(i);
			tempMapList = new ArrayList<>();

			for (int j = 0; j < seq.getItemSetList().size(); j++) {
				ItemSet itemSet = seq.getItemSetList().get(j);
				tempMap = new HashMap<>();
				for (int itemNum : itemSet.getItems()) {
					tempMap.put(itemNum, j + 1);
				}

				tempMapList.add(tempMap);
			}

			itemNum2Time.add(tempMapList);
		}
	}

	/**
	 * 进行GSP算法计算
	 */
	public void gspCalculate() {
		ArrayList<Sequence> oneSeq;
		ArrayList<Sequence> twoSeq;
		ArrayList<Sequence> candidateSeq;

		initItemNumToTimeMap();
		oneSeq = generateOneFrequencyItem();
		twoSeq = generateTwoFrequencyItem(oneSeq);
		candidateSeq = twoSeq;

		// 不断连接生产候选集，直到没有产生出侯选集
		for (;;) {
			candidateSeq = generateCandidateItem(candidateSeq);

			if (candidateSeq.size() == 0) {
				break;
			}
		}

		outputSeqence(totalFrequencySeqs);

	}

	/**
	 * 输出序列列表信息
	 * 
	 * @param outputSeqList
	 *            待输出序列列表
	 */
	private void outputSeqence(ArrayList<Sequence> outputSeqList) {
		for (Sequence seq : outputSeqList) {
			System.out.print("<");
			for (ItemSet itemSet : seq.getItemSetList()) {
				System.out.print("(");
				for (int num : itemSet.getItems()) {
					System.out.print(num + ",");
				}
				System.out.print("), ");
			}
			System.out.println(">");
		}
	}

}
