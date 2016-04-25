package com.paintshop;

import com.google.common.collect.ImmutableList;
import com.paintshop.file.InputReader;
import com.paintshop.model.*;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CatalogBuilderTest {
    private static final CatalogBuilder BUILDER = new CatalogBuilder();
    private static final String TEST_RESOURCES_FILE_LOC = "src" + File.separator + "test" + File.separator + "resources" + File.separator;

    @Test
    public void testBuildCatalogForValidSolution() throws Exception {
        TestCase tc = new InputReader(TEST_RESOURCES_FILE_LOC + "input_with_soln.txt").getTestCases().get(0);
        Optional<List<Product>> catalogOpt = BUILDER.buildCatalog(tc);
        assertTrue("There should be a valid product catalog", catalogOpt.isPresent());
        String catalogRepresentation = catalogOpt.get().stream()
                .map(p -> "" + p.getColorFinish().getCode()).collect(Collectors.joining(" "));
        assertEquals("Catalog representation is incorrect", "1 0 0 0 0", catalogRepresentation);
    }

    @Test
    public void testBuildCatalogForNonExistingSolution() throws Exception {
        TestCase tc = new InputReader(TEST_RESOURCES_FILE_LOC + "input_with_no_soln.txt").getTestCases().get(0);
        Optional<List<Product>> catalogOpt = BUILDER.buildCatalog(tc);
        assertFalse("There should be no product catalog", catalogOpt.isPresent());
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