package com.paintshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class CustomerWish {
    // The wish for a product is final
    private final Product product;
    private boolean granted;
    private boolean visited;

    private CustomerWish(Product product) {
        this(product, false, false);
        Objects.requireNonNull(product);
    }

    public static CustomerWish makeCustomerWish(int colorCode, ColorFinish finish) {
        return new CustomerWish(new Product(colorCode, finish));
    }

    public int getColor() {
        return this.getProduct().getColor();
    }

    public ColorFinish getColorFinish() {
        return this.getProduct().getColorFinish();
    }

    public boolean isUnVisited() {
        return !isVisited();
    }

    public void unVisit() {
        this.visited = false;
    }

    public String productAsString() {
        return product.toString();
    }

    public void clearGrant() {
        this.granted = false;
    }

    public void visitAndGrant() {
        this.visited = true;
        this.granted = true;
    }

    public boolean isSame(CustomerWish cw) {
        return cw.getProduct().equals(product);
    }

    public void visit() {
        this.visited = true;
    }
}
