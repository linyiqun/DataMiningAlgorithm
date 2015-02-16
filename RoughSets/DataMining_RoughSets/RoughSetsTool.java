package DataMining_RoughSets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 粗糙集属性约简算法工具类
 * 
 * @author lyq
 * 
 */
public class RoughSetsTool {
	// 决策属性名称
	public static String DECISION_ATTR_NAME;

	// 测试数据文件地址
	private String filePath;
	// 数据属性列名称
	private String[] attrNames;
	// 所有的数据
	private ArrayList<String[]> totalDatas;
	// 所有的数据记录,与上面的区别是记录的属性是可约简的，原始数据是不能变的
	private ArrayList<Record> totalRecords;
	// 条件属性图
	private HashMap<String, ArrayList<String>> conditionAttr;
	// 属性记录集合
	private ArrayList<RecordCollection> collectionList;

	public RoughSetsTool(String filePath) {
		this.filePath = filePath;
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

		String[] array;
		Record tempRecord;
		HashMap<String, String> attrMap;
		ArrayList<String> attrList;
		totalDatas = new ArrayList<>();
		totalRecords = new ArrayList<>();
		conditionAttr = new HashMap<>();
		// 赋值属性名称行
		attrNames = dataArray.get(0);
		DECISION_ATTR_NAME = attrNames[attrNames.length - 1];
		for (int j = 0; j < dataArray.size(); j++) {
			array = dataArray.get(j);
			totalDatas.add(array);
			if (j == 0) {
				// 过滤掉第一行列名称数据
				continue;
			}

			attrMap = new HashMap<>();
			for (int i = 0; i < attrNames.length; i++) {
				attrMap.put(attrNames[i], array[i]);

				// 寻找条件属性
				if (i > 0 && i < attrNames.length - 1) {
					if (conditionAttr.containsKey(attrNames[i])) {
						attrList = conditionAttr.get(attrNames[i]);
						if (!attrList.contains(array[i])) {
							attrList.add(array[i]);
						}
					} else {
						attrList = new ArrayList<>();
						attrList.add(array[i]);
					}
					conditionAttr.put(attrNames[i], attrList);
				}
			}
			tempRecord = new Record(array[0], attrMap);
			totalRecords.add(tempRecord);
		}
	}

	/**
	 * 将数据记录根据属性分割到集合中
	 */
	private void recordSpiltToCollection() {
		String attrName;
		ArrayList<String> attrList;
		ArrayList<Record> recordList;
		HashMap<String, String> collectionAttrValues;
		RecordCollection collection;
		collectionList = new ArrayList<>();

		for (Map.Entry entry : conditionAttr.entrySet()) {
			attrName = (String) entry.getKey();
			attrList = (ArrayList<String>) entry.getValue();

			for (String s : attrList) {
				recordList = new ArrayList<>();
				// 寻找属性为s的数据记录分入到集合中
				for (Record record : totalRecords) {
					if (record.isContainedAttr(s)) {
						recordList.add(record);
					}
				}
				collectionAttrValues = new HashMap<>();
				collectionAttrValues.put(attrName, s);
				collection = new RecordCollection(collectionAttrValues,
						recordList);

				collectionList.add(collection);
			}
		}
	}

	/**
	 * 构造属性集合图
	 * 
	 * @param reductAttr
	 *            需要约简的属性
	 * @return
	 */
	private HashMap<String, ArrayList<RecordCollection>> constructCollectionMap(
			ArrayList<String> reductAttr) {
		String currentAtttrName;
		ArrayList<RecordCollection> cList;
		// 集合属性对应图
		HashMap<String, ArrayList<RecordCollection>> collectionMap = new HashMap<>();

		// 截取出条件属性部分
		for (int i = 1; i < attrNames.length - 1; i++) {
			currentAtttrName = attrNames[i];

			// 判断此属性列是否需要约简
			if (reductAttr != null && reductAttr.contains(currentAtttrName)) {
				continue;
			}

			cList = new ArrayList<>();

			for (RecordCollection c : collectionList) {
				if (c.isContainedAttrName(currentAtttrName)) {
					cList.add(c);
				}
			}

			collectionMap.put(currentAtttrName, cList);
		}

		return collectionMap;
	}

