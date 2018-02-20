# CF4J: Collaborative Filtering for Java

A Java's Collaborative Filtering library to carry out experiments in research of Collaborative Filtering based Recommender Systems. The library has been designed from researchers to researchers.

## Index

1. [Installation](#installation)
2. [Getting Started](#getting-started)
3. [Package Structure](#package-structure)
4. [Main Classes](#main-classes)
5. [Customize CF4J](#customize-cf4j)
6. [Examples](#examples)
7. [Algorithm List](#algorithm-list)
8. [Datasets](#datasets)

## Installation

### For Maven

Add the following lines to the dependencies section in the `pom.xml` of the Maven's project.

```xml
<dependency>
    <groupId>com.github.ferortega</groupId>
    <artifactId>cf4j-recsys</artifactId>
    <version>1.1.0</version>
</dependency>
```

### For Gradle

```
compile group: 'com.github.ferortega', name: 'cf4j-recsys', version: '1.0.0'
```

### For SBT

```
libraryDependencies += "com.github.ferortega" % "cf4j-recsys" % "1.0.0"
```

### For other types of project

To use the library in other type of project, you must add the `jar` packetized version of CF4J to your project's classpath. For example, if you are using Eclipse, copy the file to your project directory, make right click on the file and select `Build Path > Add to Build Path`.

You can find the `jar` packetized version of CF4J into the release section of github.

You can also packetize your own `jar` file . To do that, clone the repository using `git clone git@github.com:ferortega/cf4j.git` and packetize it with `mvn package`. You must have [`git-lfs`](https://git-lfs.github.com/) installed if you want to download test datasets.

## Getting Started

Let's encode our first experiment with CF4J. In this experiment, we will compare the MAE of a knn CF using both Pearson Correlation and JMSD as similarity metrics. We will use [MovieLens 1M dataset](https://grouplens.org/datasets/movielens/) as ratings' database.

1. First of all, we will load the database from the ratings file using the `open()` method of the `Kernel` class. We will set the percentage of test users and test items to the 20%.

	```Java
	String dbName = "datasets/movielens/ratings.dat";
	double testUsers = 0.20; // 20% of test users
	double testItems = 0.20; // 20% of test items

	Kernel.getInstance().open(dbName, testUsers, testItems, "::");
	```

2. Now, we will build a loop to test both similarity metrics. Each similarity metric will compute the similarity of the `TestUser` which any existing `User`. We will use the `Processor` class to speed-up the execution of these similarity metrics.

	```Java
	String dbName = "datasets/movielens/ratings.dat";
	double testUsers = 0.20; // 20% of test users
	double testItems = 0.20; // 20% of test items

	Kernel.getInstance().open(dbName, testUsers, testItems, "::");

	String [] similarityMetrics = {"COR", "JMSD"};

	for (String sm : similarityMetrics) {

		// Compute similarity
		if (sm.equals("COR")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricCorrelation()); 			
		}
		else if (sm.equals("JMSD")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricJMSD());
		}
	}
	```

3. Once the similarities are computed we must find the k nearest neighbors of each `TestUser`. We want to test our algorithm for different values of k, so we must iterate over them.

	```Java
	String dbName = "datasets/movielens/ratings.dat";
	double testUsers = 0.20; // 20% of test users
	double testItems = 0.20; // 20% of test items

	Kernel.getInstance().open(dbName, testUsers, testItems, "::");

	String [] similarityMetrics = {"COR", "JMSD"};
	int [] numberOfNeighbors = {50, 100, 150, 200, 250, 300, 350, 400};

	for (String sm : similarityMetrics) {

		// Compute similarity
		if (sm.equals("COR")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricCorrelation()); 			
		}
		else if (sm.equals("JMSD")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricJMSD());
		}

		// For each value of k
		for (int k : numberOfNeighbors) {

			// Find the neighbors
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.neighbors.Neighbors(k));
		}
	}
	```

4. With the neighbors set computed, we are able to compute predictions using an aggregation approach. Let's use `DeviationFromMean`.

	```Java
	String dbName = "datasets/movielens/ratings.dat";
	double testUsers = 0.20; // 20% of test users
	double testItems = 0.20; // 20% of test items

	Kernel.getInstance().open(dbName, testUsers, testItems, "::");

	String [] similarityMetrics = {"COR", "JMSD"};
	int [] numberOfNeighbors = {50, 100, 150, 200, 250, 300, 350, 400};

	for (String sm : similarityMetrics) {

		// Compute similarity
		if (sm.equals("COR")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricCorrelation()); 			
		}
		else if (sm.equals("JMSD")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricJMSD());
		}

		// For each value of k
		for (int k : numberOfNeighbors) {

			// Find the neighbors
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.neighbors.Neighbors(k));

			// Compute predictions using DFM
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.aggregationApproaches.DeviationFromMean());
		}
	}
	```

5. Now, we want to check the quality of the predictions using `MAE` measure. To store the results, we use an instance of `PrintableQualityMeasure`. This class is useful to store the results of a quality measure for different data series (the similarity metrics that we are testing) and one parameter (the number of neighbors).

	```Java
	String dbName = "datasets/movielens/ratings.dat";
	double testUsers = 0.20; // 20% of test users
	double testItems = 0.20; // 20% of test items

	Kernel.getInstance().open(dbName, testUsers, testItems, "::");

	String [] similarityMetrics = {"COR", "JMSD"};
	int [] numberOfNeighbors = {50, 100, 150, 200, 250, 300, 350, 400};

	PrintableQualityMeasure mae = new cf4j.utils.PrintableQualityMeasure ("MAE", numberOfNeighbors, similarityMetrics);

	for (String sm : similarityMetrics) {

		// Compute similarity
		if (sm.equals("COR")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricCorrelation()); 			
		}
		else if (sm.equals("JMSD")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricJMSD());
		}

		// For each value of k
		for (int k : numberOfNeighbors) {

			// Find the neighbors
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.neighbors.Neighbors(k));

			// Compute predictions using DFM
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.aggregationApproaches.DeviationFromMean());

			// Compute MAE
			Processor.getInstance().testUsersProcess(new MAE());

			// Retrieve mae an store it
			mae.putError(k, sm, Kernel.gi().getQualityMeasure("MAE"));
		}
	}
	```

6. Finally, we will print the MAE.

	```Java
	String dbName = "datasets/movielens/ratings.dat";
	double testUsers = 0.20; // 20% of test users
	double testItems = 0.20; // 20% of test items

	Kernel.getInstance().open(dbName, testUsers, testItems, "::");

	String [] similarityMetrics = {"COR", "JMSD"};
	int [] numberOfNeighbors = {50, 100, 150, 200, 250, 300, 350, 400};

	PrintableQualityMeasure mae = new cf4j.utils.PrintableQualityMeasure("MAE", numberOfNeighbors, similarityMetrics);

	for (String sm : similarityMetrics) {

		// Compute similarity
		if (sm.equals("COR")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricCorrelation()); 			
		}
		else if (sm.equals("JMSD")) {
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.similarities.MetricJMSD());
		}

		// For each value of k
		for (int k : numberOfNeighbors) {

			// Find the neighbors
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.neighbors.Neighbors(k));

			// Compute predictions using DFM
			Processor.getInstance().testUsersProcess(new cf4j.knn.userToUser.aggregationApproaches.DeviationFromMean());

			// Compute MAE
			Processor.getInstance().testUsersProcess(new cf4j.qualityMeasures.MAE());

			// Retrieve mae an store it
			mae.putError(k, sm, Kernel.gi().getQualityMeasure("MAE"));
		}
	}

	// Print the results
	mae.print();
	```

7. The script will print an output like the following one. You can copy the MAE table to your favorite spreadsheets and create amazing charts to impress your colleges.

	```
	MAE COR JMSD
	50 0,9128474573520218 0,7553942348328571
	100 0,8893806700955731 0,736709246499725
	150 0,8738127218924089 0,7312775724076641
	200 0,8592615730876683 0,7271108717474274
	250 0,8464060693641936 0,7247435187124639
	300 0,8335663821836425 0,7244976530735382
	350 0,824260042847522 0,7249207675916145
	400 0,8152269060959684 0,7252641754669042
	```

## Package Structure

CF4J has been designed using the packages structure shown in the following figure:

![Package structure](http://rs.etsisi.upm.es/cf4j/images/cf4j-packages.png "Package structure")

The `cf4j` package is the root of the package tree; it contains the main classes to load, read and manipulate the CF information.

The `cf4j.model` package includes the implementations of model based CF methods. In this version, we have included the following matrix factorization based CF implementations:

* Hernando, A., Bobadilla, J., &amp; Ortega, F. (2016). A non negative matrix factorization for collaborative filtering recommender systems on a Bayesian probabilistic model. Knowledge-Based Systems, 97, 188-202 (`Bmf` class).
* Koren, Y., Bell, R., &amp; Volinsky, C. (2009). Matrix factorization techniques for recommender systems. Computer, 42 (8), 30-37 (`Pmf` class).

Other models will be easily added into this package.

The `cf4j.knn` package contains all the required classes to compute recommendations using memory based CF. It includes both user-to-user (`cf4j.knn.userToUser` package) and item-to-item (`cf4j.knn.itemToItem` package) approaches. Memory based CF algorithm is defined as:

1. Compute similarity between each pair of users.
2. Find the k users most similar to the active one (k nearest neighbors)
3. Compute predictions for the items unrated by the active user using the ratings of the k nearest neighbors

To compute similarities, several traditional similarity metrics have been included:

* Pearson Correlation (`MetricCorrelation` class).
* Pearson Correlation Constrained (`MetricCorrelationConstrained` class).
* Cosine (`MetricCosine` class).
* Adjusted Cosine (`MetricAdjustedCosine` class).
* Jaccard Index (`MetricJaccard` class).
* MSD (`MetricMSD` class).
* Spearman Rank (`MetricSpearmanRank` class).

Furthermore, different published similarity metrics have been included:

* Bobadilla, J., Serradilla, F., &amp; Bernal, J. (2010). A new collaborative filtering metric that improves the behavior of Recommender Systems, Knowledge-Based Systems, 23 (6), 520-528 (`MetricJMSD` class).
* Bobadilla, J., Ortega, F., Hernando, A., &amp; Arroyo, A. (2012). A Balanced Memory-Based Collaborative Filtering Similarity Measure, International Journal of Intelligent Systems, 27, 939-946 (`MetricCJMSD` class).
* Ahn, H. J. (2008). A new similarity measure for collaborative filtering to alleviate the new user cold-starting problem, Information Sciences, 178, 37-51 (`MetricPIP` class).
* Bobadilla, J., Ortega, F., &amp; Hernando, A. (2012). A collaborative filtering similarity measure based on singularities, Information Processing and Management, 48 (2), 204-217 (`MetricSingularities` class).

To find neighbors, an efficient sort method has been included in the `Neighbors` class.

To compute predictions, different aggregation approaches has been coded:

* Mean (`Mean` class).
* WeightedMean (`WeightedMean` class).
* Deviation from Mean (`DeviationFromMean`class).

The `cf4j.qualityMeasures` package contains different measures to check the goodness of the predictions and recommendations. The library includes:

* MAE (`MAE` class).
* Coverage (`Coverage` class).
* Precision and recall (`PrecisionRecall` class).
* F1 (`F1` class).

The `cf4j.utils` package contains different utilities to be used with the library. Read the [javadoc](http://rs.etsisi.upm.es/cf4j/) documentation for additional information.

## Main classes

In this section most important classes to work with CF Lib are explained. Please, read the [javadoc](http://rs.etsisi.upm.es/cf4j/) documentation for further information.

### User class

This class contains information related with the users of the recommender system. An `User` is defined by:

* An unique code for the user.
* Array of items codes that the user has rated.
* Array of ratings that the user has made. The indexes of the ratings’ array are the same that the indexes of the items’ array. For example, `user.getItems()[i]` will return the code of the i-th item rated by `user` and `user.getRatings()[i]` will return the rating of `user` to that item.
* A map to store any additional information related to the user. Several shorthand methods to store / retrieve similarities, neighbors and predictions has been defined. These maps can be used to store additional information related to the users such us gender, age or location.

The `TestUser` class extends `User` class with:

* Array of test items codes that the user has rated.
* Array of the ratings that the user has made to the test items. The indexes of this array overlaps with the indexes of the test items' array.

### Item class

This class contains information related with the items of the recommender system. An `Item` is defined by:

* An unique code for the item.
* Array of users codes that have rated the item.
* Array of ratings that the item has received. The indexes of the ratings’ array are the same that the indexes of the users' array. For example, `item.getUsers()[i]` will return the code of the i-th user that have rated the `item` and `item.getRatings()[i]` will return the rating that the user has made to `item`.
* A map to store any additional information related to the item. Several shorthand methods to store / retrieve similarities and neighbors has been defined. These maps can be used to store additional information related to the items such us description, title or location.

The `TestItem` class extends `Item` class with:

* Array of test user codes that have rated the item.
* Array of the ratings that the item has received. The indexes of this array overlaps with the indexes of the test users' array.

### Kernel class

`Kernel` class manages all the information related with the RS. This class implements the singleton pattern and contains methods to load and manipulate ratings of the users to the items. This class also split the users and items sets into test and training sets.

The Kernel class creates all the instances of the users and items from a text file. This file contains the sparse rating matrix of the users to the items in CSV format. Each line must have the following format (separator is customizable):

```
user_code;item_code;rating
```

`Kernel` class is defined with the following attributes:

* A map to store global properties of the RS. This map is used to store quality measures results. Several shorthand methods has been included to get additional properties.

* An array to store (training) `User` instances. This array is sorted by user code.

* An array to store `TestUser` instances. This array is sorted by user code.

* An array to store (training) `Item` instances. This array is sorted by item code.

* An array to store `TestItem` instances. This array is sorted by item code.

* Some properties about the dataset such us number of users, number of item, maximum rating value, minimum rating value, etc.

### Processor class

In Collaborative Filtering, recommendations are computed making calculations over the sets of users and/or items. These calculations can usually be parallelized to improve the efficiency of the algorithms. For example, when computing similarity metrics, the similarity between each pair of users is independent of any other similarity and can be computed in different threads. The library provides the `Processor` class that parallel executes `Partible` implementations over the test users (`TestUsersPartible` interface), user (`UsersPartible` interface), test items (`TestItemsPartible` interface) or items (`ItemsPartible` interface) arrays. A `Partible` is an interface that contains three methods:

* `beforeRun()`: this method is executed once before the parallelization. It is useful to initialize some resources.
* `run(index)`: this method is executed once for each user, test user, item or test item index. The `index` variable refers to the indexes of the `User`, `TestUser`, `Item` or `TestItem` arrays contained in the `Kernel` class. Please, use the methods `getUsers()[index]`, `getTestUsers()[index]`, `getItems()[index]` or `getTestItems()[index]` of the `Kernel` class to retrieve the `User`, `TestUser`, `Item` or `TestItem` respectively. Different indexes are executed in parallel. The number of execution threads is set to the double of the number of available processors by default, but it is customizable.
* `afterRun()`: this method is executed once after the parallelization. It is useful to aggregate results.

Most of the implementations provided in this library has been encoded using the `Partible` interfaces.

Processor object implements the Singleton pattern.

## Customize CF4J

The library include several abstract classes and interfaces to allow developers to build their our Collaborative Filtering algorithms.

The abstract classes `cf4j.knn.userToUser.similarities.UsersSimilarities` and `cf4j.knn.itemToItem.similarities.ItemsSimilarities` allow developers to implements new similarity metrics for user-to-user or item-to-item CF algorithms. `similarity()` methods must be implemented to define the new metric. This abstract methods has two users (or items) as parameters and must return a `double` value with the similarity between that users. This abstract class implements `Partible` interface to be used by `Processor` object.

Let's see a toy example. Imagine that we define the similarity between two users as the quadratic difference of their number of ratings * (rating average + their standard deviation). We will encode like this:

```Java
public class MyAwesomeSimilarity extends UsersSimilarities {

	@Override
	public double similarity (TestUser activeUser, User targetUser) {		

		double a = activeUser.getNumberOfRatings() * (activeUser.getRatingAverage() + activeUser.getRatingStandardDeviation());

		double t = targetUser.getNumberOfRatings() * (targetUser.getRatingAverage() + targetUser.getRatingStandardDeviation());

		return Math.abs(a - t);
	}
}
```

The abstract class `cf4j.qualityMeasures.qualityMeasure` allow developers to encode their own quality measure. To do so, the constructor must be redefined giving a name to the quality measure and the method `getMeasure()` must be implemented.

Let's see an example. We are going to create a quality measure that count the percentage of perfect predictions. We will encode like this:

```Java
public class Perfect extends QualityMeasure {

	private double threshold;

	public Perfect (double threshold) {
		super("Perfect");
		this.threshold = threshold;
	}

	@Override
	public double getMeasure (TestUser testUser) {

		double [] predictions = testUser.getPredictions();
		double [] ratings = testUser.getTestRatings();

		int perfect = 0;

		for (int i = 0; i < ratings.length; i++) {
			double rating = ratings[i];
			double prediction = predictions[i];

			if (prediction >= (rating - threshold) && prediction <= (rating + threshold)) {
				perfect++;
			}
		}

		return (double) perfect / testUser.getNumberOfTestRatings();
	}
}
```

Matrix factorization models are the most popular implementation of model based CF. `FactorizationModel` interface has been included to easily create new factorization models. It has two methods:

* `train()`: that must estimate the parameters of the factorized matrices.
* `getPrediction()`: that returns the prediction of an user to an item by their indexes.

The `FactorizationPrediction` class will generate the predictions for the test ratings from a `FactorizationModel`.

The final developer is free to implement any other methods that it needs to their experiments. The `Partible` interface (and its descendants `UsersPartible`, `TestUsersPartible`, `ItemsPartible` and `TestItemsPartible`) is an useful method to iterate over the arrays of users and items. However, there is certain situations in which this iteration is no plausible. The usage of `Partible` interface is not mandatory, is recommendable. Any experiment can be carry out using only the `Kernel` class and the map attributes of the `User` and `Item` classes.

By default, division between test and training users and items is made randomly. However, the `DatasetSplitters` class includes some built-in methods to define how this division is made. This division is made using lambda functions, so final developer can define their own lambda functions to split dataset based on his/her rules.

## Examples

### User-to-user vs item-to-item

In this example we compare MAE using JMSD similarity metric for user to user and item to items collaborative filtering approaches.

```Java
private static String dataset = "MovieLens1M.txt";
private static double testItems = 0.2; // 20% test items 					
private static double testUsers = 0.2; // 20% test users 					

private static int [] numberOfNeighbors = {50, 100, 150, 200, 250, 300, 350, 400, 450, 500};

public static void main (String [] args) {

	// To store experiment results
	String [] approaches = {"user-to-user", "item-to-item"};
	PrintableQualityMeasure mae = new PrintableQualityMeasure ("MAE", numberOfNeighbors, approaches);    

	// Load the database
	Kernel.getInstance().open(dataset, testUsers, testItems, "::");

	// User to user approach
	Processor.getInstance().testUsersProcess(new recsys.knn.userToUser.similarities.MetricJMSD());		

	// For each number of neighbors
	for (int k : numberOfNeighbors) {

		// Compute neighbors
		Processor.getInstance().testUsersProcess(new recsys.knn.userToUser.neighbors.Neighbors(k));

		// Compute predictions using Weighted Mean
		Processor.getInstance().testUsersProcess(new recsys.knn.userToUser.aggregationApproaches.WeightedMean());

		// Get MAE
		Processor.getInstance().testUsersProcess(new MAE());
		mae.putError(k, "user-to-user", Kernel.gi().getQualityMeasure("MAE"));
	}

	// Item to item approach
	Processor.getInstance().testItemsProcess(new recsys.knn.itemToItem.similarities.MetricJMSD());		

	// For each number of neighbors
	for (int k : numberOfNeighbors) {

		// Compute neighbors
		Processor.getInstance().testItemsProcess(new recsys.knn.itemToItem.neighbors.Neighbors(k));

		// Compute predictions using DFM
		Processor.getInstance().testUsersProcess(new recsys.knn.itemToItem.aggreagationApproaches.WeightedMean());

		// Get MAE
		Processor.getInstance().testUsersProcess(new MAE());
		mae.putError(k, "item-to-item", Kernel.gi().getQualityMeasure("MAE"));
	}

	// Print results
	mae.print();
}
```

This example has been included into the library's repository. You can find it on the class `examples.Example1`. To execute it using the command line tool you must use the following commands:

```
mvn package
cd src/examples
javac -cp ../../target/cf4j-recsys-1.1.0.jar Example2.java
java -classpath ../../target/cf4j-recsys-1.1.0.jar:../ examples.Example2
```

### Similarity metrics comparison

In this example we compare the similarity metrics COR, MSD, Jaccard and JMSD using the quality measures MAE, Coverage, Precision, Recall and F1.

```Java
private static String dataset = "MovieLens1M.txt";
private static double testItems = 0.2; // 20% test items 					
private static double testUsers = 0.2; // 20% test users 					

private static int [] numberOfNeighbors = {50, 100, 150, 200, 250, 300, 350, 400, 450, 500};
private static int [] numberOfRecommendations = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

private static double precisionRecallThreshold = 5;
private static int precisionRecallK = 200;

private static String [] similarityMetrics = {"COR", "MSD", "JAC", "JMSD"};

public static void main (String [] args) {

	// To store the quality measures results
	PrintableQualityMeasure mae = new PrintableQualityMeasure ("MAE", numberOfNeighbors, similarityMetrics);    

	PrintableQualityMeasure coverage = new PrintableQualityMeasure ("Coverage", numberOfNeighbors, similarityMetrics);

	PrintableQualityMeasure precision = new PrintableQualityMeasure ("Precision", numberOfRecommendations, similarityMetrics);

	PrintableQualityMeasure recall = new PrintableQualityMeasure ("Recall", numberOfRecommendations, similarityMetrics);

	PrintableQualityMeasure f1 = new PrintableQualityMeasure ("F1", numberOfRecommendations, similarityMetrics);

	// Load the database
	Kernel.getInstance().open(dataset, testUsers, testItems, "::");

	// Test each similarity metric
	for (String sm : similarityMetrics) {

		// Compute similarity
		if (sm.equals("COR")) {
			Processor.getInstance().testUsersProcess(new MetricCorrelation()); 			
		}
		else if (sm.equals("MSD")) {
			Processor.getInstance().testUsersProcess(new MetricMSD());
		}
		else if (sm.equals("JAC")) {
			Processor.getInstance().testUsersProcess(new MetricJaccard());		
		}
		else if (sm.equals("JMSD")) {
			Processor.getInstance().testUsersProcess(new MetricJMSD());
		}

		// For each number of neighbors
		for (int k : numberOfNeighbors) {

			// Compute neighbors
			Processor.getInstance().testUsersProcess(new Neighbors(k));

			// Compute predictions using DFM
			Processor.getInstance().testUsersProcess(new DeviationFromMean());

			// Get MAE
			Processor.getInstance().testUsersProcess(new MAE());
			mae.putError(k, sm, Kernel.gi().getQualityMeasure("MAE"));

			// Get Coverage
			Processor.getInstance().testUsersProcess(new Coverage());
			coverage.putError(k, sm, Kernel.gi().getQualityMeasure("Coverage"));
		}

		// For each number of recommendations
		Processor.getInstance().testUsersProcess(new Neighbors(precisionRecallK));
		Processor.getInstance().testUsersProcess(new DeviationFromMean());

		for (int n : numberOfRecommendations) {

			// Get precision and recall
			Processor.getInstance().testUsersProcess(new PrecisionRecall(n, precisionRecallThreshold));
			precision.putError(n, sm, Kernel.gi().getQualityMeasure("Precision"));
			recall.putError(n, sm, Kernel.gi().getQualityMeasure("Recall"));

			// Get F1 score
			Processor.getInstance().testUsersProcess(new F1(n, precisionRecallThreshold));
			f1.putError(n, sm, Kernel.gi().getQualityMeasure("F1"));
		}


		// Print results
		mae.print();
		coverage.print();
		precision.print();
		recall.print();
		f1.print();
	}
}
```

This example has been included into the library's repository. You can find it on the class `examples.Example2`. To execute it using the command line tool you must use the following commands:

```
mvn package
cd src/examples
javac -cp ../../target/cf4j-recsys-1.1.0.jar Example2.java
java -classpath ../../target/cf4j-recsys-1.1.0.jar:../ examples.Example2
```

### PMF vs BMF

In this example we compare MAE and Precision of PMF and BMF.

```Java
private static String dataset = "MovieLens1M.txt";
private static double testItems = 0.2; // 20% test items
private static double testUsers = 0.2; // 20% test users

private static int numRecommendations = 10;
private static double threshold = 4.0;

private static int pmf_numTopics = 15;
private static int pmf_numIters = 50;
private static double pmf_lambda = 0.055;

private static int bmf_numTopics = 6;
private static int bmf_numIters = 50;
private static double bmf_alpha = 0.8;
private static double bmf_beta = 5;

public static void main (String [] args) {

	// Load the database
	Kernel.getInstance().open(dataset, testUsers, testItems, "::");

	// PMF
	Pmf pmf = new Pmf (pmf_numTopics, pmf_numIters, pmf_lambda);
	pmf.train();

	Processor.getInstance().testUsersProcess(new FactorizationPrediction(pmf));

	System.out.println("\nPMF:");

	Processor.getInstance().testUsersProcess(new MAE());
	System.out.println("- MAE: " + Kernel.gi().getQualityMeasure("MAE"));

	Processor.getInstance().testUsersProcess(new Precision(numRecommendations, threshold));
	System.out.println("- Precision: " + Kernel.gi().getQualityMeasure("Precision"));

	// BMF
	Bmf bmf = new Bmf (bmf_numTopics, bmf_numIters, bmf_alpha, bmf_beta);
	bmf.train();

	Processor.getInstance().testUsersProcess(new FactorizationPrediction(bmf));

	System.out.println("\nBMF:");

	Processor.getInstance().testUsersProcess(new MAE());
	System.out.println("- MAE: " + Kernel.gi().getQualityMeasure("MAE"));

	Processor.getInstance().testUsersProcess(new Precision(numRecommendations, threshold));
	System.out.println("- Precision: " + Kernel.gi().getQualityMeasure("Precision"));
}
```

This example has been included into the library's repository. You can find it on the class `examples.Example3`. To execute it using the command line tool you must use the following commands:

```
mvn package
cd src/examples
javac -cp ../../target/cf4j-recsys-1.1.0.jar Example3.java
java -classpath ../../target/cf4j-recsys-1.1.0.jar:../ examples.Example3
```

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

* Model based CF:

  + Matrix factorization:

    - PMF ([link](https://dl.acm.org/citation.cfm?id=1608614))
    - BMF ([link](https://www.sciencedirect.com/science/article/pii/S0950705115005006))

* Quality measures:

    + MAE ([link](https://www.sciencedirect.com/science/article/pii/S0957417411008049))
    + Coverage ([link](https://www.sciencedirect.com/science/article/pii/S0957417411008049))
    + Precision & Recall ([link](https://www.sciencedirect.com/science/article/pii/S0957417411008049))
    + F1 ([link](https://www.sciencedirect.com/science/article/pii/S0957417411008049))

## Datasets

You can find awesome datasets to use with CF4J at this site: [http://shuaizhang.tech/2017/03/15/Datasets-For-Recommender-System/](http://shuaizhang.tech/2017/03/15/Datasets-For-Recommender-System/).
