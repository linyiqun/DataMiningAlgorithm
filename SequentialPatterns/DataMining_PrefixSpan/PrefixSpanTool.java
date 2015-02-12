package DataMining_PrefixSpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * PrefixSpanTool序列模式分析算法工具类
 * 
 * @author lyq
 * 
 */
public class PrefixSpanTool {
	// 测试数据文件地址
	private String filePath;
	// 最小支持度阈值比例
	private double minSupportRate;
	// 最小支持度，通过序列总数乘以阈值比例计算
	private int minSupport;
	// 原始序列组
	private ArrayList<Sequence> totalSeqs;
	// 挖掘出的所有序列频繁模式
	private ArrayList<Sequence> totalFrequentSeqs;
	// 所有的单一项，用于递归枚举
	private ArrayList<String> singleItems;

	public PrefixSpanTool(String filePath, double minSupportRate) {
		this.filePath = filePath;
		this.minSupportRate = minSupportRate;
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

		minSupport = (int) (dataArray.size() * minSupportRate);
		totalSeqs = new ArrayList<>();
		totalFrequentSeqs = new ArrayList<>();
		Sequence tempSeq;
		ItemSet tempItemSet;
		for (String[] str : dataArray) {
			tempSeq = new Sequence();
			for (String s : str) {
				tempItemSet = new ItemSet(s);
				tempSeq.getItemSetList().add(tempItemSet);
			}
			totalSeqs.add(tempSeq);
		}

		System.out.println("原始序列数据：");
		outputSeqence(totalSeqs);
	}

	/**
	 * 输出序列列表内容
	 * 
	 * @param seqList
	 *            待输出序列列表
	 */
	private void outputSeqence(ArrayList<Sequence> seqList) {
		for (Sequence seq : seqList) {
			System.out.print("<");
			for (ItemSet itemSet : seq.getItemSetList()) {
				if (itemSet.getItems().size() > 1) {
					System.out.print("(");
				}

				for (String s : itemSet.getItems()) {
					System.out.print(s + " ");
				}

				if (itemSet.getItems().size() > 1) {
					System.out.print(")");
				}
			}
			System.out.println(">");
		}
	}

	/**
	 * 移除初始序列中不满足最小支持度阈值的单项
	 */
	private void removeInitSeqsItem() {
		int count = 0;
		HashMap<String, Integer> itemMap = new HashMap<>();
		singleItems = new ArrayList<>();

		for (Sequence seq : totalSeqs) {
			for (ItemSet itemSet : seq.getItemSetList()) {
				for (String s : itemSet.getItems()) {
					if (!itemMap.containsKey(s)) {
						itemMap.put(s, 1);
					}
				}
			}
		}

		String key;
		for (Map.Entry entry : itemMap.entrySet()) {
			count = 0;
			key = (String) entry.getKey();
			for (Sequence seq : totalSeqs) {
				if (seq.strIsContained(key)) {
					count++;
				}
			}

			itemMap.put(key, count);

		}

		for (Map.Entry entry : itemMap.entrySet()) {
			key = (String) entry.getKey();
			count = (int) entry.getValue();

			if (count < minSupport) {
				// 如果支持度阈值小于所得的最小支持度阈值，则删除该项
				for (Sequence seq : totalSeqs) {
					seq.deleteSingleItem(key);
				}
			} else {
				singleItems.add(key);
			}
		}

		Collections.sort(singleItems);
	}

	/**
	 * 递归搜索满足条件的序列模式
	 * 
	 * @param beforeSeq
	 *            前缀序列
	 * @param afterSeqList
	 *            后缀序列列表
	 */
	private void recursiveSearchSeqs(Sequence beforeSeq,
			ArrayList<Sequence> afterSeqList) {
		ItemSet tempItemSet;
		Sequence tempSeq2;
		Sequence tempSeq;
		ArrayList<Sequence> tempSeqList = new ArrayList<>();

		for (String s : singleItems) {
			// 分成2种形式递归，以<a>为起始项，第一种直接加入独立项集遍历<a,a>,<a,b> <a,c>..
			if (isLargerThanMinSupport(s, afterSeqList)) {
				tempSeq = beforeSeq.copySeqence();
				tempItemSet = new ItemSet(s);
				tempSeq.getItemSetList().add(tempItemSet);

				totalFrequentSeqs.add(tempSeq);

				tempSeqList = new ArrayList<>();
				for (Sequence seq : afterSeqList) {
					if (seq.strIsContained(s)) {
						tempSeq2 = seq.extractItem(s);
						tempSeqList.add(tempSeq2);
					}
				}

				recursiveSearchSeqs(tempSeq, tempSeqList);
			}

			// 第二种递归为以元素的身份加入最后的项集内以a为例<(aa)>,<(ab)>,<(ac)>...
			// a在这里可以理解为一个前缀序列，里面可能是单个元素或者已经是多元素的项集
			tempSeq = beforeSeq.copySeqence();
			int size = tempSeq.getItemSetList().size();
			tempItemSet = tempSeq.getItemSetList().get(size - 1);
			tempItemSet.getItems().add(s);

			if (isLargerThanMinSupport(tempItemSet, afterSeqList)) {
				tempSeqList = new ArrayList<>();
				for (Sequence seq : afterSeqList) {
					if (seq.compoentItemIsContain(tempItemSet)) {
						tempSeq2 = seq.extractCompoentItem(tempItemSet
								.getItems());
						tempSeqList.add(tempSeq2);
					}
				}
				totalFrequentSeqs.add(tempSeq);

				recursiveSearchSeqs(tempSeq, tempSeqList);
			}
		}
	}

