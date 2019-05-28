package com.luxoft.training.solid.store.integration.persistence;

import com.luxoft.training.solid.store.Cart;
import com.luxoft.training.solid.store.Product;
import com.luxoft.training.solid.store.discount.NoDiscount;
import com.luxoft.training.solid.store.discount.NoDiscountsRepo;
import com.luxoft.training.solid.store.persistence.NotEnoughInStockException;
import com.luxoft.training.solid.store.persistence.ProductNotFoundException;
import com.luxoft.training.solid.store.persistenceservice.h2.H2CartsRepo;
import com.luxoft.training.solid.store.persistenceservice.h2.H2Connection;
import com.luxoft.training.solid.store.persistenceservice.h2.H2ProductsRepo;
import org.junit.Assert;
import org.junit.Test;

public class H2PersistenceTest {

    @Test
    public void testOneProduct() throws Exception {
        try (H2Connection connection = new H2Connection()) {
            H2ProductsRepo productsRepo = new H2ProductsRepo(connection);
            H2CartsRepo cartsRepo = new H2CartsRepo(connection);
            NoDiscountsRepo noDiscountsRepo = new NoDiscountsRepo();

            productsRepo.addProduct("wine", 100, 25);
            Product wineInCart = new Product(productsRepo.takeProduct("wine", 10), new NoDiscount());

            int newCartId = cartsRepo.createNewCart();
            Cart cartSaved = Cart.load(newCartId, cartsRepo, noDiscountsRepo);
            cartSaved.addDelivery();
            cartSaved.addProduct(wineInCart);

            H2CartsRepo cartsRepoNewInst = new H2CartsRepo(connection);
            Cart cartLoaded = Cart.load(newCartId, cartsRepoNewInst, noDiscountsRepo);

            Assert.assertEquals(cartSaved, cartLoaded);
        }
    }

    @Test(expected = NotEnoughInStockException.class)
    public void testNotEnoughInStock() throws Exception {
        try (H2Connection connection = new H2Connection()) {
            H2ProductsRepo productsRepo = new H2ProductsRepo(connection);

            productsRepo.addProduct("wine", 100, 25);
            productsRepo.takeProduct("wine", 200);
        }
    }

    @Test(expected = ProductNotFoundException.class)
    public void testProductNotFound() throws Exception {
        try (H2Connection connection = new H2Connection()) {
            H2ProductsRepo productsRepo = new H2ProductsRepo(connection);

            productsRepo.addProduct("wine", 100, 25);
            productsRepo.takeProduct("bread", 200);
        }
    }
}
