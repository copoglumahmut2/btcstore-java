package com.btc_store.domain.enums;

public enum CallRequestActionType {
    CREATED,              // Oluşturuldu
    ASSIGNED_TO_GROUP,    // Gruba Atandı
    ASSIGNED_TO_USER,     // Kullanıcıya Atandı
    STATUS_CHANGED,       // Durum Değişti
    PRIORITY_CHANGED,     // Öncelik Değişti
    EMAIL_SENT,           // Mail Gönderildi
    COMMENT_ADDED,        // Yorum Eklendi
    COMPLETED,            // Tamamlandı
    CANCELLED             // İptal Edildi
}
