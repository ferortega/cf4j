# CF4J: Collaborative Filtering for Java

A Java's Collaborative Filtering library to carry out experiments in research of Collaborative Filtering based Recommender Systems. The library has been designed from researchers to researchers.

## Index

1. [Installation](#installation)
2. [Getting Started](#getting-started)
3. [Project Structure](#proyect-structure)
4. [Main Classes](#main-classes)
5. [Customize CF4J](#customize-cf4j)
6. [Algorithm List](#algorithm-list)
7. [Datasets](#datasets)

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
   
3. Once all the data are loaded, we are going to build and train our first recommendation models. To create a recommendation model it is necessary to instantiate a `Recommender` from a `DataModel`. `Recommender` is an abstract class that contains all the methods and attributes required to perform a recommendation. CF4J contains several implementations of `Recommender` (see [algorithms list](#algorithm-list)). 

   First, we instantiate the `Pmf` recommender. We set the model's hyper-parameters using the `Pmf` class constructor: `numFactors` = 10, `gamma = 0.01`, `lambda = 0.1` and `numIters = 100`. We also fix the random seed to ensure the reproducibility of this experiment. Once the model is instantiate, we train the model parameters using `fit()` method.
   
   ```Java
   Pmf pmf = new Pmf(datamodel, 10, 100, 0.1, 0.01, 43);
   pmf.fit();
   ```
   
   We can now repeat this process for the `Nmf` recommender. 
   
   ```Java
   Nmf nmf = new Nmf(datamodel, 10, 100, 43);
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
   
   You can find the full code of this example into `es.upm.etsisi.cf4j.examples.GettingStartedExamen` class:

## Project Structure


## Main classes


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
   | BNMF     | `es.upm.etsisi.cf4j.recommender.matrixFactorization.Bnmf`        | Hernando, A., Bobadilla, J., &amp; Ortega, F. (2016). A non negative matrix factorization for collaborative filtering recommender systems on a Bayesian probabilistic model. Knowledge-Based Systems, 97, 188-202 |
   | CLiMF    | `es.upm.etsisi.cf4j.recommender.matrixFactorization.CLiMF`       | Shi, Y., Karatzoglou, A., Baltrunas, L., Larson, M., Oliver, N., &amp; Hanjalic, A. (2012, September). CLiMF: learning to maximize reciprocal rank with collaborative less-is-more filtering. In Proceedings of the sixth ACM conference on Recommender systems (pp. 139-146) |
   | HPF      | `es.upm.etsisi.cf4j.recommender.matrixFactorization.Hpf`         | Gopalan, P., Hofman, J. M., &amp; Blei, D. M. (2015, July). Scalable Recommendation with Hierarchical Poisson Factorization. In UAI (pp. 326-335) |
   | NMF      | `es.upm.etsisi.cf4j.recommender.matrixFactorization.Nmf`         | Lee, D. D., &amp;  Seung, H. S. (2001). Algorithms for non-negative matrix factorization. In Advances * in neural information processing systems (pp. 556-562) |
   | PMF      | `es.upm.etsisi.cf4j.recommender.matrixFactorization.Pmf`         | Mnih, A., &amp; Salakhutdinov, R. R. (2008). Probabilistic matrix factorization. In Advances in neural information processing systems (pp. 1257-1264) |
   | Svd++    | `es.upm.etsisi.cf4j.recommender.matrixFactorization.SvdPlusPlus` | Koren, Y. (2008, August). Factorization meets the neighborhood: a multifaceted collaborative filtering model. In Proceedings of the 14th ACM SIGKDD international conference on Knowledge discovery and data mining (pp. 426-434) |
   | URP      | `es.upm.etsisi.cf4j.recommender.matrixFactorization.Urp`         | Marlin, B. M. (2004). Modeling user rating profiles for collaborative filtering. In Advances in neural information processing systems (pp. 627-634) |

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
