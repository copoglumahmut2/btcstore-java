package com.btc_store.facade;

import com.btc_store.domain.data.custom.user.UserData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserFacade {
    List<UserData> getAllUsers();
    List<UserData> searchUsers(String query);
    UserData getUserByCode(String code);
    UserData saveUser(UserData userData, MultipartFile pictureFile, boolean removePicture);
    void deleteUser(String code);
}
