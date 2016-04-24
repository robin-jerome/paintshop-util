package com.paintshop.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProductTest {

    @Test
    public void testToString() throws Exception {
        assertEquals("1 0", new Product(1, ColorFinish.GLOSSY).toString());
        assertEquals("1 1", new Product(1, ColorFinish.MATTE).toString());
    }

    @Test
    public void testEquals() throws Exception {
        assertEquals(new Product(1, ColorFinish.GLOSSY), new Product(1, ColorFinish.GLOSSY));
        assertNotEquals(new Product(1, ColorFinish.MATTE), new Product(1, ColorFinish.GLOSSY));
        assertNotEquals(new Product(1, ColorFinish.MATTE), new Product(2, ColorFinish.MATTE));
    }
}