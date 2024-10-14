package io.github.muhammadredin.tokonyadiaapi.constant;

public enum PaymentMethod {
    QRIS,
    GOPAY,
    SHOPEEPAY,
    VIRTUAL_ACCOUNT;

    public static PaymentMethod getAndValidatePaymentMethod(String paymentMethod) {
        try {
            return PaymentMethod.valueOf(paymentMethod.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment method: " + paymentMethod);
        }
    }
}
