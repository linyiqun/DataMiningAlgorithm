package DataMining_RoughSets;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据记录，包含这条记录所有属性
 * 
 * @author lyq
 * 
 */
public class Record {
	// 记录名称
	private String name;
	// 记录属性键值对
	private HashMap<String, String> attrValues;

	public Record(String name, HashMap<String, String> attrValues) {
		this.name = name;
		this.attrValues = attrValues;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * 此数据是否包含此属性值
	 * 
	 * @param attr
	 *            待判断属性值
	 * @return
	 */
	public boolean isContainedAttr(String attr) {
		boolean isContained = false;

		if (attrValues.containsValue(attr)) {
			isContained = true;
		}

		return isContained;
	}

	/**
	 * 判断数据记录是否是同一条记录，根据数据名称来判断
	 * 
	 * @param record
	 *            目标比较对象
	 * @return
	 */
	public boolean isRecordSame(Record record) {
		boolean isSame = false;

		if (this.name.equals(record.name)) {
			isSame = true;
		}

		return isSame;
	}

	/**
	 * 数据的决策属性分类
	 * 
	 * @return
	 */
	public String getRecordDecisionClass() {
		String value = null;

		value = attrValues.get(RoughSetsTool.DECISION_ATTR_NAME);

		return value;
	}

	/**
	 * 根据约简属性输出决策规则
	 * 
	 * @param reductAttr
	 *            约简属性集合
	 */
	public String getDecisionRule(ArrayList<String> reductAttr) {
		String ruleStr = "";
		String attrName = null;
		String value = null;
		String decisionValue;

		decisionValue = attrValues.get(RoughSetsTool.DECISION_ATTR_NAME);
		ruleStr += "属性";
		for (Map.Entry entry : this.attrValues.entrySet()) {
			attrName = (String) entry.getKey();
			value = (String) entry.getValue();

			if (attrName.equals(RoughSetsTool.DECISION_ATTR_NAME)
					|| reductAttr.contains(attrName) || value.equals(name)) {
				continue;
			}

			ruleStr += MessageFormat.format("{0}={1},", attrName, value);
		}
		ruleStr += "他的分类为" + decisionValue;
		
		return ruleStr;
	}
}
