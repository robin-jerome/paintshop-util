package com.paintshop;

import com.google.common.collect.ImmutableList;
import com.paintshop.model.ColorFinish;
import com.paintshop.model.Customer;
import com.paintshop.model.CustomerWish;
import com.paintshop.model.Product;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CatalogBuilderTest {

    private static final CatalogBuilder BUILDER = new CatalogBuilder();

    @Test
    public void testBuildCatalog() throws Exception {

    }

    @Test
    public void testChangeColorFinishOfProduct() throws Exception {
        Product product = new Product(1, ColorFinish.GLOSSY);
        BUILDER.changeColorFinishOfProduct(product);
        assertEquals("Color Finish of the product is not changed", ColorFinish.MATTE, product.getColorFinish());
        BUILDER.changeColorFinishOfProduct(product);
        assertEquals("Color Finish of the product is not changed", ColorFinish.GLOSSY, product.getColorFinish());
    }

    @Test
    public void testIsCustomerSatisfied() throws Exception {
        List<Product> catalog = ImmutableList.of(
                new Product(1, ColorFinish.GLOSSY),
                new Product(2, ColorFinish.GLOSSY),
                new Product(3, ColorFinish.MATTE)
        );
        Customer customer = Customer.makeCustomer(ImmutableList.of(CustomerWish.makeCustomerWish(1, ColorFinish.GLOSSY)));
        assertTrue("Customer is not satisfied with the catalog", BUILDER.isCustomerSatisfied(catalog, customer));
        customer = Customer.makeCustomer(ImmutableList.of(CustomerWish.makeCustomerWish(1, ColorFinish.MATTE)));
        assertFalse("Customer is satisfied with the catalog", BUILDER.isCustomerSatisfied(catalog, customer));
        customer = Customer.makeCustomer(ImmutableList.of(CustomerWish.makeCustomerWish(4, ColorFinish.MATTE)));
        assertFalse("Customer is satisfied with the catalog", BUILDER.isCustomerSatisfied(catalog, customer));
    }
}