package cf4j.process;

import cf4j.data.DataModel;

public abstract class UserPartible extends Partible {
    public UserPartible (DataModel dataModel){
        super(dataModel);
    }

    @Override
    public int getTotalIndexes (){
        return dataModel.getNumberOfUsers();
    }
}
