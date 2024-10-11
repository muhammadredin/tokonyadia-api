package io.github.muhammadredin.tokonyadiaapi.constant;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_ADMIN("Admin"),
    ROLE_STORE("Toko"),
    ROLE_USER("Pelanggan");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public static String findByDescription(String role) {
        for (UserRole userRole : values()) {
            if (userRole.description.equalsIgnoreCase(role)) {
                return userRole.name();
            }
        }
        return null;
    }
}
