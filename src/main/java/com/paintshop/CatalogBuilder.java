package com.paintshop;

import com.paintshop.file.InputReader;
import com.paintshop.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CatalogBuilder {

    public static void main(String[] args) {
        try {
            CatalogBuilder solver = new CatalogBuilder();
            List<String> resultLines = new ArrayList<>();
            List<TestCase> testCases = new InputReader("input.txt").getTestCases();
            for (int i = 0, j = 1; i < testCases.size(); i++, j++) {
                System.out.println("Solving testcase " + j);
                TestCase tc = testCases.get(i);
                Optional<List<Product>> catalogOpt = solver.buildCatalog(tc);
                String result = "Case #" + j + ": ";
                if (!catalogOpt.isPresent()) {
                    result += "IMPOSSIBLE";
                } else {
                    result += catalogOpt.get().stream().map(p -> "" + p.getColorFinish().getCode()).collect(Collectors.joining(" "));
                }
                resultLines.add(result);
            }
            Files.write(Paths.get("output.txt"), resultLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<List<Product>> buildCatalog(TestCase testCase) {
        // All products are GLOSSY by default
        List<Customer> customers = new ArrayList<>(testCase.getCustomers());
        List<Product> products = testCase.getProducts(); // Step 1
        // Working on copy of customerList
        boolean hasSolution = true;
        boolean colorFinishChanged;
        do {
            colorFinishChanged = false;
            Iterator<Customer> customerIterator = customers.iterator();
            while (customerIterator.hasNext()) {
                Customer customer = customerIterator.next();
                if (!isCustomerSatisfied(products, customer)) {
                    if (customer.getWishes().stream().noneMatch(cw -> cw.getColorFinish().equals(ColorFinish.MATTE))) {
                        hasSolution = false;
                        break;
                    } else {
                        colorFinishChanged = true;
                        customerIterator.remove();
                        CustomerWish wish = customer.getWishes().stream().filter(cw -> cw.getColorFinish().equals(ColorFinish.MATTE)).findFirst().get();
                        products.stream().filter(p -> p.getColor() == wish.getColor()).findFirst().ifPresent(p -> changeColorFinishOfProduct(p));
                    }
                }
            }
        } while (colorFinishChanged);

        if (hasSolution) {
            return Optional.of(products);
        } else {
            return Optional.empty();
        }

    }

    private void changeColorFinishOfProduct(Product product) {
        switch (product.getColorFinish()) {
            case GLOSSY:
                product.setColorFinish(ColorFinish.MATTE);
                break;
            default:
                product.setColorFinish(ColorFinish.GLOSSY);
        }
    }

    private boolean isCustomerSatisfied(List<Product> products, Customer customer) {
        return customer.getWishes()
                .stream().anyMatch(cw -> products.stream().anyMatch(p -> p.getColor() == cw.getColor()
                        && p.getColorFinish().equals(cw.getColorFinish())));
    }

}
