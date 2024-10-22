package io.github.muhammadredin.tokonyadiaapi.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderStatus {
    CANCELLED("Cancelled"),
    PENDING("Pending"),
    VERIFIED("Verified"),
    ON_PROCESS("OnProcess"),
    COMPLETED("Completed");

    private final String description;

    public static OrderStatus fromDescription(String description) {
        for (OrderStatus type : OrderStatus.values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }
        return null;
    }
}
