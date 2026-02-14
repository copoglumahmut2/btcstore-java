package com.btc.domain.constant;

import java.util.Locale;

public interface DomainConstant {
    String CODE_IDX = "_code_idx";

    Locale TURKISH = Locale.forLanguageTag("tr");
    Locale SPANISH = Locale.forLanguageTag("es");
    String UNIQUE_KEYS = "_unique_keys";
    String USENAME_IDX = "_username_idx";
    String SITE_TABLE_NAME = "sites";
    String LANGUAGE_TABLE_NAME = "languages";
    String DELETE_AUDIT_LOG_TABLE_NAME = "deleteauditlogs";
    String PARAMETERMODEL_TABLE_NAME = "parameters";
    String USERROLE_TABLE_NAME = "userroles";
    String USERGROUP_TABLE_NAME = "usergroups";
    String USER_TABLE_NAME = "users";
    String MEDIA_TABLE_NAME = "medias";
    String CMSCATEGORYMODEL_TABLE_NAME = "cmscategories";
    String CMSCATEGORYTYPEMODEL_TABLE_NAME = "cmscategorytypes";
}
