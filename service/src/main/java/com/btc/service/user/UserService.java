package com.btc.service.user;

import com.btc.domain.model.custom.SiteModel;
import com.btc.domain.model.custom.localize.LanguageModel;
import com.btc.domain.model.custom.user.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

/**
 * User Service Interface
 *
 * @author mcatal
 * @version v1.0
 * @since 28.10.2021
 */

public interface UserService extends UserDetailsService {
    UserModel getUserModelForStore(String username, SiteModel siteModel);

    UserModel getUserModelForStoreByCode(String code, SiteModel siteModel);

    UserModel getUserModelForBack(String username, SiteModel siteModel);

    UserModel getUserModelForBackByCode(String code, SiteModel siteModel);

    UserModel getUserModelWithoutExistCheck(String username, SiteModel siteModel);

    boolean existUser(String username, SiteModel siteModel);

    UserModel getCurrentUser();

    Page<UserModel> getUserModelsForBack(Pageable pageable, SiteModel siteModel);;

    String getCurrentUserJWTId();

    boolean isUserSuperAdmin(UserModel userModel);

    boolean isMatchUserGroup(UserModel userModel,String... userGroups);

    Set<UserModel> getUserModelsForStore(SiteModel siteModel);

    LanguageModel getCurrentUserLanguage();

    Set<String> getCurrentUserAuthorities();

    Set<UserModel> getUsersByUsernames(Set<String> usernames, SiteModel siteModel);
}
