package com.paintshop.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CustomerWishTest {

    @Test
    public void testIsSame() throws Exception {
        CustomerWish wish1 = CustomerWish.makeCustomerWish(1, ColorFinish.GLOSSY);
        CustomerWish wish2 = CustomerWish.makeCustomerWish(1, ColorFinish.GLOSSY);
        assertTrue("Wishes for the same product are the same", wish1.isSame(wish2));
    }
}