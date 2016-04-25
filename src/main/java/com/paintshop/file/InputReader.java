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

public class InputReader {
    private final String inputFileName;

    public InputReader(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public static void main(String[] args) {
        try {
            new InputReader("input.txt").getTestCases().forEach(t -> {
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
        int testCaseCount = extractInt(lines.get(0));
        if (testCaseCount > 0) {
            while (allTests.size() < testCaseCount) {
                int startingLine = 1 + allTests.stream().mapToInt(x -> x.linesConsumed()).sum();
                allTests.add(extractTestCase(lines, startingLine));
            }
        } else {
            throw new InvalidInputException("Number of test cases in file should be greater than zero");
        }
        return allTests;
    }

    protected TestCase extractTestCase(List<String> lines, int startingLine) throws IOException {
        int productCount = extractInt(lines.get(startingLine));
        int customerCount = extractInt(lines.get(startingLine + 1));
        List<Customer> customers = lines.subList(startingLine + 2, startingLine + 2 + customerCount).stream()
                .map(l -> toCustomer(l)).collect(Collectors.toList());
        return TestCase.makeWithProductCountAndCustomers(productCount, customers);
    }

    protected Customer toCustomer(String line) {
        List<CustomerWish> wishes = new ArrayList<>();
        List<Integer> numbers = Arrays.asList(line.split(" ")).stream()
                .map(Integer::valueOf)
                .collect(Collectors.toList());
        int wishCount = numbers.get(0);
        if (numbers.size() == (wishCount * 2) + 1) {
            for (int i = 1; i <= wishCount * 2; i += 2) {
                int colorCode = numbers.get(i);
                ColorFinish finish = ColorFinish.of(numbers.get(i + 1));
                wishes.add(CustomerWish.makeCustomerWish(colorCode, finish));
            }
        } else {
            throw new InvalidInputException("Customer wish count does not match count of customer wishes for line "+ line);
        }
        return Customer.makeCustomer(wishes);
    }

    private Integer extractInt(String line) {
        return Integer.valueOf(line.trim());
    }

}
