# 数据挖掘算法

## 算法目录
#### 18大DM算法
包名 | 目录名 | 算法名 |
-----| ------ |--------|
AssociationAnalysis | DataMining_Apriori | Apriori-关联规则挖掘算法
AssociationAnalysis | DataMining_FPTree | FPTree-频繁模式树算法
BaggingAndBoosting | DataMining_AdaBoost | AdaBoost-装袋提升算法
Classification | DataMining_CART | CART-分类回归树算法
Classification | DataMining_ID3 | ID3-决策树分类算法
Classification | DataMining_KNN | KNN-k最近邻算法工具类
Classification | DataMining_NaiveBayes | NaiveBayes-朴素贝叶斯算法
Clustering | DataMining_BIRCH | BIRCH-层次聚类算法
Clustering | DataMining_KMeans | KMeans-K均值算法
GraphMining | DataMining_GSpan | GSpan-频繁子图挖掘算法
IntegratedMining | DataMining_CBA | CBA-基于关联规则的分类算法
LinkMining | DataMining_HITS | HITS-链接分析算法
LinkMining | DataMining_PageRank | PageRank-网页重要性/排名算法
RoughSets | DataMining_RoughSets | RoughSets-粗糙集属性约简算法
SequentialPatterns | DataMining_GSP | GSP-序列模式分析算法
SequentialPatterns | DataMining_PrefixSpan | PrefixSpan-序列模式分析算法
StatisticalLearning | DataMining_EM | EM-期望最大化算法
StatisticalLearning | DataMining_SVM | SVM-支持向量机算法

#### 其他经典DM算法
包名 | 目录名 | 算法名 |
-----| ------ |--------|
Others | DataMining_ACO | ACO-蚁群算法
Others | DataMining_BayesNetwork | BayesNetwork-贝叶斯网络算法
Others | DataMining_CABDDCC | CABDDCC-基于连通图的分裂聚类算法
Others | DataMining_Chameleon | Chameleon-两阶段合并聚类算法
Others | DataMining_DBSCAN | DBSCAN-基于密度的聚类算法
Others | DataMining_GA | GA-遗传算法
Others | DataMining_GA_Maze | GA_Maze-遗传算法在走迷宫游戏中的应用算法
Others | DataMining_KDTree | KDTree-k维空间关键数据检索算法工具类
Others | DataMining_MSApriori | MSApriori-基于多支持度的Apriori算法
Others | DataMining_RandomForest | RandomForest-随机森林算法
Others | DataMining_TAN | TAN-树型朴素贝叶斯算法
Others | DataMining_Viterbi | Viterbi-维特比算法

## 18大经典DM算法
18大数据挖掘的经典算法以及代码实现，涉及到了决策分类，聚类，链接挖掘，关联挖掘，模式挖掘等等方面,后面都是相应算法的博文链接，希望能够帮助大家学。
目前追加了其他的一些经典的DM算法，在others的包中涉及聚类，分类，图算法，搜索算等等，没有具体分类。

* ### C4.5
C4.5算法与ID3算法一样，都是数学分类算法，C4.5算法是ID3算法的一个改进。ID3算法采用信息增益进行决策判断，而C4.5采用的是增益率。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/42395865)

* ### CART
CART算法的全称是分类回归树算法，他是一个二元分类，采用的是类似于熵的基尼指数作为分类决策，形成决策树后之后还要进行剪枝，我自己在实现整个算法的时候采用的是代价复杂度算法，[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/42558235)

* ### KNN
K最近邻算法。给定一些已经训练好的数据，输入一个新的测试数据点，计算包含于此测试数据点的最近的点的分类情况，哪个分类的类型占多数，则此测试点的分类与此相同，所以在这里,有的时候可以复制不同的分类点不同的权重。近的点的权重大点，远的点自然就小点。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/42613011)

* ### Naive Bayes
朴素贝叶斯算法。朴素贝叶斯算法是贝叶斯算法里面一种比较简单的分类算法，用到了一个比较重要的贝叶斯定理，用一句简单的话概括就是条件概率的相互转换推导。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/42680161)

* ### SVM
支持向量机算法。支持向量机算法是一种对线性和非线性数据进行分类的方法，非线性数据进行分类的时候可以通过核函数转为线性的情况再处理。其中的一个关键的步骤是搜索最大边缘超平面。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/42780439)

* ### EM
期望最大化算法。期望最大化算法，可以拆分为2个算法，1个E-Step期望化步骤,和1个M-Step最大化步骤。他是一种算法框架，在每次计算结果之后，逼近统计模型参数的最大似然或最大后验估计。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/42921789)

* ### Apriori
Apriori算法是关联规则挖掘算法，通过连接和剪枝运算挖掘出频繁项集，然后根据频繁项集得到关联规则，关联规则的导出需要满足最小置信度的要求。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43059211)

* ### FP-Tree
频繁模式树算法。这个算法也有被称为FP-growth算法，这个算法克服了Apriori算法的产生过多侯选集的缺点，通过递归的产生频度模式树，然后对树进行挖掘，后面的过程与Apriori算法一致。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43234309)

* ### PageRank
网页重要性/排名算法。PageRank算法最早产生于Google,核心思想是通过网页的入链数作为一个网页好快的判定标准，如果1个网页内部包含了多个指向外部的链接，则PR值将会被均分，PageRank算法也会遭到LinkSpan攻击。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43311943)

* ### HITS
HITS算法是另外一个链接算法，部分原理与PageRank算法是比较相似的，HITS算法引入了权威值和中心值的概念，HITS算法是受用户查询条件影响的，他一般用于小规模的数据链接分析，也更容易遭受到攻击。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43311943)

