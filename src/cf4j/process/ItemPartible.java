package cf4j.process;

import cf4j.data.DataModel;

public abstract class ItemPartible extends Partible {
    public ItemPartible(DataModel dataModel){
        super(dataModel);
    }

    @Override
    public int getTotalIndexes (){
        return dataModel.getNumberOfItems();
    }
}
