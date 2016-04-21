package com.paintshop.model;

import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class Product {
    // Color of product is final
    private final int color;
    @Setter
    private ColorFinish colorFinish = ColorFinish.GLOSSY;

    public Product(int color) {
        this.color = color;
    }
}
