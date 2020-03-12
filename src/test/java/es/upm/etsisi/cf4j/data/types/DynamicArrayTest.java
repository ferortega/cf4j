package es.upm.etsisi.cf4j.data.types;

import static org.junit.jupiter.api.Assertions.*;
import cf4j.data.types.DynamicArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

class DynamicArrayTest {
    static SortedArrayList<String> testArray = new SortedArrayList<String>(10);

    @BeforeAll
    static void initAll() {
        //Using Arraylist api to add few elements
        testArray.add("left");  //append
        testArray.add("right"); //append
        testArray.add(1,"middle");  //inserted
        testArray.set(1,"middleMix");   //substituted

        //Asserting everything is OK
        assertEquals("left", testArray.get(0));
        assertEquals("middleMix", testArray.get(1));
        assertEquals("right", testArray.get(2));
    }

    @Test
    void addOrdered() {
        testArray.addGettingIndex("000");
        testArray.addGettingIndex("lefz");
        testArray.addGettingIndex("middle");
        testArray.addGettingIndex("middlz");
        testArray.addGettingIndex("righz");

        //Asserting everything is OK
        assertEquals("000", testArray.get(0));
        assertEquals("left", testArray.get(1));
        assertEquals("lefz", testArray.get(2));
        assertEquals("middle", testArray.get(3));
        assertEquals("middleMix", testArray.get(4));
        assertEquals("middlz", testArray.get(5));
        assertEquals("right", testArray.get(6));
        assertEquals("righz", testArray.get(7));
    }

    @Test
    void get() {
        //Asserting everything is OK
        assertEquals(-1, testArray.get("000"));
        assertEquals(0, testArray.get("left"));
        assertEquals(-1, testArray.get("lefz"));
        assertEquals(-1, testArray.get("middle"));
        assertEquals(1, testArray.get("middleMix"));
        assertEquals(-1, testArray.get("middlz"));
        assertEquals(2, testArray.get("right"));
        assertEquals(-1, testArray.get("righz"));
    }
}