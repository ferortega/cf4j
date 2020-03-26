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

To use the library in other type of project, you must add the `jar` packetized version of CF4J to your project's classpath. For example, if you are using IntelliJ IDEA, copy the file to your project's directory, make right click on the `jar` file and select `Add as Library`.

You can find the `jar` packetized version of CF4J into the release section of github.

You can also packetize your own `jar` file . To do that, clone the repository using `git clone git@github.com:ferortega/cf4j.git` and packetize it with `mvn package`.

## Getting Started

Let's encode our first experiment with CF4J. In this experiment, we will compare the Mean Squared Error (MSE) of two well known matrix factorization models: Probabilistic Matrix Factorization (PMF) and Non-negative Matrix Factorization (Nmf). We will use [MovieLens 1M dataset](https://grouplens.org/datasets/movielens/) as ratings' database.

1. First of all, we are going to load the database from the ratings file using an instance of `DataSet` interface. We choose `RandomSplitDataSet` that automatically splits the ratings set into training ratings and test ratings. We select 20% of users and 20% of items as test users and items respectively. To ensure the reproducibility of the example, we are going to fix the random seed to 43.

    ```Java
    String filename = "ml1m.dat";
    double testUsers = 0.2;
    double testItems = 0.2;
    String separator = "::";
    long seed = 43;
    DataSet ml1m = new RandomSplitDataSet(filename, testUsers, testItems, separator, seed);
	```

2. Now, we are going to create a `DataModel` from the previous `DataSet`. A `DataModel` is a high level in memory representation of the  data structured required by collaborative filtering algorithms. 

    ```Java
   DataModel datamodel = new DataModel(ml1m);
   ```
   
