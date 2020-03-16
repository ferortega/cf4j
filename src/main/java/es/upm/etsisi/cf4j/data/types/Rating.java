package es.upm.etsisi.cf4j.data.types;

import org.apache.commons.lang3.tuple.Pair;

public class Rating extends Pair<Integer, Double> {

    private int elementIndex;
    private double rating;

    public Rating (int elementIndex, double rating)
    {
        this.elementIndex = elementIndex;
        this.rating = rating;
    }

    @Override
    public Integer getLeft(){
            return this.elementIndex;
            }

    @Override
    public Double getRight(){
            return this.rating;
            }

    @Override
    public Double setValue(Double value){
            return this.rating = value;
            }
}
