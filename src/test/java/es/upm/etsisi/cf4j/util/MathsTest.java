package es.upm.etsisi.cf4j.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MathsTest {

    @Test
    void range() {
        Assertions.assertArrayEquals(new int[]{0, 1, 2}, Maths.range(2));
        Assertions.assertArrayEquals(new int[]{1, 2, 3}, Maths.range(1, 3));
        Assertions.assertArrayEquals(new int[]{1, 3, 5}, Maths.range(1, 5, 2));
        Assertions.assertArrayEquals(new int[]{1, 3, 5}, Maths.range(1, 6, 2));
        Assertions.assertArrayEquals(new int[]{1, 3}, Maths.range(1, 5, 2, false));
    }

    @Test
    void linespace() {
        Assertions.assertArrayEquals(new double[]{0.0, 1.0, 2.0}, Maths.linespace(2, 3));
        Assertions.assertArrayEquals(new double[]{1.0, 1.5, 2.0}, Maths.linespace(1, 2, 3));
        Assertions.assertArrayEquals(new double[]{0.0, 0.5}, Maths.linespace(0, 1, 2, false));
        Assertions.assertArrayEquals(new double[]{0.0, 0.5, 1.0}, Maths.linespace(0, 1, 3, true));
    }

    @Test
    void logspace() {
        Assertions.assertArrayEquals(new double[]{0.001, 0.01, 0.1, 1.0}, Maths.logspace(-3, 0, 4));
        Assertions.assertArrayEquals(new double[]{2, 4, 8, 16}, Maths.logspace(1, 4, 4, 2));
        Assertions.assertArrayEquals(new double[]{1, 10, 100}, Maths.logspace(0, 3, 3, false));
        Assertions.assertArrayEquals(new double[]{3, 27}, Maths.logspace(1, 3, 2, true, 3));
    }
}
