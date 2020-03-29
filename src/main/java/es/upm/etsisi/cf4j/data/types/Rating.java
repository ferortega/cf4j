package es.upm.etsisi.cf4j.data.types;

import java.io.Serializable;

public class Rating implements Serializable, Comparable<Rating> {

    private static final long serialVersionUID = 20200314L;

    private int index;
    private double rating;

    public Rating (int index, double rating)
    {
        this.index = index;
        this.rating = rating;
    }

    public Integer getIndex(){
            return this.index;
            }

    public Double getRating(){
            return this.rating;
            }

    public Double setRating(Double value){
            return this.rating = value;
            }

    public int compareTo(Rating other) {
        return (this.index < other.index) ? -1 : ((this.index == other.index) ? 0 : 1);
    }

    @Override
    public String toString() {
        return "(" + this.getIndex() + ',' + this.getRating() + ')';
    }
}
