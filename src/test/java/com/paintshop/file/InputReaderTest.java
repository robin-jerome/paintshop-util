package com.paintshop.file;

import com.paintshop.model.Customer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InputReaderTest {

    private static final InputReader READER = new InputReader("input.txt");

    @Test
    public void testLineToCustomer() throws Exception {
        Customer customer = READER.toCustomer("2 1 0 2 0");
        assertEquals(2, customer.getWishes().size());
        customer = READER.toCustomer("1 1 1");
        assertEquals(1, customer.getWishes().size());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testInvalidDataThrowsException2() throws Exception {
        READER.toCustomer("1 1");
    }
}