package com.paintshop.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class CustomerWish {
    // The wish for a product is final
    private final Product product;

    private CustomerWish(Product product) {
        this.product = product;
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

    public boolean isSame(CustomerWish cw) {
        return cw.getProduct().equals(product);
    }
}


