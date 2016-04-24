package com.paintshop;

import com.paintshop.file.InputReader;
import com.paintshop.model.Customer;
import com.paintshop.model.CustomerWish;
import com.paintshop.model.Product;
import com.paintshop.model.TestCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Solver {

    public static void main(String[] args) {
        try {
            Solver solver = new Solver();
            List<String> resultLines = new ArrayList<>();
            List<TestCase> testCases = new InputReader("input.txt").getTestCases();
            for (int i = 0, j = 1; i < testCases.size(); i++, j++) {
                TestCase tc = testCases.get(i);
                Map<Integer, List<CustomerWish>> solutions  = solver.solve(tc);
                System.out.println("Test case has " + solutions.keySet().size() + " solution(s)");
                String result = "Case #" + j + ": ";
                if (solutions.isEmpty()) {
                    // Push output to file
                    result += "IMPOSSIBLE";
                } else {
                    Set<Product> catalog = solver.makeProductCatalog(solutions, tc.getProducts());
                    result += catalog.stream().map(p -> "" + p.getColorFinish().getCode()).collect(Collectors.joining(" "));
                }
                resultLines.add(result);
            }
            Files.write(Paths.get("output.txt"), resultLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<Product> makeProductCatalog(Map<Integer, List<CustomerWish>> solutions, List<Product> products) {
        // TreeSet to make colors ordered by their color code
        Set<Product> catalog = new TreeSet<>((o1, o2) -> Integer.valueOf(o1.getColor()).compareTo(Integer.valueOf(o2.getColor())));
        catalog.addAll(findCheapestSolution(solutions));
        // Add the colors that customers didn't request for also to the catalog
        products.forEach(p -> {
            boolean isColorPresent = catalog.stream().anyMatch(c -> p.getColor() == c.getColor());
            if (!isColorPresent) {
                catalog.add(p);
            }
        });
        return catalog;
    }

    private Set<Product> findCheapestSolution(Map<Integer, List<CustomerWish>> solutions) {
        return solutions.values().stream()
                .map(x -> new HashSet<>(wishesToProducts(x)))
                .sorted((a, b) -> costOfSolution(a).compareTo(costOfSolution(b)))
                .findFirst().get();
    }

    private List<Product> wishesToProducts(List<CustomerWish> customerWishes) {
        return customerWishes.stream().map(CustomerWish::getProduct).collect(Collectors.toList());
    }

    private Integer costOfSolution(Collection<Product> products) {
        return products.stream().map(p -> p.getColorFinish().getCode()).
                reduce(0, Integer::sum);
    }

    private Map<Integer, List<CustomerWish>> solve(TestCase testCase) {
        List<Customer> customers = new ArrayList<>(testCase.getCustomers());
        // Sort for optimal computing
        sortCustomersAndWishes(customers);
        Map<Integer, List<CustomerWish>> solutions = new HashMap<>();
        List<CustomerWish> tempGrants = new ArrayList<>();
        int solutionIndex = 0;
        // Iterate through all customers
        for (int i = 0; i < customers.size(); i++) { // Begin FOR LOOP for customers
            Customer customer = customers.get(i);
            boolean wishGranted = false;
            // Iterate through unvisited wishes of a customer
            for (CustomerWish wish : customer.getUnVisitedWishes()) { // Begin FOR LOOP for customer wishes
                if (isGrantable(wish, tempGrants)) {
                    wish.visitAndGrant();
                    wishGranted = true;
                    tempGrants.add(wish);
                    System.out.println("Granting wish for customer " + i);
                    if (isSolved(customers)) {
                        solutions.put(solutionIndex++, new ArrayList<>(tempGrants));
                        System.out.println("Found solution number " + solutionIndex);
                        if (i > 0) {
                            // Remove the recently added wish from the list
                            removeFromGrants(wish, tempGrants);
                            int nextIndex = performResetActions(customers, tempGrants, customer);
                            i = nextIndex;
                            break;
                        } else {
                            System.out.println("Vulnerability point 1");
                            return solutions;
                        }
                    }
                } else {
                    wish.visit();
                }
            } // End FOR LOOP for customer wishes

            if (!wishGranted) {
                // Reset previous successful wish grant and move back to that customer's wishes
                if (i > 0) {
                    int nextIndex = performResetActions(customers, tempGrants, customer);
                    i = nextIndex;
                } else {
                    // Reached top of the grid - return the solutions map
                    return solutions;
                }
            }
        } // End FOR LOOP for customer wishes
        return solutions;
    }

    private int performResetActions(List<Customer> customers, List<CustomerWish> tempGrants, Customer customer) {
        int nextIndex = getFirstCustomerIndexWithLastGrantedWish(tempGrants, customers);
        // Reset all customer wishes from next index to last
        for (int j = nextIndex + 1; j < customers.size(); j++) {
            // remove grants and clear visits completely
            customer.getGrantedWish().ifPresent(w -> {
                removeFromGrants(w, tempGrants);
            });
            customer.clearVisitsAndGrants();
        }
        customers.get(nextIndex).getGrantedWish().ifPresent(w -> {
            // Mark as visited but un-granted
            removeFromGrants(w, tempGrants);
            w.clearGrant();
        });
        return nextIndex;
    }

    private int getFirstCustomerIndexWithLastGrantedWish(List<CustomerWish> tempGrants, List<Customer> customers) {
        CustomerWish lastGranted = tempGrants.get(tempGrants.size() - 1);
        for (int i = 0; i < customers.size(); i++) {
            boolean hasSameWish = customers.get(i).getGrantedWish().filter(w -> w.isSame(lastGranted)).isPresent();
            if (hasSameWish) {
                return i;
            }
        }
        return 0;
    }

    private void sortCustomersAndWishes(List<Customer> customers) {
        // Sort by customers with least preferences
        customers.sort((a, b) -> Integer.valueOf(a.getWishes().size()).compareTo(Integer.valueOf(b.getWishes().size())));
        // Wishes with least cost come first
        customers.forEach(c -> c.getWishes()
                .sort((a, b) -> Integer.valueOf(a.getColorFinish().getCode())
                        .compareTo(Integer.valueOf(b.getColorFinish().getCode()))));
    }

    private boolean isSolved(List<Customer> customers) {
        return customers.stream().allMatch(c -> c.hasAGrantedWish());
    }

    private void removeFromGrants(CustomerWish wish, List<CustomerWish> tempGrant) {
        tempGrant.removeIf(w -> w.getColor() == wish.getColor() && w.getColorFinish().equals(wish.getColorFinish()));
    }

    private boolean isGrantable(CustomerWish wish, List<CustomerWish> tempGrant) {
        return tempGrant.stream()
                .noneMatch(w -> w.getColor() == wish.getColor() &&
                        !w.getColorFinish().equals(wish.getColorFinish()));
    }


}
