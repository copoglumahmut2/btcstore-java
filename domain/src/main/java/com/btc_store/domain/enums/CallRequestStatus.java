package com.btc_store.domain.enums;

public enum CallRequestStatus {
    PENDING,              // Beklemede
    ASSIGNED,             // Atandı
    IN_PROGRESS,          // İşlemde
    CUSTOMER_INFORMED,    // Müşteri Bilgilendirildi
    COMPLETED,            // Tamamlandı
    CANCELLED,            // İptal Edildi
    CLOSED                // Kapatıldı
}
