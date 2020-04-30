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
    databank.setBooleanArray("booleanArray", new boolean[] {true, false});
    databank.setDouble("double", 123.123);
    databank.setDoubleArray("doubleArray", new double[] {123.123, 234.234});
    databank.setInt("integer", 69);
    databank.setIntArray("integerArray", new int[] {68, 70});
    databank.setString("string", "goodbye");
    databank.setStringArray("stringArray", new String[] {"goodbye", "hi"});
  }

  @Test
  void gettingValues() {
    assertTrue(databank.contains("boolean"));
    assertTrue(databank.contains("booleanArray"));
    assertFalse(databank.getBoolean("boolean"));
    assertEquals(databank.getBooleanArray("booleanArray").length, 2);
    assertTrue(databank.getBooleanArray("booleanArray")[0]);
    assertFalse(databank.getBooleanArray("booleanArray")[1]);

    assertTrue(databank.contains("double"));
    assertTrue(databank.contains("doubleArray"));
    assertEquals(123.123, databank.getDouble("double"));
    assertEquals(2, databank.getDoubleArray("doubleArray").length);
    assertEquals(123.123, databank.getDoubleArray("doubleArray")[0]);
    assertEquals(234.234, databank.getDoubleArray("doubleArray")[1]);

    assertTrue(databank.contains("integer"));
    assertTrue(databank.contains("integerArray"));
    assertEquals(69, databank.getInt("integer"));
    assertEquals(2, databank.getIntArray("integerArray").length);
    assertEquals(68, databank.getIntArray("integerArray")[0]);
    assertEquals(70, databank.getIntArray("integerArray")[1]);

    assertTrue(databank.contains("string"));
    assertTrue(databank.contains("stringArray"));
    assertEquals("goodbye", databank.getString("string"));
    assertEquals(2, databank.getStringArray("stringArray").length);
    assertEquals("goodbye", databank.getStringArray("stringArray")[0]);
    assertEquals("hi", databank.getStringArray("stringArray")[1]);

    assertFalse(databank.contains("etsisi"));
  }

  @AfterAll
  static void deletingValues() {
    databank.delete("integer");
    assertFalse(databank.contains("integer"));

    databank.deleteAll();
    assertFalse(databank.contains("doubleArray"));
  }
}
