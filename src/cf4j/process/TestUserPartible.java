package cf4j.process;

import cf4j.data.DataModel;

public abstract class TestUserPartible extends PartibleThreads {
    public TestUserPartible(DataModel dataModel){
        super(dataModel);
    }

    @Override
    public int getTotalIndexes (){
        return dataModel.getNumberOfTestUsers();
    }
}
