package es.upm.etsisi.cf4j.recommender;


import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.TestUser;

public abstract class Recommender {

    protected DataModel datamodel;

    protected Recommender(DataModel datamodel) {
        this.datamodel = datamodel;
    }

    public DataModel getDataModel() {
        return this.datamodel;
    }

    public abstract void fit();

    public abstract double predict(int userIndex, int itemIndex);

    public double[] predict(TestUser testUser) {
        int userIndex = testUser.getUserIndex();
        double[] predictions = new double[testUser.getNumberOfTestRatings()];
        for (int i = 0; i < predictions.length; i++) {
            int itemIndex = testUser.getTestItemAt(i);
            predictions[i] = this.predict(userIndex, itemIndex);
        }
        return predictions;
    }
}
