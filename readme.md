# CF4J: Collaborative Filtering for Java

A Java's Collaborative Filtering library to carry out experiments in research of Collaborative Filtering based Recommender Systems. The library has been designed from researchers to researchers.

## Index

1. [Installation](#installation)
2. [Getting Started](#getting-started)
3. [Project Structure](#project-structure)
4. [Customize CF4J](#customize-cf4j)
5. [Algorithm List](#algorithm-list)
6. [Examples](#examples)
7. [Datasets](#datasets)

## Installation

Add the following lines to the dependencies section in the `pom.xml` of the Maven's project.

```xml
<dependency>
  <groupId>com.github.ferortega</groupId>
  <artifactId>cf4j</artifactId>
  <version>2.0.1</version>
</dependency>
```

To use the library in other type of project, you must add the `jar` packaged version of CF4J to your project's classpath. For example, if you are using IntelliJ IDEA, copy the file to your project's directory, make right click on the `jar` file and select `Add as Library`.

You can find the `jar` packaged version of CF4J into the release section of github.

You can also package your own `jar` file . To do that, clone the repository using `git clone git@github.com:ferortega/cf4j.git` and package it with `mvn package`.

## Getting Started

Let's encode our first experiment with CF4J. In this experiment, we will compare the Mean Squared Error (MSE) of two well known matrix factorization models: Probabilistic Matrix Factorization (PMF) and Non-negative Matrix Factorization (NMF). We will use [MovieLens 100k dataset](https://grouplens.org/datasets/movielens/100k/) as ratings' database.

1. First of all, we are going to load the database from the ratings file using an instance of `DataSet` interface. We choose `RandomSplitDataSet` that automatically splits the ratings set into training ratings and test ratings. We select 20% of users and 20% of items as test users and items respectively. To ensure the reproducibility of the example, we are going to fix the random seed to 43.

    ```Java
    String filename = "ml100k.data";
    double testUsers = 0.2;
    double testItems = 0.2;
    String separator = "\t";
    long seed = 43;
    DataSet ml100k = new RandomSplitDataSet(filename, testUsers, testItems, separator, seed);
	```

2. Now, we are going to create a `DataModel` from the previous `DataSet`. A `DataModel` is a high level in memory representation of the data structure required by collaborative filtering algorithms. 

    ```Java
   DataModel datamodel = new DataModel(ml100k);
   ```
   
3. Once all the data are loaded, we are going to build and train our first recommendation model. To create a recommendation model it is necessary to instantiate a `Recommender` from a `DataModel`. `Recommender` is an abstract class that contains all the methods and attributes required to perform a recommendation. CF4J contains several implementations of `Recommender` (see [algorithms list](#algorithm-list)). 

   First, we instantiate the `PMF` recommender. Then, we set the model's hyper-parameters using the `PMF` class constructor: `numFactors = 10`, `gamma = 0.01`, `lambda = 0.1` and `numIters = 100`. We also fix the random `seed = 43` to ensure the reproducibility of this experiment. Once the model is instantiated, we train the model parameters using `fit()` method.
   
   ```Java
   PMF pmf = new PMF(datamodel, 10, 100, 0.1, 0.01, seed);
   pmf.fit();
   ```
   
   We can now repeat this process for the `NMF` recommender. 
   
   ```Java
   NMF nmf = new NMF(datamodel, 10, 100, seed);
   nmf.fit();
   ```
   
4. Finally we are going to compare the quality of both recommendation models using a `QualityMeasure`. This class simplifies the evaluation of collaborative filtering based recommendation models. In this example, we are going to use the `MSE` quality measure that compares the quadratic difference between the test ratings and the model predictions. To do so, we just need to create a new instance of the `MSE` class from the `Recommender` and compute the score using `getScore()` method.

   ```Java
   QualityMeasure mse;
   
   mse = new MSE(pmf);
   System.out.println("\nMSE (PMF): " + mse.getScore());
   
   mse = new MSE(nmf);
   System.out.println("MSE (NMF): " + mse.getScore());
   ```
  
5. The program will prints the following output.

   ```
   MSE (PMF): 1.045296527045507
   MSE (NMF): 1.4204697638323218
   ```
   
   You can find the full code of this example in [GettingStartedExample](src/main/java/com/github/ferortega/cf4j/examples/GettingStartedExample.java).

## Project Structure

The following image shows the summarized class diagram of the whole project. You can see the full version of this diagram [here](http://rs.etsisi.upm.es/cf4j-2.0.1/images/class-diagram.jpg). The project has been divided into 4 main packages: `data`, `recommender`, `qualityMeasure` and `util`.

![CF4J class diagram](http://rs.etsisi.upm.es/cf4j-2.0.1/images/class-diagram-summarized.jpg)

### `com.github.ferortega.cf4j.data` package

This package contains all the classes that are needed to extract, transform, load and manipulate the data used by collaborative filtering algorithms. The most important classes of this package are:

- `DataSet`. This interface is used to iterate over training and test ratings. Two implementations of this interface have been included: `RandomSplitDataSet` that randomly splits the ratings contained in a file into training and test ratings; and `TrainTestFilesDataSet` that loads training and test ratings from two different files. 

- `DataModel`. This class manages all the information related with a collaborative filtering based recommender system. A `DataModel` must be instantiated from a `DataSet`. Once the `DataModel` is created, it is composed by: 
  + An array to store (training) `User` instances.
  + An array to store `TestUser` instances.
  + An array to store (training) `Item` instances.
  + An array to store `TestItem` instances.
  
- `User`. This class represents a training user. Each `User` is defined by his/her index in the `User` array of the `DataModel` and an unique identifier. The `User` class contains the list of items rated by the user. These ratings can be retrieved using `getItemAt(pos)`, that returns the index of the item rated at the `pos` position, and `getRatingAt(pos)`, that returns the rating value of the item rated at the `pos` position. Items' indexes returned by `getItemAt(pos)` are sorted from lower to higher.

- `Item`. This class represents a training item. Each `Item` is defined by its index in the `Item` array of the `DataModel` and an unique identifier. The `Item` class contains the list of users that have rated the item. These ratings can be retrieved using `getUserAt(pos)`, that returns the user index of the `User` instance that have rated the item at the `pos` position, and `getRatingAt(pos)`, that returns the rating value at the `pos` position. Users' indexes returned by `getUserAt(pos)` are sorted from lower to higher.

- `TestUser`. This class represents a test user. Every `TestUser` is also a `User` due to the heritage relation between `User` and `TestUser` classes. Each `TestUser` is defined by his/her index in the `TestUser` array of the `DataModel`. The `TestUser` class contains the list of test items rated in test by the test user. These test ratings can be retrieved using `getTestItemAt(pos)`, that returns the index of the item rated at the `pos` position, and `getTestRatingAt(pos)`, that returns the test rating value of the test item rated at the `pos` position. Test items' indexes returned by `getTestItemAt(pos)` are sorted from lower to higher.

- `TestItem`. This class represents a test item. Every `TestItem` is also a `Item` due to the heritage relation between `Item` and `TestItem` classes. Each `TestItem` is defined by his/her index in the `TestItem` array of the `DataModel`. The `TestItem` class contains the list of test users that have rated in test the item. These test ratings can be retrieved using `getTestUserAt(pos)`, that returns the index of the `testUser` instance that have rated the test item at the `pos` position, and `getTestRatingAt(pos)`, that returns the test rating value at the `pos` position. Test users' indexes returned by `getTestUserAt(pos)` are sorted from lower to higher.

### `com.github.ferortega.cf4j.recommender` package

This package contains several implementations of collaborative filtering algorithms. You can check the full list in the [Algorithm List](#algorithm-list) section. Each collaborative filtering algorithm included in CF4J must extends the `Recommender` abstract class. This class forces to implement the following abstract methods:

- `fit()`: used to estimate collaborative filtering recommender parameters given the hyper-parameters usually defined in the class constructor. To speed up the fitting process, most of the computations has been parallelized using [`Parallelizer`](http://rs.etsisi.upm.es/cf4j-2.0.1/apidocs/com/github/ferortega/cf4j/util/Parallelizer.html) util.

- `predict(userIndex, itemIndex)`: used to estimate the rating prediction of the user with index `userIndex` to the item with index `itemIndex`.

Each `Recommender` must be created from a `DataModel` instance and will be fitted to it.

### `com.github.ferortega.cf4j.qualityMeasure` package

This package contains the implementation of different quality measures for collaborative filtering based recommender systems. These quality measures are used to evaluate the performance of a `Recommender` instance. Included quality measures has been classified into two categories:

- Quality measures for predictions, allocated into `com.github.ferortega.cf4j.qualityMeasures.prediction` package.
- Quality measures for recommendations, allocated into `com.github.ferortega.cf4j.qualityMeasures.recommendation` package.

Each quality measure included in CF4J extends `QualityMeasure` abstract class. This class simplifies the computation of a quality measure from the test ratings. It contains the `getScore()` method that computes the score of the quality measure for each test user and returns the averaged score. The computation of the quality measure score for each test user is performed in parallel.

### `com.github.ferortega.cf4j.util` package

This package contains different utilities to be used with the library. 

Read the [javadoc](http://rs.etsisi.upm.es/cf4j-2.0.1/apidocs/) documentation for additional information.

## Customize CF4J

CF4J has been designed for the collaborative filtering's research community, so its extendability has been one of the main requirements of this project. As described above, an execution with CF4J includes the following steps:

1. Load a dataset using an implementation of the `DataSet` class.
2. Create a new `DataModel` from the loaded `DataSet`.
3. Fit a `Recommender` to the `DataSet`.
4. Evaluate the performance of a `Recommender` using a `QualityMeasure`.

Therefore, if you want to customize CF4J, you must work with `DataSet`, `DataModel`, `Recommender` and `QualityMeasure` classes:

`DataSet` is an interface that contains two methods to iterate over training ratings (`getRatingsIterator()`) and test ratings (`getTestRatingsIterator()`). The iteration is carried out over `DataSetEntry` instances, that contains the user, item and value of a rating. Any class that implements this interface may be used to create a `DataModel`.

`DataModel` is a class that should not be modified. It has been encoded to manage the essential information required by most of collaborative filtering algorithms (i.e. users, items and ratings). However, there are several algorithms that includes additional information to the recommendation process such as demographic information about the users or items description. Both `DataModel`, `User` and `Item` includes a `DataBank` instance (see [javadoc](http://rs.etsisi.upm.es/cf4j-2.0.1/apidocs/com/github/ferortega/cf4j/data/DataBank.html)) to store and retrieve any additional information required by a custom `Recommender`.

`Recommender` class can be extended to create your own collaborative filtering algorithm. As mentioned above, to create a new `Recommender` you must define the `fit()` and `predict(userIndex, itemIndex)` methods. In addition, to create a new similarity metric for a kNN based collaborative filtering, you should extend `UserSimilarityMetric` or `ItemSimilarityMetric` for user-to-user or item-to-item approaches of kNN, respectively.

`QualityMeasure` class allows to easily define new quality measures for both predictions and recommendations. This class includes an abstract method, `getScore(TestUser testUser, double[] predictions)`, that must be implemented to compute the score of a `testUser` given his/her `predictions`.

## Algorithm List

In this section we include the full list of algorithms implemented in the library.

* Matrix factorization algorithms (`com.github.ferortega.cf4j.recommender.matrixFactorization` package):

   | Class         | Publication |
   |---------------|-------------|
   | `BiasedMF`    | Koren, Y., Bell, R., &amp; Volinsky, C. (2009). *Matrix factorization techniques for recommender systems*. Computer, (8), 30-37 |
   | `BNMF`        | Hernando, A., Bobadilla, J., &amp; Ortega, F. (2016). *A non negative matrix factorization for collaborative filtering recommender systems on a Bayesian probabilistic model*. Knowledge-Based Systems, 97, 188-202 |
   | `CLiMF`       | Shi, Y., Karatzoglou, A., Baltrunas, L., Larson, M., Oliver, N., &amp; Hanjalic, A. (2012, September). *CLiMF: learning to maximize reciprocal rank with collaborative less-is-more filtering*. In Proceedings of the sixth ACM conference on Recommender systems (pp. 139-146) |
   | `HPF`         | Gopalan, P., Hofman, J. M., &amp; Blei, D. M. (2015, July). *Scalable Recommendation with Hierarchical Poisson Factorization*. In UAI (pp. 326-335) |
   | `NMF`         | Lee, D. D., &amp;  Seung, H. S. (2001). *Algorithms for non-negative matrix factorization*. In Advances in neural information processing systems (pp. 556-562) |
   | `PMF`         | Mnih, A., &amp; Salakhutdinov, R. R. (2008). *Probabilistic matrix factorization*. In Advances in neural information processing systems (pp. 1257-1264) |
   | `SVDPlusPlus` | Koren, Y. (2008, August). *Factorization meets the neighborhood: a multifaceted collaborative filtering model*. In Proceedings of the 14th ACM SIGKDD international conference on Knowledge discovery and data mining (pp. 426-434) |
   | `URP`         | Marlin, B. M. (2004). *Modeling user rating profiles for collaborative filtering*. In Advances in neural information processing systems (pp. 627-634) |


* kNN based CF (both user-to-user and item-to-item approaches):

  + Traditional similarity metrics inspired by statistics (`com.github.ferortega.cf4j.recommender.knn.userSimilairtyMetrics` and `com.github.ferortega.cf4j.recommender.knn.itemSimilairtyMetrics` packages):
    - Pearson Correlation (`Correlation`)
    - Pearson Correlation Constrained (`CorrelationConstrained`)
    - Cosine similarity (`Cosine`)
    - Adjusted Cosine similarity (`AdjustedCosine`)
    - Jaccard index (`Jaccard`)
    - Mean Squared Difference (`MSD`)
    - Spearman Rank (`SpearmanRank`)

  + Similarity metrics created ad-hoc for collaborative filtering algorithm (`com.github.ferortega.cf4j.recommender.knn.userSimilairtyMetrics` and `com.github.ferortega.cf4j.recommender.knn.itemSimilairtyMetrics` packages):
  
     | Class           | Publication |
     |-----------------|-------------|
     | `CJMSD`         | Bobadilla, J., Ortega, F., Hernando, A., &amp; Arroyo, A. (2012). *A Balanced Memory-Based Collaborative Filtering Similarity Measure*, International Journal of Intelligent Systems, 27, 939-946. |
     | `JMSD`          | Bobadilla, J., Serradilla, F., &amp; Bernal, J. (2010). *A new collaborative filtering metric that improves the behavior of Recommender Systems*, Knowledge-Based Systems, 23 (6), 520-528. |
     | `PIP`           | Ahn, H. J. (2008). *A new similarity  measure for collaborative filtering to alleviate the new user cold-starting problem*, Information Sciences, 178, 37-51. |
     | `Singularities` | Bobadilla, J., Ortega, F., &amp; Hernando, A. (2012). *A collaborative filtering similarity measure based on singularities*, Information Processing and Management, 48 (2), 204-217. |
 
 
* Quality measures:

  + For prediction (`com.github.ferortega.cf4j.qualityMeasure.prediction` package):
    - Coverage (`Coverage`)
    - Mean Absolute Error (`MAE`)
    - Max User Error (`Max`)
    - Mean Squared Error (`MSE`)
    - Mean Squared Logarithmic Error (`MSLE`)
    - Percentage of prefect predictions (`Perfect`)
    - Coefficient of determination R2 (`R2`)
    - Root Mean Squared Error (`RMSE`)
    
  + For recommendation (`com.github.ferortega.cf4j.qualityMeasure.recommendation` package):
    - Precision (`Precision`)
    - Recall (`Recall`)
    - F1 (`F1`)
    - Normalized Discounted Cumulative Gain (`NDCG`)
    - Novelty (`Novelty`)
    - Discovery (`Discovery`)
    - Diversity (`Diversity`)

## Examples

In this section we include additional examples to show the operation of CF4J. 

In [MatrixFactorizationComparison](src/main/java/com/github/ferortega/cf4j/examples/MatrixFactorizationComparison.java) we compare the RMSE and F1 for different matrix factorization models varying the number of latent factors:

```java
// Grid search over number of factors hyper-parameter
private static final int[] numFactors = Range.ofIntegers(5,5,5);

// Same number of iterations for all matrix factorization models
private static final int numIter = 50;

// Random seed to guaranty reproducibility of the experiment
private static final long randomSeed = 43;

public static void main (String [] args) throws IOException {

    // Step 1: Preparing the dataset to be splitted in two parts: training and test (Load MovieLens 100K dataset)
    DataSet ml100k = new RandomSplitDataSet("src/main/resources/datasets/ml100k.data", 0.2, 0.2, "\t", randomSeed);

    // Step 2: Storing the data in the DataModel to be efficiently accessed by the recommenders
    DataModel datamodel = new DataModel(ml100k);

    // To store results
    PrintableQualityMeasure rmseScores = new PrintableQualityMeasure("RMSE", numFactors);
    PrintableQualityMeasure f1Scores = new PrintableQualityMeasure("F1", numFactors);

    // Step 3.a: Generating an specific recommender (Probabilistic matrix factorization) with different factors
    for (int factors : numFactors) {
        Recommender pmf = new PMF(datamodel, factors, numIter, randomSeed);
        pmf.fit();

        // Step 4.a: Setting up a RMSE and F1 quality measures with PMF recommender.
        QualityMeasure rmse = new RMSE(pmf);
        rmseScores.putScore(factors, "PMF", rmse.getScore());

        QualityMeasure f1 = new F1(pmf,10, 4);
        f1Scores.putScore(factors, "PMF", f1.getScore());
    }

    // Step 3.b: Generating an specific recommender (Bayesian Non-negative Matrix Factorization) with different factors
    for (int factors : numFactors) {
        Recommender bnmf = new BNMF(datamodel, factors, numIter, 0.2, 10, randomSeed);
        bnmf.fit();

        // Step 4.b: Setting up a RMSE and F1 quality measures with BNMF recommender.
        QualityMeasure rmse = new RMSE(bnmf);
        rmseScores.putScore(factors, "BNMF", rmse.getScore());

        QualityMeasure f1 = new F1(bnmf,10, 4);
        f1Scores.putScore(factors, "BNMF", f1.getScore());
    }

    // Step 3.c: Generating an specific recommender (Biased Matrix Factorization) with different factors
    for (int factors : numFactors) {
        Recommender biasedmf = new BiasedMF(datamodel, factors, numIter, randomSeed);
        biasedmf.fit();

        // Step 4.c: Setting up a RMSE and F1 quality measures with BiasedMF recommender.
        QualityMeasure rmse = new RMSE(biasedmf);
        rmseScores.putScore(factors, "BiasedMF", rmse.getScore());

        QualityMeasure f1 = new F1(biasedmf,10, 4);
        f1Scores.putScore(factors, "BiasedMF", f1.getScore());
    }

    // Step 3.d: Generating an specific recommender (Non-negative Matrix Factorization) with different factors
    for (int factors : numFactors) {
        Recommender nmf = new NMF(datamodel, factors, numIter, randomSeed);
        nmf.fit();

        // Step 4.d: Setting up a RMSE and F1 quality measures with NMF recommender.
        QualityMeasure rmse = new RMSE(nmf);
        rmseScores.putScore(factors, "NMF", rmse.getScore());

        QualityMeasure f1 = new F1(nmf,10, 4);
        f1Scores.putScore(factors, "NMF", f1.getScore());
    }

    // Step 3.f: Generating an specific recommender (Collaborative Less-is-More Filtering) with different factors
    for (int factors : numFactors) {
        Recommender climf = new CLiMF(datamodel, factors, numIter, randomSeed);
        climf.fit();

        // Step 4.f: Setting up a RMSE and F1 quality measures with CLiMF recommender.
        QualityMeasure rmse = new RMSE(climf);
        rmseScores.putScore(factors, "CLiMF", rmse.getScore());

        QualityMeasure f1 = new F1(climf,10, 4);
        f1Scores.putScore(factors, "CLiMF", f1.getScore());
    }

    // Step 3.g: Generating an specific recommender (SVD++) with different factors
    for (int factors : numFactors) {
        Recommender svdPlusPlus = new SVDPlusPlus(datamodel, factors, numIter, randomSeed);
        svdPlusPlus.fit();

        // Step 4.g: Setting up a RMSE and F1 quality measures with SVDPlusPlus recommender.
        QualityMeasure rmse = new RMSE(svdPlusPlus);
        rmseScores.putScore(factors, "SVDPlusPlus", rmse.getScore());

        QualityMeasure f1 = new F1(svdPlusPlus,10, 4);
        f1Scores.putScore(factors, "SVDPlusPlus", f1.getScore());
    }

    // Step 3.h: Generating an specific recommender (Hierarchical Poisson Factorization) with different factors
    for (int factors : numFactors) {
        Recommender hpf = new HPF(datamodel, factors, numIter, randomSeed);
        hpf.fit();

        // Step 4.h: Setting up a RMSE and F1 quality measures with HPF recommender.
        QualityMeasure rmse = new RMSE(hpf);
        rmseScores.putScore(factors, "HPF", rmse.getScore());

        QualityMeasure f1 = new F1(hpf,10, 4);
        f1Scores.putScore(factors, "HPF", f1.getScore());
    }

    // Step 3.i: Generating an specific recommender (User Rating Profiles) with different factors
    for (int factors : numFactors) {
        double[] ratings = {1.0, 2.0, 3.0, 4.0, 5.0};
        Recommender urp = new URP(datamodel, factors, ratings, numIter, randomSeed);
        urp.fit();

        // Step 4.i: Setting up a RMSE and F1 quality measures with URP recommender.
        QualityMeasure rmse = new RMSE(urp);
        rmseScores.putScore(factors, "URP", rmse.getScore());

        QualityMeasure f1 = new F1(urp,10, 4);
        f1Scores.putScore(factors, "URP", f1.getScore());
    }

    // Step 5: Printing the results
    rmseScores.print();
    f1Scores.print();
}
```

The program will output the following results:

```
RMSE
            URP     BiasedMF  SVDPlusPlus          NMF          PMF         BNMF        CLiMF          HPF
5      1,147216     0,945209     1,045447     1,023785     0,968681     1,059451     2,668724     2,862586
10     1,145944     0,943000     1,130097     1,060075     0,969985     1,073015     1,671452     2,862586
15     1,146991     0,942857     1,221443     1,075078     0,982645     1,075971     1,297566     2,862586
20     1,137211     0,939024     1,321603     1,092861     0,963954     1,084917     1,812653     2,862586
25     1,143487     0,948637     1,397062     1,098636     0,977123     1,101971     2,891408     2,862586

F1
            URP     BiasedMF  SVDPlusPlus          NMF          PMF         BNMF        CLiMF          HPF
5      0,665028     0,679093     0,665240     0,675659     0,677784     0,661660     0,625939     0,639927
10     0,666203     0,686026     0,653261     0,663828     0,682080     0,661192     0,605024     0,639927
15     0,664832     0,675804     0,654551     0,665490     0,684804     0,662722     0,626594     0,639927
20     0,669554     0,681793     0,657067     0,667218     0,683441     0,663840     0,618655     0,639927
25     0,666377     0,683050     0,650450     0,668642     0,681276     0,655716     0,620043     0,639927
```

In [ItemKnnComparison](src/main/java/com/github/ferortega/cf4j/examples/ItemKnnComparison.java) we compare the MSLE and nDCG quality measures scores for different similarity metrics applied to item-to-item knn based collaborative filtering. Each similarity metric is tested with different number of neighbors:

```java
// Grid search over number of neighbors hyper-parameter
private static final int[] numNeighbors = Range.ofIntegers(100,50,5);

// Fixed aggregation approach
private static final ItemKNN.AggregationApproach aggregationApproach = ItemKNN.AggregationApproach.MEAN;

// Random seed to guaranty reproducibility of the experiment
private static final long randomSeed = 43;

public static void main (String [] args) throws IOException {

    // Step 1: Preparing the dataset to be splitted in two parts: training and test (Load MovieLens 100K dataset)
    DataSet ml1m = new RandomSplitDataSet("src/main/resources/datasets/ml100k.data", 0.2, 0.2, "\t", randomSeed);

    // Step 2: Storing the data in the DataModel to be efficiently accessed by the recommenders
    DataModel datamodel = new DataModel(ml1m);

    // DataSet parameters
    double[] relevantRatings = {3, 4, 5};
    double[] notRelevantRatings = {1, 2};

    // To store results
    PrintableQualityMeasure msleScores = new PrintableQualityMeasure("MSLE", numNeighbors);
    PrintableQualityMeasure ndcgScores = new PrintableQualityMeasure("NDCG", numNeighbors);

    // Create similarity metrics
    ArrayList<ItemSimilarityMetric> metrics = new ArrayList<>();
    metrics.add(new AdjustedCosine());
    metrics.add(new Correlation());
    metrics.add(new Cosine());
    metrics.add(new Jaccard());
    metrics.add(new JMSD());
    metrics.add(new MSD());
    metrics.add(new PIP());
    metrics.add(new Singularities(relevantRatings, notRelevantRatings));
    metrics.add(new SpearmanRank());

    // Evaluate ItemKNN recommender
    for (ItemSimilarityMetric metric : metrics) {
        String metricName = metric.getClass().getSimpleName();

        for (int k : numNeighbors) {
            // Step 3: Generating an specific recommender (ItemKNN) with a number of neighbors applying different metrics.
            Recommender knn = new ItemKNN(datamodel, k, metric, aggregationApproach);
            knn.fit();

            // Step 4: Setting up a MSLE and nDCG quality measures with ItemKNN recommender.
            QualityMeasure msle = new MSLE(knn);
            msleScores.putScore(k, metricName, msle.getScore());

            QualityMeasure ndcg = new NDCG(knn,10);
            ndcgScores.putScore(k, metricName, ndcg.getScore());
        }
    }

    // Step 5: Printing the results
    msleScores.print();
    ndcgScores.print();
}
```

The program will output the following results:

```
MSLE
        Correlation   Singularities            JMSD             PIP  AdjustedCosine         Jaccard    SpearmanRank          Cosine             MSD
100        0,318433        0,251566        0,241642        0,257104        0,270548        0,244898        0,256119        0,361521        0,259081
150        0,326281        0,251691        0,244389        0,255251        0,265932        0,245888        0,255657        0,364797        0,257521
200        0,314223        0,252069        0,245720        0,255092        0,271698        0,247856        0,256358        0,342082        0,248004
250        0,288312        0,253365        0,245416        0,255254        0,252651        0,248229        0,257171        0,319616        0,246424
300        0,278303        0,253110        0,246938        0,254554        0,246946        0,249424        0,257068        0,296390        0,246937

NDCG
        Correlation   Singularities            JMSD             PIP  AdjustedCosine         Jaccard    SpearmanRank          Cosine             MSD
100        0,512928        1,171205        1,143986        1,139746        0,704328        1,141128        1,166582        0,354474        0,782335
150        0,644135        1,164531        1,145066        1,132707        0,868873        1,135258        1,130220        0,378641        0,901750
200        0,764740        1,151330        1,148762        1,130443        0,998609        1,145257        1,144629        0,458238        0,997616
250        0,880271        1,139038        1,141368        1,136442        1,058628        1,129431        1,134793        0,602078        1,055863
300        0,951793        1,136819        1,142463        1,142724        1,108925        1,136880        1,130153        0,778681        1,089436
```

Finally, in [UserKnnComparison](src/main/java/com/github/ferortega/cf4j/examples/UserKnnComparison.java) we compare the MAE, Coverage, Precision and Recall quality measures scores for different similarity metrics applied to user-to-user knn based collaborative filtering. Each similarity metric is tested with different number of neighbors:

```java
// Grid search over number of neighbors hyper-parameter
private static final int[] numNeighbors = Range.ofIntegers(100,50,5);

// Fixed aggregation approach
private static final UserKNN.AggregationApproach aggregationApproach = UserKNN.AggregationApproach.DEVIATION_FROM_MEAN;

// Random seed to guaranty reproducibility of the experiment
private static final long randomSeed = 43;

public static void main (String [] args) throws IOException {

    // Step 1: Preparing the dataset to be splitted in two parts: training and test (Load MovieLens 100K dataset)
    DataSet ml1m = new RandomSplitDataSet("src/main/resources/datasets/ml100k.data", 0.2, 0.2, "\t", randomSeed);

    // Step 2: Storing the data in the DataModel to be efficiently accessed by the recommenders
    DataModel datamodel = new DataModel(ml1m);

    // Step 3 and 4: Generating an ItemKNN recommender which uses different item similarity metrics:

    // Dataset parameters
    double[] relevantRatings = {3, 4, 5};
    double[] notRelevantRatings = {1, 2};

    // To store results
    PrintableQualityMeasure maeScores = new PrintableQualityMeasure("MAE", numNeighbors);
    PrintableQualityMeasure coverageScores = new PrintableQualityMeasure("Coverage", numNeighbors);
    PrintableQualityMeasure precisionScores = new PrintableQualityMeasure("Precision", numNeighbors);
    PrintableQualityMeasure recallScores = new PrintableQualityMeasure("Recall", numNeighbors);

    // Create similarity metrics
    List<UserSimilarityMetric> metrics = new ArrayList<>();
    metrics.add(new AdjustedCosine());
    metrics.add(new CJMSD());
    metrics.add(new Correlation());
    metrics.add(new Cosine());
    metrics.add(new Jaccard());
    metrics.add(new JMSD());
    metrics.add(new MSD());
    metrics.add(new PIP());
    metrics.add(new Singularities(relevantRatings, notRelevantRatings));
    metrics.add(new SpearmanRank());

    // Evaluate UserKNN recommender
    for (UserSimilarityMetric metric : metrics) {
        String metricName = metric.getClass().getSimpleName();

        for (int k : numNeighbors) {
            // Step 3: Generating an specific recommender (UserKNN) with a number of neighbors applying different metrics.
            Recommender knn = new UserKNN(datamodel, k, metric, aggregationApproach);
            knn.fit();

            // Step 4: Setting up a MAE, Coverage, Precision, and Recall quality measures with UserKNN recommender.
            QualityMeasure mae = new MAE(knn);
            maeScores.putScore(k, metricName, mae.getScore());

            QualityMeasure coverage = new Coverage(knn);
            coverageScores.putScore(k, metricName, coverage.getScore());

            QualityMeasure precision = new Precision(knn,10, 4);
            precisionScores.putScore(k, metricName, precision.getScore());

            QualityMeasure recall = new Recall(knn,10, 4);
            recallScores.putScore(k, metricName, recall.getScore());
        }
    }

    // Step 5: Printing the results
    maeScores.print();
    coverageScores.print();
    precisionScores.print();
    recallScores.print();
}
```

The program will output the following results:

```
MAE
        Correlation   Singularities           CJMSD            JMSD             PIP  AdjustedCosine         Jaccard    SpearmanRank          Cosine             MSD
100        0,898954        0,809687        0,807358        0,791859        0,788755        0,869157        0,799340        0,801607        0,930473        0,851600
150        0,860057        0,808615        0,805246        0,798793        0,789560        0,834456        0,795797        0,802933        0,887269        0,814720
200        0,818200        0,805083        0,804379        0,802710        0,792320        0,824575        0,807378        0,804484        0,857773        0,813759
250        0,799799        0,803750        0,805203        0,805071        0,793557        0,813849        0,807961        0,802212        0,822812        0,807216
300        0,797065        0,803339        0,804846        0,807055        0,793313        0,804269        0,811045        0,802861        0,820105        0,805873

Coverage
        Correlation   Singularities           CJMSD            JMSD             PIP  AdjustedCosine         Jaccard    SpearmanRank          Cosine             MSD
100        0,840302        0,995471        0,998924        0,977997        0,998069        0,845187        0,975364        0,995106        0,760310        0,887770
150        0,939709        0,996753        0,999319        0,988961        0,998647        0,955332        0,981784        0,997884        0,856974        0,942291
200        0,977278        0,997823        0,999442        0,992595        0,998686        0,979125        0,992902        0,998941        0,919125        0,970749
250        0,987640        0,998155        0,999442        0,994516        0,999319        0,987933        0,994265        0,999021        0,960297        0,983528
300        0,992497        0,999260        0,999482        0,994847        0,999359        0,994979        0,994807        0,999359        0,979326        0,990228

Precision
        Correlation   Singularities           CJMSD            JMSD             PIP  AdjustedCosine         Jaccard    SpearmanRank          Cosine             MSD
100        0,674916        0,689492        0,691702        0,684017        0,693912        0,674960        0,682072        0,694464        0,676775        0,681709
150        0,678534        0,691702        0,692254        0,688880        0,696674        0,680711        0,684943        0,695569        0,678851        0,691023
200        0,687214        0,689492        0,692807        0,684997        0,695017        0,681277        0,684997        0,691702        0,677015        0,696363
250        0,691465        0,693359        0,693912        0,686900        0,695017        0,687124        0,684690        0,693359        0,682727        0,695074
300        0,696332        0,693359        0,694464        0,688005        0,693359        0,691176        0,688558        0,692807        0,692940        0,699033

Recall
        Correlation   Singularities           CJMSD            JMSD             PIP  AdjustedCosine         Jaccard    SpearmanRank          Cosine             MSD
100        0,694929        0,769980        0,772757        0,748551        0,773014        0,687654        0,745197        0,772204        0,664665        0,729907
150        0,738375        0,768980        0,773544        0,759807        0,774568        0,746743        0,750369        0,775188        0,697824        0,749910
200        0,756536        0,767954        0,774722        0,758982        0,773027        0,753570        0,761582        0,770051        0,721896        0,766568
250        0,763937        0,772247        0,775265        0,761334        0,772949        0,761056        0,762359        0,774427        0,747312        0,765097
300        0,771250        0,773957        0,775951        0,762531        0,771665        0,768762        0,765254        0,773652        0,762526        0,771868
```

As you can observe, we have used the [PrintableQualityMeasure](http://rs.etsisi.upm.es/cf4j-2.0.1/apidocs/com/github/ferortega/cf4j/util/PrintableQualityMeasure.html) utility to simplify the manage of the results reported for each tested recommender. This class contains a `.toString()` method that can be used to export its output to a `csv` format to be used by external programs in order to make a detailed analysis of these results.

## Datasets

You can find awesome datasets to use with CF4J at this site: [http://shuaizhang.tech/2017/03/15/Datasets-For-Recommender-System/](http://shuaizhang.tech/2017/03/15/Datasets-For-Recommender-System/).
