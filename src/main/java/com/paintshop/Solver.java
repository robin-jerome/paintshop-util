package com.paintshop;

import com.paintshop.file.InputReader;
import com.paintshop.model.Customer;
import com.paintshop.model.CustomerWish;
import com.paintshop.model.TestCase;

import java.io.IOException;
import java.util.*;

public class Solver {

    public static void main(String[] args) {
        try {
            Solver solver = new Solver();
            new InputReader("input.txt").getTestCases().forEach(t -> solver.solve(t));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, Set<CustomerWish>> solve(TestCase testCase) {
        List<Customer> customers = new ArrayList<>(testCase.getCustomers());
        // Sort by customers with least preferences
        customers.sort((a, b) -> Integer.valueOf(a.getWishes().size()).compareTo(Integer.valueOf(b.getWishes().size())));
        Map<Integer, Set<CustomerWish>> solutions = new HashMap<>();
        Set<CustomerWish> tempGrants = new HashSet<>();
        int solutionIndex = 0;
        // Iterate through all customers
        for (int i = 0; i < customers.size(); i++) { // Begin FOR LOOP for customers
            Customer customer = customers.get(i);
            boolean wishGranted = false;
            // Iterate through unvisited wishes of a customer
            for (CustomerWish wish : customer.getUnVisitedWishes()) { // Begin FOR LOOP for customer wishes
                if (isGrantable(wish, tempGrants)) {
                    System.out.println("Granting a wish");
                    wish.visitAndGrant();
                    wishGranted = true;
                    tempGrants.add(wish);
                    /**
                     * If a complete solution is formed
                     *      Add solution to the map of solutions
                     *      Remove the granted wish of the current customer from the set
                     *      Mark the last granted wish of the previous customer as un-granted but visited and continue the loop
                     *      Mark all the wishes or customers from current one to the last as un-visited and un-granted
                     *      Reduce the value of index by 1
                     */
                    if (isSolved(customers)) {
                        System.out.printf("Houston we have landed on a solution");
                        solutions.put(solutionIndex, new HashSet<>(tempGrants));
                        solutionIndex++;
                        if (i > 0) {
                            System.out.println("We are going back up the grid to find more solution");
                            // TODO : Fix logic here - When going up, don't remove a grant which was granted for an earlier customer
                            clearGrantAndRemoveFromGrants(wish, tempGrants);
                            for (int j = i; j < customers.size(); j++) {
                                customer.clearVisitsAndGrants();
                            }
                            // Reduce index to move up the grid
                            i--;
                        } else {
                            // TODO: re-check this
                            return solutions;
                        }
                    }
                } else {
                    System.out.println("Visited a wish");
                    wish.visit();
                }
            } // End FOR LOOP for customer wishes

            if (!wishGranted) {
                // Reset previous successful wish grant and move back to that customer's wishes
                if (i > 0) {
                    // Not reached the top of the grid yet
                    CustomerWish wish = customer.getGrantedWish().get();
                    clearGrantAndRemoveFromGrants(wish, tempGrants);
                    for (int j = i; j < customers.size(); j++) {
                        customer.clearVisitsAndGrants();
                    }
                    // Mark the last granted wish of the previous customer as un-granted but visited and continue the loop
                    // TODO : Fix logic here - When going up, don't remove a grant which was granted for an earlier customer as well as the current customer
                    Customer previous = customers.get(i - 1);
                    previous.getGrantedWish().ifPresent(CustomerWish::clearGrant);
                    System.out.println("We are going back up the grid");
                    i--;
                } else {
                    // Reached top of the grid - return the solutions map
                    return solutions;
                }
            }
        } // End FOR LOOP for customer wishes

        //TODO: Execution should not come here - Add assertion
        System.out.println("In here");
        return solutions;
    }

    private boolean isSolved(List<Customer> customers) {
        return customers.stream().allMatch(c -> c.hasAGrantedWish());
    }

    private void clearVisitsOfCustomerWishes(Customer customer) {
        customer.getWishes().forEach(CustomerWish::unVisit);
    }

    private void clearGrantAndRemoveFromGrants(CustomerWish wish, Set<CustomerWish> tempGrant) {
        wish.clearGrant();
        tempGrant.removeIf(w -> w.getColor() == wish.getColor() && w.getColorFinish().equals(wish.getColorFinish()));
    }

    private boolean isGrantable(CustomerWish wish, Set<CustomerWish> tempGrant) {
        return tempGrant.stream()
                .anyMatch(w -> w.getColor() == wish.getColor() &&
                        !w.getColorFinish().equals(wish.getColorFinish()));
    }


}
