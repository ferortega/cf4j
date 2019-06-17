package cf4j.data;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataModelTest {

    private static DataModel dataModel;

    @BeforeAll
    static void initAll() {
        dataModel = new DataModel();

        //Hand-made populated:
        //It's mandatory give test ratings first.
        dataModel.addTestRating("Tim","Milk",1.1f);
        dataModel.addTestRating("Tim","Potatoad",1.2f);
        dataModel.addTestRating("Kim","Milk",2.1f);
        dataModel.addTestRating("Kim","Potatoad",2.2f);

        //Now, normal ratings.
        dataModel.addRating("Tim","WiredThing",1.3f);
        dataModel.addRating("Tim","Yeah,IsWired",1.4f);
        dataModel.addRating("Kim","WiredThing",2.3f);
        dataModel.addRating("Kim","Yeah,IsWired",2.4f);
        dataModel.addRating("Laurie","Potatoad",3.1f);
        dataModel.addRating("Laurie","Yeah,IsWired",3.2f);
        dataModel.addRating("Laurie","Milk",3.3f);
        dataModel.addRating("Mike","Milk",4.1f);
        dataModel.addRating("Mike","WiredThing",0f);
        dataModel.addRating("Mike","Potatoad",4.3f);
        dataModel.addRating("Mike","WiredThing",4.2f); //Overriding a rating.
        dataModel.addRating("Mike","Yeah,IsWired",4.4f);
    }

    @Test
    void checkingContents () {
        //Users
        assertEquals(dataModel.getNumberOfUsers(),  4);
        assertEquals(dataModel.getUser("Tim").getNumberOfRatings(), 2);
        assertEquals(dataModel.getUser("Kim").getNumberOfRatings(), 2);
        assertEquals(dataModel.getUser("Laurie").getNumberOfRatings(), 3);
        assertEquals(dataModel.getUser("Mike").getNumberOfRatings(), 4);
        //Items
        assertEquals(dataModel.getNumberOfItems(),  4);
        assertEquals(dataModel.getItem("Milk").getNumberOfRatings(), 2);
        assertEquals(dataModel.getItem("Potatoad").getNumberOfRatings(), 2);
        assertEquals(dataModel.getItem("WiredThing").getNumberOfRatings(), 3);
        assertEquals(dataModel.getItem("Yeah,IsWired").getNumberOfRatings(), 4);
        //TestUsers
        assertEquals(dataModel.getNumberOfTestUsers(),  2);
        assertEquals(dataModel.getTestUser("Tim").getNumberOfRatings(), 2);
        assertEquals(dataModel.getTestUser("Kim").getNumberOfRatings(), 2);
        //TestItems
        assertEquals(dataModel.getNumberOfTestItems(),  2);
        assertEquals(dataModel.getTestItem("Milk").getNumberOfRatings(), 2);
        assertEquals(dataModel.getTestItem("Potatoad").getNumberOfRatings(), 2);
    }

    @Test
    void checkingElementOrders () {
        //Users
        assertEquals(dataModel.getUserIndex("Kim"), 0);
        assertEquals(dataModel.getUserIndex("Laurie"), 1);
        assertEquals(dataModel.getUserIndex("Mike"), 2);
        assertEquals(dataModel.getUserIndex("Tim"), 3);
        //Items
        assertEquals(dataModel.getItemIndex("Milk"), 0);
        assertEquals(dataModel.getItemIndex("Potatoad"), 1);
        assertEquals(dataModel.getItemIndex("WiredThing"), 2);
        assertEquals(dataModel.getItemIndex("Yeah,IsWired"), 3);
        //TestUsers
        assertEquals(dataModel.getTestUserIndex("Kim"), 0);
        assertEquals(dataModel.getTestUserIndex("Tim"), 1);
        //TestItems
        assertEquals(dataModel.getTestItemIndex("Milk"), 0);
        assertEquals(dataModel.getTestItemIndex("Potatoad"), 1);
    }

    @Test
    void checkingSubElementOrdersAndRatings () {
        //Users
        assertEquals(dataModel.getUserAt(0).getItemAt(0), "WiredThing");
        assertEquals(dataModel.getUserAt(0).getRatingAt(0),2.3f);
        assertEquals(dataModel.getUserAt(0).getItemAt(1), "Yeah,IsWired");
        assertEquals(dataModel.getUserAt(0).getRatingAt(1),2.4f);
        assertEquals(dataModel.getUserAt(1).getItemAt(0),"Milk");
        assertEquals(dataModel.getUserAt(1).getRatingAt(0),3.3f);
        assertEquals(dataModel.getUserAt(1).getItemAt(1), "Potatoad");
        assertEquals(dataModel.getUserAt(1).getRatingAt(1),3.1f);
        assertEquals(dataModel.getUserAt(1).getItemAt(2), "Yeah,IsWired");
        assertEquals(dataModel.getUserAt(1).getRatingAt(2),3.2f);
        assertEquals(dataModel.getUserAt(2).getItemAt(0),"Milk");
        assertEquals(dataModel.getUserAt(2).getRatingAt(0),4.1f);
        assertEquals(dataModel.getUserAt(2).getItemAt(1),"Potatoad");
        assertEquals(dataModel.getUserAt(2).getRatingAt(1),4.3f);
        assertEquals(dataModel.getUserAt(2).getItemAt(2),"WiredThing");
        assertEquals(dataModel.getUserAt(2).getRatingAt(2),4.2f);
        assertEquals(dataModel.getUserAt(2).getItemAt(3),"Yeah,IsWired");
        assertEquals(dataModel.getUserAt(2).getRatingAt(3),4.4f);
        assertEquals(dataModel.getUserAt(3).getItemAt(0), "WiredThing");
        assertEquals(dataModel.getUserAt(3).getRatingAt(0),1.3f);
        assertEquals(dataModel.getUserAt(3).getItemAt(1), "Yeah,IsWired");
        assertEquals(dataModel.getUserAt(3).getRatingAt(1),1.4f);
        //Items
        assertEquals(dataModel.getItemAt(0).getUserAt(0), "Laurie");
        assertEquals(dataModel.getItemAt(0).getRatingAt(0),3.3f);
        assertEquals(dataModel.getItemAt(0).getUserAt(1), "Mike");
        assertEquals(dataModel.getItemAt(0).getRatingAt(1),4.1f);
        assertEquals(dataModel.getItemAt(1).getUserAt(0), "Laurie");
        assertEquals(dataModel.getItemAt(1).getRatingAt(0),3.1f);
        assertEquals(dataModel.getItemAt(1).getUserAt(1), "Mike");
        assertEquals(dataModel.getItemAt(1).getRatingAt(1),4.3f);
        assertEquals(dataModel.getItemAt(2).getUserAt(0), "Kim");
        assertEquals(dataModel.getItemAt(2).getRatingAt(0),2.3f);
        assertEquals(dataModel.getItemAt(2).getUserAt(1), "Mike");
        assertEquals(dataModel.getItemAt(2).getRatingAt(1),4.2f);
        assertEquals(dataModel.getItemAt(2).getUserAt(2), "Tim");
        assertEquals(dataModel.getItemAt(2).getRatingAt(2),1.3f);
        assertEquals(dataModel.getItemAt(3).getUserAt(0), "Kim");
        assertEquals(dataModel.getItemAt(3).getRatingAt(0),2.4f);
        assertEquals(dataModel.getItemAt(3).getUserAt(1), "Laurie");
        assertEquals(dataModel.getItemAt(3).getRatingAt(1),3.2f);
        assertEquals(dataModel.getItemAt(3).getUserAt(2), "Mike");
        assertEquals(dataModel.getItemAt(3).getRatingAt(2),4.4f);
        assertEquals(dataModel.getItemAt(3).getUserAt(3), "Tim");
        assertEquals(dataModel.getItemAt(3).getRatingAt(3),1.4f);
        //TestUsers TestPart
        assertEquals(dataModel.getTestUserAt(0).getTestItemAt(0), "Milk");
        assertEquals(dataModel.getTestUserAt(0).getTestRatingAt(0),2.1f);
        assertEquals(dataModel.getTestUserAt(0).getTestItemAt(1), "Potatoad");
        assertEquals(dataModel.getTestUserAt(0).getTestRatingAt(1),2.2f);
        assertEquals(dataModel.getTestUserAt(1).getTestItemAt(0), "Milk");
        assertEquals(dataModel.getTestUserAt(1).getTestRatingAt(0),1.1f);
        assertEquals(dataModel.getTestUserAt(1).getTestItemAt(1), "Potatoad");
        assertEquals(dataModel.getTestUserAt(1).getTestRatingAt(1),1.2f);
        //TestUsers No-TestPart
        assertEquals(dataModel.getTestUserAt(0).getItemAt(0), "WiredThing");
        assertEquals(dataModel.getTestUserAt(0).getRatingAt(0),2.3f);
        assertEquals(dataModel.getTestUserAt(0).getItemAt(1), "Yeah,IsWired");
        assertEquals(dataModel.getTestUserAt(0).getRatingAt(1),2.4f);
        assertEquals(dataModel.getTestUserAt(1).getItemAt(0), "WiredThing");
        assertEquals(dataModel.getTestUserAt(1).getRatingAt(0),1.3f);
        assertEquals(dataModel.getTestUserAt(1).getItemAt(1), "Yeah,IsWired");
        assertEquals(dataModel.getTestUserAt(1).getRatingAt(1),1.4f);
        //TestItems TestPart
        assertEquals(dataModel.getTestItemAt(0).getTestUserAt(0), "Kim");
        assertEquals(dataModel.getTestItemAt(0).getTestRatingAt(0),2.1f);
        assertEquals(dataModel.getTestItemAt(0).getTestUserAt(1), "Tim");
        assertEquals(dataModel.getTestItemAt(0).getTestRatingAt(1),1.1f);
        assertEquals(dataModel.getTestItemAt(1).getTestUserAt(0), "Kim");
        assertEquals(dataModel.getTestItemAt(1).getTestRatingAt(0),2.2f);
        assertEquals(dataModel.getTestItemAt(1).getTestUserAt(1), "Tim");
        assertEquals(dataModel.getTestItemAt(1).getTestRatingAt(1),1.2f);
        //TestItems No-TestPart
        assertEquals(dataModel.getTestItemAt(0).getUserAt(0), "Laurie");
        assertEquals(dataModel.getTestItemAt(0).getRatingAt(0),3.3f);
        assertEquals(dataModel.getTestItemAt(0).getUserAt(1), "Mike");
        assertEquals(dataModel.getTestItemAt(0).getRatingAt(1),4.1f);
        assertEquals(dataModel.getTestItemAt(1).getUserAt(0), "Laurie");
        assertEquals(dataModel.getTestItemAt(1).getRatingAt(0),3.1f);
        assertEquals(dataModel.getTestItemAt(1).getUserAt(1), "Mike");
        assertEquals(dataModel.getTestItemAt(1).getRatingAt(1),4.3f);
    }
}