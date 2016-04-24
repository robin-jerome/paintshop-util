package com.paintshop.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CustomerWishTest {

    @Test
    public void testIsSame() throws Exception {
        CustomerWish wish1 = CustomerWish.makeCustomerWish(1, ColorFinish.GLOSSY);
        CustomerWish wish2 = CustomerWish.makeCustomerWish(1, ColorFinish.GLOSSY);
        assertTrue("Wishes for the same product are the same", wish1.isSame(wish2));
        wish1.visitAndGrant();
        // Granting the wish still keeps the wish as same
        assertTrue("A granted wish is same to an un granted wish for the same product", wish1.isSame(wish2));
        CustomerWish wish3 = CustomerWish.makeCustomerWish(2, ColorFinish.GLOSSY);
        assertTrue("Wishes for different colors are not the same", wish1.isSame(wish3));
        CustomerWish wish4 = CustomerWish.makeCustomerWish(1, ColorFinish.MATTE);
        assertTrue("Wishes for different color finishes are not the same", wish1.isSame(wish4));
    }
}