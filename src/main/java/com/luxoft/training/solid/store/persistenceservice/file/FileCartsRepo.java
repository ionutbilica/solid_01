package com.luxoft.training.solid.store.persistenceservice.file;

import com.luxoft.training.solid.store.idgen.IdGenerator;
import com.luxoft.training.solid.store.persistence.CartData;
import com.luxoft.training.solid.store.persistence.CartNotFoundException;
import com.luxoft.training.solid.store.persistence.CartsRepo;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class FileCartsRepo implements CartsRepo {

    private final IdGenerator idGenerator;
    private final File file;
    private final Map<Integer, SerializableCartData> carts;

    private static final File DEFAULT_FILE = new File("carts.repo");

    public FileCartsRepo(IdGenerator idGenerator) {
        this(idGenerator, DEFAULT_FILE);
    }

    public FileCartsRepo(IdGenerator idGenerator, File file) {
        this.idGenerator = idGenerator;
        this.file = file;
        carts = new FileBackedMap<>(DEFAULT_FILE);
    }

    @Override
    public int createNewCart() {
        int id = idGenerator.generateId();
        carts.put(id, new SerializableCartData(new CartData(id)));
        return id;
    }

    @Override
    public CartData getCartData(int cartId) {
        SerializableCartData cartData = carts.get(cartId);
        if (cartData == null) {
            throw new CartNotFoundException(cartId);
        }
        return cartData.asCartData();
    }

    @Override
    public void saveCart(CartData cartData) {
        carts.put(cartData.getId(), new SerializableCartData(cartData));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileCartsRepo that = (FileCartsRepo) o;
        return idGenerator.equals(that.idGenerator) && file.equals(that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idGenerator, carts);
    }

    @Override
    public String toString() {
        return "FileCartsRepo{" +
                "idGenerator=" + idGenerator +
                '}';
    }
}
