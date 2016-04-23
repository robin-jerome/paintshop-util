package com.paintshop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Product {
    // Color of product is final
    private final int color;
    @Setter
    private ColorFinish colorFinish = ColorFinish.GLOSSY;

    public Product(int color) {
        this.color = color;
    }
}
