package com.paintshop.file;

import com.paintshop.model.ColorFinish;
import com.paintshop.model.Customer;
import com.paintshop.model.CustomerWish;
import com.paintshop.model.TestCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InputFileParser {
    private final String inputFileName;

    public InputFileParser(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public static void main(String[] args) {
        try {
            new InputFileParser("input.txt").getTestCases().forEach(t -> {
                System.out.println("Products" + " - " + t.getProducts().size());
                System.out.println("Customers" + " - " + t.getCustomers().size());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<TestCase> getTestCases() throws IOException {
        List<TestCase> allTests = new ArrayList<>();
        List<String> lines = Files.lines(Paths.get(inputFileName)).collect(Collectors.toList());
        final int testCaseCount = extractIntegerFromLine(lines.get(0));
        if (testCaseCount > 0) {
            while (allTests.size() < testCaseCount) {
                int startingLine = 1 + allTests.stream().mapToInt(x -> x.linesConsumed()).sum();
                allTests.add(extractTestCase(lines, startingLine));
            }
        } else {
            throw new InvalidInputDataException("Number of test cases in file should be greater than zero");
        }
        return allTests;
    }

    private TestCase extractTestCase(List<String> lines, int startingLine) throws IOException {
        int productCount = extractIntegerFromLine(lines.get(startingLine));
        int customerCount = extractIntegerFromLine(lines.get(startingLine + 1));
        List<Customer> customers = lines.subList(startingLine + 2, startingLine + 2 + customerCount).stream()
                .map(l -> toCustomer(l)).collect(Collectors.toList());
        if (customers.size() != customerCount) {
            throw new InvalidInputDataException("Customer count and count of customers do not match");
        }
        return TestCase.makeWithProductCountAndCustomers(productCount, customers);
    }

    private Customer toCustomer(String line) {
        List<Integer> numbers = Arrays.asList(line.split(" ")).stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        List<CustomerWish> wishes = new ArrayList<>();
        int wishCount = numbers.get(0);
        for (int i = 1; i <= wishCount * 2; i += 2) {
            int colorCode = numbers.get(i);
            ColorFinish finish = ColorFinish.of(numbers.get(i + 1));
            wishes.add(CustomerWish.makeCustomerWish(colorCode, finish));
        }
        if (wishes.size() != wishCount) {
            throw new InvalidInputDataException("Customer wish count and count of wishes do not match");
        }
        return Customer.makeCustomer(wishes);
    }

    private Integer extractIntegerFromLine(String line) {
        return Integer.valueOf(line.trim());
    }

}