	/**
	 * 所传入的项组合在所给定序列中的支持度是否超过阈值
	 * 
	 * @param s
	 *            所需匹配的项
	 * @param seqList
	 *            比较序列数据
	 * @return
	 */
	private boolean isLargerThanMinSupport(String s, ArrayList<Sequence> seqList) {
		boolean isLarge = false;
		int count = 0;

		for (Sequence seq : seqList) {
			if (seq.strIsContained(s)) {
				count++;
			}
		}

		if (count >= minSupport) {
			isLarge = true;
		}

		return isLarge;
	}

	/**
	 * 所传入的组合项集在序列中的支持度是否大于阈值
	 * 
	 * @param itemSet
	 *            组合元素项集
	 * @param seqList
	 *            比较的序列列表
	 * @return
	 */
	private boolean isLargerThanMinSupport(ItemSet itemSet,
			ArrayList<Sequence> seqList) {
		boolean isLarge = false;
		int count = 0;

		if (seqList == null) {
			return false;
		}

		for (Sequence seq : seqList) {
			if (seq.compoentItemIsContain(itemSet)) {
				count++;
			}
		}

		if (count >= minSupport) {
			isLarge = true;
		}

		return isLarge;
	}

	/**
	 * 序列模式分析计算
	 */
	public void prefixSpanCalculate() {
		Sequence seq;
		Sequence tempSeq;
		ArrayList<Sequence> tempSeqList = new ArrayList<>();
		ItemSet itemSet;
		removeInitSeqsItem();

		for (String s : singleItems) {
			// 从最开始的a,b,d开始递归往下寻找频繁序列模式
			seq = new Sequence();
			itemSet = new ItemSet(s);
			seq.getItemSetList().add(itemSet);

			if (isLargerThanMinSupport(s, totalSeqs)) {
				tempSeqList = new ArrayList<>();
				for (Sequence s2 : totalSeqs) {
					// 判断单一项是否包含于在序列中，包含才进行提取操作
					if (s2.strIsContained(s)) {
						tempSeq = s2.extractItem(s);
						tempSeqList.add(tempSeq);
					}
				}

				totalFrequentSeqs.add(seq);
				recursiveSearchSeqs(seq, tempSeqList);
			}
		}

		printTotalFreSeqs();
	}

	/**
	 * 按模式类别输出频繁序列模式
	 */
	private void printTotalFreSeqs() {
		System.out.println("序列模式挖掘结果：");
		
		ArrayList<Sequence> seqList;
		HashMap<String, ArrayList<Sequence>> seqMap = new HashMap<>();
		for (String s : singleItems) {
			seqList = new ArrayList<>();
			for (Sequence seq : totalFrequentSeqs) {
				if (seq.getItemSetList().get(0).getItems().get(0).equals(s)) {
					seqList.add(seq);
				}
			}
			seqMap.put(s, seqList);
		}

		int count = 0;
		for (String s : singleItems) {
			count = 0;
			System.out.println();
			System.out.println();

			seqList = (ArrayList<Sequence>) seqMap.get(s);
			for (Sequence tempSeq : seqList) {
				count++;
				System.out.print("<");
				for (ItemSet itemSet : tempSeq.getItemSetList()) {
					if (itemSet.getItems().size() > 1) {
						System.out.print("(");
					}

					for (String str : itemSet.getItems()) {
						System.out.print(str + " ");
					}

					if (itemSet.getItems().size() > 1) {
						System.out.print(")");
					}
				}
				System.out.print(">, ");

				// 每5个序列换一行
				if (count == 5) {
					count = 0;
					System.out.println();
				}
			}

		}
	}

}
