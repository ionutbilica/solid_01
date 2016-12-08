package com.luxoft.training.solid.store.receiptservice;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TextReceipt extends AbstractReceipt {

    private final int no;
    private final Date date;

    public TextReceipt(int no, Date date) {
        this.no = no;
        this.date = date;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Our Store");
        s.append("Receipt no.: " + no + "\n");

        appendProducts(s);

        if (hasDelivery) {
            s.append("Delivery: " + deliveryCost + "\n");
        }
        s.append("Total: " + totalPrice + "\n");
        s.append("Date: " + new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(date) + "\n");
        return s.toString();
    }

    private void appendProducts(StringBuilder s) {
        for (ProductReceiptLine p : productLines) {
            s.append(p.name + ": " + p.count + " x " + p.price + " = " + p.priceForAll + "\n");
        }
    }
}
