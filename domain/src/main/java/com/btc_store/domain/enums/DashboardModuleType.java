package com.btc_store.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DashboardModuleType {
    
    CARD("CARD"),           // Dashboard kartları (sayı gösterir)
    QUICK_ACTION("QUICK_ACTION");  // Hızlı işlem linkleri

    private final String value;
}
