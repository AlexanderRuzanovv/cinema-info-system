package com.cinemastore.entity;

/**
 * Перечисление ролей пользователей системы
 */
public enum Role {
    CUSTOMER("Зритель"),
    CASHIER("Кассир"),
    MANAGER("Менеджер"),
    ADMIN("Администратор");
    
    private final String displayName;
    
    Role(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}


