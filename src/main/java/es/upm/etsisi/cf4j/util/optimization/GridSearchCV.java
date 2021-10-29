package es.upm.etsisi.cf4j.util.optimization;

import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;

import java.util.HashMap;
import java.util.Map;

public class GridSearchCV extends RandomSearchCV {

    public GridSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, int cv) {
        super(datamodel, grid, recommenderClass, qualityMeasureClass, null, cv, grid.getDevelopmentSetSize());
    }

    public GridSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams, int cv) {
        super(datamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, cv, grid.getDevelopmentSetSize(), System.currentTimeMillis());
    }

    public GridSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, int cv, long seed) {
        super(datamodel, grid, recommenderClass, qualityMeasureClass, null, cv, grid.getDevelopmentSetSize(), seed);
    }

    public GridSearchCV(DataModel datamodel, ParamsGrid grid, Class<? extends Recommender> recommenderClass, Class<? extends QualityMeasure> qualityMeasureClass, Map<String, Object> qualityMeasureParams, int cv, long seed) {
        super(datamodel, grid, recommenderClass, qualityMeasureClass, qualityMeasureParams, cv, grid.getDevelopmentSetSize(), seed);
    }
}
