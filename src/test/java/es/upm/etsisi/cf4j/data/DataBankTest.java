package es.upm.etsisi.cf4j.data;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DataBankTest {

    private static DataBank databank;

    @BeforeAll
    static void initAll() {
        databank = new DataBank();
        databank.setBoolean("boolean", false);
        databank.setBooleanArray("booleanArray", new boolean[]{true, false});
        databank.setDouble("double",123.123);
        databank.setDoubleArray("doubleArray", new double[]{123.123, 234.234});
        databank.setInt("integer", 69);
        databank.setIntArray("integerArray", new int[]{68, 70});
        databank.setString("string", "goodbye");
        databank.setStringArray("stringArray", new String[]{"goodbye", "hi"});
    }

    @Test
    void gettingValues () {
        assertTrue(databank.contains("boolean"));
        assertTrue(databank.contains("booleanArray"));
        assertFalse(databank.getBoolean("boolean"));
        assertEquals(databank.getBooleanArray("booleanArray").length, 2);
        assertTrue(databank.getBooleanArray("booleanArray")[0]);
        assertFalse(databank.getBooleanArray("booleanArray")[1]);

        assertTrue(databank.contains("double"));
        assertTrue(databank.contains("doubleArray"));
        assertEquals(databank.getDouble("double"), 123.123);
        assertEquals(databank.getDoubleArray("doubleArray").length, 2);
        assertEquals(databank.getDoubleArray("doubleArray")[0], 123.123);
        assertEquals(databank.getDoubleArray("doubleArray")[1], 234.234);

        assertTrue(databank.contains("integer"));
        assertTrue(databank.contains("integerArray"));
        assertEquals(databank.getInt("integer"), 69);
        assertEquals(databank.getIntArray("integerArray").length, 2);
        assertEquals(databank.getIntArray("integerArray")[0], 68);
        assertEquals(databank.getIntArray("integerArray")[1], 70);

        assertTrue(databank.contains("string"));
        assertTrue(databank.contains("stringArray"));
        assertEquals(databank.getString("string"), "goodbye");
        assertEquals(databank.getStringArray("stringArray").length, 2);
        assertEquals(databank.getStringArray("stringArray")[0], "goodbye");
        assertEquals(databank.getStringArray("stringArray")[1], "hi");

        assertFalse(databank.contains("etsisi"));
    }

    @AfterAll
    static void deletingValues(){
        databank.delete("integer");
        assertFalse(databank.contains("integer"));

        databank.deleteAll();
        assertFalse(databank.contains("doubleArray"));
    }
}