	/**
	 * 根据已有的分裂集合计算知识系统
	 */
	private ArrayList<RecordCollection> computeKnowledgeSystem(
			HashMap<String, ArrayList<RecordCollection>> collectionMap) {
		String attrName = null;
		ArrayList<RecordCollection> cList = null;
		// 知识系统
		ArrayList<RecordCollection> ksCollections;

		ksCollections = new ArrayList<>();

		// 取出1项
		for (Map.Entry entry : collectionMap.entrySet()) {
			attrName = (String) entry.getKey();
			cList = (ArrayList<RecordCollection>) entry.getValue();
			break;
		}
		collectionMap.remove(attrName);

		for (RecordCollection rc : cList) {
			recurrenceComputeKS(ksCollections, collectionMap, rc);
		}

		return ksCollections;
	}

	/**
	 * 递归计算所有的知识系统，通过计算所有集合的交集
	 * 
	 * @param ksCollection
	 *            已经求得知识系统的集合
	 * @param map
	 *            还未曾进行过交运算的集合
	 * @param preCollection
	 *            前个步骤中已经通过交运算计算出的集合
	 */
	private void recurrenceComputeKS(ArrayList<RecordCollection> ksCollections,
			HashMap<String, ArrayList<RecordCollection>> map,
			RecordCollection preCollection) {
		String attrName = null;
		RecordCollection tempCollection;
		ArrayList<RecordCollection> cList = null;
		HashMap<String, ArrayList<RecordCollection>> mapCopy = new HashMap<>();
		
		//如果已经没有数据了，则直接添加
		if(map.size() == 0){
			ksCollections.add(preCollection);
			return;
		}

		for (Map.Entry entry : map.entrySet()) {
			cList = (ArrayList<RecordCollection>) entry.getValue();
			mapCopy.put((String) entry.getKey(), cList);
		}

		// 取出1项
		for (Map.Entry entry : map.entrySet()) {
			attrName = (String) entry.getKey();
			cList = (ArrayList<RecordCollection>) entry.getValue();
			break;
		}

		mapCopy.remove(attrName);
		for (RecordCollection rc : cList) {
			// 挑选此属性的一个集合进行交运算，然后再次递归
			tempCollection = preCollection.overlapCalculate(rc);

			if (tempCollection == null) {
				continue;
			}

			// 如果map中已经没有数据了,说明递归到头了
			if (mapCopy.size() == 0) {
				ksCollections.add(tempCollection);
			} else {
				recurrenceComputeKS(ksCollections, mapCopy, tempCollection);
			}
		}
	}

	/**
	 * 进行粗糙集属性约简算法
	 */
	public void findingReduct() {
		RecordCollection[] sameClassRcs;
		KnowledgeSystem ks;
		ArrayList<RecordCollection> ksCollections;
		// 待约简的属性
		ArrayList<String> reductAttr = null;
		ArrayList<String> attrNameList;
		// 最终可约简的属性组
		ArrayList<ArrayList<String>> canReductAttrs;
		HashMap<String, ArrayList<RecordCollection>> collectionMap;

		sameClassRcs = selectTheSameClassRC();
		// 这里讲数据按照各个分类的小属性划分了9个集合
		recordSpiltToCollection();

		collectionMap = constructCollectionMap(reductAttr);
		ksCollections = computeKnowledgeSystem(collectionMap);
		ks = new KnowledgeSystem(ksCollections);
		System.out.println("原始集合分类的上下近似集合");
		ks.getDownSimilarRC(sameClassRcs[0]).printRc();
		ks.getUpSimilarRC(sameClassRcs[0]).printRc();
		ks.getDownSimilarRC(sameClassRcs[1]).printRc();
		ks.getUpSimilarRC(sameClassRcs[1]).printRc();

		attrNameList = new ArrayList<>();
		for (int i = 1; i < attrNames.length - 1; i++) {
			attrNameList.add(attrNames[i]);
		}

		ArrayList<String> remainAttr;
		canReductAttrs = new ArrayList<>();
		reductAttr = new ArrayList<>();
		// 进行条件属性的递归约简
		for (String s : attrNameList) {
			remainAttr = (ArrayList<String>) attrNameList.clone();
			remainAttr.remove(s);
			reductAttr = new ArrayList<>();
			reductAttr.add(s);
			recurrenceFindingReduct(canReductAttrs, reductAttr, remainAttr,
					sameClassRcs);
		}
		
		printRules(canReductAttrs);
	}

