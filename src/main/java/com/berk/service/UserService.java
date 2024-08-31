package com.berk.service;

import com.berk.domain.VerificationType;
import com.berk.model.User;

public interface UserService {

    public User findUserByJwtToken(String jwtToken) throws Exception;
    public User findUserByEmail(String email) throws Exception;
    public User findUserById(Long id) throws Exception;

    public User enableTwoFactorAuth(VerificationType verificationType, String sendTo, User user);

    User updatePassword(User user, String newPassword);
}
