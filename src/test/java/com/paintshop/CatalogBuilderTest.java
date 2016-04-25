package com.paintshop;

import com.paintshop.model.ColorFinish;
import com.paintshop.model.Product;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

    }
}