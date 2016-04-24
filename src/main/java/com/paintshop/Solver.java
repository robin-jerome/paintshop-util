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
                System.out.println("Solving testcase " + j);
                TestCase tc = testCases.get(i);
                Map<Integer, List<CustomerWish>> solutions = solver.solve(tc);
                System.out.println("Test case has " + solutions.keySet().size() + " solution(s)");
                String result = "Case #" + j + ": ";
                if (solutions.isEmpty()) {
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

    private Map<Integer, List<CustomerWish>> solve(TestCase testCase) {
        List<Customer> customers = new ArrayList<>(testCase.getCustomers());
        // Sort for optimal computing
        sortCustomersAndWishes(customers);
        Map<Integer, List<CustomerWish>> solutions = new HashMap<>();
        List<CustomerWish> wishGrants = new ArrayList<>();
        int solutionIndex = 0;
        // Iterate through all customers
        for (int i = 0; i < customers.size(); i++) { // Begin FOR LOOP for customers
            boolean wishGranted = false;
            // Iterate through unvisited wishes of a customer
            for (CustomerWish wish : customers.get(i).getUnVisitedWishes()) { // Begin FOR LOOP for customer wishes
                if (isGrantable(wish, wishGrants)) {
                    wish.visitAndGrant();
                    wishGranted = true;
                    wishGrants.add(wish);
                    System.out.println("Granting wish " + wish.productAsString() + " for customer " + i);
                    if (isSolved(customers)) {
                        solutions.put(solutionIndex++, new ArrayList<>(wishGrants));
                        System.out.println("Found solution number " + solutionIndex);
                        if (i > 0) {
                            // Remove the recently added wish from the list
                            removeFromGrants(wish, wishGrants);
                            int nextIndex = performResetActionsAndGetNextIndex(customers, wishGrants);
                            // System.out.println("Moving to index " + nextIndex);
                            i = nextIndex - 1;
                            break;
                        } else {
                            return solutions;
                        }
                    } else {
                        // Move to next customer
                        break;
                    }
                } else {
                    wish.visit();
                }
            } // End FOR LOOP for customer wishes

            if (!wishGranted) {
                // Reset previous successful wish grant and move back to that customer's wishes
                if (i > 0) {
                    int nextIndex = performResetActionsAndGetNextIndex(customers, wishGrants);
                    // System.out.println("Moving to index " + nextIndex);
                    i = nextIndex - 1;
                } else {
                    // Reached top of the grid - return the solutions map
                    return solutions;
                }
            }
        } // End FOR LOOP for customer wishes
        return solutions;
    }

    /**
     * This method resets visits and grants of all customers who come after the next index of iteration
     * For the nextIndex, the current granted wish is made as un granted but visited
     * @param customers List of customers
     * @param wishGrants List of granted wishes
     * @return the next index for the iteration
     */
    private int performResetActionsAndGetNextIndex(List<Customer> customers, List<CustomerWish> wishGrants) {
        int nextIndex = getFirstCustomerIndexWithLastGrantedWish(wishGrants, customers);
        // Reset all customer wishes from next index to last
        for (int i = nextIndex + 1; i < customers.size(); i++) {
            // remove grants and clear visits completely
            customers.get(i).getGrantedWish().ifPresent(w -> {
                removeFromGrants(w, wishGrants);
            });
            customers.get(i).clearVisitsAndGrants();
        }
        customers.get(nextIndex).getGrantedWish().ifPresent(w -> {
            // Mark as visited but un-granted
            removeFromGrants(w, wishGrants);
            w.clearGrant();
            System.out.println("Un grant wish " + w.productAsString() + " for customer " + nextIndex);
        });
        return nextIndex;
    }

    /**
     * This method returns the index of the first customer which has been granted the last granted wish
     * @param wishGrants List of granted wishes
     * @param customers List of customers
     * @return int index of the customer
     */
    private int getFirstCustomerIndexWithLastGrantedWish(List<CustomerWish> wishGrants, List<Customer> customers) {
        if (wishGrants.size() > 0) {
            CustomerWish lastGranted = wishGrants.get(wishGrants.size() - 1);
            for (int i = 0; i < customers.size(); i++) {
                Optional<CustomerWish> cwOpt = customers.get(i).getGrantedWish();
                if (cwOpt.isPresent() && cwOpt.get().isSame(lastGranted)) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * This method sorts
     *
     * The customers such that
     * 1. Customers with least number of wishes are moved to the front
     * 2. If the number of wishes are the same, customers with more cost of solution are moved to the front
     *
     * The customer wishes such that
     * 1. Glossy finishes are before matte finishes
     * 2. Wishes are sorted by their color if their color finish is the same
     * @param customers The list of customers to sort
     */
    private void sortCustomersAndWishes(List<Customer> customers) {
        // Sort by customers with least preferences
        customers.sort((a, b) -> {
            if (a.getWishes().size() != b.getWishes().size()) {
                return Integer.valueOf(a.getWishes().size()).compareTo(Integer.valueOf(b.getWishes().size()));
            } else {
                return costOfSolution(wishesToProducts(b.getWishes())).compareTo(costOfSolution(wishesToProducts(a.getWishes())));
            }
        });
        // Wishes with least cost come first
        customers.forEach(c -> c.getWishes()
                .sort((a, b) ->  {
                    if (a.getColorFinish().equals(b.getColorFinish())) {
                        return Integer.valueOf(a.getColor()).compareTo(Integer.valueOf(b.getColor()));
                    } else {
                        return Integer.valueOf(a.getColorFinish().getCode())
                                .compareTo(Integer.valueOf(b.getColorFinish().getCode()));
                    }
                }));
    }

    /**
     * This method checks to see if all the customers have at least one granted wish.
     * @param customers The list of customers
     * @return boolean If the all the customers are satisfied with the current products
     */
    private boolean isSolved(List<Customer> customers) {
        return customers.stream().allMatch(c -> c.hasAGrantedWish());
    }

    /**
     * This method removed the specified wish from the list of wishes granted
     * @param wish The wish which needs to be removed from the list of granted wishes
     * @param wishGrants
     */
    private void removeFromGrants(CustomerWish wish, List<CustomerWish> wishGrants) {
        wishGrants.removeIf(w -> w.isSame(wish));
    }

    /**
     * This method checks to find out if the current list of granted wishes contains a wish for the same color but in a
     * different finish. If this is the case, the wish is not granted, otherwise the wish is granted
     * @param wish The wish that needs to be checked
     * @param wishGrants The list of wishes that have been granted already
     * @return boolean
     */
    private boolean isGrantable(CustomerWish wish, List<CustomerWish> wishGrants) {
        return wishGrants.stream()
                .noneMatch(w -> w.getColor() == wish.getColor() &&
                        !w.getColorFinish().equals(wish.getColorFinish()));
    }

    /**
     * This method prepared the product catalog by adding items that were not requested by the customer prepared with
     * the cheapest possible cost
     * @param solutions List of solutions obtained from the solve method
     * @param products List of all the possible colors in GLOSSY finish
     * @return Set of product catalog
     */
    private Set<Product> makeProductCatalog(Map<Integer, List<CustomerWish>> solutions, List<Product> products) {
        // TreeSet to make colors ordered by their color code
        Set<Product> catalog = new TreeSet<>((o1, o2) -> Integer.valueOf(o1.getColor())
                .compareTo(Integer.valueOf(o2.getColor())));
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


    /**
     * This method finds the cheapest solution from a list of solutions by using the costOfSolution method
     * @param solutions List of solutions
     * @return Set of products that form the cheapest solution
     */
    private Set<Product> findCheapestSolution(Map<Integer, List<CustomerWish>> solutions) {
        return solutions.values().stream()
                .map(x -> new HashSet<>(wishesToProducts(x)))
                .sorted((a, b) -> costOfSolution(a).compareTo(costOfSolution(b)))
                .findFirst().get();
    }

    /**
     * This method converts the list of customer wishes to the corresponding list of products
     * @param customerWishes List of customer wishes
     * @return  List of products
     */
    private List<Product> wishesToProducts(List<CustomerWish> customerWishes) {
        return customerWishes.stream().map(CustomerWish::getProduct).collect(Collectors.toList());
    }

    /**
     * This method gives a metric for relative cost of preparation of products considering the logic that MATTE finish
     * is costlier than glossy
     * @param products List of products
     * @return A representation of relative cost of preparation of products
     */
    private Integer costOfSolution(Collection<Product> products) {
        return products.stream().map(p -> p.getColorFinish().getCode()).
                reduce(0, Integer::sum);
    }
}
