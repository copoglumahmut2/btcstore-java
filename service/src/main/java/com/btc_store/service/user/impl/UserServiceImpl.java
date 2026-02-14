package com.btc_store.service.user.impl;

import com.btc_store.domain.data.custom.login.JwtUserData;
import com.btc_store.domain.data.custom.login.LoginRequest;
import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.localize.LanguageModel;
import com.btc_store.domain.model.custom.role.UserRoleModel;
import com.btc_store.domain.model.custom.user.UserGroupModel;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.persistence.dao.user.UserDao;
import com.btc_store.service.ParameterService;
import com.btc_store.service.SearchService;
import com.btc_store.service.constant.ServiceConstant;
import com.btc_store.service.exception.user.UserNotActiveException;
import com.btc_store.service.user.UserGroupService;
import com.btc_store.service.user.UserService;
import com.btc_store.service.util.ServiceUtils;
import constant.MessageConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import util.StoreStringUtils;
import util.StoreWebUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User Service Class
 *
 * @author mcatal
 * @version v1.0
 * @since 28.10.2021
 */

@Service
@Qualifier("bambooUserService")
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    protected final UserDao userDao;
    protected final UserGroupService userGroupService;
    protected final ParameterService parameterService;
    protected final SearchService searchService;

    private static final String SUPER_ADMIN = "super_admin";

    @Override
    public UserModel getUserModelForStore(String username, SiteModel siteModel) {
        UserModel userModel = null;
        var userRoles = new HashSet<UserRoleModel>();
        if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
            var jwtUsername = SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString();
            var jwtUser = this.getUserModel(jwtUsername, siteModel);
            //if jwt user was super admin, not must be relatedUnit check

            if (CollectionUtils.isNotEmpty(jwtUser.getUserGroups())) {
                userRoles.addAll(jwtUser.getUserGroups()
                        .stream()
                        .map(UserGroupModel::getUserRoles)
                        .filter(CollectionUtils::isNotEmpty)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()));
            }
            userRoles.addAll(jwtUser.getUserRoles());
            if (userRoles.stream().anyMatch(i -> SUPER_ADMIN.equals(i.getCode()))) {
                userModel = userDao.getByUsernameIgnoreCaseAndSite(username, siteModel);
            }
        } else {
            userModel = userDao.getByUsernameIgnoreCaseAndSite(username, siteModel);
        }

        ServiceUtils.checkItemModelIsExist(userModel, UserModel.class, siteModel, username);
        userModel.setUserRoles(userRoles);
        return userModel;
    }

    @Override
    public UserModel getUserModelForStoreByCode(String code, SiteModel siteModel) {
        UserModel userModel = null;
        var userRoles = new HashSet<UserRoleModel>();
        if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
            var jwtUsername = SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString();
            var jwtUser = this.getUserModel(jwtUsername, siteModel);
            //if jwt user was super admin, not must be relatedUnit check

            if (CollectionUtils.isNotEmpty(jwtUser.getUserGroups())) {
                userRoles.addAll(jwtUser.getUserGroups()
                        .stream()
                        .map(UserGroupModel::getUserRoles)
                        .filter(CollectionUtils::isNotEmpty)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()));
            }
            userRoles.addAll(jwtUser.getUserRoles());
            if (userRoles.stream().anyMatch(i -> SUPER_ADMIN.equals(i.getCode()))) {
                userModel = userDao.getByCodeIgnoreCaseAndSite(code, siteModel);
            }
        } else {
            userModel = userDao.getByCodeIgnoreCaseAndSite(code, siteModel);
        }

        ServiceUtils.checkItemModelIsExist(userModel, UserModel.class, siteModel, code);
        userModel.setUserRoles(userRoles);
        return userModel;
    }

    @Override
    public UserModel getUserModelForBack(String username, SiteModel siteModel) {
        username = StoreStringUtils.clearTurkishCharacter(username);
        username = StringUtils.lowerCase(username, Locale.ENGLISH);
        var userModel = userDao.getByUsernameIgnoreCaseAndSite(username, siteModel);
        ServiceUtils.checkItemModelIsExist(userModel, UserModel.class, siteModel, username);
        return userModel;
    }

    @Override
    public UserModel getUserModelForBackByCode(String code, SiteModel siteModel) {
        var userModel = userDao.getByCodeIgnoreCaseAndSite(code, siteModel);
        ServiceUtils.checkItemModelIsExist(userModel, UserModel.class, siteModel, code);
        return userModel;
    }

    @Override
    public UserModel getUserModelWithoutExistCheck(String username, SiteModel siteModel) {
        username = StoreStringUtils.clearTurkishCharacter(username);
        username = StringUtils.lowerCase(username, Locale.ENGLISH);
        return userDao.getByUsernameIgnoreCaseAndSite(username, siteModel);
    }

    @Override
    public boolean existUser(String username, SiteModel siteModel) {
        return userDao.existsByUsernameAndSite(username, siteModel);
    }

    private UserModel getUserModel(String username, SiteModel siteModel) {
        username = StoreStringUtils.clearTurkishCharacter(username);
        username = StringUtils.lowerCase(username, Locale.ENGLISH);
        var userModel = userDao.getByUsernameIgnoreCaseAndSite(username, siteModel);
        ServiceUtils.checkItemModelIsExist(userModel, UserModel.class, siteModel, username);
        return userModel;
    }

    @Override
    public UserModel getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = String.valueOf(authentication.getPrincipal());
        var jwtUserData = (JwtUserData) authentication.getDetails();
        return this.getUserModelForBack(username, jwtUserData.getSite());
    }

    @Override
    public Page<UserModel> getUserModelsForBack(Pageable pageable, SiteModel siteModel) {
        Page<UserModel> userModels = null;
        if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
            var jwtUsername = SecurityContextHolder.getContext().getAuthentication()
                    .getPrincipal().toString();
            var jwtUser = this.getUserModel(jwtUsername, siteModel);
            //if jwt user was super admin, not must be relatedUnit check
            var userRoles = new ArrayList<UserRoleModel>();
            if (!CollectionUtils.isEmpty(jwtUser.getUserGroups())) {
                userRoles.addAll(jwtUser.getUserGroups()
                        .stream()
                        .map(UserGroupModel::getUserRoles)
                        .filter(i -> !CollectionUtils.isEmpty(i))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet()));
            }
            userRoles.addAll(jwtUser.getUserRoles());
            if (userRoles.stream().anyMatch(i -> SUPER_ADMIN.equals(i.getCode()))) {
                userModels = userDao.getBySiteOrderByLastModifiedDateDesc(pageable, siteModel);
            }
        } else {
            userModels = userDao.getBySiteOrderByLastModifiedDateDesc(pageable, siteModel);
        }
        return userModels;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var currentRequest = StoreWebUtils.getCurrentHttpRequest();
        var loginRequest = (LoginRequest) currentRequest.getAttribute("loginRequest");
        username = StoreStringUtils.clearTurkishCharacter(username).toLowerCase(Locale.ENGLISH);
        var user = userDao.getByUsernameIgnoreCaseAndSite(username, loginRequest.getSite());

        if (user == null) {
            log.error("User: [{}] is not found.", username);
            throw new UsernameNotFoundException(username);
        } else {
            log.info("User: [{}] found in the database.", username);
        }

        if (BooleanUtils.isFalse(user.isActive())) {
            throw new UserNotActiveException(String.format("%s username is not active", username),
                    MessageConstant.USER_NOT_ACTIVE_MESSAGE, new Object[]{username});
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getUserGroups().forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getCode())));
        return new User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public String getCurrentUserJWTId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(auth)) {
            var jwtUserData = (JwtUserData) auth.getDetails();
            return jwtUserData.getJwtId();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public boolean isUserSuperAdmin(UserModel userModel) {
        return this.isMatchUserGroup(userModel, ServiceConstant.SUPER_ADMIN);
    }

    @Override
    public boolean isMatchUserGroup(UserModel userModel, String... userGroups) {
        var userGroupModels = userModel.getUserGroups();
        if (CollectionUtils.isNotEmpty(userGroupModels)) {
            return userGroupModels.stream().anyMatch(p ->
                    Arrays.stream(userGroups).anyMatch(ug -> StringUtils.equals(ug, p.getCode())));
        }
        return Boolean.FALSE;
    }

    @Override
    public Set<UserModel> getUserModelsForStore(SiteModel siteModel) {
        var userModels = userDao.getByActiveAndDeletedAndSite(Boolean.TRUE, Boolean.FALSE, siteModel);
        return userModels;
    }

    @Override
    public LanguageModel getCurrentUserLanguage() {
        var currentUser = getCurrentUser();
        var userLanguage = currentUser.getLanguage();
        if (Objects.nonNull(userLanguage)) {
            return userLanguage;
        }
        return currentUser.getSite().getLanguage();
    }

    @Override
    public Set<String> getCurrentUserAuthorities() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }


    @Override
    public Set<UserModel> getUsersByUsernames(Set<String> usernames, SiteModel siteModel) {
        return userDao.getUserModelByUsernameInAndSite(usernames, siteModel);
    }
}
