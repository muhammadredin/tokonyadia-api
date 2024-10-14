package io.github.muhammadredin.tokonyadiaapi.constant;

import lombok.Getter;

@Getter
public enum ShippingProvider {
    JNE,
    JNT,
    GOSEND;

    public static ShippingProvider getAndValidateShippingProvider(String name) {
        try {
            return ShippingProvider.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid shipping provider name: " + name);
        }
    }
}
