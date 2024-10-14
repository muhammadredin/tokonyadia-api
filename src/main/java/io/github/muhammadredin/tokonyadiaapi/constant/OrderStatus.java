package io.github.muhammadredin.tokonyadiaapi.constant;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CANCELLED,
    PENDING,
    VERIFIED,
    ON_PROCESS,
    COMPLETED
}
