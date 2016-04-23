package com.paintshop;

import com.paintshop.file.InputReader;
import com.paintshop.model.Customer;
import com.paintshop.model.CustomerWish;
import com.paintshop.model.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solver {

    public static void main(String[] args) {
        try {
            Solver solver = new Solver();
            new InputReader("input.txt").getTestCases().forEach(t -> solver.solve(t));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, List<CustomerWish>> solve(TestCase testCase) {
        List<Customer> customers = new ArrayList<>(testCase.getCustomers());
        // Sort by customers with least preferences
        customers.sort((a, b) -> Integer.valueOf(a.getWishes().size()).compareTo(Integer.valueOf(b.getWishes().size())));
        // Wishes with least cost come first
        customers.forEach(c -> c.getWishes()
                .sort((a, b) -> Integer.valueOf(a.getColorFinish().getCode())
                        .compareTo(Integer.valueOf(b.getColorFinish().getCode()))));

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
                    /**
                     * If a complete solution is formed
                     *      Add solution to the map of solutions
                     *      Remove the granted wish of the current customer from the set
                     *      Mark the last granted wish of the previous customer as un-granted but visited and continue the loop
                     *      Mark all the wishes or customers from current one to the last as un-visited and un-granted
                     *      Reduce the value of index by 1
                     */
                    if (isSolved(customers)) {
                        System.out.println("Founded solution number " + solutionIndex + 1);
                        solutions.put(solutionIndex, new ArrayList<>(tempGrants));
                        solutionIndex++;
                        if (i > 0) {
                            // TODO : Fix logic here - When going up, don't remove a grant which was granted for an earlier customer
                            removeFromGrants(wish, tempGrants);
                            for (int j = i; j < customers.size(); j++) {
                                customer.clearVisitsAndGrants();
                            }
                            customers.get(i - 1).getGrantedWish().ifPresent(CustomerWish::clearGrant);
                            // Reduce index to move up the grid
                            i--;
                            break;
                        } else {
                            // TODO: re-check this
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
                    System.out.println("Reached here --- do something about it");
//                    // Not reached the top of the grid yet
//                    CustomerWish wish = customer.getGrantedWish().get();
//                    removeFromGrants(wish, tempGrants);
//                    for (int j = i; j < customers.size(); j++) {
//                        customer.clearVisitsAndGrants();
//                    }
//                    // Mark the last granted wish of the previous customer as un-granted but visited and continue the loop
//                    // TODO : Fix logic here - When going up, don't remove a grant which was granted for an earlier customer as well as the current customer
//                    Customer previous = customers.get(i - 1);
//                    previous.getGrantedWish().ifPresent(CustomerWish::clearGrant);
//                    System.out.println("We are going back up the grid");
//                    // i--;
                } else {
                    // Reached top of the grid - return the solutions map
                    return solutions;
                }
            }
        } // End FOR LOOP for customer wishes

        //TODO: Execution should not come here - Add assertion
        return solutions;
    }

    private boolean isSolved(List<Customer> customers) {
        return customers.stream().allMatch(c -> c.hasAGrantedWish());
    }

    private void clearVisitsOfCustomerWishes(Customer customer) {
        customer.getWishes().forEach(CustomerWish::unVisit);
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
