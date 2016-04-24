package com.paintshop.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorFinishTest {

    @Test
    public void colorFinishCodeToEnum() throws Exception {
        assertEquals("Code to color finish enum mapping is incorrect", ColorFinish.GLOSSY, ColorFinish.of(0));
        assertEquals("Code to color finish enum mapping is incorrect", ColorFinish.MATTE, ColorFinish.of(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void enumOfIllegalCodeThrowsException() throws Exception {
        ColorFinish.of(0);
    }

}