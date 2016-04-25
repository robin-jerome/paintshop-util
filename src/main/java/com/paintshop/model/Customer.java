package com.paintshop.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
}
