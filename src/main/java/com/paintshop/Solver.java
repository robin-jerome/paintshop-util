package com.paintshop;

import com.paintshop.file.InputReader;
import com.paintshop.model.Customer;
import com.paintshop.model.CustomerWish;
import com.paintshop.model.Product;
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
        List<Product> products = testCase.getProducts();
        // Sort by customers with least preferences
        customers.sort((a, b) -> Integer.valueOf(a.getWishes().size()).compareTo(Integer.valueOf(b.getWishes().size())));
        Map<Integer, Set<CustomerWish>> solutions = new HashMap<>();
        Set<CustomerWish> tempGrants = new HashSet<>();
        int solutionIndex = 0;
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            Optional<CustomerWish> wishOpt = customer.getFirstUnVisitedWish();
            if (wishOpt.isPresent()) {
                if (isGrantable(wishOpt.get(), tempGrants)) {
                    wishOpt.get().visitAndGrant();
                    tempGrants.add(wishOpt.get());
                    if (isSolved(customers)) {
                        System.out.printf("Houston we have landed on a solution");

                    } else {
                        

                    }
                } else {
                    wishOpt.get().visit();
                }



                /**
                 * If a complete solution is formed
                 *      Add solution to the map of solutions
                 *
                 *      Reduce the value of index by 1
                 *      Un-grant the wish of the current customer + remove entry from the set
                 *      Mark all entries of the current customer as unvisited
                 *      Mark the last granted wish of the customer as un-granted but visited and continue the loop
                 *
                 *
                 *      Mark all the wishes or customers after current one as un-visited and un-granted
                 */
            } else {
                /**
                 * If index is more than zero
                 *      Reduce the value of index by 1
                 *      Un-grant the wish of the current customer + remove entry from the set
                 *      Mark all entries of the current customer as unvisited
                 *      Mark the last granted wish of the previous customer as un-granted but visited and continue the loop
                 *
                 * If index is zero
                 *      Check if all wishes are granted and return solution map
                 */
                if (i > 0) {
                    // Not reached the top of the grid yet
                    CustomerWish wish = customer.getGrantedWish().get();
                    clearGrantAndRemoveFromGrants(wish, tempGrants);
                    clearVisitsOfCustomerWishes(customer);
                    // Mark the last granted wish of the previous customer as un-granted but visited and continue the loop
                    Customer previous = customers.get(i - 1);
                    previous.getGrantedWish().ifPresent(CustomerWish::clearGrant);
                    System.out.println("We are going back up the grid");
                    i--;
                } else {
                    // Reached top of the grid - return the solutions map
                    return solutions;
                }
            }
        }

        //TODO: Execution should not come here - Add assertion
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
