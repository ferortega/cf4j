# CF4J: Collaborative Filtering for Java

[![Build Status](https://travis-ci.org/ferortega/cf4j.svg?branch=master)](https://travis-ci.org/ferortega/cf4j)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/es.upm.etsisi/cf4j/badge.svg)](https://maven-badges.herokuapp.com/maven-central/es.upm.etsisi/cf4j)
[![javadoc](https://javadoc.io/badge2/es.upm.etsisi/cf4j/javadoc.svg)](https://javadoc.io/doc/es.upm.etsisi/cf4j)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/ferortega/cf4j.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/ferortega/cf4j/context:java)

A Java's Collaborative Filtering library to carry out experiments in research on Collaborative Filtering based Recommender Systems. The library has been designed from researchers to researchers.

## Cite us

If you enjoy cf4j, please cite us:

> Ortega, F., Mayor, J., López-Fernández, D., & Lara-Cabrera, R. (2021). **CF4J 2.0: Adapting Collaborative Filtering for Java to new challenges of collaborative filtering based recommender systems**. *Knowledge-Based Systems*, 215, 106629.

```
@article{ortega2021cf4j,
  title={CF4J 2.0: Adapting Collaborative Filtering for Java to new challenges of collaborative filtering based recommender systems},
  author={Ortega, Fernando and Mayor, Jes{\'u}s and L{\'o}pez-Fern{\'a}ndez, Daniel and Lara-Cabrera, Ra{\'u}l},
  journal={Knowledge-Based Systems},
  volume={215},
  pages={106629},
  year={2021},
  publisher={Elsevier}
}
```

> Ortega, F., Zhu, B., Bobadilla, J., & Hernando, A. (2018). **CF4J: Collaborative filtering for Java**. *Knowledge-Based Systems*, 152, 94-99.

```
@article{ortega2018cf4j,
  title={CF4J: Collaborative filtering for Java},
  author={Ortega, Fernando and Zhu, Bo and Bobadilla, Jes{\'u}s and Hernando, Antonio},
  journal={Knowledge-Based Systems},
  volume={152},
  pages={94--99},
  year={2018},
  publisher={Elsevier}
}
```

## Index

1. [Installation](#installation)
2. [Getting Started](#getting-started)
3. [Project Structure](#project-structure)
4. [Customize CF4J](#customize-cf4j)
5. [Algorithm List](#algorithm-list)
6. [Examples](#examples)
7. [Datasets](#datasets)

## Installation

CF4J is available in the most popular dependency management tools for Java. To add it to your project, you must add the following lines to your dependency management.

For Maven:

```xml
<dependency>
  <groupId>es.upm.etsisi</groupId>
  <artifactId>cf4j</artifactId>
  <version>2.3.0</version>
</dependency>
```

For Gradle:

```
compile group: 'es.upm.etsisi', name: 'cf4j', version: '2.3.0'
```

For SBT:

```
libraryDependencies += "es.upm.etsisi" % "cf4j" % "2.3.0"
```

For Ivy:

```xml
<dependency org="es.upm.etsisi" name="cf4j" rev="2.3.0"/>
```

For Grape:

```
@Grapes(
    @Grab(group='es.upm.etsisi', module='cf4j', version='2.3.0')
)
```

For Leiningen:

```
[es.upm.etsisi/cf4j "2.3.0"]
```

For Buildr:

```
'es.upm.etsisi:cf4j:jar:2.3.0'
```

You can find additional information about these dependencies in [https://mvnrepository.com/artifact/es.upm.etsisi/cf4j](https://mvnrepository.com/artifact/es.upm.etsisi/cf4j)

If you prefer to use the library without a dependency management tool, you must add the `jar` packaged version of CF4J to your project's classpath. For example, if you are using IntelliJ IDEA, copy the file to your project's directory, make right click on the `jar` file and select `Add as Library`.

You can find the `jar` packaged version of CF4J into the release section of github.

You can also package your own `jar` file . To do that, clone the repository using `git clone git@github.com:ferortega/cf4j.git` and package it with `mvn package`.

## Getting Started

Let's encode our first experiment with CF4J. 

1. First of all, we need to load MovieLens's ratings. CF4J includes a preloaded version of most popular ratings databases. You can retrieve them using [`BenchmarkDataModels`](http://cf4j.etsisi.upm.es/apidocs/latest/es/upm/etsisi/cf4j/data/BenchmarkDataModels.html) class. In this experiment we will load [MovieLens 100k dataset](https://grouplens.org/datasets/movielens/100k/).

   ```java
   DataModel datamodel = BenchmarkDataModels.MovieLens100K();
   ```  
   
   As you can observe, MovieLens dataset has been loaded into a `DataModel`. A `DataModel` is a high level in memory representation of the data structure required by collaborative filtering algorithms. 
   
2. Now, we need to create an object store the results of our experiment. CF4J includes some amazing tools to analyze the experimental results. You can find them in the [`es.upm.etsisi.plot`](http://cf4j.etsisi.upm.es/apidocs/latest/es/upm/etsisi/cf4j/plot/package-summary.html) package. In this case, we want to analyze how the Mean Squared Error (MSE) varies according to the value of the regularization term in Probabilistic Matrix Factorization ([`PMF`](http://cf4j.etsisi.upm.es/apidocs/latest/es/upm/etsisi/cf4j/recommender/matrixFactorization/PMF.html)) recommender, so we will use a [`LinePlot`](http://cf4j.etsisi.upm.es/apidocs/latest/es/upm/etsisi/cf4j/plot/LinePlot.html)].

   ```java
   double[] regValues = {0.000, 0.025, 0.05, 0.075, 0.100, 0.125, 0.150, 0.175, 0.200, 0.225, 0.250};
   LinePlot plot = new LinePlot(regValues, "regularization", "MSE");
   ```
   
3. At this point everything is ready to perform the experiment. We add a new empty series to the plot:

   ```java
   plot.addSeries("PMF");
   ```
   
   And we iterate over the different regularization values fitting a new instance of ([`PMF`](http://cf4j.etsisi.upm.es/apidocs/latest/es/upm/etsisi/cf4j/recommender/matrixFactorization/PMF.html)) recommender for each of them, computing the [`MSE`](http://cf4j.etsisi.upm.es/apidocs/latest/es/upm/etsisi/cf4j/qualityMeasure/prediction/MSE.html) of the fitted recommender predictions and adding the MSE score to the plot data. Note that the remaining model's hyper-parameters has been fixed for this experiment (`numFactors=6`, `numIters=50`, `gamma=0.01` and `seed=43`):
   
   ```java
   for (double reg : regValues) {
     PMF pmf = new PMF(datamodel, 6, 50, reg, 0.01, 43);
     pmf.fit();

     QualityMeasure mse = new MSE(pmf);
     double mseScore = mse.getScore();

     plot.setValue("PMF", reg, mseScore);
   }
   ``` 
   
4. Finally, we visualize the experimental results.

   To draw the plot we use:
  
   ```java
   plot.draw();
   ```

   And we obtain the following chart:
   
   ![PMF regularization term experiment](http://cf4j.etsisi.upm.es/github-resources/pmf-regularization-getting-started.png)
   
   To print the plot data in the standard output console we use:
   
   ```java
   plot.printData("0.000");
   ```
   
   And we obtain the following output:
   
   ```
   +----------------+-------+
   | regularization | PMF   |
   +----------------+-------+
   |          0.000 | 1.150 |
   +----------------+-------+
   |          0.025 | 1.070 |
   +----------------+-------+
   |          0.050 | 1.021 |
   +----------------+-------+
   |          0.075 | 0.990 |
   +----------------+-------+
   |          0.100 | 0.972 |
   +----------------+-------+
   |          0.125 | 0.966 |
   +----------------+-------+
   |          0.150 | 0.969 |
   +----------------+-------+
   |          0.175 | 0.979 |
   +----------------+-------+
   |          0.200 | 0.993 |
   +----------------+-------+
   |          0.225 | 1.009 |
   +----------------+-------+
   |          0.250 | 1.027 |
   +----------------+-------+
   ```
   
 You can find the full code of this example in [GettingStartedExample](src/main/java/es/upm/etsisi/cf4j/examples/GettingStartedExample.java).

## Project Structure

The following image shows the  class diagram of the whole project.  The project has been divided into four main packages: `data`, `recommender`, `qualityMeasure` and `util`.

![CF4J class diagram](http://cf4j.etsisi.upm.es/github-resources/cf4j-class-diagram.png)

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

- `BenchmarkDataModels`. This class contains preloaded DataModel instances with the most popular datasets used in collaborative filtering research. See [Datasets](#datasets) section for more details.

### `es.upm.etsisi.cf4j.recommender` package

This package contains several implementations of collaborative filtering algorithms. You can check the full list in the [Algorithm List](#algorithm-list) section. Each collaborative filtering algorithm included in CF4J must extends the `Recommender` abstract class. This class forces to implement the following abstract methods:

- `fit()`: used to estimate collaborative filtering recommender parameters given the hyper-parameters usually defined in the class constructor. To speed up the fitting process, most of the computations has been parallelized using [`Parallelizer`](http://rs.etsisi.upm.es/cf4j-2.0.1/apidocs/es/upm/etsisi/cf4j/util/Parallelizer.html) util.

- `predict(userIndex, itemIndex)`: used to estimate the rating prediction of the user with index `userIndex` to the item with index `itemIndex`.

Each `Recommender` must be created from a `DataModel` instance and will be fitted to it.

### `es.upm.etsisi.cf4j.qualityMeasure` package

This package contains the implementation of different quality measures for collaborative filtering based recommender systems. These quality measures are used to evaluate the performance of a `Recommender` instance. Included quality measures has been classified into two categories:

- Quality measures for predictions, allocated into `es.upm.etsisi.cf4j.qualityMeasures.prediction` package.
- Quality measures for recommendations, allocated into `es.upm.etsisi.cf4j.qualityMeasures.recommendation` package.

Each quality measure included in CF4J extends `QualityMeasure` abstract class. This class simplifies the computation of a quality measure from the test ratings. It contains the `getScore()` method that computes the score of the quality measure for each test user and returns the averaged score. The computation of the quality measure score for each test user is performed in parallel.

### `es.upm.etsisi.cf4j.util` package

This package contains different utilities designed to ease common operations used in collaborative filtering research. This package includes the following sub-packages:

- `es.upm.etsisi.cf4j.util.plot` includes plotting tools designed to analyze data of results obtained as consequence of collaborative filtering research. The following plot types:
 
  - `LinePlot`. Displays multiple data series with common numerical values on the x axis. Example:
  
    ![LinePlot example](http://cf4j.etsisi.upm.es/github-resources/line-plot.png)
    
  - `XYPlot`.Displays multiple data series defined by a sequence of XY points. All the points in a series must be assigned to a common plot's label. Example:
  
    ![XYPlot example](http://cf4j.etsisi.upm.es/github-resources/xy-plot.png)
    
  - `ScatterPlot`. Displays the values of two numerical variables. Example:
  
    ![ScatterPlot example](http://cf4j.etsisi.upm.es/github-resources/scatter-plot.png)
   
  - `HistogramPlot`. Displays the histogram of a numerical variable by defining the number of bins. Example:
  
    ![HistogramPlot example](http://cf4j.etsisi.upm.es/github-resources/histogram-plot.png)
   
  - `ColumnPlot`. Displays numerical values related with a discrete variable placed on the x axis. Example:
  
    ![ColumnPlot example](http://cf4j.etsisi.upm.es/github-resources/column-plot.png) 
    
- `es.upm.etsisi.cf4j.util.optimization`  includes optimization utils designed to tune recommenders' hyper-parameters.

- `es.upm.etsisi.cf4j.util.process` includes processing utils designed to simplify the parallelization of collaborative filtering algorithms.

Read the [javadoc](http://cf4j.etsisi.upm.es/apidocs/latest/) documentation for additional information.

## Customize CF4J

CF4J has been designed for the collaborative filtering's research community, so its extendability has been one of the main requirements of this project. As described above, an execution with CF4J includes the following steps:

1. Load a dataset using an implementation of the `DataSet` class.
2. Create a new `DataModel` from the loaded `DataSet`.
3. Fit a `Recommender` to the `DataSet`.
4. Evaluate the performance of a `Recommender` using a `QualityMeasure`.

Therefore, if you want to customize CF4J, you must work with `DataSet`, `DataModel`, `Recommender` and `QualityMeasure` classes:

`DataSet` is an interface that contains two methods to iterate over training ratings (`getRatingsIterator()`) and test ratings (`getTestRatingsIterator()`). The iteration is carried out over `DataSetEntry` instances, that contains the user, item and value of a rating. Any class that implements this interface may be used to create a `DataModel`.

`DataModel` is a class that should not be modified. It has been encoded to manage the essential information required by most of collaborative filtering algorithms (i.e. users, items and ratings). However, there are several algorithms that includes additional information to the recommendation process such as demographic information about the users or items description. Both `DataModel`, `User` and `Item` includes a `DataBank` instance (see [javadoc](http://rs.etsisi.upm.es/cf4j-2.0.1/apidocs/es/upm/etsisi/cf4j/data/DataBank.html)) to store and retrieve any additional information required by a custom `Recommender`.

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
   | `BeMF`        | Ortega, F., Lara-Cabrera, R., González-Prieto, Á., &amp; Bobadilla, J. (2021). *Providing reliability in recommender systems through Bernoulli matrix factorization*. Information Sciences, 553, 110-128. |
   | `DeepMF`      | Lara-Cabrera, R., González-Prieto, Á., &amp; Ortega, F. (2020). *Deep matrix factorization approach for collaborative filtering recommender systems*. Applied Sciences, 10(14), 4926. |
   | `DirMF`       | Lara-Cabrera, R., González, Á., Ortega, F., & González-Prieto, Á. (2022). *Dirichlet Matrix Factorization: A Reliable Classification-Based Recommender System*. Applied Sciences, 12(3), 1223. |

* Collaborative filtering based on neural networks (`es.upm.etisi.recommender.neural` package):

   | Class   | Publication                                                                                                                                                                                                                                             |
   |---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------|
   | `NCCF`  | Bobadilla, J., Ortega, F., Gutiérrez, A., & Alonso, S. (2020). *Classification-based Deep Neural Network Architecture for Collaborative Filtering Recommender Systems*. International Journal of Interactive Multimedia & Artificial Intelligence, 6(1) |
   | `GMF`   | He, Xiangnan & Liao, Lizi & Zhang, Hanwang. (2017). *Neural Collaborative Filtering*. Proceedings of the 26th International Conference on World Wide Web.                                                                                               |
   | `MLP`   | He, Xiangnan & Liao, Lizi & Zhang, Hanwang. (2017). *Neural Collaborative Filtering*. Proceedings of the 26th International Conference on World Wide Web.                                                                                               |
   | `NeuMF` | He, Xiangnan & Liao, Lizi & Zhang, Hanwang. (2017). *Neural Collaborative Filtering*. Proceedings of the 26th International Conference on World Wide Web.                                                                                               |

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

  + For prediction (`es.upm.etsisi.cf4j.qualityMeasure.prediction` package):
    - Coverage (`Coverage`)
    - Mean Absolute Error (`MAE`)
    - Max User Error (`Max`)
    - Mean Squared Error (`MSE`)
    - Mean Squared Logarithmic Error (`MSLE`)
    - Percentage of prefect predictions (`Perfect`)
    - Coefficient of determination R2 (`R2`)
    - Root Mean Squared Error (`RMSE`)
    
  + For recommendation (`es.upm.etsisi.cf4j.qualityMeasure.recommendation` package):
    - Precision (`Precision`)
    - Recall (`Recall`)
    - F1 (`F1`)
    - Normalized Discounted Cumulative Gain (`NDCG`)
    - Novelty (`Novelty`)
    - Discovery (`Discovery`)
    - Diversity (`Diversity`)

## Examples

In `src/main/java/es/upm/etsisi/cf4j/examples` you can find the following examples that shows the main features of CF4J.

In `examples/recommender` you will find examples showing how to compare different `Recommender` instances:

- [`MatrixFactorizationComparison`](src/main/java/es/upm/etsisi/cf4j/examples/recommender/MatrixFactorizationComparison.java) compares the RMSE score for different matrix factorization models varying the number of latent factors.
- [`UserKnnComparison`](src/main/java/es/upm/etsisi/cf4j/examples/recommender/UserKnnComparison.java) compares the MAE, Coverage, Precision and Recall quality measures scores for different similarity metrics applied to user-to-user knn based collaborative filtering. Each similarity metric is tested with different number of neighbors.
- [`ItemKnnComparison`](src/main/java/es/upm/etsisi/cf4j/examples/recommender/ItemKnnComparison.java) compares the MSLE and nDCG quality measures scores for different similarity metrics applied to item-to-item knn based collaborative filtering. Each similarity metric is tested with different number of neighbors.

In `examples/plot` you will find examples examples showing how to plot with CF4J:

- [`ColumnPlotExample`](src/main/java/es/upm/etsisi/cf4j/examples/plot/ColumnPlotExample.java) analyzes the rating value distribution of MovieLens 1M dataset using a ColumnPlot.
- [`HistogramPlotExample`](src/main/java/es/upm/etsisi/cf4j/examples/plot/HistogramPlotExample.java) analyzes the average rating of each item that belongs to MovieLens 1M dataset. It shows the results using a HistogramPlot.
- [`LinePlotExample`](src/main/java/es/upm/etsisi/cf4j/examples/plot/LinePlotExample.java) compares the F1 score of the recommendations performed by PMF and NMF recommenders. Results are included in a LinePlot that contains the number of recommendations performed in the x axis.
- [`ScatterPlotExample`](src/main/java/es/upm/etsisi/cf4j/examples/plot/ScatterPlotExample.java) builds an ScatterPlot comparing the number of ratings of each test user with his/her averaged prediction error using BiasedMF as recommender.
- [`XYPlotExample`](src/main/java/es/upm/etsisi/cf4j/examples/plot/XYPlotExample.java) compares the Precision score (y axis) and the Recall score (x axis) for PMF and NMF recommenders using an XYPlot.

In `examples/gridSearch` you will find examples showing how to use GridSearch tool:

- [`BiasedMFGridSearch`](src/main/java/es/upm/etsisi/cf4j/examples/gridSearch/BiasedMFGridSearch.java) tunes the hyper-parameters of BiasedMF recommender using the GridSearch tool. Top 5 results with lowest Mean Absolute Error (MAE) are printed.
- [`UserKNNGridSearch`](src/main/java/es/upm/etsisi/cf4j/examples/gridSearch/UserKNNGridSearch.java) tunes the parameters of UserKNN recommender using the GridSearch tool. Top 5 results with highest Precision score are printed.
- [`PMFRandomSearchCV`](src/main/java/es/upm/etsisi/cf4j/examples/gridSearch/PMFRandomSearchCV.java) tunes the parameters of PMF recommender using the RandomSearchCV tool. Top 10 results with lowest Mean Squared Error (MSE) are printed.


## Datasets

CF4J includes the most popular datasets used in collaborative filtering research. These datasets have been preloaded into DataModel instances and can be retrieved using [`BenchmarkDataModels`](http://cf4j.etsisi.upm.es/apidocs/latest/es/upm/etsisi/cf4j/data/BenchmarkDataModels.html) class.

The datasets included in CF4J are:

| Dataset       | Number of users | Number of items | Number of ratings | Number of test ratings | Rating scale |
|---------------|-----------------|-----------------|-------------------|------------------------|--------------|
| MovieLens100K | 943             | 1,682           | 92,026            | 7,974                  | 1 to 5       |
| MovieLens1M   | 6,040           | 3,706           | 911,031           | 89,178                 | 1 to 5       |
| MovieLens10M  | 69,878          | 10,677          | 9,104,681         | 895,373                | 0.5 to 5.0   |
| FilmTrust     | 1,508           | 2,071           | 32,675            | 2,819                  | 0.5 to 4.0   |
| BookCrossing  | 77,805          | 185,973         | 390,351           | 43,320                 | 1 to 10      |
| LibimSeTi     | 135,359         | 168,791         | 15,846,347        | 1,512,999              | 1 to 10      |
| MyAnimeList   | 69,600          | 9,927           | 5,788,207         | 549,027                | 1 to 10      |
| Jester        | 54,905          | 140             | 1,662,713         | 179,657                | -10 to 10    |
| Netflix Prize | 480,189         | 17,770          | 99,945,049        | 535,458                | 1 to 5       |
| BoardGameGeek | 411,375         | 21,925          | 18,273,394        | 63,6134                | 1 to 10      |

**ALERT**: due to security changes on the server hosting the [`BenchmarkDataModels`](http://cf4j.etsisi.upm.es/apidocs/latest/es/upm/etsisi/cf4j/data/BenchmarkDataModels.html), these will no longer be available for versions lower than `2.3.0`. If you need to continue using the [`BenchmarkDataModels`](http://cf4j.etsisi.upm.es/apidocs/latest/es/upm/etsisi/cf4j/data/BenchmarkDataModels.html), please upgrade to version `2.3.0` or higher.
