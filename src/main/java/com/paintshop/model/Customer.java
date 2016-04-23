package com.paintshop.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class Customer {
    private static int objectCounter = 0;
    // Customer Id is final
    private final int id;
    private List<CustomerWish> wishes = new ArrayList<>();

    private Customer(List<CustomerWish> wishes) {
        Objects.requireNonNull(wishes);
        this.id = objectCounter++;
        this.wishes = wishes;
    }

    public static Customer makeCustomer(List<CustomerWish> wishes) {
        return new Customer(wishes);
    }

    public Optional<CustomerWish> getFirstUnVisitedWish() {
        return wishes.stream().filter(CustomerWish::isUnVisited).findFirst();
    }

    public List<CustomerWish> getUnVisitedWishes() {
        return wishes.stream().filter(CustomerWish::isUnVisited).collect(Collectors.toList());
    }

    public Optional<CustomerWish> getGrantedWish() {
        return wishes.stream().filter(CustomerWish::isGranted).findFirst();
    }

    public boolean hasAGrantedWish() {
        return getGrantedWish().isPresent();
    }

    public void clearVisitsAndGrants() {
        wishes.forEach(c -> {
            c.unVisit();
            c.clearGrant();
        });
    }
}
