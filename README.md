# DataMiningAlgorithm
数据挖掘中经典的算法实现和详细的注释

18大数据挖掘的经典算法以及代码实现，涉及到了决策分类，聚类，链接挖掘，关联挖掘，模式挖掘等等方面,后面都是相应算法的博文链接，希望能够帮助大家学习。

1.C4.5算法。C4.5算法与ID3算法一样，都是数学分类算法，C4.5算法是ID3算法的一个改进。ID3算法采用信息增益进行决策判断，而C4.5采用的是增益率。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/42395865。

2.CART算法。CART算法的全称是分类回归树算法，他是一个二元分类，采用的是类似于熵的基尼指数作为分类决策，形成决策树后之后还要进行剪枝，我自己在实现整个算法的时候采用的是代价复杂度算法，
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/42558235。

3.KNN(K最近邻)算法。给定一些已经训练好的数据，输入一个新的测试数据点，计算包含于此测试数据点的最近的点的分类情况，哪个分类的类型占多数，则此测试点的分类与此相同，所以在这里,有的时候可以复制不同的分类点不同的权重。近的点的权重大点，远的点自然就小点。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/42613011。

4.Naive Bayes(朴素贝叶斯)算法。朴素贝叶斯算法是贝叶斯算法里面一种比较简单的分类算法，用到了一个比较重要的贝叶斯定理，用一句简单的话概括就是条件概率的相互转换推导。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/42680161。

5.SVM(支持向量机)算法。支持向量机算法是一种对线性和非线性数据进行分类的方法，非线性数据进行分类的时候可以通过核函数转为线性的情况再处理。其中的一个关键的步骤是搜索最大边缘超平面。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/42780439。

6.EM(期望最大化)算法。期望最大化算法，可以拆分为2个算法，1个E-Step期望化步骤,和1个M-Step最大化步骤。他是一种算法框架，在每次计算结果之后，逼近统计模型参数的最大似然或最大后验估计。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/42921789。

7.Apriori算法。Apriori算法是关联规则挖掘算法，通过连接和剪枝运算挖掘出频繁项集，然后根据频繁项集得到关联规则，关联规则的导出需要满足最小置信度的要求。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43059211。

8.FP-Tree(频繁模式树)算法。这个算法也有被称为FP-growth算法，这个算法克服了Apriori算法的产生过多侯选集的缺点，通过递归的产生频度模式树，然后对树进行挖掘，后面的过程与Apriori算法一致。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43234309。

9.PageRank(网页重要性/排名)算法。PageRank算法最早产生于Google,核心思想是通过网页的入链数作为一个网页好快的判定标准，如果1个网页内部包含了多个指向外部的链接，则PR值将会被均分，PageRank算法也会遭到Link Span攻击。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43311943。

10.HITS算法。HITS算法是另外一个链接算法，部分原理与PageRank算法是比较相似的，HITS算法引入了权威值和中心值的概念，HITS算法是受用户查询条件影响的，他一般用于小规模的数据链接分析，也更容易遭受到攻击。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43311943。

11.K-Means(K均值)算法。K-Means算法是聚类算法，k在在这里指的是分类的类型数，所以在开始设定的时候非常关键，算法的原理是首先假定k个分类点，然后根据欧式距离计算分类，然后去同分类的均值作为新的聚簇中心，循环操作直到收敛。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43373159。

12.BIRCH算法。BIRCH算法利用构建CF聚类特征树作为算法的核心，通过树的形式，BIRCH算法扫描数据库，在内存中建立一棵初始的CF-树，可以看做数据的多层压缩。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43532111。

13.AdaBoost算法。AdaBoost算法是一种提升算法，通过对数据的多次训练得到多个互补的分类器，然后组合多个分类器，构成一个更加准确的分类器，
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43635115。

14.GSP算法。GSP算法是序列模式挖掘算法。GSP算法也是Apriori类算法，在算法的过程中也会进行连接和剪枝操作，不过在剪枝判断的时候还加上了一些时间上的约束等条件。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43699083。

15.PreFixSpan算法。PreFixSpan算法是另一个序列模式挖掘算法，在算法的过程中不会产生候选集，给定初始前缀模式，不断的通过后缀模式中的元素转到前缀模式中，而不断的递归挖掘下去。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43766253。

16.CBA(基于关联规则分类)算法。CBA算法是一种集成挖掘算法，因为他是建立在关联规则挖掘算法之上的，在已有的关联规则理论前提下，做分类判断，只是在算法的开始时对数据做处理，变成类似于事务的形式。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43818787。

17.RoughSets(粗糙集)算法。粗糙集理论是一个比较新颖的数据挖掘思想。这里使用的是用粗糙集进行属性约简的算法，通过上下近似集的判断删除无效的属性，进行规制的输出。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43876001。

18.gSpan算法。gSpan算法属于图挖掘算法领域。，主要用于频繁子图的挖掘，相较于其他的图算法，子图挖掘算法是他们的一个前提或基础算法。gSpan算法用到了DFS编码，和Edge五元组，最右路径子图扩展等概念，算法比较的抽象和复杂。
详细介绍链接：http://blog.csdn.net/androidlushangderen/article/details/43924273。
