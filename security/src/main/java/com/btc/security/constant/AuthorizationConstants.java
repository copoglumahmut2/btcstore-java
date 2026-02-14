package com.btc.security.constant;

import com.btc.service.constant.ServiceConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import util.StoreClassUtils;

@Component
public class AuthorizationConstants {


    public static String SUPER_ADMIN = "super_admin";

    public static final String READ = "Read";
    public static final String SAVE = "Save";
    public static final String DELETE = "Delete";


    public static String[] generateRoles(String typeName, String role) {
        var className = StoreClassUtils.generateClassName(typeName, ServiceConstant.HYPHEN, StringUtils.EMPTY);
        return new String[]{className.concat(ServiceConstant.UNDERSCORE).concat(role), SUPER_ADMIN};

    }

}