	/**
	 * 递归进行属性约简
	 * 
	 * @param resultAttr
	 *            已经计算出的约简属性组
	 * @param reductAttr
	 *            将要约简的属性组
	 * @param remainAttr
	 *            剩余的属性
	 * @param sameClassRc
	 *            待计算上下近似集合的同类集合
	 */
	private void recurrenceFindingReduct(
			ArrayList<ArrayList<String>> resultAttr,
			ArrayList<String> reductAttr, ArrayList<String> remainAttr,
			RecordCollection[] sameClassRc) {
		KnowledgeSystem ks;
		ArrayList<RecordCollection> ksCollections;
		ArrayList<String> copyRemainAttr;
		ArrayList<String> copyReductAttr;
		HashMap<String, ArrayList<RecordCollection>> collectionMap;
		RecordCollection upRc1;
		RecordCollection downRc1;
		RecordCollection upRc2;
		RecordCollection downRc2;

		collectionMap = constructCollectionMap(reductAttr);
		ksCollections = computeKnowledgeSystem(collectionMap);
		ks = new KnowledgeSystem(ksCollections);
		
		downRc1 = ks.getDownSimilarRC(sameClassRc[0]);
		upRc1 = ks.getUpSimilarRC(sameClassRc[0]);
		downRc2 = ks.getDownSimilarRC(sameClassRc[1]);
		upRc2 = ks.getUpSimilarRC(sameClassRc[1]);

		// 如果上下近似没有完全拟合原集合则认为属性不能被约简
		if (!upRc1.isCollectionSame(sameClassRc[0])
				|| !downRc1.isCollectionSame(sameClassRc[0])) {
			return;
		}
		//正类和负类都需比较
		if (!upRc2.isCollectionSame(sameClassRc[1])
				|| !downRc2.isCollectionSame(sameClassRc[1])) {
			return;
		}

		// 加入到结果集中
		resultAttr.add(reductAttr);
		//只剩下1个属性不能再约简
		if (remainAttr.size() == 1) {
			return;
		}

		for (String s : remainAttr) {
			copyRemainAttr = (ArrayList<String>) remainAttr.clone();
			copyReductAttr = (ArrayList<String>) reductAttr.clone();
			copyRemainAttr.remove(s);
			copyReductAttr.add(s);
			recurrenceFindingReduct(resultAttr, copyReductAttr, copyRemainAttr,
					sameClassRc);
		}
	}

	/**
	 * 选出决策属性一致的集合
	 * 
	 * @return
	 */
	private RecordCollection[] selectTheSameClassRC() {
		RecordCollection[] resultRc = new RecordCollection[2];
		resultRc[0] = new RecordCollection();
		resultRc[1] = new RecordCollection();
		String attrValue;

		// 找出第一个记录的决策属性作为一个分类
		attrValue = totalRecords.get(0).getRecordDecisionClass();
		for (Record r : totalRecords) {
			if (attrValue.equals(r.getRecordDecisionClass())) {
				resultRc[0].getRecord().add(r);
			}else{
				resultRc[1].getRecord().add(r);
			}
		}

		return resultRc;
	}
	
	/**
	 * 输出决策规则
	 * @param reductAttrArray
	 * 约简属性组
	 */
	public void printRules(ArrayList<ArrayList<String>> reductAttrArray){
		//用来保存已经描述过的规则，避免重复输出
		ArrayList<String> rulesArray;
		String rule;
		
		for(ArrayList<String> ra: reductAttrArray){
			rulesArray = new ArrayList<>();
			System.out.print("约简的属性：");
			for(String s: ra){
				System.out.print(s + ",");
			}
			System.out.println();
			
			for(Record r: totalRecords){
				rule = r.getDecisionRule(ra);
				if(!rulesArray.contains(rule)){
					rulesArray.add(rule);
					System.out.println(rule);
				}
			}
			System.out.println();
		} 
	}

	/**
	 * 输出记录集合
	 * 
	 * @param rcList
	 *            待输出记录集合
	 */
	public void printRecordCollectionList(ArrayList<RecordCollection> rcList) {
		for (RecordCollection rc : rcList) {
			System.out.print("{");
			for (Record r : rc.getRecord()) {
				System.out.print(r.getName() + ", ");
			}
			System.out.println("}");
		}
	}
}
