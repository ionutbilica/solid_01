package com.luxoft.training.solid.store;

import com.luxoft.training.solid.store.persistence.CartsRepo;
import com.luxoft.training.solid.store.persistence.Stock;
import com.luxoft.training.solid.store.receipt.Receipt;
import com.luxoft.training.solid.store.receipt.ReceiptFactory;

public class Store implements Sales {

    private final Stock stock;
    private final CartsRepo cartsRepo;
    private final ReceiptFactory receiptFactory;
    private double cash;

    public Store(Stock stock, CartsRepo cartsRepo, ReceiptFactory receiptFactory) {
        this.stock = stock;
        this.cartsRepo = cartsRepo;
        this.receiptFactory = receiptFactory;
        cash = 0;
    }

    @Override
    public int createNewCart() {
        return cartsRepo.createNewCart();
    }

    @Override
    public void addProductToCart(String name, int cartId) {
        addProductToCart(name, 1, cartId);
    }

    @Override
    public void addProductToCart(String name, int count, int cartId) {
        Cart cart = Cart.load(cartId, cartsRepo);
        Product product = new Product(stock.takeProduct(name, count));
        cart.addProduct(product);
    }

    @Override
    public double getCartTotal(int cartId) {
        Cart cart = Cart.load(cartId, cartsRepo);
        return cart.getTotalPrice();
    }

    @Override
    public void addDeliveryToCart(int cartId) {
        Cart cart = Cart.load(cartId, cartsRepo);
        cart.addDelivery();
    }

    @Override
    public String pay(int cartId, String receiptFormat) {
        Cart cart = Cart.load(cartId, cartsRepo);
        double moneyFromTheClient = cart.getTotalPrice();
        cash += moneyFromTheClient;
        Receipt receipt = receiptFactory.createReceipt(receiptFormat);
        return cart.fillReceipt(receipt);
    }

    @Override
    public double getCashAmount() {
        return cash;
    }

}
