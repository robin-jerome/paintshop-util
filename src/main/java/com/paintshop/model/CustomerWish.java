package com.paintshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class CustomerWish {
    // The wish for a product is final
    private final Product product;
    @Setter
    private boolean met;

    private CustomerWish(Product product) {
        this(product, false);
        Objects.requireNonNull(product);
    }

    public static CustomerWish makeCustomerWish(int colorCode, ColorFinish finish) {
        return new CustomerWish(new Product(colorCode, finish));
    }
}
