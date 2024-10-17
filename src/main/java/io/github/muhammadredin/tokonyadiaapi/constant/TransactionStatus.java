package io.github.muhammadredin.tokonyadiaapi.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TransactionStatus {
    PENDING("pending"),
    SETTLEMENT("settlement"),
    EXPIRE("expire");

    private final String description;

    public static TransactionStatus fromDescription(String description) {
        for (TransactionStatus status : TransactionStatus.values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No enum constant with description " + description);
    }
}
