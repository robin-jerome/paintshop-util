package com.paintshop.model;

import lombok.Getter;

@Getter
public enum ColorFinish {
    MATTE(0),
    GLOSSY(1);

    private final int code;

    ColorFinish(int code) {
        this.code = code;
    }

    public static ColorFinish of(int value) {
        switch (value) {
            case 0:
                return MATTE;
            case 1:
                return GLOSSY;
            default:
                throw new IllegalArgumentException("Invalid value of Color Finish");
        }
    }
}
