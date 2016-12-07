package com.luxoft.training.solid.store.persistence;

public interface CartsRepo {
    int createNewCart();

    CartData getCartData(int cartId);

    void saveCart(CartData cart);
}
