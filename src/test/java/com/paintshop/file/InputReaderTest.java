package com.paintshop.file;

import com.google.common.collect.ImmutableList;
import com.paintshop.model.Customer;
import com.paintshop.model.TestCase;
import org.junit.Test;

import java.util.List;

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

    @Test(expected = InvalidInputException.class)
    public void testInvalidDataThrowsException1() throws Exception {
        READER.toCustomer("1 1");
    }

    @Test(expected = InvalidInputException.class)
    public void testInvalidDataThrowsException2() throws Exception {
        READER.toCustomer("1 1 1 2");
    }

    @Test
    public void testSingleTestCaseExtraction() throws Exception {
        List<String> lines = ImmutableList.of(
                "1",
                "1",
                "2",
                "1 1 0",
                "1 1 1"
        );
        TestCase tc = READER.extractTestCase(lines, 1);
        assertEquals(1, tc.getProducts().size());
        assertEquals(2, tc.getCustomers().size());
        tc.getCustomers().forEach(c -> assertEquals(1, c.getWishes().size()));
    }

    @Test
    public void testMultipleTestCaseExtraction() throws Exception {
        List<String> lines = ImmutableList.of(
                "2",
                "5",
                "3",
                "1 1 1",
                "2 1 0 2 0",
                "1 5 1",
                "1",
                "2",
                "1 1 0",
                "1 1 1"
        );
        TestCase tc = READER.extractTestCase(lines, 6);
        assertEquals(1, tc.getProducts().size());
        assertEquals(2, tc.getCustomers().size());
        tc.getCustomers().forEach(c -> assertEquals(1, c.getWishes().size()));
    }

}