* ### K-Means
K-Means算法是聚类算法，k在在这里指的是分类的类型数，所以在开始设定的时候非常关键，算法的原理是首先假定k个分类点，然后根据欧式距离计算分类，然后去同分类的均值作为新的聚簇中心，循环操作直到收敛。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43373159)

* ### BIRCH
BIRCH算法利用构建CF聚类特征树作为算法的核心，通过树的形式，BIRCH算法扫描数据库，在内存中建立一棵初始的CF-树，可以看做数据的多层压缩。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43532111)

* ### AdaBoost
AdaBoost算法是一种提升算法，通过对数据的多次训练得到多个互补的分类器，然后组合多个分类器，构成一个更加准确的分类器。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43635115)

* ### GSP
GSP算法是序列模式挖掘算法。GSP算法也是Apriori类算法，在算法的过程中也会进行连接和剪枝操作，不过在剪枝判断的时候还加上了一些时间上的约束等条件。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43699083)

* ### PreFixSpan
PreFixSpan算法是另一个序列模式挖掘算法，在算法的过程中不会产生候选集，给定初始前缀模式，不断的通过后缀模式中的元素转到前缀模式中，而不断的递归挖掘下去。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43766253)

* ### CBA
基于关联规则分类算法。CBA算法是一种集成挖掘算法，因为他是建立在关联规则挖掘算法之上的，在已有的关联规则理论前提下，做分类判断，只是在算法的开始时对数据做处理，变成类似于事务的形式。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43818787)

* ### RoughSets
粗糙集算法。粗糙集理论是一个比较新颖的数据挖掘思想。这里使用的是用粗糙集进行属性约简的算法，通过上下近似集的判断删除无效的属性，进行规制的输出。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43876001)

* ### GSpan
gSpan算法属于图挖掘算法领域。，主要用于频繁子图的挖掘，相较于其他的图算法，子图挖掘算法是他们的一个前提或基础算法。gSpan算法用到了DFS编码，和Edge五元组，最右路径子图扩展等概念，算法比较的抽象和复杂。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/43924273)

##Others目录下的算法：

* ### GA
遗传算法。遗传算法运用了生物进化理论的知识来寻找问题最优解的算法，算法的遗传进化过程分选择，交叉和变异操作，其中选择操是非常关键的步骤，把更适应的基于组遗传给下一代。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/44041499)

* ### DbScan
基于空间密度聚类算法。dbScan作为一种特殊聚类算法，弥补了其他算法的一些不足，基于空间密，实现聚类效果，可以发现任意形状的聚簇。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/44311309)

* ### GA_Maze
遗传算法在走迷宫游戏中的应用。将走迷宫中的搜索出口路径的问题转化为遗传算法中的问题通过构造针对此特定问题的适值函数，基因移动方向的定位，巧的进行问题的求解。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/44656809)

* ### CABDDCC
基于连通图的分裂聚类算法。也是属于层次聚类算法主要分为2个阶段，第一阶段构造连通图。第二个阶段是分裂连通图，最终形成聚类结果。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/44463997)

* ### Chameleon
两阶段聚类算法。与CABDDCC算法相反，最后是通过对小簇集合的合并，形成最终的结果，在第一阶段主要是通过K近邻的思想形成小规模的连通图，第二阶段通过RI(相对互连性)和RC(相对近似性)来选一个最佳的簇进行合并。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/44569077)

* ### RandomForest
随机森林算法。算法思想是决策树+boosting.决策树采用的是CART分类回归数,通过组合各个决策树的弱分类器,构成一个最终的强分类器,在构造决策树的时候采取随机数量的样本数和随机的部分属性进行子决策树的构建,避免了过分拟合的现象发生。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/44756943)

* ### KDTree
K-Dimension Tree。多维空间划分树，数据在多维空间进行划分与查找。主要用于关键信息的搜索，类似于在空间中的二分搜索，大大提高了搜索效率，在寻找目标元素时，使用了DFS深度优先的方式和回溯进行最近点的寻找。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/44985259)

* ### MS-Apriori
基于多支持度的Apriori算法。是Apriori算法的升级算法，弥补了原先Apriori算法的不足，还增加了支持度差别限制以及支持度计数统计方面的优化，无须再次重新扫描整个数据集，产生关联规则的时候可以根据子集的关系避免一些置信度的计算。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/45082337)

* ### ACO
蚁群算法。蚁群算法又称为蚂蚁算法。同GA遗传算法类似，也是运用了大自然规律的算法，用于在图中寻找最优路径的概率型算法。灵感来源于蚂蚁在寻找食物时会散播信息素的发现路径行为。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/45395491)

* ### BayesNetwork
贝叶斯网络算法。弥补了朴素贝叶斯算法中必须要事件独立性的缺点，利用了贝叶斯网络的DAG有向无环图，允许各个事件保留一定的依赖关系，网络结构中的每个节点代表一种属性，边代表相应的条件概率值，通过计算从而能得到精准的分类效果。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/46683729)

* ### TAN
树型朴素贝叶斯算法。此算法又被称为加强版朴素贝叶斯算法。在满足原有朴素贝叶斯条件的基础上，他允许部条件属性直接的关联性。形成树型的结构。[详细介绍链接](http://blog.csdn.net/androidlushangderen/article/details/46763427)

* ### Viterbi
维特比算法。给定一个隐马尔科夫模型以及一个观察序列，求出潜在的状态序列信息，每个潜在状态信息又会受到前一个状态信息的影响。

## 算法使用方法
在每个算法中给出了3大类型，主算法程序，调用程序，输入数据，调用方法如下：
* 将需要数据的测试数据转化成与给定的输入格式相同
* 然后以Client类的测试程序调用方式进行使用。
* 也可以自行修改算法程序，来适用于自己的使用场景
