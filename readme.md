# CF4J: Collaborative Filtering for Java

A Java's Collaborative Filtering library to carry out experiments in research of Collaborative Filtering based Recommender Systems. The library has been designed from researchers to researchers.

## Index

1. [Installation](#installation)
2. [Getting Started](#getting-started)
3. [Project Structure](#proyect-structure)
4. [Customize CF4J](#customize-cf4j)
5. [Algorithm List](#algorithm-list)
6. [Datasets](#datasets)

## Installation

### For Maven

Add the following lines to the dependencies section in the `pom.xml` of the Maven's project.

```xml

```

### For Gradle

```

```

### For SBT

```

```

### For other types of project

To use the library in other type of project, you must add the `jar` packaged version of CF4J to your project's classpath. For example, if you are using IntelliJ IDEA, copy the file to your project's directory, make right click on the `jar` file and select `Add as Library`.

You can find the `jar` packaged version of CF4J into the release section of github.

You can also package your own `jar` file . To do that, clone the repository using `git clone git@github.com:ferortega/cf4j.git` and package it with `mvn package`.

## Getting Started

Let's encode our first experiment with CF4J. In this experiment, we will compare the Mean Squared Error (MSE) of two well known matrix factorization models: Probabilistic Matrix Factorization (PMF) and Non-negative Matrix Factorization (Nmf). We will use [MovieLens 1M dataset](https://grouplens.org/datasets/movielens/) as ratings' database.

1. First of all, we are going to load the database from the ratings file using an instance of `DataSet` interface. We choose `RandomSplitDataSet` that automatically splits the ratings set into training ratings and test ratings. We select 20% of users and 20% of items as test users and items respectively. To ensure the reproducibility of the example, we are going to fix the random seed to 43.

    ```Java
    String filename = "ml1m.dat";
    double testUsers = 0.2;
    double testItems = 0.2;
    String separator = "::";
    long seed = 42;
    DataSet ml1m = new RandomSplitDataSet(filename, testUsers, testItems, separator, seed);
	```

2. Now, we are going to create a `DataModel` from the previous `DataSet`. A `DataModel` is a high level in memory representation of the data structure required by collaborative filtering algorithms. 

    ```Java
   DataModel datamodel = new DataModel(ml1m);
   ```
   
3. Once all the data are loaded, we are going to build and train our first recommendation model. To create a recommendation model it is necessary to instantiate a `Recommender` from a `DataModel`. `Recommender` is an abstract class that contains all the methods and attributes required to perform a recommendation. CF4J contains several implementations of `Recommender` (see [algorithms list](#algorithm-list)). 

   First, we instantiate the `PMF` recommender. Then, we set the model's hyper-parameters using the `PMF` class constructor: `numFactors = 10`, `gamma = 0.01`, `lambda = 0.1` and `numIters = 100`. We also fix the random `seed = 42` to ensure the reproducibility of this experiment. Once the model is instantiated, we train the model parameters using `fit()` method.
   
   ```Java
   PMF pmf = new PMF(datamodel, 10, 100, 0.1, 0.01, 42);
   pmf.fit();
   ```
   
   We can now repeat this process for the `NMF` recommender. 
   
   ```Java
   NMF nmf = new NMF(datamodel, 10, 100, 43);
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
  
5. The program will print the following output.

   ```
   MSE (PMF): 0.8166798255450183
   MSE (NMF): 0.9438406097978671
   ```
   
   You can find the full code of this example in [GettingStartedExample](src/main/java/es/upm/etsisi/cf4j/examples/GettingStartedExample.java).

## Project Structure

The following image shows the class diagram of the whole project. You can see a high resolution version of this diagram [here](http://rs.etsisi.upm.es/cf4j-2.0/images/class-diagram.png). The project has been divided into 4 main packages: `data`, `recommender`, `qualityMeasure` and `util`.

![CF4J class diagram](http://rs.etsisi.upm.es/cf4j-2.0/images/class-diagram.png)

### `es.upm.etsisi.cf4j.data` package

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

### `es.upm.etsisi.cf4j.recommender` package

This package contains several implementations of collaborative filtering algorithms. You can check the full list in the [Algorithm List](##algorithm-list) section. Each collaborative filtering algorithm included in CF4J must extends the `Recommender` abstract class. This class forces to implement the following abstract methods:

- `fit()`: used to estimate collaborative filtering recommender parameters given the hyper-parameters usually defined in the class constructor. To speed up the fitting process, most of the computations has been parallelized using [`Parallelizer`](link-to-javadoc) util.

- `predict(userIndex, itemIndex)`: used to estimate the rating prediction of the user with index `userIndex` to the item with index `itemIndex`.

Each `Recommender` must be created from a `DataModel` instance and will be fitted to it.

### `es.upm.etsisi.cf4j.qualityMeasure` package

This package contains the implementation of different quality measures for collaborative filtering based recommender systems. These quality measures are used to evaluate the performance of a `Recommender` instance. Included quality measures has been classified into two categories:

- Quality measures for predictions, allocated into `es.upm.etsisi.cf4j.qualityMeasures.prediction` package.
- Quality measures for recommendations, allocated into `es.upm.etsisi.cf4j.qualityMeasures.recommendation` package.

Each quality measure included in CF4J extends `QualityMeasures` abstract class. This class simplifies the computation of a quality measure from the test ratings. It contains the `getScore()` method that computes the score of the quality measure for each test user and returns the averaged score. The computation of the quality measure score for each test user is performed in parallel.

### `es.upm.etsisi.cf4j.util` package

This package contains different utilities to be used with the library. Read the [javadoc]() documentation for additional information.

## Customize CF4J

CF4J has been designed for the collaborative filtering's research community, so its extendability has been one of the main requirements of this project. As described above, an execution with CF4J includes the following steps:

1. Load a dataset using an implementation of the `DataSet` class.
2. Create a new `DataModel` from the loaded `DataSet`.
3. Fit a `Recommender` to the `DataSet`.
4. Evaluate the performance of a `Recommender` using a `QualityMeasure`.

Therefore, if you want to customize CF4J, you should modify any of these main classes: `DataSet`, `DataModel`, `Recommender` and `QualityMeasure`.

`DataSet` is an interface that contains two methods to iterate over training ratings (`getRatingsIterator()`) and test ratings (`getTestRatingsIterator()`). The iteration is carried out over `DataSetEntry` instances, that contains the user, item and value of a rating. Any class that implements this interface may be used to create a `DataModel`.

`DataModel` is a class that should not be modified. It has been encoded to manage the essential information required by most of collaborative filtering algorithms (i.e. users, items and ratings). However, there are several algorithms that includes additional information to the recommendation process such as demographic information about the users or items description. Both `DataModel`, `User` and `Item` includes a `DataBank` instance (see [javadoc](link-to-javadoc)) to store and retrieve any additional information required by a custom `Recommender`.

`Recommender` class can be extended to create your own collaborative filtering algorithm. As mentioned above, to create a new `Recommender` you must define the `fit()` and `predict(userIndex, itemIndex)` methods. In addition, to create a new similarity metric for a kNN based collaborative filtering, you should extend `UserSimilarityMetric` or `ItemSimilarityMetric` for user-to-user or item-to-item approaches of kNN, respectively.

`QualityMeasure` class allows to easily define new quality measures for both predictions and recommendations. This class includes an abstract method, `getScore(TestUser testUser, double[] predictions)`, that must be implemented to compute the score of a `testUser` given his/her `predictions`.

## Algorithm List

In this section we include the full list of algorithms implemented in the library.

* Matrix factorization algorithms (`es.upm.etsisi.cf4j.recommender.matrixFactorization` package):

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

  + Traditional similarity metrics inspired by statistics (`es.upm.etsisi.cf4j.recommender.knn.userSimilairtyMetrics` and `es.upm.etsisi.cf4j.recommender.knn.itemSimilairtyMetrics` packages):
    - Pearson Correlation (`Correlation`)
    - Pearson Correlation Constrained (`CorrelationConstrained`)
    - Cosine similarity (`Cosine`)
    - Adjusted Cosine similarity (`AdjustedCosine`)
    - Jaccard index (`Jaccard`)
    - Mean Squared Difference (`MSD`)
    - Spearman Rank (`SpearmanRank`)

  + Similarity metrics created ad-hoc for collaborative filtering algorithm (`es.upm.etsisi.cf4j.recommender.knn.userSimilairtyMetrics` and `es.upm.etsisi.cf4j.recommender.knn.itemSimilairtyMetrics` packages):
  
     | Class           | Publication |
     |-----------------|-------------|
     | `CJMSD`         | Bobadilla, J., Ortega, F., Hernando, A., &amp; Arroyo, A. (2012). *A Balanced Memory-Based Collaborative Filtering Similarity Measure*, International Journal of Intelligent Systems, 27, 939-946. |
     | `JMSD`          | Bobadilla, J., Serradilla, F., &amp; Bernal, J. (2010). *A new collaborative filtering metric that improves the behavior of Recommender Systems*, Knowledge-Based Systems, 23 (6), 520-528. |
     | `PIP`           | Ahn, H. J. (2008). *A new similarity  measure for collaborative filtering to alleviate the new user cold-starting problem*, Information Sciences, 178, 37-51. |
     | `Singularities` | Bobadilla, J., Ortega, F., &amp; Hernando, A. (2012). *A collaborative filtering similarity measure based on singularities*, Information Processing and Management, 48 (2), 204-217. |
 
 
* Quality measures:

  + For prediction (`es.etsisi.upm.cf4j.qualityMeasure.prediction` package):
    - Coverage (`Coverage`)
    - Mean Absolute Error (`MAE`)
    - Max User Error (`Max`)
    - Mean Squared Error (`MSE`)
    - Mean Squared Logarithmic Error (`MSLE`)
    - Percentage of prefect predictions (`Perfect`)
    - Coefficient of determination R2 (`R2`)
    - Root Mean Squared Error (`RMSE`)
    
  + For recommendation (`es.etsisi.upm.cf4j.qualityMeasure.recommendation` package):
    - Precision (`Precision`)
    - Recall (`Recall`)
    - F1 (`F1`)
    - Normalized Discounted Cumulative Gain (`NDCG`)

## Datasets

You can find awesome datasets to use with CF4J at this site: [http://shuaizhang.tech/2017/03/15/Datasets-For-Recommender-System/](http://shuaizhang.tech/2017/03/15/Datasets-For-Recommender-System/).
