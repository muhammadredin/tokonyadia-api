package io.github.muhammadredin.tokonyadiaapi.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentType {
    QRIS("qris"),
    GOPAY("gopay"),
    SHOPEEPAY("shopeepay"),
    VIRTUAL_ACCOUNT("virtual");

    private final String description;

    public static PaymentType fromDescription(String description) {
        for (PaymentType type : PaymentType.values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with description " + description);
    }
}