3. Once all the data are loaded, we are going to build and train our first recommendation model. To create a recommendation model it is necessary to instantiate a `Recommender` from a `DataModel`. `Recommender` is an abstract class that contains all the methods and attributes required to perform a recommendation. CF4J contains several implementations of `Recommender` (see [algorithms list](#algorithm-list)). 

   First, we instantiate the `Pmf` recommender. We set the model's hyper-parameters using the `Pmf` class constructor: `numFactors = 10`, `gamma = 0.01`, `lambda = 0.1` and `numIters = 100`. We also fix the random seed to ensure the reproducibility of this experiment. Once the model is instantiated, we train the model parameters using `fit()` method.
   
   ```Java
   PMF pmf = new PMF(datamodel, 10, 100, 0.1, 0.01, 43);
   pmf.fit();
   ```
   
   We can now repeat this process for the `Nmf` recommender. 
   
   ```Java
   NMF nmf = new NMF(datamodel, 10, 100, 43);
   nmf.fit();
   ```
   
4. Finally we are going to compare the quality of both recommendation models using a `QualityMeasure`. This class has been designed to simplify the evaluation of collaborative filtering based recommendation models. In this example, we are going to use the `Mse` quality measure that compares the quadratic difference between the test ratings and the model predictions. To do so, we just need to create a new instance of `Mse` class from the `Recommender` and compute the score using `getScore()` method.

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
   
   You can find the full code of this example into `es.upm.etsisi.cf4j.examples.GettingStartedExample` class:

## Project Structure

The following image shows the class diagram of the whole project. You can see a high resolution version of this diagram [here](http://rs.etsisi.upm.es/cf4j-2.0/images/class-diagram.png). The project has been divided in 4 main packages: `data`, `recommender`, `qualityMeasure` and `util`.

![CF4J class diagram](http://rs.etsisi.upm.es/cf4j-2.0/images/class-diagram.png)

### `es.upm.etsisi.cf4j.data` package

This package contains all the classes necessary to extract, transform, load and manipulate the data used by collaborative filtering algorithms. The most important classes of this package are:

- `DataSet`. This interface is used to iterate over training and test ratings. Two implementations of this interface have been included: `RandomSplitDataSet` that randomly splits the ratings contained in a file into training and test ratings; and `TrainTestFilesDataSet` that loads training and test ratings from two different files. 

- `DataModel`. This class is class manages all the information related with the collaborative filtering based recommender system. A `DataModel` must be instantiated from a `DataSet`. Once the `DataModel` is created, it is composed by: 
  + An array to store (training) `User` instances.
  + An array to store `TestUser` instances.
  + An array to store (training) `Item` instances.
  + An array to store `TestItem` instances.
  
- `User`. This class represents a training user. Each `User` is defined by his/her index in the `User` array of the `DataModel` and an unique identifier. The `User` class contains the list of items rated by the user. These ratings can be retrieved using `getItemAt(pos)`, that returns the item index of the item rated at the `pos` position, and `getRatingAt(pos)`, that returns the rating value of the item rated at the `pos` position. Items' indexes returned by `getItemAt(pos)` are sorted from lower to higher.

- `Item`. This class represents a training item. Each `Item` is defined by its index in the `Item` array of the `DataModel` and an unique identifier. The `Item` class contains the list of users that have rated the item. These ratings can be retrieved using `getUserAt(pos)`, that returns the user index of the user that have rated the item at the `pos` position, and `getRatingAt(pos)`, that returns the rating value at the `pos` position. Users' indexes returned by `getUserAt(pos)` are sorted from lower to higher.

- `TestUser`. This class represents a test user. Every `TestUser` is also a `User` due to the heritage relation between `User` and `TestUser` classes. Each `TestUser` is defined by his/her index in the `TestUser` array of the `DataModel`. The `TestUser` class contains the list of test items rated in test by the test user. These test ratings can be retrieved using `getTestItemAt(pos)`, that returns the test item index of the item rated at the `pos` position, and `getTestRatingAt(pos)`, that returns the test rating value of the test item rated at the `pos` position. Test items' indexes returned by `getTestItemAt(pos)` are sorted from lower to higher.

- `TestItem`. This class represents a test item. Every `TestItem` is also a `Item` due to the heritage relation between `Item` and `TestItem` classes. Each `TestItem` is defined by his/her index in the `TestItem` array of the `DataModel`. The `TestItem` class contains the list of test users that have rated in test the item. These test ratings can be retrieved using `getTestUserAt(pos)`, that returns the the test user index of the test user that have test rated the test item at the `pos` position, and `getTestRatingAt(pos)`, that returns the test rating value at the `pos` position. Test users' indexes returned by `getTestUserAt(pos)` are sorted from lower to higher.

### `es.upm.etsisi.cf4j.recommender` package

This package several implementations of collaborative filtering algorithms (see [Algorithm List](##algorithm-list) section). Each collaborative filtering algorithm included in CF4J must extends the `Recommender` abstract class. This class forces to implement two abstract methods:

- `fit()`: used to estimate collaborative filtering recommender parameters give the parameters usually defined in the class constructor. To speed up the fitting process, most of the computations has been parallelized using [`Parallelizer`](link-to-javadoc) util.

- `predict(userIndex, itemIndex)`: used to estimate the rating prediction of the user with index `userIndex` to the item with index `itemIndex`.

Each `Recommender` must be created from a `DataModel` instance and will be fitted to it.

### `es.upm.etsisi.cf4j.qualityMeasure` package

This package contains the implementation of different quality measures for collaborative filtering based recommender systems.

### `es.upm.etsisi.cf4j.util` package

This package contains different utilities to be used with the library. Read the [javadoc]() documentation for additional information.

## Customize CF4J


## Algorithm List

In this section we include the full list of algorithms implemented in the library and a link to the paper in which it is explained.

* KNN based CF (both user-to-user and item-to-item approaches):

  + Similarity metrics:
    - Pearson Correlation ([link](https://www.sciencedirect.com/science/article/pii/S0950705113001044))
    - Pearson Correlation Constrained ([link](https://www.sciencedirect.com/science/article/pii/S0950705113001044))
    - Cosine ([link](https://www.sciencedirect.com/science/article/pii/S0950705113001044))
    - Adjusted Cosine ([link](https://www.sciencedirect.com/science/article/pii/S0950705113001044))
    - Jaccard Index ([link](https://www.sciencedirect.com/science/article/pii/S0950705113001044))
    - MSD ([link](https://www.sciencedirect.com/science/article/pii/S0950705113001044))
    - Spearman Rank ([link](https://www.sciencedirect.com/science/article/pii/S0950705113001044))
    - JSMD ([link](https://www.sciencedirect.com/science/article/pii/S0950705110000444))
    - CJMSD ([link](http://onlinelibrary.wiley.com/doi/10.1002/int.21556/full))
    - Singularities ([link](https://www.sciencedirect.com/science/article/pii/S0306457311000409))
    - PIP ([link](https://www.sciencedirect.com/science/article/pii/S0020025507003751))

  + Aggregation approaches:
    - Mean ([link](http://ieeexplore.ieee.org/abstract/document/1423975/))
    - WeightedMean ([link](http://ieeexplore.ieee.org/abstract/document/1423975/))
    - Deviation from Mean ([link](http://ieeexplore.ieee.org/abstract/document/1423975/))

* Matrix factorization:

   | Acronym  | Class                                                            | Publication |
   |----------|------------------------------------------------------------------|-------------|
   | BiasedMF | `es.upm.etsisi.cf4j.recommender.matrixFactorization.BiasedMF`    | Koren, Y., Bell, R., &amp; Volinsky, C. (2009). Matrix factorization techniques for recommender systems. Computer, (8), 30-37 |
   | BNMF     | `es.upm.etsisi.cf4j.recommender.matrixFactorization.BNMF`        | Hernando, A., Bobadilla, J., &amp; Ortega, F. (2016). A non negative matrix factorization for collaborative filtering recommender systems on a Bayesian probabilistic model. Knowledge-Based Systems, 97, 188-202 |
   | CLiMF    | `es.upm.etsisi.cf4j.recommender.matrixFactorization.CLiMF`       | Shi, Y., Karatzoglou, A., Baltrunas, L., Larson, M., Oliver, N., &amp; Hanjalic, A. (2012, September). CLiMF: learning to maximize reciprocal rank with collaborative less-is-more filtering. In Proceedings of the sixth ACM conference on Recommender systems (pp. 139-146) |
   | HPF      | `es.upm.etsisi.cf4j.recommender.matrixFactorization.HPF`         | Gopalan, P., Hofman, J. M., &amp; Blei, D. M. (2015, July). Scalable Recommendation with Hierarchical Poisson Factorization. In UAI (pp. 326-335) |
   | NMF      | `es.upm.etsisi.cf4j.recommender.matrixFactorization.NMF`         | Lee, D. D., &amp;  Seung, H. S. (2001). Algorithms for non-negative matrix factorization. In Advances * in neural information processing systems (pp. 556-562) |
   | PMF      | `es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF`         | Mnih, A., &amp; Salakhutdinov, R. R. (2008). Probabilistic matrix factorization. In Advances in neural information processing systems (pp. 1257-1264) |
   | Svd++    | `es.upm.etsisi.cf4j.recommender.matrixFactorization.SVDPlusPlus` | Koren, Y. (2008, August). Factorization meets the neighborhood: a multifaceted collaborative filtering model. In Proceedings of the 14th ACM SIGKDD international conference on Knowledge discovery and data mining (pp. 426-434) |
   | URP      | `es.upm.etsisi.cf4j.recommender.matrixFactorization.URP`         | Marlin, B. M. (2004). Modeling user rating profiles for collaborative filtering. In Advances in neural information processing systems (pp. 627-634) |

* Quality measures:

  + For prediction:
    - Mean Absolute Error (MAE)
    - Mean Squared Error (MSE)
    - Max User Error (Max)
    - Coverage
    
  + For recommendation:
    - Precision
    - Recall
    - F1
    - Normalized Discounted Cumulative Gain (nDCG)

## Datasets

You can find awesome datasets to use with CF4J at this site: [http://shuaizhang.tech/2017/03/15/Datasets-For-Recommender-System/](http://shuaizhang.tech/2017/03/15/Datasets-For-Recommender-System/).
