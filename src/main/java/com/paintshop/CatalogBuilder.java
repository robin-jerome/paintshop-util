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

    /**
     * This method builds a product catalog (if possible) that matches wishes of multiple customers.
     * @param testCase
     * @return Optional catalog if possible, otherwise returns Optional.empty();
     */
    public Optional<List<Product>> buildCatalog(TestCase testCase) {
        // Working on copy of customerList as customers will be removed once their needs are satisfied
        List<Customer> customers = new ArrayList<>(testCase.getCustomers());
        // All products are initialized to GLOSSY by default
        List<Product> products = testCase.getProducts();

        boolean hasSolution = true;
        boolean colorFinishChanged;
        do {
            colorFinishChanged = false;
            Iterator<Customer> customerIterator = customers.iterator(); // Iterators used to modify the list during execution
            while (customerIterator.hasNext()) {
                Customer customer = customerIterator.next();
                if (!isCustomerSatisfied(products, customer)) {
                    if (customer.getWishes().stream().noneMatch(cw -> cw.getColorFinish().equals(ColorFinish.MATTE))) {
                        // Customer has not requested for MATTE -> Some other customer has requested a color change
                        // Not possible to satisfy both customers
                        hasSolution = false;
                        break;
                    } else {
                        // Change the color of the product and remove from the list
                        colorFinishChanged = true;
                        customerIterator.remove(); // Remove the customer
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


    /**
     * This method changes the color finish of the product
     * @param product The product whose color finish needs to be changed
     */
    protected void changeColorFinishOfProduct(Product product) {
        switch (product.getColorFinish()) {
            case GLOSSY:
                product.setColorFinish(ColorFinish.MATTE);
                break;
            default:
                product.setColorFinish(ColorFinish.GLOSSY);
        }
    }

    /**
     * This method checks if a customer is satisfied with the current product catalog
     * This is done by checking if any of the products in the catalog are present in the customer wish list
     * @param products List of products in the catalog
     * @param customer Customer whose satisfaction needs to be checked
     * @return true if wish is satisfied else false
     */
    protected boolean isCustomerSatisfied(List<Product> products, Customer customer) {
        return customer.getWishes()
                .stream().anyMatch(cw -> products.stream().anyMatch(p -> p.getColor() == cw.getColor()
                        && p.getColorFinish().equals(cw.getColorFinish())));
    }

}
