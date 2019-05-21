package cf4j.process;

import cf4j.data.DataModel;

public abstract class TestItemPartible extends PartibleThreads {
    public TestItemPartible(DataModel dataModel){
        super(dataModel);
    }

    @Override
    public int getTotalIndexes (){
        return dataModel.getNumberOfTestItems();
    }
}
