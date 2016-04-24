package com.paintshop.model;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCaseTest {

    @Test
    public void testLinesConsumed() throws Exception {
        assertEquals("Lines consumed by testcases are incorrect",
                2, TestCase.makeWithProductCountAndCustomers(1, ImmutableList.of()).linesConsumed());
        assertEquals("Lines consumed by testcases are incorrect",
                3, TestCase.makeWithProductCountAndCustomers(1,
                        ImmutableList.of(Customer.makeCustomer(ImmutableList.of()))).linesConsumed());
    }
}