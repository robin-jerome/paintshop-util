package com.paintshop.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TestCase {
    private List<Product> products = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    // Prevent external creation
    private TestCase() {

    }

    public int linesConsumed() {
        // One line for number of colors
        // + One line for number of customers
        // + n lines for 'n' customers
        return 1 + 1 + customers.size();
    }

    private void createProducts(int productCount) {
        // Product indexing starts from 1
        for (int i = 1; i <= productCount ; i++) {
            products.add(new Product(i));
        }
    }

    public static TestCase makeWithProductCountAndCustomers(int productCount, List<Customer> customers) {
        TestCase testCase = new TestCase();
        testCase.createProducts(productCount);
        testCase.customers = customers;
        return testCase;
    }
}
