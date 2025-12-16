package com.cinemastore.entity;

/**
 * Перечисление статусов билета
 */
public enum TicketStatus {
    RESERVED("Забронирован"),
    PAID("Оплачен"),
    ACTIVE("Активен"),
    USED("Использован"),
    COMPLETED("Завершен"),
    CANCELLED("Отменен");
    
    private final String displayName;
    
    TicketStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}


