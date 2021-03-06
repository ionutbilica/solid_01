package com.luxoft.training.solid.store;

import com.luxoft.training.solid.store.accounting.Accounting;
import com.luxoft.training.solid.store.discount.DiscountsRepo;
import com.luxoft.training.solid.store.persistence.CartsRepo;
import com.luxoft.training.solid.store.persistence.Stock;
import com.luxoft.training.solid.store.receipt.Receipt;
import com.luxoft.training.solid.store.receipt.ReceiptFactory;

public class Store implements Sales {

    private final Stock stock;
    private final DiscountsRepo discountsRepo;
    private final CartsRepo cartsRepo;
    private final ReceiptFactory receiptFactory;
    private final Accounting accounting;

    public Store(Stock stock, DiscountsRepo discountsRepo, CartsRepo cartsRepo, ReceiptFactory receiptFactory, Accounting accounting) {
        this.stock = stock;
        this.discountsRepo = discountsRepo;
        this.cartsRepo = cartsRepo;
        this.receiptFactory = receiptFactory;
        this.accounting = accounting;
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
        Cart cart = Cart.load(cartId, cartsRepo, discountsRepo);
        Product product = new Product(stock.takeProduct(name, count), discountsRepo.getDiscount(name));
        cart.addProduct(product);
    }

    @Override
    public double getCartTotal(int cartId) {
        Cart cart = Cart.load(cartId, cartsRepo, discountsRepo);
        return cart.getTotalPrice();
    }

    @Override
    public void addDeliveryToCart(int cartId) {
        Cart cart = Cart.load(cartId, cartsRepo, discountsRepo);
        cart.addDelivery();
    }

    @Override
    public String pay(int cartId, String paymentMethod, String receiptFormat) {
        Cart cart = Cart.load(cartId, cartsRepo, discountsRepo);
        double moneyFromTheClient = cart.getTotalPrice();
        accounting.receivePayment(moneyFromTheClient, paymentMethod);
        Receipt receipt = receiptFactory.createReceipt(receiptFormat);
        return cart.fillReceipt(receipt);
    }

    @Override
    public String getAccountingReport() {
        return accounting.getReport();
    }
}
