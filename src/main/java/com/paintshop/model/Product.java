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

    @Override
    public String toString() {
        return color + " " + colorFinish.getCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (color != product.color) return false;
        return colorFinish == product.colorFinish;

    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + colorFinish.hashCode();
        return result;
    }
}
