package com.luxoft.training.solid.store;

import com.luxoft.training.solid.store.persistence.CartData;
import com.luxoft.training.solid.store.persistence.CartsRepo;
import com.luxoft.training.solid.store.persistence.ProductData;
import com.luxoft.training.solid.store.receipt.Receipt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cart {

    public static final double DELIVERY_COST = 12;

    private final int id;
    private final CartsRepo cartsRepo;
    private List<Product> products;
    private boolean hasDelivery;

    public static Cart load(int id, CartsRepo cartsRepo) {
        CartData data = cartsRepo.getCartData(id);
        Cart cart = new Cart(data.getId(), cartsRepo);
        if (data.hasDelivery()) {
            cart.addDelivery();
        }
        for (ProductData pd : data.getProducts()) {
            cart.addProduct(new Product(pd));
        }
        return cart;
    }

    private Cart(int id, CartsRepo cartsRepo) {
        this.id = id;
        this.cartsRepo = cartsRepo;
        hasDelivery = false;
        products = new ArrayList<>();
    }

    public void addProduct(Product product) {
        products.add(product);
        saveToRepo();
    }

    public void addDelivery() {
        this.hasDelivery = true;
        saveToRepo();
    }

    public double getTotalPrice() {
        double productsTotal = 0;
        for (Product p : products) {
            productsTotal += p.getFullPriceForAll();
        }
        double deliveryCost = hasDelivery ? DELIVERY_COST : 0;
        return productsTotal + deliveryCost;
    }

    public String fillReceipt(Receipt receipt) {
        for (Product p : products) {
            p.fillReceipt(receipt);
        }
        if (hasDelivery) {
            receipt.addDelivery(DELIVERY_COST);
        }
        receipt.setTotalPrice(getTotalPrice());
        return receipt.toString();
    }

    private void saveToRepo() {
        List<ProductData> pd = new ArrayList<>();
        for (Product p : products) {
            pd.add(p.getData());
        }
        CartData cartData = new CartData(id, pd, hasDelivery);
        cartsRepo.saveCart(cartData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return id == cart.id &&
                cartsRepo.equals(cart.cartsRepo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cartsRepo);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", cartsRepo=" + cartsRepo +
                ", products=" + products +
                ", hasDelivery=" + hasDelivery +
                '}';
    }
}
