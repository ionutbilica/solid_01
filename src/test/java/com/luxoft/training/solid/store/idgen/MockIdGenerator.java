package com.luxoft.training.solid.store.idgen;

import java.util.Objects;

public class MockIdGenerator implements IdGenerator {

    private final int fixedId;

    public MockIdGenerator(int fixedId) {
        this.fixedId = fixedId;
    }

    @Override
    public int generateId() {
        return fixedId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MockIdGenerator that = (MockIdGenerator) o;
        return fixedId == that.fixedId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fixedId);
    }

    @Override
    public String toString() {
        return "MockIdGenerator{" +
                "fixedId=" + fixedId +
                '}';
    }
}
