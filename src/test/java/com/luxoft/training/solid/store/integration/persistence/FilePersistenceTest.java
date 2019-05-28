package com.luxoft.training.solid.store.integration.persistence;

import com.luxoft.training.solid.store.Cart;
import com.luxoft.training.solid.store.Product;
import com.luxoft.training.solid.store.discount.NoDiscount;
import com.luxoft.training.solid.store.discount.NoDiscountsRepo;
import com.luxoft.training.solid.store.idgen.MockIdGenerator;
import com.luxoft.training.solid.store.persistence.CartsRepo;
import com.luxoft.training.solid.store.persistence.NotEnoughInStockException;
import com.luxoft.training.solid.store.persistence.ProductNotFoundException;
import com.luxoft.training.solid.store.persistenceservice.file.FileCartsRepo;
import com.luxoft.training.solid.store.persistenceservice.file.FileProductsRepo;
import com.luxoft.training.solid.store.provisioning.ProductsRepo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FilePersistenceTest {

    @After
    public void clean() {
        new File("products.repo").delete();
        new File("carts.repo").delete();
    }

    @Test
    public void testOneProduct() throws Exception {
        ProductsRepo productsRepo = new FileProductsRepo();
        CartsRepo cartsRepo = new FileCartsRepo(new MockIdGenerator(1));
        NoDiscountsRepo noDiscountsRepo = new NoDiscountsRepo();

        productsRepo.addProduct("wine", 100, 25);
        Product wineInCart = new Product(productsRepo.takeProduct("wine", 10), new NoDiscount());

        int newCartId = cartsRepo.createNewCart();

        Cart cartSaved = Cart.load(newCartId, cartsRepo, noDiscountsRepo);
        cartSaved.addDelivery();
        cartSaved.addProduct(wineInCart);

        CartsRepo cartsRepoNewInst = new FileCartsRepo(new MockIdGenerator(1));
        Cart cartLoaded = Cart.load(newCartId, cartsRepoNewInst, noDiscountsRepo);

        Assert.assertEquals(cartSaved, cartLoaded);
    }

    @Test(expected = NotEnoughInStockException.class)
    public void testNotEnoughInStock() throws Exception {
        ProductsRepo productsRepo = new FileProductsRepo();

        productsRepo.addProduct("wine", 100, 25);
        productsRepo.takeProduct("wine", 200);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testProductNotFound() throws Exception {
        ProductsRepo productsRepo = new FileProductsRepo();

        productsRepo.addProduct("wine", 100, 25);
        productsRepo.takeProduct("bread", 200);
    }
}
