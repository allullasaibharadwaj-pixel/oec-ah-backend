package com.example.ecommerce.service;

import java.math.BigDecimal;

/** Delivery pricing rules shared by the cart and checkout. */
public final class Pricing {
    public static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("100.00");
    public static final BigDecimal DELIVERY_CHARGE = new BigDecimal("5.00");

    private Pricing() {}

    public static BigDecimal deliveryCharge(BigDecimal subtotal) {
        if (subtotal.signum() == 0 || subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return DELIVERY_CHARGE;
    }
}
